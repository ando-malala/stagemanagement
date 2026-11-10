# Bibliographie

Toutes les références ci-dessous ont été consultées pour la préparation du document le 10 novembre 2026. Les mémoires de référence servent uniquement de repères académiques et visuels. Les références P01 à P36 identifient les preuves propres au projet, détaillées dans l’annexe A et dans MATRICE_PREUVES.md.

[R1] RIJARILANTO Helena Candy. Application de gestion point de vente. Mémoire de Licence en Informatique, option développement, IT University, novembre 2025. Document PDF fourni, 54 pages.

[R2] RANDRIAMIRADO Nasandratriniavo Tsinjoniaina Samuëlah. Génération Automatisée et Analyse Intelligente des Comptes Rendus dans Async (SmartAsyncReports). Mémoire de Licence en Informatique, IT University, octobre 2025. Document PDF fourni, 57 pages.

[R3] Spring. Using @Transactional. Documentation officielle du Spring Framework. https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html

[R4] PostgreSQL Global Development Group. PostgreSQL 15 Documentation — Constraints. https://www.postgresql.org/docs/15/ddl-constraints.html

[R5] React. Thinking in React. Documentation officielle. https://react.dev/learn/thinking-in-react

[R6] Spring. Authorize HttpServletRequests. Documentation officielle de Spring Security. https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html

[R7] Projet Stagemanagement. Code source backend, frontend et conception/Conception2.sql, révision 9e1a7d8. Copie locale examinée le 10 novembre 2026. Auteur et attribution administrative [À CONFIRMER].

[R8] Projet Stagemanagement. Documents techniques locaux : BACKEND_AUDIT.md, REMEDIATION_STATUS.md, RECETTE_FINALE.md et DEPLOYMENT_READINESS.md. Leurs constats sont interprétés selon les mises à jour et confrontés au code courant.

[R9] Campagne documentaire du 10 novembre 2026. Journaux des tests unitaires backend, des tests frontend, d’ESLint et de la construction Vite. Répertoire memoire/tmp ; résultats synthétisés au tableau 6.

# Annexes

## Annexe A Repères de preuve

Le tableau 8 relie les principaux groupes d’affirmations aux sources. Les chemins Java sont relatifs à backend/src/main/java/mg/mef/stagemanagement/. Les références détaillées, la fonction, la preuve, le niveau de confiance et le statut de chaque affirmation figurent dans MATRICE_PREUVES.md. Les preuves statiques décrivent le contenu du dépôt ; les preuves d’exécution sont limitées aux commandes effectivement consignées.

:::table Repères de preuve pour la soutenance
Repères | Sujet | Sources principales
P01 à P03 | Pile technique et échanges | pom.xml ; package.json ; package-lock.json ; frontend/src/api/apiClient.js.
P04 à P07 | Session, rôles et ressources | AuthService ; ApplicationRoles ; SecurityConfig ; DossierAccessService.
P08 à P11 | Portail, données et affectation | PortailStagiaireService ; Conception2.sql ; StageParticipant ; ParticipationRulesService.
P12 à P16 | Calendrier, rapports et clôture | CalculPeriodesRapportService ; RapportActiviteGestionService ; StageGestionService ; EvenementParticipationRequest.
P17 à P19 | Pièces et évaluation | DocumentContentValidator ; FichierGestionService ; EvaluationGestionService.
P20 à P23 | Alertes, pilotage et exports | AlerteScheduler ; AlerteOrchestrationService ; AlerteClotureService ; PilotageService ; ExportService.
P24 à P26 | Routage, configuration et migrations | AppRoutes.jsx ; routeConfig.js ; application.properties ; test-integration.ps1 ; DEPLOYMENT_READINESS.md.
P27 à P31 | Résultats et réserves | Journaux conservés dans memoire/tmp ; RECETTE_FINALE.md ; DEPLOYMENT_READINESS.md.
P32 à P36 | Admission, comptes, synthèse et audit | StagiaireGestionService ; UtilisateurGestionService ; DossierSyntheseService ; SoutenanceGestionService ; JournalAuditService.
:::

## Annexe B Inventaire du modèle physique

Le tableau 9 regroupe les 44 tables déclarées dans Conception2.sql. Ce regroupement facilite la lecture ; il ne crée pas de sous-systèmes de déploiement indépendants. Les six vues et les seize triggers sont recensés dans l’audit. Les noms et colonnes explicitement annotés dans les 44 entités ont été comparés au script, sans exécuter celui-ci sur une base métier.

:::table Inventaire des tables par famille
Famille | Tables
Référentiels généraux | types_statut ; statuts ; etablissements ; filieres ; niveaux_etude ; mois_stage ; roles.
Paramétrage du suivi | types_piece_jointe ; types_evenement_participation ; types_alerte ; parametres_suivi ; seuils_alerte.
Comptes et traces | utilisateurs ; utilisateurs_role ; sessions_utilisateur ; historique_connexion_utilisateur ; utilisateur_statut_historique ; journal_audit.
Organisation | departements ; departement_statut_historique ; responsable_departements ; structures_accueil.
Stagiaires et pièces | fichiers ; stagiaires ; stagiaire_statut_historique ; stagiaire_academique ; stagiaire_fichiers.
Encadrement et stages | encadreurs ; encadreur_statut_historique ; cadres_stage ; stages.
Participations | stage_participant ; stage_participant_statut_historique ; stage_participant_evenements ; stage_participant_fichiers.
Rapports et transitions | rapports_activite ; rapports_activite_statut_historique ; rapport_observations ; transitions_statut.
Évaluation et alertes | criteres_evaluation ; evaluations_finales ; evaluation_criteres ; soutenances ; alertes.
:::

## Annexe C Recette complémentaire et captures

Le tableau 10 constitue une proposition de recette à exécuter sur un environnement isolé. Ses résultats sont à relever ; aucune ligne n’est déclarée réussie par anticipation. Les comptes et données utilisés pour les captures doivent être des données de recette ou être anonymisés sans modifier la signification du parcours.

:::table Scénarios de recette restant à exécuter
Scénario | Vérification attendue | État
Dossiers distincts | Un stagiaire ou encadreur non affecté ne lit ni ne modifie le dossier d’autrui, y compris par URL directe. | Non exécuté ici en HTTP complet.
Rapport hebdomadaire | Brouillon, soumission, correction, nouvelle soumission et validation avec les bons comptes. | À rejouer dans le navigateur.
Clôture ordinaire et forcée | Refus ou trace des manquants selon la modalité ; persistance de l’événement, du statut et des alertes. | À rejouer sur PostgreSQL isolé.
Pièces | Dépôt valide, refus de format, refus de taille et téléchargement selon les droits. | Parcours navigateur à compléter.
Impression et responsive | Lisibilité des tableaux, navigation clavier, petits écrans et pages imprimées. | Non exécuté.
Exploitation | HTTPS, traitement à 06 h, sauvegarde et restauration coordonnée des données et des fichiers. | Non exécuté.
:::

[CAPTURE À FOURNIR : page de connexion réelle, sans identifiants ni jeton visibles.]

[CAPTURE À FOURNIR : dossier d’un stagiaire de recette et ses profils académiques.]

[CAPTURE À FOURNIR : participation et dialogue de clôture administrative.]

[CAPTURE À FOURNIR : grille des rapports et cycle d’examen avec un jeu de recette.]

[CAPTURE À FOURNIR : tableau de bord et dossiers prioritaires sur données de recette.]

Ces emplacements ne sont pas comptés comme des figures. Lors de leur remplacement, chaque capture devra recevoir une légende numérotée, une référence dans le texte et une vérification de confidentialité.

## Annexe D Informations à valider avant dépôt

L’identité de l’auteur, le numéro étudiant, les encadreurs, le jury, les dates officielles du stage, l’année universitaire et la date de soutenance restent [À CONFIRMER]. La présentation officielle du SIIGFP et la charte universitaire doivent également être rapprochées des documents administratifs applicables.

Les observations du processus antérieur et le planning devront provenir de documents ou d’entretiens confirmés. Les remerciements et le bilan personnel doivent être validés par l’auteur. Les conditions d’emploi de la clôture forcée, la recette métier, la livraison et les résultats d’usage nécessitent une validation de l’organisme d’accueil.

Avant impression, actualiser la table des matières, les listes de figures et de tableaux ainsi que la pagination dans Word, puis relire les sauts de page et les tableaux. La liste complète des éléments à vérifier est conservée dans POINTS_A_CONFIRMER.md.
