"""Actualisation documentaire du mémoire, sans lancer Word ni LibreOffice."""
from pathlib import Path
from copy import deepcopy
import json, re, hashlib
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.text.paragraph import Paragraph

ROOT = Path(__file__).resolve().parent
SOURCE = Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_concept1.docx')
OUT = ROOT / 'Memoire_Licence_MEF_SIIGFP_complet.docx'
D = Document(SOURCE)
P = list(D.paragraphs)
assert len(P) == 242, 'La version source a changé.'
source_hash = hashlib.sha256(SOURCE.read_bytes()).hexdigest()

def replace(i, text):
    p = P[i]
    props = deepcopy(p.runs[0]._r.rPr) if p.runs and p.runs[0]._r.rPr is not None else None
    p.clear()
    r = p.add_run(text)
    if props is not None:
        r._r.insert(0, props)
    return p

def insert_before(anchor, text, style='Normal'):
    p = anchor.insert_paragraph_before(text, style)
    if style == 'Normal':
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    return p

def before(index, texts):
    for style, text in texts:
        insert_before(P[index], text, style)

def remove(i):
    p = P[i]._p
    p.getparent().remove(p)

updates = {
6: 'Novembre 2026',
25: 'SIIGFP — Système Intégré Informatisé de Gestion des Finances Publiques. Le stage se déroule à la Cellule d’Exploitation SIIGFP de la Direction du Budget. [R10, p. 1 et 21]',
31: 'Ce mémoire présente le travail réalisé dans le cadre de mon stage de fin d’études de Licence en Informatique, option Web, à IT University. D’une durée de trois mois, ce stage s’inscrit au sein de la Cellule d’Exploitation SIIGFP de la Direction du Budget du Ministère de l’Économie et des Finances de Madagascar.',
32: 'Le projet porte sur la conception et la réalisation d’une application web de suivi des stagiaires et de leurs participations à un stage. Il relie les enseignements de modélisation des données, de programmation web et de génie logiciel à un besoin de gestion exprimé par la structure d’accueil.',
33: 'IT University constitue le cadre universitaire de ce travail de Licence. L’option Web situe le mémoire dans le domaine de la conception d’applications accessibles depuis un navigateur. La présentation qui suit privilégie les compétences mobilisées dans le projet : analyse des besoins, développement frontend et backend, persistance relationnelle, sécurité et validation.',
34: 'La structure d’accueil est la Cellule d’Exploitation SIIGFP de la Direction du Budget, au sein du MEF. Le cahier des charges identifie RAJOELISOLO Mitanjo comme encadreur professionnel et RAKOTONIAINA Andomalala Erica comme stagiaire. L’application étudiée concerne le suivi des stages ; son rattachement au contexte SIIGFP ne signifie pas qu’elle échange des données avec les applications financières du ministère. [R10, p. 1]',
35: 'L’analyse distingue trois sources : le cahier des charges, qui définit les besoins ; le classeur d’estimation, qui précise les tâches et les durées prévisionnelles ; le dépôt logiciel, qui permet de décrire la réalisation. Les vérifications du 10 novembre 2026 portaient sur la révision 9e1a7d8. La mise à jour documentaire du 11 novembre prend également en compte les évolutions visibles dans la révision d8819ad et les comptes rendus associés. Ces dates ne remplacent pas les dates administratives du stage. [R7, R9–R12]',
38: 'Je remercie IT University pour la formation dans laquelle s’inscrit ce mémoire, ainsi que le Ministère de l’Économie et des Finances et la Cellule d’Exploitation SIIGFP de la Direction du Budget pour l’accueil de ce stage.',
39: 'Je remercie RAJOELISOLO Mitanjo, mon encadreur professionnel, ainsi que les personnes ayant contribué à l’encadrement universitaire et professionnel de ce travail.',
40: '',
42: 'Le suivi d’un stage associe l’identité d’une personne, son parcours académique, une structure d’accueil, un encadreur, des dates, des activités et des décisions administratives. La Cellule d’Exploitation SIIGFP de la Direction du Budget accueille des stagiaires provenant de différents établissements et filières. Le cahier des charges souligne les difficultés liées à la dispersion des informations, à la recherche d’un dossier complet et au suivi des rapports et des échéances. Le projet vise à organiser ces informations dans une application web centralisée. [R10, §1]',
43: 'La problématique est la suivante : comment centraliser et sécuriser le suivi des stagiaires de la Cellule SIIGFP, depuis leur enregistrement jusqu’à la clôture de leur participation, tout en assurant la traçabilité, le suivi hebdomadaire et la priorisation des dossiers nécessitant une intervention ? Elle associe un besoin administratif de consultation fiable à un besoin opérationnel de contrôle et d’anticipation. Les difficultés décrites par le cahier des charges sont qualitatives ; aucun délai moyen ni taux de perte de documents n’est présenté comme mesuré.',
44: 'L’objectif général consiste à concevoir et développer une application de gestion du cycle de vie des stages. Les objectifs spécifiques sont de centraliser les données personnelles et académiques, organiser les affectations, encadrer le workflow des rapports, gérer les pièces jointes, détecter les échéances, préparer l’évaluation et le bilan, et conserver les historiques. Le cahier des charges prévoit aussi un périmètre départemental pour les responsables, des soutenances facultatives et des actions recommandées fondées sur les alertes. La réalisation de ces exigences est appréciée séparément au chapitre 3. [R10, §2–5]',
45: 'La démarche rapproche les exigences, les tâches prévues et les mécanismes du logiciel. Le schéma PostgreSQL, les services Java, les contrôleurs REST et les composants React permettent d’expliquer la conception. La campagne du 10 novembre 2026 a donné 36 tests unitaires backend et 126 tests frontend réussis, ainsi qu’un contrôle ESLint et une construction Vite réussis. Un compte rendu du 11 novembre rapporte ensuite 66 tests backend réussis, dont 13 tests PostgreSQL. Ces campagnes ont des périmètres distincts ; elles ne sont pas additionnées et ne remplacent pas une recette métier signée. [R9, R12]',
46: 'Le premier chapitre présente le contexte, l’analyse de l’existant, les acteurs, le planning et les besoins. Le deuxième décrit la conception et la réalisation, des données jusqu’aux parcours de suivi et aux contrôles de sécurité. Le troisième analyse les résultats, les écarts avec le cahier des charges et les perspectives. Les annexes regroupent les preuves, l’inventaire relationnel, les tâches estimées, les critères de recette et les livrables attendus.',
49: 'Le projet est intitulé « Création d’une application web de suivi des stagiaires et des stages au sein de la Cellule d’exploitation SIIGFP du Ministère de l’Économie et des Finances ». La structure d’accueil est rattachée à la Direction du Budget. Le besoin couvre l’accueil administratif, les affectations, le suivi professionnel, les documents et la préparation de la fin de stage. La finalité exprimée est de disposer d’une source d’information commune, cohérente et traçable pour les acteurs concernés. [R10, p. 1 et §1–2]',
51: 'Les services de synthèse et de pilotage répondent au besoin de rendre visibles les échéances, les pièces manquantes, l’avancement des rapports et les alertes. Le cahier des charges précise les critères de recette CR01 à CR20 qui serviront à apprécier ces fonctions. La présence d’un calcul ou d’un écran dans le code constitue un élément de réalisation ; la satisfaction effective des utilisateurs doit être constatée lors de la recette. [R10, §5.14 et §12.3 ; P22, P34]',
52: '1.2 Analyse de l’existant et périmètre du projet',
53: 'Le cahier des charges décrit un suivi rendu difficile par la dispersion des informations entre plusieurs supports, l’absence de suivi hebdomadaire centralisé et le manque de visibilité sur les stages en cours, à venir ou terminés. Il évoque les documents papier, les classeurs isolés et les fichiers non synchronisés comme situations conduisant à une consultation lente et à un historique incomplet. Ces éléments fondent le besoin de centralisation ; ils ne permettent pas d’attribuer au service un logiciel précis ni de quantifier les erreurs antérieures. [R10, §1.1–1.2]',
54: 'L’analyse fonctionnelle fait apparaître trois difficultés liées : retrouver un dossier complet, connaître son état réel et déterminer l’action à effectuer. Un dossier centralisé répond à la première ; les rapports, pièces et historiques répondent à la deuxième ; les alertes et la priorisation répondent à la troisième. La solution doit donc préserver les relations entre les informations et les responsabilités de traitement, au-delà de leur simple enregistrement.',
55: 'Le périmètre prévu comprend les référentiels, les comptes et autorisations, les stagiaires et profils académiques, les structures et encadreurs, les stages et participations, les rapports, les documents, les alertes, l’évaluation, le bilan et les soutenances lorsqu’elles existent. L’historisation, la recherche et les exports traversent ces modules. La comparaison avec le code identifie les écarts, notamment le rôle de responsable départemental, certaines consultations d’historiques et les contrôles de clôture. [R10, §3 ; R12]',
57: 'Le cahier des charges distingue quatre acteurs : l’administrateur, qui administre l’application ; le responsable des stages, qui gère les dossiers des départements auxquels il est affecté ; l’encadreur professionnel, qui suit les participants et examine leurs rapports ; le stagiaire, qui consulte son dossier et renseigne ses activités. Un utilisateur peut cumuler plusieurs rôles. La version logicielle observée expose cependant trois rôles actifs : ADMIN, ENCADREUR et STAGIAIRE. Le tableau 1 et la figure 1 décrivent cette réalisation, sans faire disparaître l’exigence d’un responsable à périmètre départemental. [R10, §4 ; P05, P07]',
64: 'Les exigences non fonctionnelles portent sur la sécurité, la confidentialité, la fiabilité, les performances, l’ergonomie, la compatibilité, la maintenabilité, la disponibilité et la traçabilité. Le cahier des charges demande notamment la pagination, un affichage adapté aux ordinateurs et tablettes, la prise en charge des navigateurs récents Chrome, Firefox et Edge, des sauvegardes régulières et une restauration testée. Il impose des contrôles serveur et la protection des fichiers. Aucun objectif chiffré de disponibilité ou de temps de réponse n’y est fixé ; ces seuils restent à convenir pour la recette d’exploitation. [R10, §9.1–11]',
66: 'Le planning prévisionnel est défini dans le cahier des charges et détaillé dans le classeur Estimation_taches_ETU003156.xlsm. Il répartit 65 jours estimés entre quatre phases : préparation, 11 jours ; réalisation, 42 jours ; tests, 8 jours ; livraison, 4 jours. Les sommes des tâches du classeur concordent avec ces quatre sous-totaux et avec le total du cahier des charges. Le détail figure en annexe E. [R10, §14 ; R11, feuille Estimation, D7, D41, D47, D52 et D53]',
67: 'La préparation couvre la compréhension du contexte, l’analyse des besoins et des règles, la conception des données et des workflows, puis les interfaces. La réalisation assemble ensuite le socle technique, la sécurité, les dossiers, les affectations, le suivi hebdomadaire, les documents, les alertes, l’évaluation et le pilotage. La phase de tests prévoit les contrôles unitaires, l’intégration PostgreSQL, la sécurité, les scénarios frontend/backend et la recette. La livraison comprend le déploiement, la sauvegarde et la restauration, la documentation et la formation. Cette décomposition relie chaque activité à un résultat attendu, sans présumer de son achèvement.',
68: 'Les 65 jours constituent une estimation de travail et non un relevé du temps réellement consommé. Le classeur ne fournit ni dates de début et de fin par tâche ni avancement constaté. La durée de stage de trois mois indiquée dans l’avant-propos ne suffit donc pas à établir un calendrier journalier ou un écart de réalisation. La succession préparation, réalisation, tests et livraison fournit un cadre prévisionnel ; elle ne permet pas d’affirmer l’application d’une méthode Scrum ou l’existence de jalons validés qui ne sont pas documentés.',
106: 'Les rôles actifs supportés sont ADMIN, ENCADREUR et STAGIAIRE. Les anciennes attributions RESPONSABLE_STAGE sont interprétées comme ADMIN ; le jeu d’initialisation courant ne crée plus ce quatrième rôle. Cette compatibilité ne réalise pas la restriction départementale demandée par RG21. Pour les rapports, seul le stagiaire associé rédige et soumet, et seul l’encadreur affecté peut examiner, à condition de ne pas être lui-même propriétaire du dossier. Le contrôle d’accès par dossier est donc présent, tandis que le périmètre d’un responsable départemental reste un écart fonctionnel. [R10, §4 et RG21 ; P05, P13 ; R12]',
110: 'L’association d’un compte au dossier est explicite. Le portail utilise cette liaison, et non une ressemblance de nom ou de courriel. La révision d8819ad ajoute des listes de comptes éligibles et renforce la vérification du rôle, du statut actif et de l’unicité de l’association. Un verrou sur le compte contribue à empêcher son affectation concurrente à plusieurs dossiers. L’association du compte encadreur dispose également d’un parcours administratif dédié. La séparation entre compte et personne permet de constituer un dossier avant l’ouverture de l’accès utilisateur. [P08 ; R12]',
123: 'Les tests unitaires couvrent notamment le refus d’une transition, la clôture déjà effectuée, le mode ordinaire, la dérogation documentaire et le mode forcé. Ils vérifient également que les dérogations ne suppriment pas les gardes d’accès. Les 13 scénarios de ClotureParticipationTests ont réussi dans la campagne du 10 novembre. Cette observation porte sur les services testés avec leurs doubles. Les résultats PostgreSQL rapportés le 11 novembre sont présentés séparément en section 2.12 ; la recette métier doit encore qualifier les conditions de décision administrative. [R9, R12]',
160: 'Hibernate utilise le mode validate, avec open-in-view désactivé : le schéma doit exister avant le démarrage. Le script Conception2.sql est un script d’initialisation comportant une suppression et une création de base ; il ne doit pas être exécuté sur des données à conserver comme s’il s’agissait d’une migration. Depuis la première analyse, le dépôt contient V002__association_eligibility.sql, V003__participation_report_consistency.sql et checks/preflight.sql. Ces scripts ajoutent des protections de cohérence sans être installés automatiquement par Spring Boot. Leur présence et leur procédure sont prises en compte dans cette version du mémoire. [P25 ; R12]',
162: 'La validation distingue les résultats exécutés le 10 novembre, les résultats rapportés par la documentation du 11 novembre et les essais encore attendus. Le tableau 6 conserve la première campagne pour préserver son périmètre et sa date. Les tests frontend de cette campagne associent fonctions, contrats HTTP et rendu de composants ; ils ne correspondent pas à une recette humaine dans le navigateur. La mise à jour du mémoire n’a pas relancé les suites logicielles. [R9, R12]',
166: 'Le fichier backend/migrations/README.md rapporte, pour le 11 novembre 2026, 66 tests réussis dont 13 tests PostgreSQL, sans échec ni test ignoré, ainsi qu’un précontrôle SQL réussi. AUDIT_COHERENCE_METIER.md décrit la réussite après relance autorisée, tout en conservant des paragraphes relatifs à l’échec initial. Le résultat ultérieur est donc cité comme résultat documentaire daté, et non comme une nouvelle exécution liée à cette rédaction. Les migrations concernent des bases isolées de test ; leur application à la base métier n’est pas affirmée. Les totaux de campagnes différentes ne sont pas cumulés. [R12]',
167: 'La recette à compléter doit associer chaque exigence à un scénario, un jeu de données, un résultat attendu et une observation signée. L’annexe F reprend les vingt critères CR01 à CR20. Les parcours navigateur, les refus entre départements, les modalités de clôture, la restauration et les mesures de charge demandent une validation spécifique. Une réussite de tests automatisés ne vaut ni validation de toutes les règles métier ni procès-verbal de livraison. [R10, §12]',
169: 'La construction frontend a réussi lors de la campagne du 10 novembre. Le dépôt courant comprend des Dockerfile pour le backend et le frontend, un fichier docker-compose.yml et une configuration Nginx. Ces éléments préparent une installation reproductible, mais leur présence ne démontre pas un service déployé et accepté par la structure d’accueil. Les paramètres de connexion PostgreSQL et le répertoire documentaire sont externalisés ; leur configuration sur la cible doit être validée. [P25 ; R12]',
170: 'La procédure d’intégration décrit désormais la création d’une base locale isolée, l’installation du schéma, l’application de V002 puis V003, les tests et le précontrôle. Les migrations ne modifient pas automatiquement les données métier existantes. Leur utilisation sur une cible d’exploitation suppose l’examen préalable des anomalies, la conservation des données et une fenêtre adaptée aux verrous SQL. Le déploiement doit distinguer création d’un environnement neuf et évolution d’une installation contenant déjà des dossiers. [R12]',
174: 'Une difficulté concerne la correspondance entre règles métier et réalisation. Le cahier des charges prévoit un blocage ou une confirmation motivée en cas de dossier incomplet. Le code propose trois modalités de clôture, et l’interface utilise la décision forcée. L’analyse consiste à préciser les contrôles réellement maintenus, la trace des éléments manquants et les conditions de décision qui restent à approuver, notamment le caractère obligatoire du motif et de l’évaluation finale. Cette confrontation permet d’expliquer un écart sans confondre exigence et comportement actuel. [R10, §5.19 et RG30 ; P15, P16]',
175: 'Les essais du 10 novembre ont également révélé une sensibilité des tests de rendu à la casse des chemins de résolution des modules. Une relance depuis le chemin canonique a permis aux 126 tests frontend de réussir sans modification du code. Les évolutions du 11 novembre apportent, quant à elles, des contrôles de références actives, d’association des comptes et de cohérence des rapports. Ces deux exemples montrent l’importance de conserver des résultats datés et de vérifier à la fois l’environnement technique et les règles de gestion. [R9, R12]',
179: 'Le tableau 7 présente les réalisations et leur niveau de validation. Les résultats testés doivent être rattachés à la campagne citée : le 10 novembre pour les vérifications du mémoire initial, et le 11 novembre pour les résultats rapportés par les documents de cohérence. Le bilan de conformité complète cette lecture : un module présent et testé peut encore différer d’une responsabilité ou d’un parcours demandé dans le cahier des charges.',
185: 'Les résultats ne doivent pas être convertis en pourcentage global de fiabilité. Aucun taux de couverture global n’est fourni. Les 36 tests unitaires backend et 126 tests frontend de la campagne du 10 novembre, puis les 66 tests backend dont 13 PostgreSQL rapportés le 11 novembre, correspondent à des sélections et à des environnements identifiés. Ils ne prouvent pas toutes les combinaisons de statuts, les pannes de stockage, les performances à volume réel ou la qualification de l’environnement de production. [R9, R12]',
187: 'La reproductibilité a progressé avec l’ajout des migrations V002 et V003 et du précontrôle SQL. La limite initiale liée à leur absence est donc levée dans le dépôt courant. Il reste à conserver un jeu complet de sources, de commandes, de versions et de résultats associé à chaque livraison, puis à vérifier la procédure dans l’environnement retenu. Une base isolée de test et une base d’exploitation n’ont ni le même historique ni les mêmes contraintes opérationnelles. [R12]',
192: 'La clôture avec dérogation répond au principe de blocage ou de confirmation motivée prévu dans le cahier des charges. Son usage exige toutefois une décision explicite : la trace automatique des pièces et rapports manquants ne remplace pas nécessairement le motif métier. La version observée accepte un motif vide et complète alors la trace avec une mention générique de décision administrative. De plus, elle ne contrôle pas une évaluation finale rendue obligatoire par une règle configurable. Ces points doivent être arbitrés pour qualifier CR16 et RG30. [R10, §5.19 ; P15, P16]',
193: '3.4 Compétences mobilisées',
197: 'La validation met en relation plusieurs niveaux de preuve : un test unitaire vérifie une règle isolée, un test HTTP avec PostgreSQL vérifie une chaîne de traitement, et une recette dans le navigateur apprécie un parcours utilisateur. La tenue de documents de conception, de journaux de test et d’une matrice de conformité contribue à expliquer les choix techniques et leurs limites. Ces compétences forment le socle de l’analyse présentée dans ce mémoire ; l’attribution précise des interventions individuelles pourra être détaillée lors de la soutenance.',
199: 'La priorité est de rapprocher la réalisation du cahier des charges : définir le rôle de responsable départemental et ses autorisations, encadrer la clôture motivée, préciser les conditions d’exigibilité de l’évaluation finale et compléter les parcours attendus du stagiaire. La recette doit tester des dossiers de départements différents et les combinaisons de rôles, puis consigner les écarts acceptés ou les corrections nécessaires. Les migrations désormais présentes doivent être intégrées à une procédure de livraison identifiée.',
202: 'L’administration des seuils et des paramètres de suivi constitue également une exigence à préciser dans les parcours de gestion. Une interface dédiée devrait réserver les modifications aux personnes habilitées, valider les valeurs et conserver leur historique. Ces compléments, ainsi que les exports demandés et la consultation des historiques de connexion, doivent être priorisés avec les responsables de la recette au regard du cahier des charges. [R10, §5 et §12]',
204: 'Ce projet répond au besoin de centraliser et de sécuriser le suivi des stagiaires de la Cellule d’Exploitation SIIGFP de la Direction du Budget. Le cahier des charges relie ce besoin à la dispersion des informations, à la visibilité des échéances et à la traçabilité des décisions. La solution organise un dossier relié à des participations, des rapports, des pièces et des événements. La séparation entre identité, parcours académique et participation permet de conserver le contexte propre à chaque période de stage.',
205: 'L’architecture React, Spring Boot et PostgreSQL répartit l’interface, les règles métier et la persistance. La réalisation comporte l’authentification par sessions opaques, les contrôles d’accès par dossier, le workflow des rapports, la gestion documentaire, les évaluations, les soutenances facultatives et les alertes priorisées. La planification de référence estime l’effort à 65 jours, répartis entre préparation, réalisation, tests et livraison. Cette estimation fournit un cadre de travail ; elle ne constitue pas une mesure de l’effort réel consommé. [R10–R11]',
206: 'Les résultats automatisés disponibles apportent des preuves ciblées de fonctionnement. La campagne du 10 novembre a réussi pour les 36 tests unitaires backend et les 126 tests frontend retenus ; le compte rendu du 11 novembre rapporte 66 tests backend réussis, dont 13 tests PostgreSQL. Les migrations nécessaires aux contrôles de cohérence sont désormais présentes. Les limites portent encore sur la conformité de certains parcours, la recette utilisateur, l’exploitation et les résultats d’usage. Aucun gain chiffré ni déploiement accepté n’est déduit de ces seuls tests. [R9, R12]',
207: 'La poursuite du projet consiste à résoudre ou faire accepter les écarts identifiés, notamment le périmètre départemental, la clôture motivée, l’évaluation obligatoire lorsqu’elle est requise et certains accès du stagiaire. Elle doit aussi qualifier les exports, la restauration et l’environnement cible, puis formaliser la recette et la livraison. Le mémoire met ainsi en évidence une réalisation structurée et les conditions nécessaires pour répondre complètement aux exigences de l’organisme d’accueil.',
209: 'Les références R1 à R9 proviennent de la préparation du 10 novembre 2026. Les références R10 à R12 ont été exploitées pour la mise à jour du 11 novembre. Les mémoires de référence servent de repères académiques ; les exigences du projet proviennent du cahier des charges et du classeur d’estimation. Les repères P01 à P36 renvoient aux preuves de l’analyse initiale ; les évolutions postérieures sont identifiées par R12.',
216: '[R7] Projet Stagemanagement. Code source backend, frontend et conception/Conception2.sql, révision 9e1a7d8 examinée le 10 novembre 2026. Évolutions documentées à la révision d8819ad : voir R12.',
229: 'Le tableau 10 propose des essais complémentaires à réaliser sur un environnement isolé et des données de recette. Les résultats automatisés déjà cités ne dispensent pas de tester les parcours dans le navigateur et l’environnement cible. L’annexe F relie la recette aux critères du cahier des charges ; les observations et l’acceptation finale restent à consigner.',
237: 'Les captures destinées à la soutenance devront montrer la connexion, un dossier, une participation, le workflow des rapports et le tableau de bord avec des données de recette. Elles devront être légendées et ne pas exposer de mot de passe, de jeton ou de données personnelles réelles. Les six figures du présent mémoire sont des schémas explicatifs ; elles ne sont pas présentées comme des captures de l’application.',
239: 'L’identité RAKOTONIAINA Andomalala Erica, la structure d’accueil et l’encadreur professionnel RAJOELISOLO Mitanjo sont établis par le cahier des charges. Le numéro ETU003156 apparaît dans le nom du classeur ; sa concordance avec le dossier universitaire reste à vérifier avant de l’ajouter à la couverture. L’encadreur pédagogique, le jury, l’année universitaire, les dates exactes du stage et de la soutenance restent à compléter à partir des documents administratifs.',
240: 'La version concept1 mentionne RAKOTOARIMALALA Tsinjo Tony dans les remerciements, avec une attribution d’encadrement contradictoire avec celle du cahier des charges. La fonction de cette personne doit être confirmée avant l’attribution définitive des remerciements. Le contexte et le planning sont désormais documentés par R10 et R11. Restent à valider les arbitrages fonctionnels, les résultats de recette, la livraison effective et les observations d’usage.',
241: 'Avant le dépôt, actualiser la table des matières et les listes de tableaux et de figures dans Word, puis vérifier la pagination, les sauts de page, la lisibilité des tableaux et la couverture. La vérification finale de la présentation et des informations personnelles est laissée à l’auteur.',
}
for i, text in updates.items():
    replace(i, text)
for i in [37, 194, 232, 233, 234, 235, 236]:
    remove(i)

before(41, [
('Heading 1', 'Résumé'),
('Normal', 'Ce mémoire présente la conception et la réalisation d’une application web de suivi des stagiaires au sein de la Cellule d’Exploitation SIIGFP de la Direction du Budget du Ministère de l’Économie et des Finances. Le besoin consiste à centraliser les dossiers, organiser les affectations, suivre les rapports hebdomadaires et anticiper les échéances. La solution repose sur React, Spring Boot et PostgreSQL. Son modèle distingue le stagiaire, son profil académique et chaque participation à un stage. Les services gèrent les autorisations, les documents, les rapports, les évaluations, les soutenances facultatives et les alertes priorisées. L’analyse confronte les réalisations au cahier des charges et au planning prévisionnel de 65 jours. Les tests disponibles étayent plusieurs règles techniques et métier, tandis que des écarts subsistent sur le périmètre départemental, certaines autorisations et les conditions de clôture. La recette utilisateur et la qualification de l’exploitation restent nécessaires pour achever la validation du produit.'),
('Normal', 'Mots-clés : suivi des stagiaires, participation, workflow, SIIGFP, React, Spring Boot, PostgreSQL.'),
('Heading 1', 'Abstract'),
('Normal', 'This dissertation presents the design and implementation of a web application for internship monitoring within the SIIGFP Operations Unit of the Budget Directorate at Madagascar’s Ministry of Economy and Finance. The application centralizes trainee records, assignments, weekly reports and deadlines. It uses React, Spring Boot and PostgreSQL, with a data model that separates trainees, academic profiles and individual internship participations. Application services manage access rights, documents, report workflows, evaluations, optional defenses and prioritized alerts. The study compares the implementation with the requirements specification and a 65-day estimated work plan. Available tests support selected technical and business rules. Remaining gaps concern department-based responsibilities, some permissions and closure conditions. User acceptance testing and operational qualification are still required to complete product validation.'),
('Normal', 'Keywords: internship monitoring, participation, workflow, SIIGFP, React, Spring Boot, PostgreSQL.'),
])
# La fin de la section liminaire suit désormais les résumés, avant l’introduction.
P[41]._p.addprevious(P[40]._p)

before(82, [
('Heading 2', '1.7 Analyse fonctionnelle et règles de gestion'),
('Normal', 'Le processus cible comporte douze étapes, depuis la préparation des référentiels jusqu’à l’archivage. L’administrateur prépare les valeurs communes et les habilitations ; le responsable enregistre le stagiaire, crée le stage et affecte la participation. Pendant le stage, le stagiaire produit ses rapports et l’encadreur les examine. Les contrôles planifiés rendent visibles les retards et les échéances. À la fin, l’évaluation, le bilan et la soutenance éventuelle précèdent le contrôle de clôture. Le dossier reste consultable après cette décision. Ce processus exprime le fonctionnement attendu ; sa traduction dans les trois rôles actuels est évaluée au chapitre 3. [R10, §6]'),
('Normal', 'Les règles RG01 à RG20 structurent le socle de gestion : rôles multiples, types de statut cohérents, relations entre les entités, périodes, unicité des identifiants et historisation. Les règles RG21 à RG29 ajoutent le périmètre départemental, le contrôle des pièces, la détection des chevauchements, le calendrier hebdomadaire, le verrouillage des rapports validés et les alertes. Les règles RG30 à RG40 couvrent la clôture, les prolongations, les transitions, la charge des encadreurs, la complétude, l’évaluation, les soutenances facultatives et la priorisation. Leur mise en œuvre doit être contrôlée au niveau serveur, même si l’interface filtre déjà les choix proposés. [R10, §7]'),
('Normal', 'Quatre cas d’utilisation organisent les besoins principaux. « Constituer un dossier » rassemble identité, études, pièces et compte d’accès. « Affecter un participant » associe stage, cadre, encadreur et période après contrôle des références et des chevauchements. « Suivre une semaine » associe rédaction, soumission, observation, correction et validation. « Préparer la fin de participation » rassemble les échéances, la complétude, l’évaluation et la décision de clôture. Chaque cas associe une responsabilité humaine, des conditions préalables, un traitement et une trace consultable.'),
('Normal', 'Le workflow attendu des rapports passe de brouillon à soumis, puis à validé ou à corriger ; une correction est suivie d’une nouvelle soumission. Un rapport validé doit être verrouillé. La soutenance est facultative : son absence ne rend pas une participation invalide et ne doit pas déclencher une alerte de soutenance. La clôture exige l’application de contrôles et, selon la politique retenue, un blocage ou une confirmation motivée. Ces règles constituent des scénarios prioritaires de recette. [R10, §5.11, §5.19 et §5.21]'),
('Heading 2', '1.8 Interfaces attendues et livrables'),
('Normal', 'Les dix-huit interfaces du cahier des charges couvrent la connexion et le tableau de bord, les utilisateurs et référentiels, les stagiaires et leur dossier, les cadres et stages, les participations, les encadreurs et structures, les rapports, historiques, exports, documents, alertes, évaluations et soutenances. La navigation doit permettre de passer d’une alerte au dossier concerné et de comprendre immédiatement le statut et l’action attendue. Les confirmations, messages d’erreur et contrôles de formulaire doivent utiliser le vocabulaire du service. [R10, §9]'),
('Normal', 'Les treize livrables attendus vont du cahier des charges au rapport de stage, en passant par les modèles, scripts, sources, guides, résultats de tests et application déployée. Ils ne se réduisent donc pas au code source. L’annexe G précise les éléments disponibles et ceux dont la validation ou la remise reste à constater. En particulier, les schémas explicatifs de ce mémoire ne remplacent pas à eux seuls un jeu de modèles conceptuel et logique validé, ni un procès-verbal de recette. [R10, §13]'),
])

before(124, [('Normal', 'Le rapprochement avec RG30 fait apparaître deux points à préciser. Le cahier des charges demande de contrôler l’évaluation finale lorsqu’elle est requise ; la branche de clôture examinée contrôle les pièces et les rapports, sans condition correspondante sur l’évaluation. De plus, la confirmation motivée prévue au §5.19 n’est pas entièrement traduite par une obligation de saisir un motif : la mention automatique de décision administrative peut être utilisée lorsque ce champ est vide. L’existence de dérogations n’est donc pas, à elle seule, contraire au cahier des charges ; c’est leur politique d’usage et leur contrôle qui doivent être complétés ou acceptés. [R10 ; R12]')])
before(138, [('Normal', 'Le rôle stagiaire est autorisé à consulter les documents accessibles à son dossier, mais le téléversement de la version étudiée reste réservé à ADMIN. Le cahier des charges prévoit que le stagiaire puisse déposer les pièces autorisées. Cette exigence nécessite soit un parcours de dépôt limité au propriétaire et aux types admis, soit une modification de périmètre explicitement validée. La présence de contrôles MIME et de taille ne suffit pas à couvrir cette différence d’autorisation. [R10, §4 et §5.17 ; P06, P18]')])
before(142, [('Normal', 'La consultation des informations de soutenance par le stagiaire fait partie des attentes du cahier des charges lorsqu’une soutenance est planifiée. Le portail actuellement décrit ne lui ouvre pas l’ensemble des parcours administratifs de soutenance et d’évaluation. La vérification doit donc porter sur les informations réellement visibles dans son dossier, et non sur la seule existence d’une page accessible à l’administrateur ou à l’encadreur. [R10, §4 et §5.21 ; P08, P35]')])
before(156, [('Normal', 'Les exports existants comprennent des listes CSV et un bilan PDF. Un CSV peut être ouvert par un tableur, mais il ne constitue pas un classeur Excel natif. Le cahier des charges demande les fiches, rapports et documents prioritaires, ainsi que les listes et statistiques en PDF ou Excel. La conformité doit donc être vérifiée document par document : contenu, droits, format, impression et caractères. La génération d’un seul bilan PDF ne démontre pas la livraison de tous les exports prévus. [R10, §5.16 ; P23]')])
before(176, [
('Heading 2', '2.15 Cohérence des associations et maintien de l’historique'),
('Normal', 'Les évolutions de la révision d8819ad renforcent la distinction entre une référence admissible pour une nouvelle affectation et une référence déjà présente dans un dossier historique. AssociationRules contrôle notamment l’activité d’une structure, d’un cadre de stage ou du statut d’un stagiaire et d’un encadreur. Une valeur devenue inactive peut rester lisible dans l’historique sans être proposée pour créer un nouveau lien. Ce principe répond à la conservation des références utilisées demandée par le cahier des charges. [R10, §5.3 ; R12]'),
('Normal', 'Le contrôle de réaffectation prend également en compte l’évaluation existante : un changement d’encadreur est refusé lorsqu’il rendrait incohérente une évaluation déjà rattachée à l’encadreur actuel. Les traitements des rapports relisent leur état dans le contexte du verrouillage. Les migrations V002 et V003 apportent des protections complémentaires sur les associations et la cohérence des périodes. Ces mécanismes réduisent les incohérences ciblées ; ils ne remplacent ni les autorisations applicatives ni la recette des cas de concurrence non couverts. [R12]'),
])
before(203, [
('Heading 2', '3.6 Conformité au cahier des charges'),
('Normal', 'La conformité est évaluée par comparaison entre le besoin formulé et le parcours effectivement accessible. Les fonctions les mieux étayées sont la constitution des dossiers, l’affectation à une participation, le workflow des rapports, les contrôles documentaires, les alertes et la génération du bilan. Les tests cités apportent une assurance ciblée sur certaines règles. L’annexe F conserve toutefois les vingt critères de recette comme critères à observer et à accepter, sans leur attribuer automatiquement un résultat réussi.'),
('Normal', 'Le principal écart d’habilitation concerne le responsable des stages. Le besoin prévoit un rattachement à un ou plusieurs départements et la limitation de son accès à ce périmètre. La compatibilité du rôle ancien avec ADMIN lui donne au contraire une portée globale. La table responsable_departements ne prouve pas que le filtrage est appliqué. L’acceptation de RG21 nécessite donc une réalisation spécifique ou une révision formelle du besoin, avec des essais croisés entre départements. [R10, RG21 ; P05, P07 ; R12]'),
('Normal', 'La gestion du cycle de vie appelle deux distinctions supplémentaires. La clôture d’une participation ne constitue pas l’archivage administratif de la personne stagiaire ; le workflow de changement du statut de cette personne n’est pas exposé dans la gestion métier relevée par l’audit. Par ailleurs, la clôture avec dérogation doit intégrer une politique de motivation et le contrôle de l’évaluation lorsqu’elle est exigée. Ces écarts concernent RG30, CR03, CR07 et CR16 et ne doivent pas être masqués par la présence de statuts en base. [R10 ; R12]'),
('Normal', 'Les autres points de conformité portent sur le dépôt autorisé de pièces par le stagiaire, la visibilité de sa soutenance, la consultation des historiques de connexion, le contenu des exports et les indicateurs demandés. Pour les performances, la pagination locale de listes déjà chargées ne répond pas entièrement à l’objectif de chargement ciblé et de requêtes optimisées. Les essais doivent donc examiner à la fois le résultat affiché, les droits et le volume de données traité. [R10, §5.12–5.21 et §10]'),
('Normal', 'La décision de recette doit distinguer une correction requise, un complément de vérification et un écart accepté. Pour chaque critère, le procès-verbal devra mentionner la version testée, les comptes utilisés, les données de recette, les résultats obtenus et la décision des personnes habilitées. Le cahier des charges fixe ce qui doit être validé ; le classeur estime le travail nécessaire ; ni l’un ni l’autre ne constitue une preuve de réalisation ou d’acceptation. [R10, §12–14 ; R11]'),
])

before(219, [
('Normal', '[R10] Cahier des charges. Application web de suivi des stagiaires. Document fourni sous le nom CDC_suivi_stagiaires.pdf, 21 pages. Structure d’accueil : Cellule d’Exploitation SIIGFP de la Direction du Budget. Exploité le 11 novembre 2026 ; date d’approbation non indiquée.'),
('Normal', '[R11] Estimation_taches_ETU003156.xlsm. Feuille Estimation, plage A1:D53. Liste des tâches, descriptions et estimations en jours ; total de 65 jours. Classeur fourni, exploité le 11 novembre 2026 sans exécution des macros. Le cahier des charges le désigne sous le nom Estimation_des_taches_ETU003156.xlsm ; les phases et sous-totaux concordent.'),
('Normal', '[R12] Projet Stagemanagement, révision d8819ad. Sources courantes, backend/migrations/README.md, migrations V002 et V003, checks/preflight.sql et AUDIT_COHERENCE_METIER.md. Documents locaux examinés le 11 novembre 2026. Résultat de 66 tests, dont 13 PostgreSQL, cité d’après le compte rendu de relance ; aucun test relancé pour cette mise à jour documentaire.'),
])

# Rectifier les réserves devenues obsolètes dans les tableaux conservés.
def cell_text(table, row, col, text):
    c = D.tables[table].cell(row, col)
    c.text = text
    for p in c.paragraphs:
        p.paragraph_format.line_spacing = 1.05
        for r in p.runs:
            r.font.name = 'Times New Roman'
            r.font.size = Pt(10.5)
cell_text(5, 6, 0, 'Intégration au 10 novembre')
cell_text(5, 6, 1, 'Non exécutée dans cette campagne')
cell_text(5, 6, 2, 'Migrations absentes à cette date ; ajoutées depuis. Le résultat du 11 novembre est présenté séparément dans le texte.')
cell_text(6, 1, 2, 'Tests unitaires du 10 novembre ; essais HTTP/SQL ciblés rapportés le 11 novembre. Périmètre départemental non réalisé.')
cell_text(6, 4, 2, 'Sources et parcours présents ; contrôles HTTP/SQL ciblés rapportés le 11 novembre. Recette utilisateur à compléter.')
cell_text(6, 5, 2, '126 tests réussis le 10 novembre sur la version correspondante ; recette humaine et changements ultérieurs à qualifier.')
cell_text(7, 7, 2, 'AppRoutes.jsx ; routeConfig.js ; application.properties ; test-integration.ps1 ; migrations V002, V003 et checks/preflight.sql (mise à jour R12).')
cell_text(9, 1, 2, 'Compléter la recette navigateur et le scénario départemental ; résultats HTTP/SQL ciblés rapportés par R12.')
cell_text(9, 3, 2, 'Compléter la recette selon la politique de clôture validée ; distinguer tests techniques et acceptation métier.')

bid = max([int(x.get(qn('w:id'))) for x in D.element.iter(qn('w:bookmarkStart'))] + [0])

def field(p, code, cache):
    for kind in ['begin', 'instruction', 'separate', 'value', 'end']:
        r = p.add_run()
        if kind in ('begin', 'separate', 'end'):
            e = OxmlElement('w:fldChar'); e.set(qn('w:fldCharType'), kind)
            if kind == 'begin': e.set(qn('w:dirty'), 'true')
            r._r.append(e)
        elif kind == 'instruction':
            e = OxmlElement('w:instrText'); e.set(qn('xml:space'), 'preserve'); e.text = ' '+code+' '; r._r.append(e)
        else: r.text = cache

new_caps = []
def table(number, title, rows, widths):
    global bid
    cap = D.add_paragraph(style='Caption')
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    cap.paragraph_format.keep_with_next = True
    cap.add_run('Tableau ')
    bid += 1
    start=OxmlElement('w:bookmarkStart');start.set(qn('w:id'),str(bid));start.set(qn('w:name'),f'tableau_{number}')
    cap._p.append(start)
    field(cap,'SEQ tableau \\* ARABIC',str(number))
    end=OxmlElement('w:bookmarkEnd');end.set(qn('w:id'),str(bid));cap._p.append(end)
    cap.add_run(' — '+title)
    new_caps.append(cap.text)
    t = D.add_table(rows=0, cols=len(widths))
    t.autofit = False; t.alignment=WD_TABLE_ALIGNMENT.CENTER
    for c,w in zip(t.columns,widths): c.width=Cm(w)
    for i,values in enumerate(rows):
        row=t.add_row()
        pr=row._tr.get_or_add_trPr();pr.append(OxmlElement('w:cantSplit'))
        if i==0:pr.append(OxmlElement('w:tblHeader'))
        for c, text, w in zip(row.cells,values,widths):
            c.width=Cm(w);c.vertical_alignment=WD_CELL_VERTICAL_ALIGNMENT.CENTER
            p=c.paragraphs[0];p.paragraph_format.line_spacing=1.05
            p.paragraph_format.space_before=Pt(4);p.paragraph_format.space_after=Pt(4)
            r=p.add_run(str(text));r.font.name='Times New Roman';r.font.size=Pt(10)
            r.bold=i==0;r.font.color.rgb=RGBColor.from_string('FFFFFF' if i==0 else '000000')
            cp=c._tc.get_or_add_tcPr()
            sh=OxmlElement('w:shd');sh.set(qn('w:fill'),'354F60' if i==0 else ('F3F5F7' if i%2==0 else 'FFFFFF'));cp.append(sh)
            borders=OxmlElement('w:tcBorders')
            for side in ['top','left','bottom','right']:
                e=OxmlElement('w:'+side);e.set(qn('w:val'),'single');e.set(qn('w:sz'),'4');e.set(qn('w:color'),'D9D9D9');borders.append(e)
            cp.append(borders)
            mar=OxmlElement('w:tcMar')
            for side in ['top','left','bottom','right']:
                e=OxmlElement('w:'+side);e.set(qn('w:w'),'80');e.set(qn('w:type'),'dxa');mar.append(e)
            cp.append(mar)
    D.add_paragraph()
    return t

def add(text, style='Normal'):
    p=D.add_paragraph(text,style)
    if style=='Normal':p.alignment=WD_ALIGN_PARAGRAPH.JUSTIFY
    return p

add('Annexe E Planning prévisionnel et détail des tâches','Heading 2')
add('Les tableaux 11 à 15 reprennent les quatre phases et les 36 tâches chiffrées du classeur fourni. Les lignes de regroupement sans estimation ne sont pas comptées comme des tâches supplémentaires. La colonne « Ligne » permet de retrouver la tâche dans la feuille Estimation ; les descriptions sont conservées pour préciser le périmètre prévu. Les durées sont des estimations, sans indication de dates réelles ni de pourcentage d’avancement. [R11]')
table(11,'Répartition de l’effort prévisionnel',[
['Phase','Jours estimés','Part du total','Source'],
['Préparation','11','16,9 %','Estimation!D7'],
['Réalisation','42','64,6 %','Estimation!D41'],
['Tests','8','12,3 %','Estimation!D47'],
['Livraison','4','6,2 %','Estimation!D52'],
['Total','65','100,0 %','Estimation!D53']], [4.5,3,3,5.3])
data=json.loads((ROOT/'tmp/complements/taches.json').read_text(encoding='utf-8'))['Estimation']['rows']
tasks=[]
for row in data:
    vals={c['cell'][0]:c['value'] for c in row}
    if vals.get('D') and not str(vals['D']).startswith('='):
        try: days=float(vals['D'])
        except ValueError:continue
        line=int(re.search(r'\d+',row[0]['cell']).group())
        tasks.append((line, vals['B'],vals.get('C',''),int(days)))
assert len(tasks)==36 and sum(t[3] for t in tasks)==65
for n,phase,lo,hi,total in [(12,'Préparation',2,6,11),(13,'Réalisation',8,40,42),(14,'Tests',42,46,8),(15,'Livraison',48,51,4)]:
    selected=[t for t in tasks if lo<=t[0]<=hi]
    assert sum(t[3] for t in selected)==total
    add(phase,'Heading 3')
    rows=[['Ligne','Tâche prévue','Description','Jours']]+[[str(t[0]),t[1],t[2],str(t[3])] for t in selected]
    rows.append(['','Sous-total',phase,str(total)])
    table(n,'Tâches de la phase '+phase.lower(),rows,[1.2,4.2,9.1,1.3])
add('La réalisation représente 42 / 65, soit environ 64,6 % de l’estimation. Les tests et la livraison totalisent 12 jours, soit environ 18,5 %. Ces ratios décrivent la répartition prévue et ne mesurent ni la qualité du produit ni l’avancement réel. Un suivi effectif devrait ajouter les dates et durées constatées, les dépendances, les anomalies et la validation des livrables.')

add('Annexe F Matrice des critères de recette','Heading 2')
add('Le tableau 16 reprend les codes CR01 à CR20 du cahier des charges. Les critères sont reformulés de façon concise ; la dernière colonne indique la vérification ou l’écart à traiter. Il ne s’agit pas d’un procès-verbal signé. Les références techniques et résultats datés des chapitres 2 et 3 restent nécessaires pour apprécier chaque ligne. [R10, §12.3]')
criteria=[
('CR01','Connexion et accès autorisés','Sessions et contrôles de ressource présents. Vérifier les refus croisés et le responsable départemental.'),
('CR02','Administration des référentiels','Contrôler ajout, modification, désactivation et conservation des valeurs déjà utilisées.'),
('CR03','Enregistrement et recherche des dossiers','Parcours présents ; compléter le workflow d’archivage de la personne stagiaire.'),
('CR04','Création, suivi et clôture des stages','Vérifier les participations indépendantes et la décision de clôture conforme à la politique retenue.'),
('CR05','Affectation à un stage, une période et un encadreur','Contrôles de références et de chevauchement présents ; valider les cas historiques et concurrents ciblés.'),
('CR06','Rapports et observations des encadreurs','Vérifier les comptes auteur et encadreur affecté, les observations et les transitions.'),
('CR07','Consultation de l’historique du stagiaire','Contrôler le parcours complet et distinguer historique des participations et archivage de la personne.'),
('CR08','Cohérence des indicateurs du tableau de bord','Comparer les valeurs affichées aux dossiers d’un jeu de recette connu.'),
('CR09','Recherche, tri, filtres et pagination','Listes disponibles ; distinguer pagination locale et pagination SQL sous volume représentatif.'),
('CR10','Consultation autorisée des historiques','Historisation présente ; consultation des connexions à compléter ou à démontrer dans un parcours autorisé.'),
('CR11','Export et impression des documents prioritaires','CSV et bilan PDF présents ; contrôler chaque fiche, rapport, statistique, format et mise en page demandé.'),
('CR12','Absence d’erreur bloquante à la livraison','Décision à prendre sur la version livrée après recette ; aucun succès global déduit des seuls tests.'),
('CR13','Dépôt, classement et consultation des pièces','Contrôles de fichiers présents ; dépôt par le stagiaire prévu mais réservé à ADMIN dans la version étudiée.'),
('CR14','Workflow et verrouillage du rapport validé','Tester brouillon, soumis, à corriger, resoumis, validé et refus de modification après validation.'),
('CR15','Détection automatique des retards et échéances','Contrôles planifiés présents ; observer les dates limites, le traitement quotidien et les alertes attendues.'),
('CR16','Application des contrôles avant clôture','Faire valider motif obligatoire, dérogations, rapports, pièces et évaluation lorsque celle-ci est requise.'),
('CR17','Évaluation et bilan consultables et exportables','Contrôler critères, notes, finalisation, droits et contenu du bilan sur le même dossier.'),
('CR18','Pilotage des rapports, dossiers et encadreurs','Vérifier dénominateurs des taux, dossiers incomplets, charge, filtres et cohérence des agrégats.'),
('CR19','Soutenance facultative et suivi lorsqu’elle existe','Tester absence de soutenance, unicité, date, lieu, alerte et consultation autorisée par le stagiaire.'),
('CR20','Priorisation des dossiers et action recommandée','Contrôler poids, cumul des alertes actives, ordre des dossiers et action affichée.'),
]
table(16,'Critères de recette et vérifications associées',[['Code','Critère attendu','Vérification ou écart']]+[list(x) for x in criteria],[1.4,5.1,9.3])
add('Pour chaque scénario de recette, relever la date, la version, les données de test, les étapes, le résultat attendu, le résultat obtenu, la preuve et la décision. Un écart doit recevoir un responsable de traitement et, selon la décision métier, une correction ou une acceptation explicite. Les essais d’accès doivent utiliser plusieurs personnes, plusieurs affectations et plusieurs départements afin de ne pas valider une restriction sur un jeu de données où elle ne pourrait jamais être mise en défaut.')

add('Annexe G Livrables attendus et état documentaire','Heading 2')
add('Le tableau 17 rapproche la liste L01 à L13 des éléments disponibles. « Présent » signifie qu’un document ou un code est consultable ; cela ne signifie pas que le livrable a été approuvé ou remis officiellement. L’installation en production, la formation et la recette finale doivent être attestées séparément. [R10, §13 ; R12]')
deliverables=[
('L01','Cahier des charges validé','Document fourni ; approbation formelle et date à confirmer.'),
('L02','Cas d’utilisation et conception','Vues fonctionnelles et schémas présents dans le mémoire ; formalisation et validation des modèles à compléter.'),
('L03','MCD et MLD','Modèle physique SQL disponible ; les vues de clés étrangères ne sont pas présentées comme des MCD/MLD validés.'),
('L04','Script de création et initialisation','Conception2.sql présent ; migrations complémentaires V002 et V003 présentes.'),
('L05','Maquettes des interfaces','Composants React présents ; dossier de maquettes validé non identifié dans les pièces examinées.'),
('L06','Code source frontend','Projet React/Vite présent dans frontend/.'),
('L07','Code source backend','Projet Java/Spring Boot présent dans backend/.'),
('L08','Base et procédure de sauvegarde','Schéma et procédures disponibles ; restauration de l’environnement cible à attester.'),
('L09','Documentation technique et installation','Procédures et configurations présentes ; vérifier leur exécution sur la cible retenue.'),
('L10','Guide utilisateur','La remise d’un guide utilisateur complet et validé reste à attester.'),
('L11','Rapport de tests et procès-verbal de recette','Journaux et comptes rendus disponibles ; procès-verbal signé non fourni.'),
('L12','Application déployée','Fichiers Docker et configuration disponibles ; déploiement accepté non établi.'),
('L13','Rapport de stage','Présent mémoire ajusté au cahier des charges et à l’estimation ; relecture finale par l’auteur.'),
]
table(17,'État des livrables prévus',[['Code','Livrable','État documentaire']]+[list(x) for x in deliverables],[1.4,5.1,9.3])

# Recréer les caches des trois sommaires sans conserver des pages devenues fausses.
# Le champ Word reste actualisable ; seules les entrées textuelles sont préremplies.
headings=[(1 if p.style.name=='Heading 1' else 2,p.text) for p in D.paragraphs
          if p.style.name in ('Heading 1','Heading 2') and p.text not in ('Table des matières','Liste des tableaux','Liste des figures')]
tablecaps=[p.text for p in D.paragraphs if re.match(r'^Tableau \d+ — ',p.text)]
figcaps=[p.text for p in D.paragraphs if re.match(r'^Figure \d+ — ',p.text)]
assert len(tablecaps)==17 and len(figcaps)==6
for sdt in list(D.element.body.findall(qn('w:sdt'))):
    oldcode=''.join(x.text or '' for x in sdt.iter(qn('w:instrText')))
    if not oldcode.strip().startswith('TOC'):continue
    content=sdt.find(qn('w:sdtContent'))
    for e in list(content):content.remove(e)
    def navp(text='',style='Normal'):
        e=OxmlElement('w:p');content.append(e);p=Paragraph(e,D._body);p.style=style
        if text:p.add_run(text)
        return p
    if '\\c "Tableau"' in oldcode:
        code='TOC \\c "Tableau" \\h \\z'; entries=[(1,t) for t in tablecaps]
    elif '\\c "Figure"' in oldcode:
        code='TOC \\c "Figure" \\h \\z'; entries=[(1,t) for t in figcaps]
    else:
        code='TOC \\o "1-2" \\h \\z';entries=headings
    p=navp()
    for kind in ('begin','instruction','separate'):
        r=p.add_run()
        if kind=='instruction':e=OxmlElement('w:instrText');e.text=' '+code+' '
        else:e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),kind)
        if kind=='begin':e.set(qn('w:dirty'),'true')
        r._r.append(e)
    for level,text in entries:
        p=navp(text,'TOC '+str(level));p.paragraph_format.space_after=Pt(4);p.paragraph_format.line_spacing=1.05
    p=navp();e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),'end');p.add_run()._r.append(e)

up=D.settings.element.find(qn('w:updateFields'))
if up is None:up=OxmlElement('w:updateFields');D.settings.element.append(up)
up.set(qn('w:val'),'true')
D.core_properties.author='RAKOTONIAINA Andomalala Erica'
D.core_properties.title='Gestion et suivi des stagiaires au Ministère de l’Économie et des Finances'
D.core_properties.subject='Mémoire de Licence en Informatique, option Web — MEF SIIGFP'
D.core_properties.comments=''
for p in D.paragraphs:
    if p.style.name.startswith('Heading'):
        p.paragraph_format.keep_with_next=True
        for r in p.runs:r.font.color.rgb=RGBColor(0,0,0)
D.save(OUT)
assert hashlib.sha256(SOURCE.read_bytes()).hexdigest()==source_hash
check=Document(OUT)
summary={'output':str(OUT),'bytes':OUT.stat().st_size,'paragraphs':len(check.paragraphs),
         'tables':len(check.tables),'images':len(check.inline_shapes),'sections':len(check.sections),
         'tasks':len(tasks),'estimated_days':sum(t[3] for t in tasks),
         'words':sum(len(p.text.split()) for p in check.paragraphs)+sum(len(c.text.split()) for t in check.tables for r in t.rows for c in r.cells),
         'visual_review':'Non réalisée, conformément à la demande de l’auteur','source_unchanged':True}
(ROOT/'tmp/complements/verification_docx_complet.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(summary,ensure_ascii=False))
