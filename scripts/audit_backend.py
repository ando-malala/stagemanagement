"""Inventory the checked-in Spring mappings, DTOs and security rules (no server access).

This deliberately targets the annotation style in this repository, not arbitrary Java.
Run from any directory: python scripts/audit_backend.py
"""
from pathlib import Path
import json
import re

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / 'backend/src/main/java/mg/mef/stagemanagement'
OUT = ROOT / 'docs'


def read(path):
    return path.read_text(encoding='utf-8-sig')


def compact(value):
    return re.sub(r'\s+', ' ', value).strip()


def strings(value):
    return re.findall(r'"([^"\n]*)"', value)


def link(path):
    return '../' + path.relative_to(ROOT).as_posix()


security = read(JAVA / 'config/SecurityConfig.java')
rules = []
for match in re.finditer(r'\.requestMatchers\((.*?)\)\s*\.(permitAll|denyAll|authenticated|hasRole|hasAnyRole)\((.*?)\)', security, re.S):
    method = re.search(r'HttpMethod\.(\w+)', match[1])
    rules.append(dict(method=method[1] if method else None, paths=strings(match[1]),
                      access=match[2], roles=strings(match[3])))


def access(method, path):
    for rule in rules:
        if rule['method'] and rule['method'] != method:
            continue
        for pattern in rule['paths']:
            regex = re.escape(re.sub(r'\{[^}]+\}', 'PARAM', pattern))
            regex = regex.replace('PARAM', '[^/]+').replace(r'/\*\*', '(?:/.*)?')
            if re.fullmatch(regex, path):
                return rule['access'], rule['roles']
    return 'denyAll', []


endpoints = []
controllers = sorted(path for path in (JAVA / 'controller').glob('*.java')
                     if re.search(r'@(Get|Post|Put|Patch|Delete)Mapping\b', read(path)))
for path in controllers:
    source = read(path)
    base = re.search(r'@RequestMapping\("([^"]+)"\)', source)[1]
    mappings = list(re.finditer(r'@(Get|Post|Put|Patch|Delete)Mapping(?:\((.*?)\))?', source, re.S))
    for i, mapping in enumerate(mappings):
        section = source[mapping.end():mappings[i + 1].start() if i + 1 < len(mappings) else len(source)]
        signature = re.search(r'public\s+(.+?)\s+(\w+)\s*\((.*?)\)\s*(?:throws\s+[\w.,\s]+)?\{', section, re.S)
        if not signature:
            raise ValueError(f'Unparsed mapping: {path.name}:{mapping[0]}')
        suffixes = strings(mapping[2] or '')
        url = base + (suffixes[0] if suffixes else '')
        method = mapping[1].upper()
        policy, roles = access(method, url)
        parameters = compact(signature[3])
        body = re.search(r'@RequestBody\s+(\w+)\s+\w+', parameters)
        status = '204' if 'noContent()' in section else '201' if 'HttpStatus.CREATED' in section else '200'
        endpoints.append(dict(method=method, path=url, controller=path.stem,
                              handler=signature[2], response=compact(signature[1]),
                              parameters=parameters, body=body[1] if body else None,
                              success=status, access=policy, roles=roles,
                              source=path.relative_to(ROOT).as_posix(),
                              line=source[:mapping.start()].count('\n') + 1))

OUT.mkdir(exist_ok=True)
(OUT / 'api-inventory.json').write_text(json.dumps(endpoints, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
lines = ['# Inventaire API — sources du dépôt', '',
         'Généré par `python scripts/audit_backend.py`. Complément métier : [audit](BACKEND_AUDIT.md).', '',
         f'{len(controllers)} contrôleurs, {len(endpoints)} opérations HTTP, '
         f'{sum(e["access"] != "denyAll" for e in endpoints)} opérations admises par SecurityConfig (certaines techniques).', '',
         'Les règles sont évaluées dans leur ordre Spring. `denyAll` signifie inaccessible même à ADMIN. '
         'Les signatures décrivent les paramètres de chemin, de requête, multipart et le corps exact. '
         'Les paramètres AuthenticationPrincipal sont injectés par le serveur. '
         'Les réponses List sont des tableaux JSON, sans enveloppe data ni pagination Spring. '
         'Les statuts indiqués sont ceux du chemin de succès; voir les erreurs des services ci-dessous, '
         'ainsi que 401/403 pour les accès protégés. Les CRUD bruts peuvent aussi renvoyer 404 via Optional. '
         'Cet inventaire statique ne prouve ni la disponibilité du serveur ni les données PostgreSQL.', '']
for path in controllers:
    lines += [f'## [{path.stem}]({link(path)})', '',
              '| Méthode | URL | Autorisation | Succès | Réponse |', '|---|---|---|---|---|']
    items = [e for e in endpoints if e['controller'] == path.stem]
    for e in items:
        policy = ', '.join(e['roles']) or e['access']
        lines.append(f'| {e["method"]} | `{e["path"]}` | {policy} | {e["success"]} | `{e["response"]}` |')
    lines.append('')
    for e in items:
        lines += [f'- `{e["handler"]}` : `{e["parameters"] or "aucun paramètre"}`.']
    lines.append('')
lines += ['## DTO — champs exacts', '', 'Les contraintes métier se trouvent principalement dans les services, sans @Valid dans les contrôleurs de gestion.', '']
for path in sorted((JAVA / 'dto').rglob('*.java')):
    fields = re.findall(r'private\s+([\w<>?, .]+?)\s+(\w+)\s*(?:=[^;]*)?;', read(path))
    lines += [f'### [{path.stem}]({link(path)})', '',
              ', '.join(f'`{name}: {kind}`' for kind, name in fields), '']
for path in sorted((JAVA / 'service').glob('*.java')):
    for name, fields in re.findall(r'public record (\w+)\((.*?)\)\s*\{', read(path), re.S):
        lines += [f'### [{path.stem}.{name}]({link(path)})', '',
                  ', '.join(f'`{compact(field)}`' for field in fields.split(',')), '']
lines += ['## Erreurs explicites des services', '',
          'Catalogue par service, à rapprocher des méthodes appelées par chaque contrôleur. '
          'Les exceptions de contraintes PostgreSQL et les erreurs de conversion Spring s’y ajoutent. '
          'Les messages dynamiques sont tronqués à leur partie littérale dans ce catalogue.', '']
for path in sorted((JAVA / 'service').glob('*.java')):
    errors = re.findall(r'new ResponseStatusException\(\s*HttpStatus\.(\w+),\s*"([^"]*)"', read(path))
    if errors:
        lines += [f'### [{path.stem}]({link(path)})', '']
        lines += [f'- `{code}` : {message}' for code, message in dict.fromkeys(errors)]
        lines.append('')
lines += ['## Modèles — relations et stockage', '', 'Ces champs ne sont pas tous des champs de DTO et ne doivent pas être envoyés aux API de gestion.', '']
for path in sorted((JAVA / 'model').glob('*.java')):
    fields = re.findall(r'private\s+([\w<>?, .]+?)\s+(\w+)\s*(?:=[^;]*)?;', read(path))
    lines += [f'- [{path.stem}]({link(path)}) : ' + ', '.join(f'`{name}: {kind}`' for kind, name in fields)]
lines += ['', '## Enums', '']
for path in sorted((JAVA / 'model/enums').glob('*.java')):
    lines += [f'- [{path.stem}]({link(path)}) : ' + ', '.join(re.findall(r'^\s*([A-Z][A-Z_]+)\s*[,;]?\s*$', read(path), re.M))]
(OUT / 'API_INVENTORY.md').write_text('\n'.join(lines) + '\n', encoding='utf-8')
print(f'{len(controllers)} controllers; {len(endpoints)} endpoints; {sum(e["access"] != "denyAll" for e in endpoints)} allowed')
