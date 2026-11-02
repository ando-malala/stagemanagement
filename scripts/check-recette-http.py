"""HTTP acceptance against the running isolated instance; never print credentials/tokens."""
from pathlib import Path
import json
import re
import urllib.request
import urllib.error

ROOT = Path(__file__).resolve().parents[1]
manifest = json.loads((ROOT / 'backend/target/recette/session.json').read_text(encoding='utf-8'))
if not re.fullmatch(r'mef_integration_[a-f0-9]{32}', manifest['database']) or manifest['url'] != 'http://127.0.0.1:15173':
    raise SystemExit('An isolated recette manifest is required.')
password = re.search(r'static final String PASSWORD = "([^"]+)"',
                     (ROOT / 'backend/src/test/java/mg/mef/stagemanagement/CompleteIntegrationTests.java').read_text(encoding='utf-8')).group(1)
results = []
tokens = []


def call(path, token=None, data=None):
    headers = {}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    if data is not None:
        headers['Content-Type'] = 'application/json'
    req = urllib.request.Request(manifest['url'] + path,
                                 data=None if data is None else json.dumps(data).encode(), headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=15) as response:
            return response.status, response.read(), {k.lower(): v for k, v in response.headers.items()}
    except urllib.error.HTTPError as error:
        return error.code, error.read(), {k.lower(): v for k, v in error.headers.items()}


def login(name):
    status, body, _ = call('/api/auth/login', data={'identifiant': name, 'motDePasse': password})
    if status != 200:
        raise RuntimeError('Test login failed (HTTP %s).' % status)
    token = json.loads(body)['token']
    tokens.append(token)
    return token


try:
    for path in ['/', '/stagiaires', '/api/auth/me', '/api/dashboard/pilotage']:
        status, _, _ = call(path)
        results.append(dict(role='ANONYME', path=path, observed=status, expected=401 if path.startswith('/api') else 200))
    admin = login('integration-admin')
    status, body, _ = call('/api/utilisateurs', admin)
    if status != 200:
        raise RuntimeError('Cannot read the isolated test accounts.')
    names = [u['identifiant'] for u in json.loads(body)]
    roles = {'ADMIN': 'integration-admin'}
    for role, suffix in [('ADMIN_LEGACY', '-manager0'), ('ENCADREUR', '-mentor0'), ('STAGIAIRE', '-trainee0')]:
        roles[role] = next(n for n in names if n.startswith('scope-') and n.endswith(suffix))
    for role, name in roles.items():
        token = admin if role == 'ADMIN' else login(name)
        for path, expected in [('/api/auth/me', 200), ('/api/stage-participants', 200),
                               ('/api/utilisateurs', 200 if role in ('ADMIN', 'ADMIN_LEGACY') else 403),
                               ('/api/dashboard/pilotage', 403 if role == 'STAGIAIRE' else 200),
                               ('/api/exports/participations.csv', 403 if role == 'STAGIAIRE' else 200)]:
            status, body, headers = call(path, token)
            check = dict(role=role, path=path, observed=status, expected=expected)
            if path.endswith('.csv') and status == 200:
                check['validContent'] = body.startswith(b'\xef\xbb\xbf') and 'text/csv' in headers.get('content-type', '')
            results.append(check)
        _, body, _ = call('/api/stage-participants', token)
        own = json.loads(body)
        if own:
            path = '/api/stage-participants/%s/bilan.pdf' % own[0]['id']
            status, body, headers = call(path, token)
            results.append(dict(role=role, path=path, observed=status, expected=200,
                                validContent=body.startswith(b'%PDF-') and headers.get('content-type') == 'application/pdf'))
finally:
    for token in tokens:
        status, _, _ = call('/api/auth/logout', token, {})
        results.append(dict(role='SESSION_TEST', path='/api/auth/logout', observed=status, expected=204))

success = all(r['observed'] == r['expected'] and r.get('validContent', True) for r in results)
report = dict(database=manifest['database'], url=manifest['url'], accounts=roles, checks=results, allPassed=success)
(ROOT / 'backend/target/recette/http-results.json').write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding='utf-8')
print(json.dumps(report, ensure_ascii=False))
raise SystemExit(0 if success else 1)
