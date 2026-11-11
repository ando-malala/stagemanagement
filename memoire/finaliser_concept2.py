"""Finalisation de concept1 : édition OOXML, navigation, légendes et dossier de suivi."""
from pathlib import Path
from copy import deepcopy
from datetime import datetime, timezone
from zipfile import ZipFile, ZIP_DEFLATED
import hashlib, json, re, sys
from lxml import etree
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK, WD_TAB_ALIGNMENT, WD_TAB_LEADER
from docx.enum.style import WD_STYLE_TYPE
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.text.paragraph import Paragraph

ROOT=Path(__file__).resolve().parent
OUTDIR=ROOT/'concept2'; OUTDIR.mkdir(exist_ok=True)
SOURCE=Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_concept1.docx')
OUTPUT=OUTDIR/'Memoire_Licence_MEF_SIIGFP_concept2.docx'
DATE='11 novembre 2026'
HASH=hashlib.sha256(SOURCE.read_bytes()).hexdigest()

# Réutiliser les transformations éditoriales, qui ouvrent bien concept1, en mémoire.
# Aucun fichier des versions précédentes n'est réenregistré.
editor=ROOT/'reviser_concept1.py'
program=editor.read_text(encoding='utf-8').split('D.save(OUT)')[0]
ns={'__file__':str(editor),'__name__':'concept2_editorial_base'}
exec(compile(program,str(editor),'exec'),ns)
D=ns['D']; P=ns['P']

def settext(p,text):
    p.clear();p.add_run(text)

def before(anchor,text='',style='Normal'):
    p=anchor.insert_paragraph_before(text,style)
    return p

def after(anchor,text='',style='Normal'):
    e=OxmlElement('w:p');anchor._p.addnext(e)
    p=Paragraph(e,D._body);p.style=style
    if text:p.add_run(text)
    return p

def find(starts):
    hits=[p for p in D.paragraphs if p.text.startswith(starts)]
    assert len(hits)==1,(starts,len(hits))
    return hits[0]

def field(p,instruction,cache=''):
    for k in ('begin','instruction','separate','value','end'):
        r=p.add_run()
        if k in ('begin','separate','end'):
            x=OxmlElement('w:fldChar');x.set(qn('w:fldCharType'),k)
            if k=='begin':x.set(qn('w:dirty'),'true')
            r._r.append(x)
        elif k=='instruction':
            x=OxmlElement('w:instrText');x.set(qn('xml:space'),'preserve');x.text=' '+instruction+' ';r._r.append(x)
        else:r.text=cache

bid=max([int(e.get(qn('w:id'))) for e in D.element.iter(qn('w:bookmarkStart'))]+[0])+10
def bookmark(p,name,whole=True):
    global bid
    bid+=1;s=OxmlElement('w:bookmarkStart');s.set(qn('w:id'),str(bid));s.set(qn('w:name'),name)
    e=OxmlElement('w:bookmarkEnd');e.set(qn('w:id'),str(bid))
    if whole:
        p._p.insert(1 if p._p.pPr is not None else 0,s);p._p.append(e)
    return s,e

# Contexte institutionnel donné explicitement pour cette finalisation.
settext(P[2],'Conception et réalisation d’une application web\nStage Management\nMEF/SG/DGBF/DB/CSIIGFP — Antaninarenina')
settext(P[7],'Encadreur pédagogique : [NOM ET FONCTION À COMPLÉTER]\nEncadreur professionnel : RAJOELISOLO Mitanjo')
for r in P[7].runs:r.font.size=Pt(11)
settext(P[31],'Ce mémoire s’inscrit dans le cadre de mon stage de fin d’études de Licence en Informatique, option Web, à IT University. D’une durée de trois mois, le stage s’est déroulé au Ministère de l’Économie et des Finances, à Antaninarenina, dans le contexte institutionnel MEF/SG/DGBF/DB/CSIIGFP. Le projet porte sur la conception et la réalisation de Stage Management, une application web de gestion et de suivi des stagiaires.')
settext(P[34],'La structure d’accueil est le Ministère de l’Économie et des Finances, à Antaninarenina. Le rattachement du stage est MEF/SG/DGBF/DB/CSIIGFP. Le cahier des charges désigne la Cellule d’Exploitation SIIGFP de la Direction du Budget comme unité d’accueil du projet. L’application couvre les dossiers, les affectations, les productions hebdomadaires et les décisions de fin de stage. Son périmètre est distinct des traitements financiers du ministère. [1]')
settext(P[38],'Je remercie les responsables de l’IT University pour le cadre de formation dans lequel s’inscrit ce mémoire. J’adresse également mes remerciements aux enseignants et aux responsables pédagogiques pour les connaissances transmises au cours de ma Licence et pour l’accompagnement de mon parcours universitaire.')
settext(P[39],'Je remercie mon responsable pédagogique du mémoire, [NOM ET FONCTION À COMPLÉTER], pour le suivi de ce travail.')
last=P[39]
for text in [
    'J’exprime ma reconnaissance aux responsables du Ministère de l’Économie et des Finances, à Antaninarenina, pour l’accueil de mon stage. Je remercie les responsables de mon unité d’accueil, au sein du rattachement MEF/SG/DGBF/DB/CSIIGFP, pour la possibilité de travailler sur un besoin concret de suivi des stagiaires.',
    'Je remercie RAJOELISOLO Mitanjo, mon encadreur professionnel, pour son accompagnement au cours du stage. Mes remerciements s’adressent également aux personnes de l’équipe d’accueil qui ont contribué au déroulement de ce travail.',
    'Enfin, je remercie ma famille et mes proches pour leur soutien et leurs encouragements tout au long de ma formation et de la préparation de ce mémoire.',
]:last=after(last,text)

# Les divergences entre versions sont signalées à part, sans arbitrage silencieux.
settext(P[160],'Hibernate fonctionne en mode validate et vérifie un schéma préexistant au démarrage. La création initiale de la base est séparée de son évolution. Le script Conception2.sql comprend une suppression et une création de base ; son utilisation concerne donc un environnement neuf. La procédure de mise à jour d’une installation existante doit conserver les données et associer le schéma, les migrations et la version du logiciel. [7]')
settext(P[170],'La préparation d’un environnement d’intégration repose sur une base isolée afin de séparer les essais des dossiers en exploitation. Elle comprend l’installation du schéma, les contrôles de cohérence et l’exécution des scénarios de test. La procédure doit être rattachée à une version identifiée du code et des scripts SQL. Le contrôle des migrations livrées avec cette version constitue une étape préalable à la mise en service. [7]')

# Références en ligne : contenu consulté le 11 novembre 2026, versions documentaires distinguées.
sources=[
 {'id':1,'author':'CELLULE D’EXPLOITATION SIIGFP DE LA DIRECTION DU BUDGET','title':'Cahier des charges : Application web de suivi des stagiaires','version':'Document interne, 21 pages','url':None,'where':'Introduction ; 1.1 à 1.4 ; 2.8 ; 3.1 et 3.2 ; annexes A et C','proof':'CDC_suivi_stagiaires.pdf fourni ; texte lu.'},
 {'id':2,'author':'PROJET STAGE MANAGEMENT','title':'Estimation des tâches','version':'Classeur Estimation_taches_ETU003156.xlsm, feuille Estimation, A1:D53','url':None,'where':'1.4 ; annexe D','proof':'Classeur lu sans macros ; 36 tâches, 65 jours ; sous-totaux 11, 42, 8 et 4.'},
 {'id':3,'author':'SPRING','title':'Using @Transactional','version':'Spring Framework, documentation consultée affichant 7.0.10','url':'https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html','where':'2.2','proof':'Titre et explication des transactions déclaratives vérifiés en ligne.'},
 {'id':4,'author':'POSTGRESQL GLOBAL DEVELOPMENT GROUP','title':'PostgreSQL 15 Documentation — 5.4 Constraints','version':'PostgreSQL 15','url':'https://www.postgresql.org/docs/15/ddl-constraints.html','where':'1.5 ; 2.11','proof':'Titre, contraintes CHECK, UNIQUE et clés étrangères consultés.'},
 {'id':5,'author':'ÉQUIPE REACT','title':'Thinking in React','version':'Documentation évolutive ; site affichant React 19.3 à la consultation','url':'https://react.dev/learn/thinking-in-react','where':'1.5','proof':'Décomposition en composants, hiérarchie et états consultés ; version du projet distincte : 19.2.8.'},
 {'id':6,'author':'SPRING','title':'Authorize HttpServletRequests','version':'Spring Security, documentation consultée affichant 7.1.2','url':'https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html','where':'2.3.2','proof':'Autorisation au niveau des requêtes et configuration authorizeHttpRequests consultées.'},
 {'id':7,'author':'PROJET STAGE MANAGEMENT','title':'Code source et documents de validation','version':'Copie locale ; révisions 9e1a7d8 et d8819ad75f8d43d8a87c749629b31d628aca7e98','url':None,'where':'1.5 ; 2.4 ; 2.11 à 2.14 ; 3.1','proof':'Sources Java/React/SQL, journaux du 10 novembre et compte rendu des migrations du 11 novembre ; aucune nouvelle exécution des tests.'},
 {'id':8,'author':'SPRING','title':'Auto-configuration','version':'Spring Boot, documentation consultée affichant 4.1.2','url':'https://docs.spring.io/spring-boot/reference/using/auto-configuration.html','where':'1.5','proof':'Configuration selon les dépendances et annotation SpringBootApplication vérifiées ; version du projet : 4.1.0.'},
 {'id':9,'author':'ORACLE','title':'Class SecureRandom','version':'Java SE 17 API Specification, java.security','url':'https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/security/SecureRandom.html','where':'2.3.1','proof':'Générateur de nombres aléatoires cryptographiques et nextBytes consultés ; appel SecureRandom présent dans AuthService.'},
 {'id':10,'author':'ÉQUIPE VITE','title':'Getting Started','version':'Guide en ligne, branche courante de Vite 8','url':'https://vite.dev/guide/','where':'1.5 et 1.6','proof':'Serveur de développement et commande de construction consultés ; Vite 8.2.2 dans le verrouillage du projet.'},
 {'id':11,'author':'OWASP FOUNDATION','title':'Authorization Cheat Sheet','version':'OWASP Cheat Sheet Series, ressource en ligne évolutive','url':'https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html','where':'2.3.2','proof':'Moindre privilège, refus par défaut et validation à chaque requête consultés.'},
]
for p in list(D.paragraphs):
    if re.match(r'^\[\d+\] ',p.text):
        p._p.getparent().remove(p._p)
for s in sources:
    entry=f"[{s['id']}] {s['author']}. {s['title']}. {s['version']}."
    if s['url']:entry+=f" [En ligne]. Disponible à l’adresse : {s['url']} (consulté le {DATE})."
    else:entry+=f" Document de projet consulté le {DATE}."
    before(P[219],entry)
before(P[74],'Spring Boot fournit une configuration automatique fondée sur les dépendances présentes dans le projet. L’annotation @SpringBootApplication active ce mécanisme au démarrage. Cette organisation permet d’assembler les composants web et de persistance, tout en conservant une configuration explicite pour les règles de sécurité et les paramètres propres à l’application. [8]')
before(P[74],'Vite fournit le serveur utilisé pendant le développement de l’interface et la commande de construction des ressources à distribuer. Il intervient ainsi dans deux étapes distinctes : les modifications et essais locaux, puis la préparation des fichiers du frontend pour le déploiement. [10]')
P[81].add_run(' [10]')
P[158].add_run(' [4]')
after(P[103],'Le jeton de session est généré avec SecureRandom, une classe de l’API Java destinée à la production de valeurs aléatoires adaptées aux usages cryptographiques. La méthode nextBytes renseigne les octets utilisés par le service d’authentification. Le jeton permet d’identifier la session sans exposer directement le mot de passe ni les rôles dans sa valeur. [9]')
after(P[105],'Cette politique rejoint les recommandations OWASP : limiter les privilèges, refuser les accès non autorisés par défaut et vérifier les permissions à chaque requête. L’application combine les rôles avec la propriété du dossier et l’affectation de l’encadreur. Cette vérification sur la ressource complète la protection des routes de l’API. [11]')

# Sous-sections réelles, sans changer les trois chapitres du mémoire.
before(P[103],'Authentification et sessions','Heading 3')
before(P[105],'Contrôle d’accès aux dossiers','Heading 3')
accounts_h=before(P[108],'Administration des comptes','Heading 3')
accounts=before(P[108],'La page Utilisateurs permet à l’administrateur de créer et de consulter les comptes. La fiche d’un compte donne accès aux rôles attribués et à la réinitialisation du mot de passe. Le panneau de rôles contrôle l’ajout et le retrait des attributions et impose la conservation d’au moins un rôle. Les changements de statut et l’historique du compte ne sont pas disponibles dans cette interface. [7]')
before(P[109],'Dossier personnel et académique','Heading 3')
before(P[111],'Gestion des encadreurs','Heading 3')
mentors=before(P[111],'La page Encadreurs présente l’identité, le compte associé, la fonction, le département, la capacité d’encadrement et le statut. La création est réservée à l’administrateur. La fiche détaillée permet de consulter l’encadreur et son association à un compte. Ces informations servent ensuite à choisir un encadreur lors de l’affectation d’une participation. [7]')
before(P[111],'Création du stage et affectation','Heading 3')
stage=before(P[111],'La création d’un stage renseigne son libellé et sa structure d’accueil. La page Stages sépare la liste des stages de celle des participations. Après création du dossier et du profil académique, l’administrateur peut affecter le stagiaire à un stage avec un cadre de travail, un encadreur et une période. Cette séparation permet d’accueillir plusieurs participants dans un même stage. [7]')
before(P[125],'Périodes attendues et saisie','Heading 3')
before(P[127],'Soumission et examen','Heading 3')
before(P[139],'Évaluation finale','Heading 3')
before(P[141],'Soutenance facultative','Heading 3')
before(P[143],'Détection et traitement des alertes','Heading 3')
alerts=after(P[145],'La page Alertes présente la priorité, le type, le stagiaire concerné, le message, l’échéance et le statut. Les filtres portent sur le statut, la priorité, la nature et le type. Une fenêtre de détail donne accès aux informations de l’alerte et aux liens vers le dossier. Les actions de traitement sont proposées selon les permissions et la nature de l’alerte. [7]')
before(P[146],'Vue d’ensemble et priorisation','Heading 3')
overview=before(P[146],'La page Alertes comprend également une Vue d’ensemble, implémentée par le composant AlertOverview. Elle affiche des compteurs pour les alertes à traiter et leur répartition par type. La sélection d’un compteur ou d’un type applique un filtre à la liste. Cette vue secondaire est intégrée à la page Alertes et reste distincte du tableau de bord principal. [7]')
before(P[147],'Tableau de bord principal','Heading 3')

# Captures : blocs provisoires compacts et explicites, aucune image simulée.
capture_style=D.styles.add_style('Emplacement capture',WD_STYLE_TYPE.PARAGRAPH)
capture_style.base_style=D.styles['Normal'];capture_style.font.size=Pt(10.5)
capture_style.font.color.rgb=RGBColor.from_string('4A5560')
capture_style.paragraph_format.left_indent=Cm(.3)
capture_style.paragraph_format.right_indent=Cm(.3)
capture_style.paragraph_format.line_spacing=1.1
capture_style.paragraph_format.space_before=Pt(4)
capture_style.paragraph_format.space_after=Pt(4)

capture_specs=[
 ('connexion',P[103],'2.3.1','Interface de connexion','Montrer les champs d’identification et le bouton de connexion, sans mot de passe saisi.','Interface de connexion à Stage Management','frontend/src/pages/auth/LoginPage.jsx'),
 ('utilisateurs',accounts,'2.3.3','Gestion des utilisateurs et rôles du compte','Montrer la fiche d’un compte de démonstration et le panneau des rôles ; ne pas illustrer un changement de statut absent de cet écran.','Administration des rôles d’un utilisateur','frontend/src/pages/utilisateurs/AccountAdministration.jsx'),
 ('stagiaires',P[109],'2.4.1','Gestion des stagiaires et dossier individuel','Montrer une fiche renseignée, son identité et ses informations académiques.','Dossier du stagiaire et informations académiques','frontend/src/pages/stagiaires/StagiaireDetailPage.jsx'),
 ('encadreurs',mentors,'2.4.2','Gestion des encadreurs','Montrer la liste avec fonction, département, compte associé et capacité d’encadrement.','Gestion des encadreurs professionnels','frontend/src/pages/encadreurs/EncadreursPage.jsx'),
 ('stage',stage,'2.4.3','Création d’un stage','Montrer le formulaire de création avec le libellé du stage et la structure d’accueil.','Création d’un stage','frontend/src/pages/stages/StagesPage.jsx'),
 ('affectation',P[111],'2.4.3','Affectation d’un stagiaire à une participation','Montrer le profil académique, le stage, le cadre, l’encadreur et les dates.','Affectation d’un stagiaire à une participation','frontend/src/pages/stages/StagesPage.jsx'),
 ('cloture',P[122],'2.5','Confirmation de clôture administrative','Montrer le motif et l’avertissement relatif aux pièces ou rapports manquants, sur un dossier de démonstration.','Confirmation de la clôture d’une participation','frontend/src/pages/stages/ParticipationActions.jsx'),
 ('rapports',P[126],'2.6.1','Grille des rapports d’activité','Montrer les semaines attendues et leurs statuts, avec une période renseignée et une période sans rapport si disponibles.','Suivi des rapports hebdomadaires','frontend/src/pages/rapports/RapportWeeks.jsx'),
 ('examen',P[131],'2.6.2','Examen d’un rapport soumis','Montrer le contenu et les actions de correction ou de validation depuis le compte de l’encadreur affecté.','Examen d’un rapport par l’encadreur','frontend/src/pages/rapports/RapportActions.jsx'),
 ('documents',P[134],'2.7','Dépôt et consultation des documents','Montrer les types de pièces, les documents présents et les actions disponibles depuis un compte administrateur.','Gestion des pièces jointes du dossier','frontend/src/components/documents/DocumentsPanel.jsx'),
 ('evaluation',P[140],'2.8.1','Évaluation finale','Montrer les critères, les notes de 0 à 5 et l’état de finalisation sur des données de démonstration.','Grille d’évaluation finale','frontend/src/pages/evaluations/EvaluationNotes.jsx'),
 ('soutenance',P[141],'2.8.2','Suivi des soutenances','Montrer une soutenance planifiée avec la participation, la date et le lieu.','Planification d’une soutenance','frontend/src/pages/soutenances/SoutenancesPage.jsx'),
 ('alertes',alerts,'2.9.1','Gestion des alertes','Montrer la liste, les filtres, la priorité, le type et le statut des alertes.','Liste et filtres des alertes','frontend/src/pages/alertes/AlertesPage.jsx'),
 ('overview',overview,'2.9.2','Vue d’ensemble des alertes','Montrer les compteurs à traiter et la répartition par type qui permettent de filtrer la liste.','Vue d’ensemble des alertes','frontend/src/pages/alertes/AlertOverview.jsx'),
 ('dashboard',P[147],'2.9.3','Tableau de bord principal','Montrer les principaux indicateurs, les échéances et les dossiers prioritaires avec des valeurs lisibles.','Tableau de bord principal du suivi des stages','frontend/src/pages/dashboard/DashboardPage.jsx'),
]

old_captions=[]
for p in D.paragraphs:
    m=re.match(r'^(Figure|Tableau) (\d+) — (.+)$',p.text)
    if m:old_captions.append({'p':p,'kind':m[1],'name':m[1].lower()+'_'+m[2],'title':m[3],'capture':False})
all_captions=list(old_captions)
captures=[]
for cid,anchor,section,screen,objective,title,code in capture_specs:
    first=after(anchor,"[CAPTURE D'ÉCRAN À INSÉRER]",'Emplacement capture')
    first.runs[0].bold=True
    shade=OxmlElement('w:shd');shade.set(qn('w:fill'),'EAF0F5');first._p.get_or_add_pPr().append(shade)
    lines=[first,after(first,'Écran : '+screen+'.','Emplacement capture')]
    lines.append(after(lines[-1],'Objectif : '+objective,'Emplacement capture'))
    cap=after(lines[-1],'','Caption');lines.append(cap)
    lines.append(after(cap,"Source : Capture de l'application Stage Management réalisée par l'auteur.",'Emplacement capture'))
    for p in lines[:-1]:p.paragraph_format.keep_with_next=True
    for p in lines:p.paragraph_format.keep_together=True
    lines[-1].paragraph_format.space_after=Pt(12)
    info={'id':cid,'p':cap,'kind':'Figure','name':'capture_'+cid,'title':title,'capture':True,'anchor':anchor,'section':section,'screen':screen,'objective':objective,'code':code}
    all_captions.append(info);captures.append(info)

# Numérotation de toutes les figures dans leur ordre réel, schémas et emplacements compris.
positions={id(p._p):i for i,p in enumerate(D.paragraphs)}
all_captions.sort(key=lambda c:positions[id(c['p']._p)])
counts={'Figure':0,'Tableau':0};numbers={}
for cap in all_captions:
    kind=cap['kind'];counts[kind]+=1;num=counts[kind];cap['number']=num;numbers[cap['name']]=num
    p=cap['p'];p.clear();p.style='Caption';p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    if cap['capture']:p.add_run('Légende prévue : ')
    p.add_run(kind+' ')
    b,e=bookmark(p,cap['name'],False);p._p.append(b)
    field(p,'SEQ '+kind+' \\* ARABIC',str(num));p._p.append(e)
    p.add_run(' — '+cap['title'])
    p.paragraph_format.keep_with_next=(kind=='Tableau' or cap['capture'])

cap_elements={id(c['p']._p) for c in all_captions}
for p in list(D.paragraphs):
    if id(p._p) in cap_elements:continue
    text=p.text
    # Les renvois existants désignent les six schémas originaux ; leurs signets restent stables.
    if re.search(r'\b(?:[Ff]igure|[Tt]ableau) \d+\b',text):
        p.clear();pos=0
        for m in re.finditer(r'\b([Ff]igure|[Tt]ableau) (\d+)\b',text):
            p.add_run(text[pos:m.start()]);p.add_run(m[1]+' ')
            key=m[1].lower()+'_'+m[2]
            assert key in numbers,key
            field(p,'REF '+key+' \\h',str(numbers[key]));pos=m.end()
        p.add_run(text[pos:])
for cap in captures:
    p=cap['anchor'];p.add_run(' (figure ');field(p,'REF '+cap['name']+' \\h',str(cap['number']));p.add_run(').')

