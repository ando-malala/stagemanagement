from pathlib import Path
import sys, hashlib, json
from zipfile import ZipFile
from lxml import etree

ROOT=Path(__file__).resolve().parent
SKILL=Path('C:/Users/Andomalala/.codex/plugins/cache/openai-primary-runtime/documents/26.1110.11613/skills/documents')
sys.path.insert(0,str(SKILL/'scripts'))
from comments_add import add_comments

src=ROOT/'Memoire_Licence_MEF_SIIGFP_concept1_revise.docx'
out=ROOT/'Memoire_Licence_MEF_SIIGFP_reperes_captures.docx'
digest=hashlib.sha256(src.read_bytes()).hexdigest()
items=[
('L’authentification repose sur une session applicative.',
 'CAPTURE 1 — Connexion — Section 2.3.\nInsérer la capture juste après ce paragraphe. Montrer la page de connexion : identifiant, mot de passe vide et bouton de connexion.\nLégende proposée : « Interface de connexion à l’application ».\nPour toutes les captures : utiliser des données de démonstration et cadrer la zone utile de l’application. Dans Word, choisir Références > Insérer une légende > Figure. Les numéros de ces commentaires sont des repères, pas les numéros définitifs des figures. Après insertion de toutes les images, actualiser les légendes, renvois et listes avec Ctrl+A puis F9. Supprimer les commentaires avant l’envoi du mémoire.'),
('L’admission commence par la création du stagiaire et l’ajout de son profil académique.',
 'CAPTURE 2 — Dossier stagiaire — Section 2.4.\nInsérer après ce paragraphe. Montrer la fiche d’un stagiaire de démonstration avec son identité, son établissement, sa filière et son niveau d’étude. Choisir une fiche renseignée pour illustrer le lien entre les informations personnelles et académiques.\nLégende proposée : « Dossier du stagiaire et informations académiques ».'),
('L’affectation crée une participation comportant les références du stage,',
 'CAPTURE 3 — Affectation — Section 2.4.\nInsérer après ce paragraphe. Montrer le formulaire d’affectation renseigné avec le stage, le profil académique, le cadre de stage, l’encadreur et les dates de début et de fin. Les listes de choix doivent rester lisibles.\nLégende proposée : « Formulaire d’affectation d’un stagiaire à une participation ».'),
('Dans l’interface, l’administrateur accède à un dialogue de confirmation',
 'CAPTURE 4 — Clôture — Section 2.5.\nInsérer après ce paragraphe. Montrer le dialogue réel de confirmation de clôture, avec le motif et l’avertissement concernant les pièces ou rapports manquants. Préférer un dossier de démonstration incomplet pour rendre la règle visible.\nLégende proposée : « Confirmation de la clôture administrative d’une participation ».'),
('L’interface présente les périodes attendues dans une grille.',
 'CAPTURE 5 — Suivi hebdomadaire — Section 2.6.\nInsérer après ce paragraphe, avant le passage présentant la séquence du rapport. Montrer la grille des semaines avec leurs dates, les statuts et les actions disponibles. Si le jeu de démonstration le permet, afficher une période sans rapport et une période déjà renseignée.\nLégende proposée : « Grille de suivi des rapports hebdomadaires ».'),
('Les changements d’état utilisent une requête PATCH sur le statut du rapport.',
 'CAPTURE 6 — Examen du rapport — Section 2.6.\nInsérer après ce paragraphe. Avec un compte encadreur affecté, ouvrir un rapport soumis et montrer le contenu, le statut et les actions permettant de demander une correction ou de valider. Ne pas déclencher de validation uniquement pour obtenir la capture.\nLégende proposée : « Examen d’un rapport soumis par l’encadreur ».'),
('Les pièces jointes sont rattachées soit au dossier général du stagiaire,',
 'CAPTURE 7 — Documents — Section 2.7.\nInsérer après ce paragraphe. Montrer le panneau des pièces jointes depuis un compte administrateur : type de pièce, nom du document et actions de dépôt ou de téléchargement. Si l’interface les affiche, conserver les indicateurs de pièces obligatoires présentes ou manquantes.\nLégende proposée : « Gestion des pièces jointes du dossier ».'),
('La finalisation vérifie que chaque critère actif obligatoire possède une note.',
 'CAPTURE 8 — Évaluation — Section 2.8.\nInsérer après ce paragraphe. Montrer une grille d’évaluation de démonstration avec les critères, les notes de 0 à 5, les commentaires et l’état de finalisation. Cadrer la grille pour que les notes restent lisibles.\nLégende proposée : « Grille d’évaluation finale d’une participation ».'),
('La soutenance est facultative. Lorsqu’elle est prévue,',
 'CAPTURE 9 — Soutenance — Facultative — Section 2.8.\nInsérer après ce paragraphe si vous souhaitez illustrer cette fonction. Montrer la fiche ou le formulaire d’une soutenance de démonstration, avec la participation, la date et le lieu. Une capture suffit ; ne pas ajouter une seconde image du même formulaire.\nLégende proposée : « Planification d’une soutenance liée à une participation ».'),
('PilotageService sélectionne les alertes accessibles à l’utilisateur,',
 'CAPTURE 10 — Dossiers prioritaires — Section 2.9.\nInsérer après ce paragraphe. Montrer la liste des dossiers prioritaires, avec le niveau de priorité, le score si celui-ci est affiché, les alertes et l’action recommandée. Choisir des données de démonstration qui produisent plusieurs niveaux de priorité.\nLégende proposée : « Classement des dossiers et actions recommandées ».'),
('Le tableau de bord présente les échéances, les rapports attendus ou manquants,',
 'CAPTURE 11 — Tableau de bord — Section 2.9.\nInsérer après ce paragraphe. Montrer une vue d’ensemble renseignée : indicateurs, échéances, rapports et charge d’encadrement, selon les éléments réellement présents à l’écran. Éviter une capture de toute une page très longue qui rendrait les chiffres illisibles. Si cette vue est identique à la capture des dossiers prioritaires, conserver une seule image commune.\nLégende proposée : « Tableau de bord du suivi des stages ».'),
('Les services de synthèse réunissent les données nécessaires aux documents de sortie.',
 'CAPTURE 12 — Bilan exporté — Facultative — Section 2.9.\nInsérer après ce paragraphe. Montrer un extrait lisible du bilan PDF généré avec un dossier de démonstration : identification de la participation, suivi des rapports ou évaluation. Cette image illustre le résultat de l’export ; elle n’est pas une nouvelle capture du tableau de bord.\nLégende proposée : « Extrait du bilan de fin de stage exporté en PDF ».'),
]
add_comments(str(src),str(out),items,author='Repères de captures',require_all=True)

W='http://schemas.openxmlformats.org/wordprocessingml/2006/main'
ns={'w':W}
with ZipFile(src) as a,ZipFile(out) as z:
    doc=etree.fromstring(z.read('word/document.xml'))
    # Place pPr first as required by the paragraph schema.
    for p in doc.xpath('//w:p[w:commentRangeStart]',namespaces=ns):
        pp=p.find('{'+W+'}pPr')
        if pp is not None:
            p.remove(pp);p.insert(0,pp)
    replacement=etree.tostring(doc,xml_declaration=True,encoding='UTF-8',standalone=True)
    contents={n:z.read(n) for n in z.namelist()}
    contents['word/document.xml']=replacement
    comments=etree.fromstring(contents['word/comments.xml'])
    ids=comments.xpath('//w:comment/@w:id',namespaces=ns)
    assert len(ids)==len(items)==12 and len(set(ids))==12
    for tag in ['commentRangeStart','commentRangeEnd','commentReference']:
        assert sorted(doc.xpath('//w:'+tag+'/@w:id',namespaces=ns))==sorted(ids)
    original=etree.fromstring(a.read('word/document.xml'))
    assert original.xpath('//w:t/text()',namespaces=ns)==doc.xpath('//w:t/text()',namespaces=ns)
    assert len(doc.xpath('//w:sectPr',namespaces=ns))==3
    assert b'comments.xml' in contents['word/_rels/document.xml.rels']
    assert b'/word/comments.xml' in contents['[Content_Types].xml']
    for n in a.namelist():
        if n.startswith('word/media/'):
            assert a.read(n)==contents[n]
from zipfile import ZIP_DEFLATED
with ZipFile(out,'w',ZIP_DEFLATED) as z:
    for n,b in contents.items():z.writestr(n,b)
with ZipFile(out) as z:assert z.testzip() is None
assert hashlib.sha256(src.read_bytes()).hexdigest()==digest
print(json.dumps({'output':str(out),'comments':len(items),'body_preserved':True,'source_preserved':True},ensure_ascii=False))
