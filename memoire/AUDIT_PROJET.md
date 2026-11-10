# Audit du projet MEF SIIGFP

Date de constat : 10 novembre 2026. Révision Git : 9e1a7d8. Le code applicatif n’a pas été modifié. Inventaire de la copie locale, inspection des chemins fonctionnels et vérifications limitées aux commandes consignées ci-dessous. Cette analyse ne certifie ni les données métier ni une installation en production.

## Références académiques

Les deux PDF fournis ont été extraits : 54 pages pour RIJARILANTO Helena Candy, 57 pour RANDRIAMIRADO Nasandratriniavo Tsinjoniaina Samuëlah. Les couvertures associent deux logos, un titre centré, l’auteur, le diplôme, la date et le jury. Tous deux présentent sommaire, listes, glossaire, avant-propos, remerciements, introduction, réalisation, bilan, conclusion et bibliographie. Candy regroupe la réalisation dans le premier grand chapitre puis le bilan dans le second ; Tsinjoniaina sépare présentation, réalisation et bilan en trois chapitres. Les pages techniques combinent explications, tableaux et figures légendées ; les annexes complètent les détails. Le nouveau mémoire retient trois chapitres, sans reprendre leurs textes, schémas, captures ou appréciations personnelles. Les extraits locaux sont des intermédiaires de lecture et ne font pas partie du mémoire.

## Architecture technique

Java 17 et Spring Boot 4.1.0 sont déclarés dans pom.xml ; PDFBox 3.0.8 est déclaré explicitement. React, React DOM, Vite et ESLint sont décrits dans package.json et verrouillés dans package-lock.json. Le navigateur utilise fetch, JSON, FormData et Bearer ; le backend utilise Spring MVC, Spring Security, JPA et le pilote PostgreSQL. La version PostgreSQL 15 apparaît dans les procédures locales ; la version effectivement déployée n’a pas été interrogée. Pas de preuve d’un JWT, de microservices ou d’un service de messagerie.

## Architecture logicielle et sécurité

Contrôleurs de gestion et DTO, services métier transactionnels, repositories JPA et entités. Le filtre de session recharge l’identité et les rôles. Le jeton opaque est généré sur 32 octets, son empreinte SHA-256 est enregistrée, la validité est de huit heures. BCrypt vérifie le mot de passe. SecurityConfig applique les règles de routes et un refus final par défaut ; DossierAccessService ajoute le périmètre propriétaire/affectation. ADMIN est global, ENCADREUR est limité aux participations affectées, STAGIAIRE au compte explicitement associé. RESPONSABLE_STAGE est un alias de lecture d’ADMIN. La rédaction et l’examen des rapports gardent des restrictions spécifiques même pour les comptes multi-rôles.

## Modèle de données

Le script contient 44 tables, 6 vues et 16 triggers. Les 44 classes d’entités correspondent aux tables. Le contrôle automatique ci-dessous compare les noms de tables et les colonnes explicitement annotées ; il ne remplace pas une validation Hibernate sur une base réelle et ne prouve pas l’équivalence des types ou de toutes les contraintes.

| Entité | Table | Colonnes nommées vérifiées | Résultat |
|---|---|---:|---|
| Alerte | alertes | 10 | Aucune colonne explicite absente |
| CadreStage | cadres_stage | 3 | Aucune colonne explicite absente |
| CritereEvaluation | criteres_evaluation | 0 | Aucune colonne explicite absente |
| Departement | departements | 1 | Aucune colonne explicite absente |
| DepartementStatutHistorique | departement_statut_historique | 5 | Aucune colonne explicite absente |
| Encadreur | encadreurs | 4 | Aucune colonne explicite absente |
| EncadreurStatutHistorique | encadreur_statut_historique | 5 | Aucune colonne explicite absente |
| Etablissement | etablissements | 0 | Aucune colonne explicite absente |
| EvaluationCritere | evaluation_criteres | 2 | Aucune colonne explicite absente |
| EvaluationFinale | evaluations_finales | 7 | Aucune colonne explicite absente |
| Fichier | fichiers | 6 | Aucune colonne explicite absente |
| Filiere | filieres | 0 | Aucune colonne explicite absente |
| HistoriqueConnexionUtilisateur | historique_connexion_utilisateur | 2 | Aucune colonne explicite absente |
| JournalAudit | journal_audit | 5 | Aucune colonne explicite absente |
| MoisStage | mois_stage | 0 | Aucune colonne explicite absente |
| NiveauEtude | niveaux_etude | 0 | Aucune colonne explicite absente |
| ParametresSuivi | parametres_suivi | 3 | Aucune colonne explicite absente |
| RapportActivite | rapports_activite | 9 | Aucune colonne explicite absente |
| RapportActiviteStatutHistorique | rapports_activite_statut_historique | 5 | Aucune colonne explicite absente |
| RapportObservation | rapport_observations | 5 | Aucune colonne explicite absente |
| ResponsableDepartement | responsable_departements | 2 | Aucune colonne explicite absente |
| Role | roles | 0 | Aucune colonne explicite absente |
| SessionUtilisateur | sessions_utilisateur | 5 | Aucune colonne explicite absente |
| SeuilAlerte | seuils_alerte | 2 | Aucune colonne explicite absente |
| Soutenance | soutenances | 3 | Aucune colonne explicite absente |
| Stage | stages | 2 | Aucune colonne explicite absente |
| StageParticipant | stage_participant | 10 | Aucune colonne explicite absente |
| StageParticipantEvenement | stage_participant_evenements | 7 | Aucune colonne explicite absente |
| StageParticipantFichier | stage_participant_fichiers | 4 | Aucune colonne explicite absente |
| StageParticipantStatutHistorique | stage_participant_statut_historique | 5 | Aucune colonne explicite absente |
| Stagiaire | stagiaires | 4 | Aucune colonne explicite absente |
| StagiaireAcademique | stagiaire_academique | 5 | Aucune colonne explicite absente |
| StagiaireFichier | stagiaire_fichiers | 4 | Aucune colonne explicite absente |
| StagiaireStatutHistorique | stagiaire_statut_historique | 5 | Aucune colonne explicite absente |
| Statut | statuts | 1 | Aucune colonne explicite absente |
| StructureAccueil | structures_accueil | 1 | Aucune colonne explicite absente |
| TransitionStatut | transitions_statut | 2 | Aucune colonne explicite absente |
| TypeAlerte | types_alerte | 3 | Aucune colonne explicite absente |
| TypeEvenementParticipation | types_evenement_participation | 0 | Aucune colonne explicite absente |
| TypePieceJointe | types_piece_jointe | 4 | Aucune colonne explicite absente |
| TypeStatut | types_statut | 0 | Aucune colonne explicite absente |
| Utilisateur | utilisateurs | 3 | Aucune colonne explicite absente |
| UtilisateurRole | utilisateurs_role | 2 | Aucune colonne explicite absente |
| UtilisateurStatutHistorique | utilisateur_statut_historique | 5 | Aucune colonne explicite absente |

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
