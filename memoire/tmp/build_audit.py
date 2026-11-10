from pathlib import Path
import re,json,collections
R=Path(__file__).resolve().parents[2]; M=R/'memoire'; J='backend/src/main/java/mg/mef/stagemanagement/'
sql=(R/'conception/Conception2.sql').read_text(encoding='utf-8')
blocks={}
for m in re.finditer(r'CREATE TABLE\s+(\w+)\s*\(',sql,re.I):
    pos=m.end(); depth=1; i=pos
    while depth:
        if sql[i]=='(': depth+=1
        elif sql[i]==')': depth-=1
        i+=1
    blocks[m.group(1)]=sql[pos:i-1]
comp=[]
for f in (R/J/'model').glob('*.java'):
    s=f.read_text(encoding='utf-8'); m=re.search(r'@Table\(\s*name\s*=\s*"([^"]+)"',s)
    if not m: continue
    t=m.group(1); cols=re.findall(r'@(?:Column|JoinColumn)\(\s*name\s*=\s*"([^"]+)"',s)
    missing=[c for c in cols if not re.search(r'^\s*'+re.escape(c)+r'\s',blocks.get(t,''),re.M)]
    comp.append((f.stem,t,len(cols),', '.join(missing) or 'Aucune colonne explicite absente'))
def write(name,text): (M/name).write_text(text.strip()+'\n',encoding='utf-8')
rows=[
('P01','Java 17, Spring Boot 4.1.0 et PDFBox 3.0.8 déclarés','backend/pom.xml','properties et dependencies','Valeurs du manifeste','Forte','Vérifié statiquement'),
('P02','React et Vite déclarés et verrouillés','frontend/package.json; frontend/package-lock.json','dependencies','Manifestes et construction Vite réussie','Forte','Construction vérifiée'),
('P03','API JSON avec en-tête Bearer','frontend/src/api/apiClient.js','createApiClient','fetch, FormData, traitement 401/403/409','Forte','Tests frontend réussis'),
('P04','Session opaque de huit heures, empreinte SHA-256, BCrypt',J+'service/AuthService.java','login, genererToken, hasherToken','32 octets SecureRandom; expiration +8h','Forte','Code inspecté; connexion non rejouée'),
('P05','Trois rôles et alias historique ADMIN',J+'security/ApplicationRoles.java','SUPPORTED, effective','RESPONSABLE_STAGE transformé en ADMIN','Forte','Code et tests accès'),
('P06','Refus par défaut des routes non autorisées',J+'config/SecurityConfig.java','securityFilterChain','anyRequest().denyAll()','Forte','Code inspecté'),
('P07','Accès aux ressources par identité et affectation',J+'service/DossierAccessService.java','readParticipation, canAuthorReport, canReviewReport','Contrôles propriétaire/encadreur; auto-validation refusée','Forte','Tests unitaires exécutés'),
('P08','Dossier personnel avec association explicite de compte',J+'service/PortailStagiaireService.java; frontend/src/pages/stagiaires/MonDossierPage.jsx','service et page','Parcours lié au compte','Moyenne','Implémenté; HTTP non rejoué'),
('P09','44 tables, 6 vues et 16 triggers','conception/Conception2.sql','DDL','Inventaire automatique des déclarations','Forte','Vérifié statiquement'),
('P10','Stage distinct de participation individuelle',J+'model/StageParticipant.java; conception/Conception2.sql','stage_participant','FK stage, profil académique, cadre, encadreur et statut','Forte','Modèle inspecté'),
('P11','Verrouillage et contrôle des chevauchements',J+'service/ParticipationRulesService.java','dates, lock','Verrou pessimiste et countOverlapping par personne','Forte','Tests unitaires exécutés; concurrence réelle non rejouée'),
('P12','Semaines tronquées avec échéance lundi',J+'service/CalculPeriodesRapportService.java','calculer','nextOrSame(SUNDAY), dimanche.plusDays(1)','Forte','Code et tests de suivi'),
('P13','Soumission par propriétaire, examen par encadreur affecté',J+'service/DossierAccessService.java; '+J+'service/RapportActiviteGestionService.java','reportTransition, changerStatutRapport','Garde de rôle, propriétaire, état et affectation','Forte','Tests unitaires exécutés'),
('P14','Clôture ordinaire exige pièces et rapports achevés validés',J+'service/StageGestionService.java; '+J+'service/ParticipationRulesService.java','appliquerEvenementAvecStatut, closureReports','Contrôles documents et calendrier','Forte','ClotureParticipationTests exécuté'),
('P15','Clôture forcée contourne complétude et trace les manquants',J+'service/StageGestionService.java; frontend/src/pages/stages/ParticipationActions.jsx','appliquerEvenementAvecStatut; onConfirm','forcerCloture=true; motif événement; accès maintenu','Forte','Tests unitaires et frontend exécutés'),
('P16','Dérogation documentaire seule conserve le contrôle des rapports',J+'dto/stage/EvenementParticipationRequest.java; '+J+'service/StageGestionService.java','ignorerDocumentsManquants','Branche distincte de forcerCloture','Forte','ClotureParticipationTests exécuté'),
('P17','Pièces PDF/JPEG/PNG contrôlées jusqu’à 10 Mio',J+'service/DocumentContentValidator.java','validate, inspectPdf','MIME, extension, analyse PDF et décodage image','Forte','DocumentContentTests exécuté'),
('P18','Stockage disque distinct des métadonnées SQL',J+'service/FichierGestionService.java','preparerTelechargement et stockage','UUID, toRealPath, autorisations et nettoyage rollback','Forte','Code inspecté; panne disque non testée'),
('P19','Notes 0 à 5 et critères obligatoires avant finalisation',J+'service/EvaluationGestionService.java','verifierNote, finaliserEvaluation','Bornes et refus après finalisation','Forte','Code inspecté; HTTP non rejoué'),
('P20','Sept détecteurs planifiés à 06h Madagascar',J+'scheduler/AlerteScheduler.java; '+J+'service/AlerteOrchestrationService.java','executerVerificationQuotidienne','cron et sept appels explicites','Forte','Code inspecté; cron non observé'),
('P21','Alertes liées aux participations clôturées expirées',J+'service/AlerteClotureService.java','expirerPourParticipation, expirerAlertesDejaCloturees','Expiration à la clôture et au démarrage','Forte','AlerteClotureTests exécuté'),
('P22','Priorités calculées après filtrage des accès',J+'service/PilotageService.java','priorites','Somme des poids, seuils 20/50 et niveaux des alertes','Forte','Code inspecté; HTTP non rejoué'),
('P23','Exports CSV et PDF, échappement des cellules',J+'service/ExportService.java','csv, cell, pdf, bilan','UTF-8 BOM; neutralisation formules; PDFBox','Forte','ExportTests exécuté'),
('P24','Routage interne History API','frontend/src/routes/AppRoutes.jsx; frontend/src/routes/routeConfig.js','navigate, matchRoute','pushState, popstate et table des routes','Forte','Tests frontend exécutés'),
('P25','Hibernate configuré en validation du schéma','backend/src/main/resources/application.properties','spring.jpa.hibernate.ddl-auto','Valeur validate; open-in-view=false','Forte','Configuration inspectée'),
('P26','Migrations référencées absentes de la copie','docs/DEPLOYMENT_READINESS.md; scripts/test-integration.ps1','backend/migrations','Références présentes, répertoire absent','Forte','Écart vérifié; intégration complète non relancée'),
('P27','36 tests unitaires backend réussis','memoire/tmp/tests-backend-unitaires.log','Résultat Maven','Tests run:36, Failures:0, Errors:0, Skipped:0','Forte','Exécuté le 10 novembre 2026'),
('P28','126 tests frontend réussis depuis le chemin canonique','memoire/tmp/tests-frontend-chemin-canonique.log','Résultat Node','126 succès, 0 échec, 0 ignoré','Forte','Exécuté le 10 novembre 2026'),
('P29','Premier lancement frontend affecté par la casse du chemin','memoire/tmp/tests-frontend.log','Rendu React SSR','115 succès/11 échecs; useContext; relance canonique réussie','Forte pour les résultats','Cause exacte non instrumentée'),
('P30','ESLint et construction frontend réussis','memoire/tmp/lint-frontend.log; memoire/tmp/build-frontend.log','eslint, vite build','Codes de sortie nuls observés','Forte','Exécuté le 10 novembre 2026'),
('P31','Déploiement de production non démontré','docs/DEPLOYMENT_READINESS.md; docs/RECETTE_FINALE.md','Réserves exploitation','Recette visuelle, TLS, restauration et charge restant à prouver','Moyenne','Impossible à confirmer sur la cible'),
('P32','Admission et profil académique',J+'service/StagiaireGestionService.java; frontend/src/pages/stagiaires/AcademiquesPanel.jsx','création et ajout académique','Services, DTO, pages et contraintes SQL','Moyenne','Implémenté; parcours complet non rejoué'),
('P33','Administration des comptes et révocation des sessions',J+'service/UtilisateurGestionService.java; '+J+'repository/SessionUtilisateurRepository.java','réinitialisation et rôles','Gestion du mot de passe et révocation dans le code','Moyenne','Implémenté; HTTP non rejoué'),
('P34','Synthèse limitée par les droits',J+'service/DossierSyntheseService.java; frontend/src/pages/stagiaires/DossierSynthese.jsx','stagiaire, participation','Agrégation et filtrage du portail','Moyenne','Implémenté; HTTP non rejoué'),
('P35','Soutenance facultative et non exigée à la clôture',J+'service/SoutenanceGestionService.java; '+J+'service/StageGestionService.java','création et clôture','Aucun appel de contrôle soutenance dans branche clôture','Forte','Inspection statique'),
('P36','Journal d’audit protégé',J+'service/JournalAuditService.java; '+J+'controller/JournalAuditController.java','enregistrer et consultation','ADMIN pour lecture; trace finalisation évaluation','Moyenne','Implémenté; couverture exhaustive non prouvée'),
]
(M/'tmp/preuves.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2),encoding='utf-8')
write('MATRICE_PREUVES.md','# Matrice des preuves\n\nÉtat observé le 10 novembre 2026, commit 9e1a7d8. Une réussite de test ne prouve que les scénarios exercés.\n\n| ID | Affirmation | Fichier source | Classe ou fonction | Preuve | Niveau de confiance | Statut |\n|---|---|---|---|---|---|---|\n'+'\n'.join('| '+' | '.join(r)+' |' for r in rows))
write('AUDIT_PROJET.md',f'''# Audit du projet MEF SIIGFP

Date de constat : 10 novembre 2026. Révision Git : 9e1a7d8. Le code applicatif n’a pas été modifié. Inventaire de la copie locale, inspection des chemins fonctionnels et vérifications limitées aux commandes consignées ci-dessous. Cette analyse ne certifie ni les données métier ni une installation en production.

## Références académiques

Les deux PDF fournis ont été extraits : 54 pages pour RIJARILANTO Helena Candy, 57 pour RANDRIAMIRADO Nasandratriniavo Tsinjoniaina Samuëlah. Les couvertures associent deux logos, un titre centré, l’auteur, le diplôme, la date et le jury. Tous deux présentent sommaire, listes, glossaire, avant-propos, remerciements, introduction, réalisation, bilan, conclusion et bibliographie. Candy regroupe la réalisation dans le premier grand chapitre puis le bilan dans le second ; Tsinjoniaina sépare présentation, réalisation et bilan en trois chapitres. Les pages techniques combinent explications, tableaux et figures légendées ; les annexes complètent les détails. Le nouveau mémoire retient trois chapitres, sans reprendre leurs textes, schémas, captures ou appréciations personnelles. Les extraits locaux sont des intermédiaires de lecture et ne font pas partie du mémoire.

## Architecture technique

Java 17 et Spring Boot 4.1.0 sont déclarés dans pom.xml ; PDFBox 3.0.8 est déclaré explicitement. React, React DOM, Vite et ESLint sont décrits dans package.json et verrouillés dans package-lock.json. Le navigateur utilise fetch, JSON, FormData et Bearer ; le backend utilise Spring MVC, Spring Security, JPA et le pilote PostgreSQL. La version PostgreSQL 15 apparaît dans les procédures locales ; la version effectivement déployée n’a pas été interrogée. Pas de preuve d’un JWT, de microservices ou d’un service de messagerie.

## Architecture logicielle et sécurité

Contrôleurs de gestion et DTO, services métier transactionnels, repositories JPA et entités. Le filtre de session recharge l’identité et les rôles. Le jeton opaque est généré sur 32 octets, son empreinte SHA-256 est enregistrée, la validité est de huit heures. BCrypt vérifie le mot de passe. SecurityConfig applique les règles de routes et un refus final par défaut ; DossierAccessService ajoute le périmètre propriétaire/affectation. ADMIN est global, ENCADREUR est limité aux participations affectées, STAGIAIRE au compte explicitement associé. RESPONSABLE_STAGE est un alias de lecture d’ADMIN. La rédaction et l’examen des rapports gardent des restrictions spécifiques même pour les comptes multi-rôles.

## Modèle de données

Le script contient {len(blocks)} tables, 6 vues et 16 triggers. Les 44 classes d’entités correspondent aux tables. Le contrôle automatique ci-dessous compare les noms de tables et les colonnes explicitement annotées ; il ne remplace pas une validation Hibernate sur une base réelle et ne prouve pas l’équivalence des types ou de toutes les contraintes.

| Entité | Table | Colonnes nommées vérifiées | Résultat |
|---|---|---:|---|
'''+ '\n'.join('| '+' | '.join(map(str,r))+' |' for r in comp)+'''

Les clés étrangères relient le stagiaire à ses profils académiques, puis ces profils aux participations. Stage représente le cadre organisationnel ; stage_participant porte les dates, l’encadreur, le cadre de stage et le statut individuel. Les rapports, événements, documents, évaluations et soutenances se rattachent aux participations. Le département est porté par la structure d’accueil du stage, pas par une colonne de stagiaires. Les CHECK, UNIQUE et triggers SQL apportent des contrôles supplémentaires qui ne sont pas tous exprimés en annotations JPA. La base accepte une note NULL, tandis que le service de notation exige une valeur ; cette différence correspond à des responsabilités de validation différentes.

## Écarts documentaires et techniques

- Le dossier backend/migrations, V002 et checks/preflight.sql sont mentionnés mais absents de la copie inspectée. Les protections SQL attribuées à V002 dans les anciens rapports ne sont pas confirmées ici. Le lanceur d’intégration dépend de ces fichiers ; il n’a pas été exécuté.
- Les anciens documents décrivent une clôture strictement conditionnée. Le code courant expose ignorerDocumentsManquants et forcerCloture ; le formulaire transmet forcerCloture=true. La branche forcée consigne les pièces et rapports manquants mais conserve accès, transition et verrouillage.
- Le schéma d’initialisation contient DROP DATABASE, CREATE DATABASE et des commandes psql ; il n’a pas été exécuté et ne doit pas servir de migration de données existantes.
- Les listes chargent fréquemment findAll avant filtrage ; aucun test de charge ni pagination SQL démontrée. localStorage porte le jeton côté navigateur. Les exports PDF utilisent une police standard et remplacent les caractères non disponibles.

## Fonctionnalités et traçabilité

La matrice P01 à P36 relie chaque affirmation aux sources : admission et académique ; stages/participations/événements ; rapports/observations ; comptes et portail ; documents ; évaluations et soutenances ; alertes et pilotage ; synthèse/export ; référentiels et audit. Les pages et clients API sont présents. Les CRUD bruts présents dans le dépôt ne sont pas automatiquement accessibles : les règles SecurityConfig restent déterminantes.

## Tests exécutés pour ce mémoire

36 tests unitaires backend réussis, 0 échec, 0 erreur, 0 ignoré. Sélection : ClotureParticipationTests, DocumentContentTests, DossierAccessTests, ExportTests, ParticipationRulesTests, SuiviServicesTests, AlerteClotureTests. Commande Maven hors ligne, sans Spring/HTTP/PostgreSQL. Le premier essai n’accédait pas au cache dans le bac à sable ; relance autorisée avec accès au cache local, réussie.

Frontend : 126 tests réussis, 0 échec, 0 ignoré avec node --test --test-concurrency=1 tests/*.test.js depuis le chemin canonique stagemanagement. Un premier essai sous la variante Stagemanagement donnait 115 succès et 11 échecs React useContext ; les traces chargeaient React depuis deux graphies du chemin. La relance canonique a réussi sans modifier le code. La casse est une explication étayée, non une cause instrumentée de manière exhaustive. ESLint et Vite build réussis. Journaux dans memoire/tmp/.

Les anciens résultats de docs/RECETTE_FINALE.md (35 tests backend et 116 frontend au 3 novembre) sont historiques. Les fichiers Surefire préexistants ne sont pas considérés comme une exécution actuelle des tests d’intégration. Aucun essai navigateur, déploiement, restauration, charge, cron à heure réelle ou appel de la base métier n’a été réalisé pour ce mémoire.

## Limites de l’audit

L’inventaire couvre les fichiers sources de la copie locale ; la revue approfondie porte sur les chemins décrits par la matrice. Il ne s’agit pas d’une preuve exhaustive de correction de chaque méthode. Les dates de stage, l’identité administrative, les encadreurs, le jury, le cahier des charges signé, les observations de l’ancien système et l’acceptation métier ne sont pas disponibles. Le document conserve les marqueurs demandés [À CONFIRMER].
''')
write('PLAN_MEMOIRE.md','''# Plan détaillé du mémoire

Pages préliminaires : couverture ITU/MEF ; table des matières automatique ; listes automatiques des tableaux et figures ; glossaire ; avant-propos ; remerciements à personnaliser.

Introduction générale : contexte fourni, question de centralisation/traçabilité, objectifs et limites des preuves.

1. Présentation du projet
1.1 Contexte institutionnel et objectifs
1.2 Analyse de l’existant et périmètre documenté
1.3 Acteurs et besoins
1.4 Démarche de travail et planning à confirmer
1.5 Technologies et justification de la solution existante
1.6 Architecture et échanges

2. Conception et réalisation
2.1 Données et relations
2.2 Couches backend et transactions
2.3 Authentification et autorisations
2.4 Admission, comptes et affectations
2.5 Cycle de vie des participations et clôture administrative
2.6 Rapports hebdomadaires et observations
2.7 Gestion documentaire
2.8 Évaluations et soutenances
2.9 Alertes, pilotage, synthèses et exports
2.10 Interface React et parcours
2.11 Contraintes SQL et cohérence JPA
2.12 Tests et validation
2.13 Préparation du déploiement
2.14 Difficultés attestées et corrections

3. Évaluation du projet et connaissances acquises
3.1 Bilan fonctionnel
3.2 Bilan technique et limites
3.3 Valeur attendue pour l’organisme
3.4 Bilan personnel à valider
3.5 Perspectives

Conclusion ; bibliographie consultée ; annexes : preuves abrégées, inventaire des tables, scénarios de recette et captures à fournir, liste administrative de vérification.

Figures originales : architecture, composants, relations physiques principales, acteurs/parcours, séquence des rapports, décision de clôture. Aucun Gantt tant que les dates ne sont pas prouvées. Aucun écran synthétique présenté comme capture.
''')
write('POINTS_A_CONFIRMER.md','''# Points à confirmer avant dépôt universitaire

- [ ] Nom et prénoms, numéro étudiant, promotion et année universitaire.
- [ ] Dates officielles de stage, date de soutenance, encadreurs et composition du jury.
- [ ] Intitulé officiel de la formation et charte ITU applicable.
- [ ] Développement officiel du sigle SIIGFP, rattachement exact du service et missions de l’unité d’accueil.
- [ ] Cahier des charges approuvé et description factuelle de l’ancien processus ; aucun tableur ni dossier papier ancien n’est présumé.
- [ ] Dates et durées du planning ; Gantt exclu en l’absence de preuve.
- [ ] Responsabilité personnelle dans les travaux et validation du bilan personnel et des remerciements.
- [ ] Récupérer backend/migrations et les précontrôles mentionnés, ou corriger les procédures devenues obsolètes.
- [ ] Valider institutionnellement la clôture forcée présente dans l’interface et les justificatifs attendus.
- [ ] Exécuter l’intégration complète sur une base isolée après rétablissement des prérequis.
- [ ] Produire des captures réelles anonymisées : connexion, dossier, participation/clôture, rapports, pilotage.
- [ ] Réaliser recette visuelle, responsive, clavier, impression et contrôles croisés avec des comptes de test.
- [ ] Confirmer le statut du déploiement, la version PostgreSQL cible, TLS/proxy, sauvegarde et restauration des fichiers et de la base.
- [ ] Mesurer temps de traitement, qualité des dossiers et satisfaction avant de revendiquer des gains.
- [ ] Mettre à jour les champs Word, vérifier la pagination et les listes avant impression.
''')
print('Audit, matrice, plan et liste de confirmation créés. Entités comparées:',len(comp),'Écarts noms:',[x for x in comp if x[3]!='Aucune colonne explicite absente'])
