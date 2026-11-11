from pathlib import Path
from copy import deepcopy
import re, json, hashlib
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK, WD_TAB_ALIGNMENT, WD_TAB_LEADER
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.enum.style import WD_STYLE_TYPE
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.text.paragraph import Paragraph

ROOT=Path(__file__).resolve().parents[2]
WORK=Path(__file__).resolve().parent
SRC=Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_concept1.docx')
OUT=ROOT/'Memoire_Licence_MEF_SIIGFP_corrige.docx'
D=Document(SRC); P=list(D.paragraphs)
assert len(P)==496 and len(D.tables)==6
changes=[]
def settext(p,text):
    old=p.text
    p.clear();p.add_run(text)
    changes.append({'avant':old,'apres':text})
def before(anchor,text,style='normal'):
    return anchor.insert_paragraph_before(text,style)
def after(anchor,text,style='normal'):
    e=OxmlElement('w:p');anchor._p.addnext(e)
    p=Paragraph(e,D._body);p.style=style;p.add_run(text);return p
def field(p,code,value):
    for kind in ('begin','instruction','separate','value','end'):
        r=p.add_run()
        if kind in ('begin','separate','end'):
            e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),kind);r._r.append(e)
        elif kind=='instruction':
            e=OxmlElement('w:instrText');e.set(qn('xml:space'),'preserve');e.text=' '+code+' ';r._r.append(e)
        else:r.text=str(value)
bid=10000
def bookmark(p,name):
    global bid
    bid+=1
    s=OxmlElement('w:bookmarkStart');s.set(qn('w:id'),str(bid));s.set(qn('w:name'),name)
    e=OxmlElement('w:bookmarkEnd');e.set(qn('w:id'),str(bid))
    p._p.insert(1 if p._p.pPr is not None else 0,s);p._p.append(e)
def link(p,text,name):
    h=OxmlElement('w:hyperlink');h.set(qn('w:anchor'),name);h.set(qn('w:history'),'1')
    r=OxmlElement('w:r');rp=OxmlElement('w:rPr');color=OxmlElement('w:color');color.set(qn('w:val'),'000000');rp.append(color);r.append(rp)
    t=OxmlElement('w:t');t.text=text;r.append(t);h.append(r);p._p.append(h)

# Remove stale generated indexes, retaining all user-authored content and media.
for e in list(D.element.body):
    if e.tag==qn('w:sdt'):D.element.body.remove(e)
for e in list(D.element.iter(qn('w:bookmarkStart')))+list(D.element.iter(qn('w:bookmarkEnd'))):
    e.getparent().remove(e)

edits={
6:'Novembre 2026',
29:'Ce mémoire présente le travail réalisé lors de mon stage de fin d’études de Licence en Informatique à IT University. D’une durée de trois mois, ce stage s’est déroulé au sein de la Cellule d’Exploitation SIIGFP de la Direction du Budget, au Ministère de l’Économie et des Finances de Madagascar.',
30:'L’avant-propos présente le cadre universitaire de ma formation et la structure d’accueil du stage. Le premier chapitre précise ensuite les besoins auxquels répond le projet.',
34:'IT University (ITU), située à Andoharanofotsy, Antananarivo, constitue le cadre universitaire de ma formation en informatique. Les enseignements suivis en développement web, bases de données et génie logiciel trouvent leur application dans ce projet de fin d’études.',
35:'La formation associe l’acquisition de connaissances techniques à leur mise en pratique dans des projets et des stages.',
36:'Le cycle de Licence permet de développer les compétences nécessaires à la conception et à la réalisation de solutions informatiques. Mon parcours relève de l’option Web.',
37:'L’université propose également des poursuites d’études en Master dans les domaines de l’informatique.',
38:'Le stage de fin d’études permet de confronter les méthodes étudiées aux besoins d’une organisation. Dans ce mémoire, cette mise en pratique concerne l’analyse du suivi des stagiaires, la conception des données et la réalisation d’une application web.',
105:'L’analyse des besoins fait ressortir plusieurs difficultés liées à l’organisation des informations, à leur accessibilité et à leur suivi. Ces difficultés structurent les exigences présentées dans la section suivante.',
124:'La version réalisée distingue trois acteurs applicatifs : l’administrateur, l’encadreur professionnel et le stagiaire. Les opérations de gestion administrative sont regroupées dans le rôle ADMIN ; l’encadreur et le stagiaire disposent de droits liés aux dossiers qui les concernent.',
162:'Ce découpage décrit l’ordre des principales activités du projet. Il ne constitue pas un relevé de durées : l’analyse et la conception précèdent le développement, tandis que les tests et les corrections accompagnent la réalisation. La préparation du déploiement est présentée en section 2.13.',
207:'La figure 3 présente un extrait du modèle relationnel de l’application. Les tables et leurs clés étrangères montrent comment les informations personnelles, académiques et administratives sont reliées. L’inventaire complet du schéma figure en annexe B.',
215:'Le tableau 4 présente les principales catégories de tables et leurs responsabilités dans le fonctionnement de l’application.',
267:'La clôture d’une participation constitue l’aboutissement du suivi administratif. L’application prévoit trois modalités, présentées dans le tableau 5 et illustrées par la figure 8. Les rapports et les documents utilisés par ces contrôles sont détaillés dans les sections 2.6 et 2.7.',
294:'La figure 11 illustre le processus de traitement d’un rapport, depuis sa rédaction par le stagiaire jusqu’à son examen par l’encadreur.',
333:'Un planificateur exécute quotidiennement les contrôles à 6 h, dans le fuseau Indian/Antananarivo. Le code prévoit notamment la détection des fins de stage proches, des stages dépassés, des rapports manquants, des validations en retard, des dossiers incomplets, de la surcharge des encadreurs et des soutenances à venir. [R12]',
393:'La gestion des données repose sur des responsabilités complémentaires : les contraintes PostgreSQL, les services métier Java, la spécification JPA, son implémentation Hibernate et les repositories Spring Data JPA.',
406:'La configuration Docker Compose définit trois services : postgres, backend et frontend. Le service PostgreSQL utilise l’image postgres:16-alpine ; cette version se distingue de PostgreSQL 15, utilisé dans les vérifications locales antérieures. Le backend reçoit la connexion à la base et l’emplacement des documents par variables d’environnement. [R12]',
408:'L’exécution conteneurisée facilite la reproduction de l’environnement, mais ne suffit pas à valider une mise en production. La sécurité de l’exposition réseau, la gestion des identifiants, les sauvegardes, la restauration, les alertes planifiées et la stabilité doivent être vérifiées sur la cible d’exploitation. L’annexe D rassemble les principaux points de contrôle.',
409:'Le déploiement avec Docker constitue ainsi une étape de réalisation. Sa validation opérationnelle reste distincte de la construction des images et de la réussite des tests automatisés.',
418:'3.1 Bilan fonctionnel',
422:'La validation s’appuie sur les vérifications présentées en section 2.12. Les journaux conservés attestent des tests unitaires backend et des tests frontend ; ils ne constituent pas, à eux seuls, une recette complète dans un navigateur ni une validation de l’environnement d’exploitation. Les scénarios complémentaires sont regroupés en annexe C.',
424:'Le projet aboutit à une application couvrant les principales fonctions de gestion et de suivi décrites dans le premier chapitre. Le niveau de validation doit toutefois être apprécié par fonction et par environnement : les fonctions développées, les scénarios testés et les contrôles d’exploitation ne désignent pas le même résultat.',
435:'3.3 Apports à l’organisme d’accueil',
442:'L’application fournit ainsi un support commun pour organiser les dossiers et suivre les stages. Les gains attendus en temps de traitement, en qualité des dossiers et en visibilité devront être mesurés en situation d’utilisation ; aucun gain chiffré n’est établi dans ce mémoire.',
454:'3.5 Limites et perspectives d’évolution',
455:'Les résultats obtenus doivent être rapprochés des limites de validation décrites en section 2.12 : les tests disponibles ne couvrent pas tous les usages dans un navigateur, les performances en charge ni la restauration complète d’un environnement. Ces limites orientent les améliorations suivantes.',
468:'Le projet intègre des mécanismes de sécurité et de contrôle d’accès, un circuit de validation des rapports hebdomadaires, une gestion documentaire, des évaluations finales et des alertes automatiques destinées au suivi des échéances et des dossiers nécessitant une intervention.',
470:'La solution répond à la problématique par la centralisation des dossiers, la séparation des responsabilités et le contrôle des opérations de suivi. Les tests documentés apportent des éléments de validation, tandis que la recette complète sur l’environnement cible, les mesures de performance et les essais de restauration restent nécessaires pour apprécier sa fiabilité en exploitation.',
71:'Ce mémoire comprend trois chapitres. Le premier présente les besoins, les acteurs, la démarche de travail et les choix techniques. Le deuxième expose la conception des données, les traitements et les parcours réalisés, puis la validation, le déploiement et les difficultés rencontrées. Le troisième met ces réalisations en regard des objectifs, présente les acquis du stage et précise les limites et les perspectives. Les annexes complètent cette progression par les règles de gestion, l’inventaire du schéma, les scénarios de recette et les repères de déploiement.'
}
for i,t in edits.items():settext(P[i],t)
P[418].style='Heading 2'
# Stable naming and precise technical vocabulary.
for p in D.paragraphs:
    if p.text and not p._p.xpath('.//w:drawing'):
        for r in p.runs:
            r.text=r.text.replace('Système Intégré Informatisé de la Gestion des Finances Publiques','Système Intégré Informatisé de Gestion des Finances Publiques').replace('Cellule d\'exploitation','Cellule d’Exploitation')
for i in [36,37]:
    P[i].paragraph_format.left_indent=None
settext(P[199],'Source : élaboration personnelle. Le schéma représente l’environnement initial PostgreSQL 15 ; la configuration Docker actuelle utilise PostgreSQL 16 (section 2.13).')
settext(P[25],'SSR (Server-Side Rendering) : génération du contenu HTML à partir des composants côté serveur. Dans ce projet, cette technique intervient dans certains tests du frontend ; elle ne décrit pas le mode de déploiement de l’interface React.')
settext(P[14],'BCrypt : fonction de hachage adaptatif utilisée pour protéger les mots de passe. La vérification compare le mot de passe saisi à son empreinte, sans conserver le mot de passe en clair.')
for r in D.tables[0].rows[1:]:
    for c in r.cells:
        for p in c.paragraphs:
            for run in p.runs:run.text=run.text.replace('generale','générale').replace('affectes','affectés').replace('relatives a','relatives à')
D.tables[2].cell(5,1).text='16 (Docker) ; 15 (essais locaux antérieurs)'

# Restore the missing methodological link between implementation and results.
h=before(P[402],'2.12 Tests et validation','Heading 2')
before(P[402],'La validation vérifie les règles de gestion et les parcours décrits dans les sections précédentes. Elle distingue les tests unitaires des services, les tests de l’interface, l’intégration avec PostgreSQL et la recette réalisée dans un navigateur. Cette distinction permet de préciser ce que chaque résultat démontre.')
before(P[402],'Les journaux du 10 novembre 2026 font état de 36 tests unitaires backend réussis et de 126 tests frontend réussis, sans échec ni test ignoré. Les contrôles ESLint et la construction du frontend sont également consignés comme réussis. Ces résultats correspondent à cette campagne et ne doivent pas être confondus avec une nouvelle exécution sur chaque version du projet. [R13]')
vp=before(P[402],'Tableau 7 — Vérifications documentées et portée des résultats','Caption')
vt=D.add_table(rows=1,cols=3);vp._p.addnext(vt._tbl)
for c,t in zip(vt.rows[0].cells,['Contrôle','Résultat documenté','Portée et limite']):c.text=t
for row in [
('Backend unitaire','36 tests réussis le 10 novembre 2026','Vérifie les scénarios exercés par les services et les contrôles métier.'),
('Frontend','126 tests réussis le 10 novembre 2026','Vérifie notamment des contrats et des rendus de composants ; ne remplace pas une recette navigateur.'),
('Qualité et construction','ESLint et build réussis','Contrôle statique et production des ressources ; ne prouve pas la qualité de tous les parcours.'),
('Intégration historique','Campagnes décrites dans RECETTE_FINALE.md','Résultats rattachés à leurs dates et prérequis ; à rejouer après évolution du schéma ou du code.'),
('Exploitation','Validation complémentaire nécessaire','Charge, sauvegarde, restauration et fonctionnement sur la cible à vérifier.')]:
    for c,t in zip(vt.add_row().cells,row):c.text=t
before(P[402],'La recette complémentaire doit couvrir l’enchaînement complet : création du dossier, affectation, dépôt et examen des rapports, ajout des pièces, évaluation et clôture. Elle doit également vérifier les refus d’accès entre comptes, la lisibilité des écrans et la restauration des données. L’annexe C fournit une grille de scénarios et de résultats attendus, sans les présenter comme déjà exécutés.')
before(P[202],'Les besoins et les choix présentés dans ce chapitre conduisent à organiser la solution autour du dossier du stagiaire et de sa participation. Le chapitre suivant explique leur traduction en données, en règles de gestion et en interfaces.')
before(P[417],'La conception et la réalisation ont permis de relier les besoins aux données, aux traitements et aux parcours utilisateurs. Les vérifications précédentes en précisent la portée. Le chapitre suivant évalue ces résultats au regard des objectifs et des limites du projet.')

# Rebuild captions, including every screenshot added by the author.
figs={129:'Acteurs et principaux parcours',198:'Architecture générale de l’application',209:'Relations principales du modèle relationnel PostgreSQL',229:'Composants de traitement d’une requête métier',241:'Interface de connexion à l’application',253:'Dossier du stagiaire et informations académiques',257:'Formulaire d’affectation à une participation',274:'Décision de clôture et contrôles conservés',279:'Confirmation de la clôture administrative',289:'Grille de suivi des rapports hebdomadaires',298:'Séquence de soumission et d’examen d’un rapport',302:'Actions d’examen d’un rapport soumis',311:'Gestion des pièces jointes du dossier',323:'Grille d’évaluation finale d’une participation',327:'Planification d’une soutenance',336:'Classement des dossiers et actions recommandées',345:'Tableau de bord du suivi des stages',351:'Extrait du bilan de fin de stage exporté en PDF'}
caps=[]
def caption(p,label,n,title):
    p.clear();p.style='Caption';p.add_run(label+' ');field(p,'SEQ '+label+' \\* ARABIC',n);p.add_run(' — '+title)
    name=('fig_' if label=='Figure' else 'tab_')+str(n);bookmark(p,name);caps.append((label,n,title,name,p))
for n,(i,title) in enumerate(figs.items(),1):
    caption(P[i],'Figure',n,title)
    if i in [241,253,257,279,289,302,311,323,327,336,345,351]:after(P[i],'Source : capture de l’application Stage Management.','SourceNote') if 'SourceNote' in D.styles else None
tabs=[(P[126],'Acteurs et responsabilités'),(P[161],'Principales phases de réalisation du projet'),(P[169],'Technologies utilisées dans le projet'),(P[217],'Ensembles du modèle relationnel'),(P[271],'Modalités de clôture'),(P[390],'Parcours et pages de l’application'),(vp,'Vérifications documentées et portée des résultats')]

# New annexes derive from the supplied text and the local project only.
def para(t,style='normal'):return D.add_paragraph(t,style)
def table(title,headers,rows):
    cp=para('', 'Caption');tabs.append((cp,title));t=D.add_table(rows=1,cols=len(headers))
    for c,x in zip(t.rows[0].cells,headers):c.text=x
    for row in rows:
        for c,x in zip(t.add_row().cells,row):c.text=str(x)
    return t
para('[R12] Projet Stage Management. Code source et configuration : conception/Conception2.sql ; backend/pom.xml ; frontend/package.json et package-lock.json ; AlerteScheduler.java ; docker-compose.yml. Copie locale examinée le 12 novembre 2026.')
para('[R13] Projet Stage Management. Journaux de validation du 10 novembre 2026 : tests-backend-unitaires.log ; tests-frontend-chemin-canonique.log ; lint-frontend.log ; build-frontend.log. Historique des campagnes : docs/RECETTE_FINALE.md.')
para('Annexes','Heading 1')
para('Les annexes apportent les détails nécessaires à la lecture technique du projet. Elles complètent les chapitres sans se substituer à l’analyse des besoins, à la description des traitements ni au bilan de validation.')
para('Annexe A Règles de gestion essentielles','Heading 2')
para('Cette synthèse reprend les règles décrites au chapitre 2. Elle facilite le rapprochement entre les besoins, les contrôles applicatifs et les scénarios de recette.')
table('Synthèse des principales règles de gestion',['Repère','Règle','Section'],[
('RG01','Séparer l’identité du stagiaire, son profil académique et chacune de ses participations.','2.1'),
('RG02','Contrôler la validité des dates et les chevauchements avant une affectation.','2.4'),
('RG03','Limiter les accès selon le rôle, le compte associé et l’affectation de l’encadreur.','2.3'),
('RG04','Réserver la rédaction et la soumission du rapport au stagiaire propriétaire ; l’examen revient à l’encadreur affecté.','2.6'),
('RG05','Autoriser les modifications du rapport aux états BROUILLON et A_CORRIGER.','2.6'),
('RG06','Contrôler le format, la taille et le contenu des pièces ; vérifier les droits avant téléchargement.','2.7'),
('RG07','Exiger les pièces obligatoires et les rapports achevés validés pour la clôture ordinaire.','2.5'),
('RG08','Conserver les contrôles d’accès et de transition lors d’une clôture dérogatoire ; tracer les éléments manquants.','2.5'),
('RG09','Vérifier les critères obligatoires de l’évaluation puis bloquer sa modification après finalisation.','2.8'),
('RG10','Conserver les historiques et traiter l’expiration des alertes liées aux participations clôturées.','2.5 et 2.9')])
para('Annexe B Inventaire du modèle relationnel','Heading 2')
para('Le script Conception2.sql déclare 44 tables, 6 vues et 16 déclencheurs. L’inventaire ci-dessous concerne ce script de référence ; il ne constitue pas une mesure du schéma réellement installé sur une cible donnée. [R12]')
families=[
('Référentiels','types_statut statuts etablissements filieres niveaux_etude mois_stage roles types_piece_jointe types_evenement_participation types_alerte parametres_suivi seuils_alerte transitions_statut criteres_evaluation'),
('Comptes et audit','utilisateurs utilisateurs_role sessions_utilisateur historique_connexion_utilisateur utilisateur_statut_historique journal_audit'),
('Organisation','departements departement_statut_historique responsable_departements structures_accueil encadreurs encadreur_statut_historique cadres_stage stages'),
('Dossier du stagiaire','stagiaires stagiaire_statut_historique stagiaire_academique stagiaire_fichiers fichiers'),
('Participations','stage_participant stage_participant_statut_historique stage_participant_evenements stage_participant_fichiers'),
('Suivi et évaluation','rapports_activite rapports_activite_statut_historique rapport_observations evaluations_finales evaluation_criteres soutenances alertes')]
sql=(ROOT.parent/'conception/Conception2.sql').read_text(encoding='utf-8')
expected=re.findall(r'CREATE TABLE\s+(\w+)',sql,re.I)
assert sorted(expected)==sorted(' '.join(x[1] for x in families).split())
table('Inventaire des 44 tables par famille',['Famille','Tables'],[(a,' ; '.join(b.split())) for a,b in families])
table('Vues du schéma de référence',['Vue','Objet'],[
('vue_charge_encadreurs','Charge de suivi par encadreur'),('vue_participations_echeances','Échéances des participations'),('vue_rapports_en_attente','Rapports en attente de traitement'),('vue_pieces_participation_manquantes','Pièces manquantes des participations'),('vue_soutenances_prochaines','Soutenances à venir'),('vue_actions_prioritaires','Situations prioritaires de suivi')])
para('Les 16 déclencheurs contrôlent notamment les types et transitions de statut, les périodes des rapports et les associations documentaires. Ils complètent les services métier décrits en section 2.11. La présence de responsable_departements dans le schéma ne crée pas un quatrième rôle applicatif : la version décrite utilise ADMIN, ENCADREUR et STAGIAIRE.')
para('Annexe C Scénarios de recette complémentaire','Heading 2')
para('Cette grille décrit des contrôles à exécuter avec des comptes et des dossiers de test. Elle ne constitue pas un procès-verbal de réussite. Pour chaque scénario, le résultat observé, la date, la version et les éventuelles anomalies doivent être consignés lors de la recette.')
table('Scénarios de recette de bout en bout',['ID et acteur','Action à vérifier','Résultat attendu'],[
('C01 — ADMIN','Créer un stagiaire, un profil académique et une participation.','Dossier consultable ; liens et dates cohérents.'),
('C02 — ADMIN','Créer une seconde participation avec une période incompatible.','Refus explicite ; aucune affectation incohérente enregistrée.'),
('C03 — STAGIAIRE','Créer un brouillon puis soumettre le rapport de sa période.','Passage à SOUMIS ; date conservée ; accès limité à son dossier.'),
('C04 — ENCADREUR','Demander une correction, puis valider le rapport soumis à nouveau.','Transitions conformes ; observations et dates conservées.'),
('C05 — Autre compte','Tenter l’accès au dossier, au rapport ou au fichier d’un autre utilisateur.','Refus d’accès ou filtrage adapté ; aucune information divulguée.'),
('C06 — Utilisateur autorisé','Déposer une pièce valide, puis une pièce de format ou taille interdits.','Pièce valide consultable ; dépôt non conforme refusé.'),
('C07 — ADMIN','Clôturer normalement un dossier incomplet.','Refus indiquant les éléments manquants.'),
('C08 — ADMIN','Confirmer une clôture forcée dans un dossier de test.','Décision et éléments manquants tracés ; dossier terminé protégé.'),
('C09 — ENCADREUR','Finaliser une évaluation, puis tenter sa modification.','Critères requis contrôlés ; modification après finalisation refusée.'),
('C10 — Exploitation','Vérifier alertes, exports, navigation au clavier et affichage mobile.','Résultats cohérents, lisibles et conformes aux droits.'),
('C11 — Exploitation','Restaurer une sauvegarde de la base et des pièces dans un environnement isolé.','Dossiers, fichiers et droits récupérés de façon cohérente.')])
para('Annexe D Repères de déploiement et de conservation','Heading 2')
para('La configuration Docker Compose organise les services et leur stockage. Les paramètres ci-dessous décrivent la configuration du projet ; ils ne valent pas validation de la cible de production. [R12]')
table('Services et stockage dans Docker Compose',['Service','Configuration','Persistance'],[
('postgres','Image postgres:16-alpine ; initialisation par les scripts SQL du projet.','Volume postgres_data pour les données PostgreSQL.'),
('backend','Construction depuis backend/Dockerfile ; connexion et répertoire documentaire configurables.','Répertoire backend/uploads monté sur /app/uploads.'),
('frontend','Construction depuis frontend/Dockerfile ; interface servie dans le conteneur.','Les données métier restent dans la base et le stockage documentaire.')])
para('Avant une mise en service, les identifiants, l’exposition des ports, la protection des échanges et les habilitations doivent être adaptés à l’environnement cible. Les valeurs de connexion ne doivent pas être diffusées dans le mémoire.')
para('Le script Conception2.sql comporte une suppression et une création de base : son initialisation concerne un environnement neuf. La mise à jour d’une installation existante doit suivre une procédure distincte préservant les données et identifiant la version du schéma.')
para('La sauvegarde doit couvrir ensemble PostgreSQL et les fichiers téléversés. Un essai de restauration isolé doit vérifier les relations entre les métadonnées et les pièces, puis les accès et les parcours essentiels. Le fonctionnement des alertes à 6 h, heure de Madagascar, doit également être observé sur la cible.')

for n,(p,title) in enumerate(tabs,1):caption(p,'Tableau',n,title)

# Consistent academic typography without changing the cover's composition.
normal=D.styles['normal'];normal.font.name='Times New Roman';normal.font.size=Pt(12)
normal.paragraph_format.line_spacing=1.15;normal.paragraph_format.space_after=Pt(6)
normal.paragraph_format.widow_control=True
for name,size in [('Heading 1',18),('Heading 2',14),('Heading 3',12),('Caption',10.5)]:
    s=D.styles[name];s.font.name='Times New Roman';s.font.size=Pt(size);s.font.color.rgb=RGBColor(0,0,0)
    s.paragraph_format.keep_with_next=name!='Caption';s.paragraph_format.keep_together=True
    s.paragraph_format.space_before=Pt(12 if name!='Caption' else 5);s.paragraph_format.space_after=Pt(6)
    s.paragraph_format.line_spacing=1.05
    if name.startswith('Heading'):s.font.bold=True
if 'SourceNote' not in D.styles:D.styles.add_style('SourceNote',WD_STYLE_TYPE.PARAGRAPH)
sn=D.styles['SourceNote'];sn.base_style=normal;sn.font.size=Pt(9);sn.font.italic=True;sn.paragraph_format.space_after=Pt(8);sn.paragraph_format.line_spacing=1
for p in D.paragraphs:
    if p._p in [x._p for x in P[:8]]:continue
    if not p.text.strip() and not p._p.xpath('.//w:drawing') and not p._p.xpath('./w:pPr/w:sectPr'):
        p._p.getparent().remove(p._p);continue
    if p.text.strip()=='.':p._p.getparent().remove(p._p);continue
    p.paragraph_format.widow_control=True
    if p.style.name.startswith('Heading'):
        p.paragraph_format.keep_with_next=True;p.paragraph_format.keep_together=True
        p.paragraph_format.page_break_before=p.style.name=='Heading 1'
        p.paragraph_format.left_indent=None;p.paragraph_format.first_line_indent=None
        for r in p.runs:r.font.color.rgb=RGBColor(0,0,0);r.font.size=None;r.bold=None
    elif p.style.name=='Caption':
        p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.keep_together=True
        p.paragraph_format.keep_with_next=p.text.startswith('Tableau')
        p.paragraph_format.left_indent=None;p.paragraph_format.first_line_indent=None
        for r in p.runs:r.font.size=Pt(10.5);r.font.name='Times New Roman';r.bold=False;r.italic=True
    elif p.text.startswith(('Source :','Sources :')):
        p.style='SourceNote';p.alignment=WD_ALIGN_PARAGRAPH.LEFT
        for r in p.runs:r.font.size=Pt(9)
    elif not p._p.xpath('.//w:drawing'):
        p.paragraph_format.space_after=Pt(6);p.paragraph_format.line_spacing=1.15
        p.paragraph_format.keep_with_next=False;p.paragraph_format.keep_together=False
        p.alignment=WD_ALIGN_PARAGRAPH.JUSTIFY
        for r in p.runs:r.font.name='Times New Roman';r.font.size=Pt(12)
    if p._p.xpath('.//w:drawing'):
        p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.keep_with_next=True
        p.paragraph_format.keep_together=True;p.paragraph_format.space_after=Pt(2)
        p.paragraph_format.line_spacing=1

for i in figs:
    if i in [241,253,257,279,289,302,311,323,327,336,345,351]:after(P[i],'Source : capture de l’application Stage Management.','SourceNote')
for i in range(13,28):
    P[i].paragraph_format.line_spacing=1.03;P[i].paragraph_format.space_after=Pt(5)
    for r in P[i].runs:r.font.size=Pt(11)
for i in range(29,46):
    if P[i]._p.getparent() is None:continue
    P[i].paragraph_format.line_spacing=1.05;P[i].paragraph_format.space_after=Pt(5)
    if P[i].style.name=='normal':
        for r in P[i].runs:r.font.size=Pt(11.5)
        np=P[i]._p.pPr.find(qn('w:numPr')) if P[i]._p.pPr is not None else None
        if np is not None:P[i]._p.pPr.remove(np)
for i in [171,175,180,185,187]:
    P[i].paragraph_format.keep_with_next=True;P[i].paragraph_format.space_before=Pt(8)
    for r in P[i].runs:r.bold=True
P[454].paragraph_format.page_break_before=True
for p in D.paragraphs:
    if p.style.name=='Caption':
        p.paragraph_format.left_indent=Cm(0);p.paragraph_format.right_indent=Cm(0)
        p.paragraph_format.first_line_indent=Cm(0)
        if p.text.startswith('Figure'):p.paragraph_format.keep_with_next=True
    if p.text.startswith('[R'):
        p.alignment=WD_ALIGN_PARAGRAPH.LEFT;p.paragraph_format.line_spacing=1.05
        p.paragraph_format.space_after=Pt(8);p.paragraph_format.keep_together=True
        for r in p.runs:r.font.size=Pt(10.5)
    if p.text.startswith(('Annexe B ','Annexe C ','Annexe D ')):p.paragraph_format.page_break_before=True
# Table grid and readable column proportions, including direct cell border overrides.
for idx,t in enumerate(D.tables):
    t.alignment=WD_TABLE_ALIGNMENT.CENTER;t.autofit=False
    n=len(t.columns);widths=([3.5,12.3] if n==2 else [3.1,5.0,7.7] if n==3 else [3.0,3.5,4.2,5.1])
    if idx==2:widths=[3.0,4.2,8.6]
    if idx==3:widths=[3.0,6.3,6.5]
    if idx==7:widths=[1.7,11.6,2.5]
    if idx==9:widths=[7.5,8.3]
    for c,w in zip(t.columns,widths):c.width=Cm(w)
    pr=t._tbl.tblPr
    for old in list(pr):
        if old.tag in [qn('w:tblBorders'),qn('w:tblCellMar')]:pr.remove(old)
    borders=OxmlElement('w:tblBorders')
    for edge in ['top','left','bottom','right','insideH','insideV']:
        e=OxmlElement('w:'+edge);e.set(qn('w:val'),'single');e.set(qn('w:sz'),'8');e.set(qn('w:color'),'D9D9D9');borders.append(e)
    pr.append(borders)
    margin=OxmlElement('w:tblCellMar')
    for edge in ['top','left','bottom','right']:
        e=OxmlElement('w:'+edge);e.set(qn('w:w'),'90');e.set(qn('w:type'),'dxa');margin.append(e)
    pr.append(margin)
    for ri,row in enumerate(t.rows):
        trpr=row._tr.get_or_add_trPr()
        for e in list(trpr):
            if e.tag==qn('w:trHeight'):trpr.remove(e)
        if ri==0:trpr.append(OxmlElement('w:tblHeader'))
        trpr.append(OxmlElement('w:cantSplit'))
        for ci,c in enumerate(row.cells):
            c.width=Cm(widths[ci]);c.vertical_alignment=WD_CELL_VERTICAL_ALIGNMENT.CENTER
            cp=c._tc.get_or_add_tcPr()
            for e in list(cp):
                if e.tag in [qn('w:tcBorders'),qn('w:shd'),qn('w:noWrap')]:cp.remove(e)
            cb=deepcopy(borders);cb.tag=qn('w:tcBorders');cp.append(cb)
            shade=OxmlElement('w:shd');shade.set(qn('w:fill'),'E8EDF2' if ri==0 else 'FFFFFF');cp.append(shade)
            for p in c.paragraphs:
                p.alignment=WD_ALIGN_PARAGRAPH.LEFT;p.paragraph_format.line_spacing=1.05
                p.paragraph_format.space_before=Pt(0);p.paragraph_format.space_after=Pt(3)
                p.paragraph_format.keep_with_next=ri==0;p.paragraph_format.keep_together=True
                for r in p.runs:r.font.name='Times New Roman';r.font.size=Pt(10);r.bold=ri==0;r.font.color.rgb=RGBColor(0,0,0)

# Keep pictures within printable width; paired screenshots retain a shared caption.
for i,s in enumerate(D.inline_shapes):
    if i<2:continue
    maxw=Cm(15.75);maxh=Cm(14.5)
    scale=min(1,maxw/s.width,maxh/s.height)
    s.width=int(s.width*scale);s.height=int(s.height*scale)

# Roman numerals for preliminary pages; Arabic numbering begins at the introduction.
sections=D.sections
for i,s in enumerate(sections):
    num=s._sectPr.find(qn('w:pgNumType'))
    if num is None:num=OxmlElement('w:pgNumType');s._sectPr.append(num)
    num.set(qn('w:start'),'1');num.set(qn('w:fmt'),'lowerRoman' if i==1 else 'decimal')
    if i:
        s.footer.is_linked_to_previous=False
        fp=s.footer.paragraphs[0];fp.clear();fp.alignment=WD_ALIGN_PARAGRAPH.CENTER;field(fp,'PAGE','1')
        for r in fp.runs:r.font.name='Times New Roman';r.font.size=Pt(10)

# Genuine Word fields with cached, clickable entries; LibreOffice updates pagination.
headings=[]
for p in D.paragraphs:
    if p.style.name in ['Heading 1','Heading 2','Heading 3'] and p.text.strip() and p._p not in [P[8]._p,P[9]._p,P[11]._p]:
        name='section_'+str(len(headings)+1);bookmark(p,name);headings.append((p,int(p.style.name[-1]),name))
for level in range(1,4):
    name='TOC '+str(level)
    if name not in D.styles:D.styles.add_style(name,WD_STYLE_TYPE.PARAGRAPH)
    s=D.styles[name];s.base_style=normal;s.font.size=Pt(11.5);s.font.bold=level==1;s.paragraph_format.line_spacing=1.08
    s.paragraph_format.space_after=Pt(5);s.paragraph_format.left_indent=Cm((level-1)*0.45)
    s.paragraph_format.tab_stops.add_tab_stop(Cm(15.7),WD_TAB_ALIGNMENT.RIGHT,WD_TAB_LEADER.DOTS)
def index(anchor,code,entries):
    first=after(anchor,'','TOC 1');field(first,code,'')
    # Move field end below all entries, while preserving native field structure.
    runs=list(first._p)[1:];end=runs[-1];first._p.remove(end)
    current=first
    for title,name,level in entries:
        current=after(current,'','TOC '+str(level));link(current,title,name);current.add_run('\t');field(current,'PAGEREF '+name+' \\h','1')
    current._p.append(end)
index(P[8],'TOC \\o "1-3" \\h \\z \\u',[(p.text,n,l) for p,l,n in headings])
index(P[9],'TOC \\c "Tableau" \\h \\z',[(f'{lab} {n} — {title}',b,1) for lab,n,title,b,p in caps if lab=='Tableau'])
index(P[11],'TOC \\c "Figure" \\h \\z',[(f'{lab} {n} — {title}',b,1) for lab,n,title,b,p in caps if lab=='Figure'])
for p in [P[8],P[9],P[11]]:
    # Do not let generated indexes include their own titles.
    p.style='TOC Heading' if 'TOC Heading' in D.styles else 'Title'
    p.paragraph_format.page_break_before=True
    for r in p.runs:r.font.size=Pt(18);r.bold=True;r.font.color.rgb=RGBColor(0,0,0)
settings=D.settings.element
u=settings.find(qn('w:updateFields'))
if u is None:u=OxmlElement('w:updateFields');settings.append(u)
u.set(qn('w:val'),'true')
D.save(OUT)
(WORK/'changes.json').write_text(json.dumps(changes,ensure_ascii=False,indent=2),encoding='utf-8')
(WORK/'structure.json').write_text(json.dumps({'headings':[(p.text,l,n) for p,l,n in headings],'captions':[(a,b,c,d) for a,b,c,d,p in caps],'source_sha256':hashlib.sha256(SRC.read_bytes()).hexdigest()},ensure_ascii=False,indent=2),encoding='utf-8')
print(OUT);print('Headings',len(headings),'figures',len(figs),'tables',len(tabs),'images',len(D.inline_shapes))
