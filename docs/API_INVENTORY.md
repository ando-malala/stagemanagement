# Inventaire API — sources du dépôt

Généré par `python scripts/audit_backend.py`. Complément métier : [audit](BACKEND_AUDIT.md).

53 contrôleurs, 300 opérations HTTP, 127 opérations admises par SecurityConfig (certaines techniques).

Les règles sont évaluées dans leur ordre Spring. `denyAll` signifie inaccessible même à ADMIN. Les signatures décrivent les paramètres de chemin, de requête, multipart et le corps exact. Les paramètres AuthenticationPrincipal sont injectés par le serveur. Les réponses List sont des tableaux JSON, sans enveloppe data ni pagination Spring. Les statuts indiqués sont ceux du chemin de succès; voir les erreurs des services ci-dessous, ainsi que 401/403 pour les accès protégés. Les CRUD bruts peuvent aussi renvoyer 404 via Optional. Cet inventaire statique ne prouve ni la disponibilité du serveur ni les données PostgreSQL.

## [AlerteDetectionController](../backend/src/main/java/mg/mef/stagemanagement/controller/AlerteDetectionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| POST | `/api/alertes/detection/stages-depasses` | ADMIN | 200 | `ResponseEntity<Map<String, Object>>` |
| POST | `/api/alertes/detection/fins-stages-proches` | ADMIN | 200 | `ResponseEntity<Map<String, Object>>` |
| POST | `/api/alertes/detection/rapports-manquants` | ADMIN | 200 | `ResponseEntity<Map<String, Object>>` |
| POST | `/api/alertes/detection/test-orchestration` | ADMIN | 200 | `ResponseEntity<Map<String, Object>>` |

- `detecterStagesDepasses` : `aucun paramètre`.
- `detecterFinsStagesProches` : `aucun paramètre`.
- `detecterRapportsManquants` : `aucun paramètre`.
- `testerOrchestration` : `aucun paramètre`.

## [AlerteGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/AlerteGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/alertes` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<AlerteResponse>>` |
| GET | `/api/alertes/non-lues` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<AlerteResponse>>` |
| POST | `/api/alertes` | ADMIN | 201 | `ResponseEntity<AlerteResponse>` |
| PATCH | `/api/alertes/{id}/lire` | ADMIN, ENCADREUR | 200 | `ResponseEntity<AlerteResponse>` |
| PATCH | `/api/alertes/tout-lire` | ADMIN, ENCADREUR | 204 | `ResponseEntity<Void>` |

- `listerAlertes` : `aucun paramètre`.
- `listerAlertesATraiter` : `aucun paramètre`.
- `creerAlerte` : `@RequestBody AlerteRequest request`.
- `marquerCommeTraitee` : `@PathVariable Integer id, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `toutMarquerCommeTraite` : `@AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.

## [AuthController](../backend/src/main/java/mg/mef/stagemanagement/controller/AuthController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| POST | `/api/auth/login` | permitAll | 200 | `ResponseEntity<LoginResponse>` |
| GET | `/api/auth/me` | authenticated | 200 | `ResponseEntity<AuthMeResponse>` |
| POST | `/api/auth/logout` | authenticated | 204 | `ResponseEntity<Void>` |

- `login` : `@RequestBody LoginRequest request`.
- `me` : `@AuthenticationPrincipal AuthMeResponse utilisateur`.
- `logout` : `@RequestHeader( value = "Authorization", required = false ) String authorizationHeader`.

## [CadreStageController](../backend/src/main/java/mg/mef/stagemanagement/controller/CadreStageController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/cadres-stage` | denyAll | 200 | `ResponseEntity<List<CadreStage>>` |
| GET | `/api/cadres-stage/{id}` | denyAll | 200 | `ResponseEntity<CadreStage>` |
| POST | `/api/cadres-stage` | denyAll | 201 | `ResponseEntity<CadreStage>` |
| PUT | `/api/cadres-stage/{id}` | denyAll | 200 | `ResponseEntity<CadreStage>` |
| DELETE | `/api/cadres-stage/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody CadreStage cadreStage`.
- `update` : `@PathVariable Integer id, @RequestBody CadreStage cadreStage`.
- `delete` : `@PathVariable Integer id`.

## [CritereEvaluationController](../backend/src/main/java/mg/mef/stagemanagement/controller/CritereEvaluationController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/criteres-evaluation` | denyAll | 200 | `ResponseEntity<List<CritereEvaluation>>` |
| GET | `/api/criteres-evaluation/{id}` | denyAll | 200 | `ResponseEntity<CritereEvaluation>` |
| POST | `/api/criteres-evaluation` | denyAll | 201 | `ResponseEntity<CritereEvaluation>` |
| PUT | `/api/criteres-evaluation/{id}` | denyAll | 200 | `ResponseEntity<CritereEvaluation>` |
| DELETE | `/api/criteres-evaluation/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody CritereEvaluation critereEvaluation`.
- `update` : `@PathVariable Integer id, @RequestBody CritereEvaluation critereEvaluation`.
- `delete` : `@PathVariable Integer id`.

## [DashboardController](../backend/src/main/java/mg/mef/stagemanagement/controller/DashboardController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/dashboard/resume` | ADMIN, ENCADREUR | 200 | `ResponseEntity<DashboardResumeResponse>` |

- `resume` : `aucun paramètre`.

## [DepartementController](../backend/src/main/java/mg/mef/stagemanagement/controller/DepartementController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/departements` | denyAll | 200 | `ResponseEntity<List<Departement>>` |
| GET | `/api/departements/{id}` | denyAll | 200 | `ResponseEntity<Departement>` |
| POST | `/api/departements` | denyAll | 201 | `ResponseEntity<Departement>` |
| PUT | `/api/departements/{id}` | denyAll | 200 | `ResponseEntity<Departement>` |
| DELETE | `/api/departements/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Departement departement`.
- `update` : `@PathVariable Integer id, @RequestBody Departement departement`.
- `delete` : `@PathVariable Integer id`.

## [DepartementStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/DepartementStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/departement-statut-historique` | denyAll | 200 | `ResponseEntity<List<DepartementStatutHistorique>>` |
| GET | `/api/departement-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<DepartementStatutHistorique>` |
| POST | `/api/departement-statut-historique` | denyAll | 201 | `ResponseEntity<DepartementStatutHistorique>` |
| PUT | `/api/departement-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<DepartementStatutHistorique>` |
| DELETE | `/api/departement-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody DepartementStatutHistorique departementStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody DepartementStatutHistorique departementStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## [EncadreurGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/EncadreurGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/encadreurs/comptes-eligibles` | ADMIN | 200 | `java.util.List<mg.mef.stagemanagement.service.PortailStagiaireService.Compte>` |
| GET | `/api/encadreurs` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<EncadreurResponse>>` |
| GET | `/api/encadreurs/{id}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<EncadreurResponse>` |
| POST | `/api/encadreurs` | ADMIN | 201 | `ResponseEntity<EncadreurResponse>` |
| PATCH | `/api/encadreurs/{id}` | ADMIN | 200 | `ResponseEntity<EncadreurResponse>` |
| PATCH | `/api/encadreurs/{id}/statut` | ADMIN | 200 | `ResponseEntity<EncadreurResponse>` |

- `comptesEligibles` : `@RequestParam(required = false) Integer encadreurId`.
- `listerEncadreurs` : `aucun paramètre`.
- `trouverEncadreur` : `@PathVariable Integer id`.
- `creerEncadreur` : `@RequestBody EncadreurRequest request`.
- `modifierEncadreur` : `@PathVariable Integer id, @RequestBody EncadreurRequest request`.
- `changerStatut` : `@PathVariable Integer id, @RequestBody ChangerStatutEncadreurRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.

## [EncadreurStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/EncadreurStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/encadreur-statut-historique` | denyAll | 200 | `ResponseEntity<List<EncadreurStatutHistorique>>` |
| GET | `/api/encadreur-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<EncadreurStatutHistorique>` |
| POST | `/api/encadreur-statut-historique` | denyAll | 201 | `ResponseEntity<EncadreurStatutHistorique>` |
| PUT | `/api/encadreur-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<EncadreurStatutHistorique>` |
| DELETE | `/api/encadreur-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody EncadreurStatutHistorique encadreurStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody EncadreurStatutHistorique encadreurStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## [EtablissementController](../backend/src/main/java/mg/mef/stagemanagement/controller/EtablissementController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/etablissements` | denyAll | 200 | `ResponseEntity<List<Etablissement>>` |
| GET | `/api/etablissements/{id}` | denyAll | 200 | `ResponseEntity<Etablissement>` |
| POST | `/api/etablissements` | denyAll | 201 | `ResponseEntity<Etablissement>` |
| PUT | `/api/etablissements/{id}` | denyAll | 200 | `ResponseEntity<Etablissement>` |
| DELETE | `/api/etablissements/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Etablissement etablissement`.
- `update` : `@PathVariable Integer id, @RequestBody Etablissement etablissement`.
- `delete` : `@PathVariable Integer id`.

## [EvaluationCritereController](../backend/src/main/java/mg/mef/stagemanagement/controller/EvaluationCritereController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/evaluation-criteres` | denyAll | 200 | `ResponseEntity<List<EvaluationCritere>>` |
| GET | `/api/evaluation-criteres/{id}` | denyAll | 200 | `ResponseEntity<EvaluationCritere>` |
| POST | `/api/evaluation-criteres` | denyAll | 201 | `ResponseEntity<EvaluationCritere>` |
| PUT | `/api/evaluation-criteres/{id}` | denyAll | 200 | `ResponseEntity<EvaluationCritere>` |
| DELETE | `/api/evaluation-criteres/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody EvaluationCritere evaluationCritere`.
- `update` : `@PathVariable Integer id, @RequestBody EvaluationCritere evaluationCritere`.
- `delete` : `@PathVariable Integer id`.

## [EvaluationFinaleController](../backend/src/main/java/mg/mef/stagemanagement/controller/EvaluationFinaleController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/evaluations-finales` | denyAll | 200 | `ResponseEntity<List<EvaluationFinale>>` |
| GET | `/api/evaluations-finales/{id}` | denyAll | 200 | `ResponseEntity<EvaluationFinale>` |
| POST | `/api/evaluations-finales` | denyAll | 201 | `ResponseEntity<EvaluationFinale>` |
| PUT | `/api/evaluations-finales/{id}` | denyAll | 200 | `ResponseEntity<EvaluationFinale>` |
| DELETE | `/api/evaluations-finales/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody EvaluationFinale evaluationFinale`.
- `update` : `@PathVariable Integer id, @RequestBody EvaluationFinale evaluationFinale`.
- `delete` : `@PathVariable Integer id`.

## [EvaluationGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/EvaluationGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/evaluations` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<EvaluationFinaleResponse>>` |
| GET | `/api/evaluations/{id}` | ADMIN, ENCADREUR | 200 | `ResponseEntity<EvaluationFinaleResponse>` |
| GET | `/api/evaluations/participation/{stageParticipantId}` | ADMIN, ENCADREUR | 200 | `ResponseEntity<EvaluationFinaleResponse>` |
| POST | `/api/evaluations` | ADMIN, ENCADREUR | 201 | `ResponseEntity<EvaluationFinaleResponse>` |
| PATCH | `/api/evaluations/{id}` | ADMIN, ENCADREUR | 200 | `ResponseEntity<EvaluationFinaleResponse>` |
| GET | `/api/evaluations/criteres` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<CritereEvaluationResponse>>` |
| GET | `/api/evaluations/{id}/notes` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<EvaluationCritereResponse>>` |
| POST | `/api/evaluations/{id}/notes` | ADMIN, ENCADREUR | 201 | `ResponseEntity<EvaluationCritereResponse>` |
| POST | `/api/evaluations/{id}/finaliser` | ADMIN, ENCADREUR | 200 | `ResponseEntity<EvaluationFinaleResponse>` |

- `listerEvaluations` : `aucun paramètre`.
- `trouverEvaluation` : `@PathVariable Integer id`.
- `trouverParParticipation` : `@PathVariable Integer stageParticipantId`.
- `creerEvaluation` : `@RequestBody EvaluationFinaleRequest request`.
- `modifierEvaluation` : `@PathVariable Integer id, @RequestBody EvaluationFinaleRequest request`.
- `listerCriteres` : `aucun paramètre`.
- `listerNotes` : `@PathVariable Integer id`.
- `noterCritere` : `@PathVariable Integer id, @RequestBody EvaluationCritereRequest request`.
- `finaliserEvaluation` : `@PathVariable Integer id`.

## [FichierController](../backend/src/main/java/mg/mef/stagemanagement/controller/FichierController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/fichiers` | denyAll | 200 | `ResponseEntity<List<Fichier>>` |
| GET | `/api/fichiers/{id}` | denyAll | 200 | `ResponseEntity<Fichier>` |
| POST | `/api/fichiers` | denyAll | 201 | `ResponseEntity<Fichier>` |
| PUT | `/api/fichiers/{id}` | denyAll | 200 | `ResponseEntity<Fichier>` |
| DELETE | `/api/fichiers/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Fichier fichier`.
- `update` : `@PathVariable Integer id, @RequestBody Fichier fichier`.
- `delete` : `@PathVariable Integer id`.

## [FichierGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/FichierGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaires/{id}/fichiers` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<FichierResponse>>` |
| POST | `/api/stagiaires/{id}/fichiers` | ADMIN | 200 | `ResponseEntity<FichierResponse>` |
| GET | `/api/stage-participants/{id}/fichiers` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<FichierResponse>>` |
| POST | `/api/stage-participants/{id}/fichiers` | ADMIN | 200 | `ResponseEntity<FichierResponse>` |
| GET | `/api/fichiers/{id}/telecharger` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<Resource>` |

- `listerFichiersStagiaire` : `@PathVariable Integer id`.
- `ajouterFichierStagiaire` : `@PathVariable Integer id, @RequestParam("typePieceJointeCode") String typePieceJointeCode, @RequestParam("fichier") MultipartFile fichier, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `listerFichiersParticipation` : `@PathVariable Integer id`.
- `ajouterFichierParticipation` : `@PathVariable Integer id, @RequestParam("typePieceJointeCode") String typePieceJointeCode, @RequestParam("fichier") MultipartFile fichier, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `telechargerFichier` : `@PathVariable Integer id`.

## [FiliereController](../backend/src/main/java/mg/mef/stagemanagement/controller/FiliereController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/filieres` | denyAll | 200 | `ResponseEntity<List<Filiere>>` |
| GET | `/api/filieres/{id}` | denyAll | 200 | `ResponseEntity<Filiere>` |
| POST | `/api/filieres` | denyAll | 201 | `ResponseEntity<Filiere>` |
| PUT | `/api/filieres/{id}` | denyAll | 200 | `ResponseEntity<Filiere>` |
| DELETE | `/api/filieres/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Filiere filiere`.
- `update` : `@PathVariable Integer id, @RequestBody Filiere filiere`.
- `delete` : `@PathVariable Integer id`.

## [HistoriqueConnexionUtilisateurController](../backend/src/main/java/mg/mef/stagemanagement/controller/HistoriqueConnexionUtilisateurController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/historique-connexions` | denyAll | 200 | `ResponseEntity<List<HistoriqueConnexionUtilisateur>>` |
| GET | `/api/historique-connexions/{id}` | denyAll | 200 | `ResponseEntity<HistoriqueConnexionUtilisateur>` |
| POST | `/api/historique-connexions` | denyAll | 201 | `ResponseEntity<HistoriqueConnexionUtilisateur>` |
| PUT | `/api/historique-connexions/{id}` | denyAll | 200 | `ResponseEntity<HistoriqueConnexionUtilisateur>` |
| DELETE | `/api/historique-connexions/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody HistoriqueConnexionUtilisateur historiqueConnexionUtilisateur`.
- `update` : `@PathVariable Integer id, @RequestBody HistoriqueConnexionUtilisateur historiqueConnexionUtilisateur`.
- `delete` : `@PathVariable Integer id`.

## [JournalAuditController](../backend/src/main/java/mg/mef/stagemanagement/controller/JournalAuditController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/journal-audit` | ADMIN | 200 | `ResponseEntity<List<JournalAuditResponse>>` |
| GET | `/api/journal-audit/utilisateur/{utilisateurId}` | ADMIN | 200 | `ResponseEntity<List<JournalAuditResponse>>` |
| GET | `/api/journal-audit/entite/{entite}/{entiteId}` | ADMIN | 200 | `ResponseEntity<List<JournalAuditResponse>>` |

- `listerJournal` : `aucun paramètre`.
- `listerParUtilisateur` : `@PathVariable Integer utilisateurId`.
- `listerParEntite` : `@PathVariable String entite, @PathVariable Integer entiteId`.

## [MoisStageController](../backend/src/main/java/mg/mef/stagemanagement/controller/MoisStageController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/mois-stage` | denyAll | 200 | `ResponseEntity<List<MoisStage>>` |
| GET | `/api/mois-stage/{id}` | denyAll | 200 | `ResponseEntity<MoisStage>` |
| POST | `/api/mois-stage` | denyAll | 201 | `ResponseEntity<MoisStage>` |
| PUT | `/api/mois-stage/{id}` | denyAll | 200 | `ResponseEntity<MoisStage>` |
| DELETE | `/api/mois-stage/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody MoisStage moisStage`.
- `update` : `@PathVariable Integer id, @RequestBody MoisStage moisStage`.
- `delete` : `@PathVariable Integer id`.

## [NiveauEtudeController](../backend/src/main/java/mg/mef/stagemanagement/controller/NiveauEtudeController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/niveaux-etude` | denyAll | 200 | `ResponseEntity<List<NiveauEtude>>` |
| GET | `/api/niveaux-etude/{id}` | denyAll | 200 | `ResponseEntity<NiveauEtude>` |
| POST | `/api/niveaux-etude` | denyAll | 201 | `ResponseEntity<NiveauEtude>` |
| PUT | `/api/niveaux-etude/{id}` | denyAll | 200 | `ResponseEntity<NiveauEtude>` |
| DELETE | `/api/niveaux-etude/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody NiveauEtude niveauEtude`.
- `update` : `@PathVariable Integer id, @RequestBody NiveauEtude niveauEtude`.
- `delete` : `@PathVariable Integer id`.

## [ParametresSuiviController](../backend/src/main/java/mg/mef/stagemanagement/controller/ParametresSuiviController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/parametres-suivi` | denyAll | 200 | `ResponseEntity<List<ParametresSuivi>>` |
| GET | `/api/parametres-suivi/{id}` | denyAll | 200 | `ResponseEntity<ParametresSuivi>` |
| POST | `/api/parametres-suivi` | denyAll | 201 | `ResponseEntity<ParametresSuivi>` |
| PUT | `/api/parametres-suivi/{id}` | denyAll | 200 | `ResponseEntity<ParametresSuivi>` |
| DELETE | `/api/parametres-suivi/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Short id`.
- `create` : `@RequestBody ParametresSuivi parametresSuivi`.
- `update` : `@PathVariable Short id, @RequestBody ParametresSuivi parametresSuivi`.
- `delete` : `@PathVariable Short id`.

## [PortailStagiaireController](../backend/src/main/java/mg/mef/stagemanagement/controller/PortailStagiaireController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaires/{id}/compte` | ADMIN | 200 | `PortailStagiaireService.Compte` |
| GET | `/api/stagiaires/{id}/comptes-eligibles` | ADMIN | 200 | `java.util.List<PortailStagiaireService.Compte>` |
| PATCH | `/api/stagiaires/{id}/compte` | ADMIN | 200 | `PortailStagiaireService.Compte` |
| GET | `/api/portail/me` | STAGIAIRE | 200 | `PortailStagiaireService.Profil` |

- `compte` : `@PathVariable Integer id`.
- `comptesEligibles` : `@PathVariable Integer id`.
- `associer` : `@PathVariable Integer id, @RequestBody CompteRequest request`.
- `me` : `aucun paramètre`.

## [RapportActiviteGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/RapportActiviteGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/rapports-activite` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<RapportActiviteResponse>>` |
| GET | `/api/rapports-activite/{id}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<RapportActiviteResponse>` |
| GET | `/api/rapports-activite/participation/{stageParticipantId}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<RapportActiviteResponse>>` |
| POST | `/api/rapports-activite` | STAGIAIRE | 201 | `ResponseEntity<RapportActiviteResponse>` |
| PATCH | `/api/rapports-activite/{id}` | STAGIAIRE | 200 | `ResponseEntity<RapportActiviteResponse>` |
| GET | `/api/rapports-activite/{id}/observations` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<RapportObservationResponse>>` |
| POST | `/api/rapports-activite/{id}/observations` | ENCADREUR | 201 | `ResponseEntity<RapportObservationResponse>` |
| PATCH | `/api/rapports-activite/{id}/statut` | ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<RapportActiviteResponse>` |

- `listerRapports` : `aucun paramètre`.
- `trouverRapport` : `@PathVariable Integer id`.
- `listerParParticipation` : `@PathVariable Integer stageParticipantId`.
- `creerRapport` : `@RequestBody RapportActiviteRequest request`.
- `modifierRapport` : `@PathVariable Integer id, @RequestBody RapportActiviteRequest request`.
- `listerObservations` : `@PathVariable Integer id`.
- `ajouterObservation` : `@PathVariable Integer id, @RequestBody RapportObservationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `changerStatut` : `@PathVariable Integer id, @RequestBody ChangerStatutRapportRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.

## [RapportActiviteStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/RapportActiviteStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/rapport-activite-statut-historique` | denyAll | 200 | `ResponseEntity<List<RapportActiviteStatutHistorique>>` |
| GET | `/api/rapport-activite-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<RapportActiviteStatutHistorique>` |
| POST | `/api/rapport-activite-statut-historique` | denyAll | 201 | `ResponseEntity<RapportActiviteStatutHistorique>` |
| PUT | `/api/rapport-activite-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<RapportActiviteStatutHistorique>` |
| DELETE | `/api/rapport-activite-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody RapportActiviteStatutHistorique rapportActiviteStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody RapportActiviteStatutHistorique rapportActiviteStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## [RapportObservationController](../backend/src/main/java/mg/mef/stagemanagement/controller/RapportObservationController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/rapport-observations` | denyAll | 200 | `ResponseEntity<List<RapportObservation>>` |
| GET | `/api/rapport-observations/{id}` | denyAll | 200 | `ResponseEntity<RapportObservation>` |
| POST | `/api/rapport-observations` | denyAll | 201 | `ResponseEntity<RapportObservation>` |
| PUT | `/api/rapport-observations/{id}` | denyAll | 200 | `ResponseEntity<RapportObservation>` |
| DELETE | `/api/rapport-observations/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody RapportObservation rapportObservation`.
- `update` : `@PathVariable Integer id, @RequestBody RapportObservation rapportObservation`.
- `delete` : `@PathVariable Integer id`.

## [ReferentielAcademiqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/ReferentielAcademiqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/referentiels/academiques/etablissements` | authenticated | 200 | `ResponseEntity<List<ReferentielResponse>>` |
| POST | `/api/referentiels/academiques/etablissements` | ADMIN | 201 | `ResponseEntity<ReferentielResponse>` |
| PATCH | `/api/referentiels/academiques/etablissements/{id}` | ADMIN | 200 | `ResponseEntity<ReferentielResponse>` |
| GET | `/api/referentiels/academiques/filieres` | authenticated | 200 | `ResponseEntity<List<ReferentielResponse>>` |
| POST | `/api/referentiels/academiques/filieres` | ADMIN | 201 | `ResponseEntity<ReferentielResponse>` |
| PATCH | `/api/referentiels/academiques/filieres/{id}` | ADMIN | 200 | `ResponseEntity<ReferentielResponse>` |
| GET | `/api/referentiels/academiques/niveaux-etude` | authenticated | 200 | `ResponseEntity<List<ReferentielResponse>>` |
| POST | `/api/referentiels/academiques/niveaux-etude` | ADMIN | 201 | `ResponseEntity<ReferentielResponse>` |
| PATCH | `/api/referentiels/academiques/niveaux-etude/{id}` | ADMIN | 200 | `ResponseEntity<ReferentielResponse>` |
| GET | `/api/referentiels/academiques/mois-stage` | authenticated | 200 | `ResponseEntity<List<ReferentielResponse>>` |
| POST | `/api/referentiels/academiques/mois-stage` | ADMIN | 201 | `ResponseEntity<ReferentielResponse>` |
| PATCH | `/api/referentiels/academiques/mois-stage/{id}` | ADMIN | 200 | `ResponseEntity<ReferentielResponse>` |

- `listerEtablissements` : `aucun paramètre`.
- `creerEtablissement` : `@RequestBody ReferentielRequest request`.
- `modifierEtablissement` : `@PathVariable Integer id, @RequestBody ReferentielRequest request`.
- `listerFilieres` : `aucun paramètre`.
- `creerFiliere` : `@RequestBody ReferentielRequest request`.
- `modifierFiliere` : `@PathVariable Integer id, @RequestBody ReferentielRequest request`.
- `listerNiveauxEtude` : `aucun paramètre`.
- `creerNiveauEtude` : `@RequestBody ReferentielRequest request`.
- `modifierNiveauEtude` : `@PathVariable Integer id, @RequestBody ReferentielRequest request`.
- `listerMoisStage` : `aucun paramètre`.
- `creerMoisStage` : `@RequestBody ReferentielRequest request`.
- `modifierMoisStage` : `@PathVariable Integer id, @RequestBody ReferentielRequest request`.

## [ReferentielOrganisationController](../backend/src/main/java/mg/mef/stagemanagement/controller/ReferentielOrganisationController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/referentiels/organisation/departements` | authenticated | 200 | `ResponseEntity<List<DepartementResponse>>` |
| POST | `/api/referentiels/organisation/departements` | ADMIN | 201 | `ResponseEntity<DepartementResponse>` |
| PATCH | `/api/referentiels/organisation/departements/{id}` | ADMIN | 200 | `ResponseEntity<DepartementResponse>` |
| GET | `/api/referentiels/organisation/structures-accueil` | authenticated | 200 | `ResponseEntity<List<StructureAccueilResponse>>` |
| POST | `/api/referentiels/organisation/structures-accueil` | ADMIN | 201 | `ResponseEntity<StructureAccueilResponse>` |
| PATCH | `/api/referentiels/organisation/structures-accueil/{id}` | ADMIN | 200 | `ResponseEntity<StructureAccueilResponse>` |
| GET | `/api/referentiels/organisation/cadres-stage` | authenticated | 200 | `ResponseEntity<List<CadreStageResponse>>` |
| POST | `/api/referentiels/organisation/cadres-stage` | ADMIN | 201 | `ResponseEntity<CadreStageResponse>` |
| PATCH | `/api/referentiels/organisation/cadres-stage/{id}` | ADMIN | 200 | `ResponseEntity<CadreStageResponse>` |

- `listerDepartements` : `aucun paramètre`.
- `creerDepartement` : `@RequestBody DepartementRequest request`.
- `modifierDepartement` : `@PathVariable Integer id, @RequestBody DepartementRequest request`.
- `listerStructuresAccueil` : `aucun paramètre`.
- `creerStructureAccueil` : `@RequestBody StructureAccueilRequest request`.
- `modifierStructureAccueil` : `@PathVariable Integer id, @RequestBody StructureAccueilRequest request`.
- `listerCadresStage` : `aucun paramètre`.
- `creerCadreStage` : `@RequestBody CadreStageRequest request`.
- `modifierCadreStage` : `@PathVariable Integer id, @RequestBody CadreStageRequest request`.

## [ResponsableDepartementController](../backend/src/main/java/mg/mef/stagemanagement/controller/ResponsableDepartementController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/responsable-departements` | denyAll | 200 | `ResponseEntity<List<ResponsableDepartement>>` |
| GET | `/api/responsable-departements/{id}` | denyAll | 200 | `ResponseEntity<ResponsableDepartement>` |
| POST | `/api/responsable-departements` | denyAll | 201 | `ResponseEntity<ResponsableDepartement>` |
| PUT | `/api/responsable-departements/{id}` | denyAll | 200 | `ResponseEntity<ResponsableDepartement>` |
| DELETE | `/api/responsable-departements/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody ResponsableDepartement responsableDepartement`.
- `update` : `@PathVariable Integer id, @RequestBody ResponsableDepartement responsableDepartement`.
- `delete` : `@PathVariable Integer id`.

## [RoleController](../backend/src/main/java/mg/mef/stagemanagement/controller/RoleController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/roles` | ADMIN | 200 | `ResponseEntity<List<Role>>` |
| GET | `/api/roles/{id}` | ADMIN | 200 | `ResponseEntity<Role>` |
| POST | `/api/roles` | denyAll | 201 | `ResponseEntity<Role>` |
| PUT | `/api/roles/{id}` | denyAll | 200 | `ResponseEntity<Role>` |
| DELETE | `/api/roles/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Role role`.
- `update` : `@PathVariable Integer id, @RequestBody Role role`.
- `delete` : `@PathVariable Integer id`.

## [SessionUtilisateurController](../backend/src/main/java/mg/mef/stagemanagement/controller/SessionUtilisateurController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/sessions-utilisateur` | denyAll | 200 | `ResponseEntity<List<SessionUtilisateur>>` |
| GET | `/api/sessions-utilisateur/{id}` | denyAll | 200 | `ResponseEntity<SessionUtilisateur>` |
| POST | `/api/sessions-utilisateur` | denyAll | 201 | `ResponseEntity<SessionUtilisateur>` |
| PUT | `/api/sessions-utilisateur/{id}` | denyAll | 200 | `ResponseEntity<SessionUtilisateur>` |
| DELETE | `/api/sessions-utilisateur/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody SessionUtilisateur sessionUtilisateur`.
- `update` : `@PathVariable Integer id, @RequestBody SessionUtilisateur sessionUtilisateur`.
- `delete` : `@PathVariable Integer id`.

## [SeuilAlerteController](../backend/src/main/java/mg/mef/stagemanagement/controller/SeuilAlerteController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/seuils-alerte` | denyAll | 200 | `ResponseEntity<List<SeuilAlerte>>` |
| GET | `/api/seuils-alerte/{id}` | denyAll | 200 | `ResponseEntity<SeuilAlerte>` |
| POST | `/api/seuils-alerte` | denyAll | 201 | `ResponseEntity<SeuilAlerte>` |
| PUT | `/api/seuils-alerte/{id}` | denyAll | 200 | `ResponseEntity<SeuilAlerte>` |
| DELETE | `/api/seuils-alerte/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody SeuilAlerte seuilAlerte`.
- `update` : `@PathVariable Integer id, @RequestBody SeuilAlerte seuilAlerte`.
- `delete` : `@PathVariable Integer id`.

## [SoutenanceGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/SoutenanceGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/soutenances` | ADMIN, ENCADREUR | 200 | `ResponseEntity<List<SoutenanceResponse>>` |
| GET | `/api/soutenances/{id}` | ADMIN, ENCADREUR | 200 | `ResponseEntity<SoutenanceResponse>` |
| POST | `/api/soutenances` | ADMIN | 201 | `ResponseEntity<SoutenanceResponse>` |
| PATCH | `/api/soutenances/{id}` | ADMIN | 200 | `ResponseEntity<SoutenanceResponse>` |

- `listerSoutenances` : `aucun paramètre`.
- `trouverSoutenance` : `@PathVariable Integer id`.
- `creerSoutenance` : `@RequestBody SoutenanceRequest request`.
- `modifierSoutenance` : `@PathVariable Integer id, @RequestBody SoutenanceRequest request`.

## [StageGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/StageGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stages` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<StageResponse>>` |
| GET | `/api/stages/{id}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<StageResponse>` |
| POST | `/api/stages` | ADMIN | 201 | `ResponseEntity<StageResponse>` |
| PATCH | `/api/stages/{id}` | ADMIN | 200 | `ResponseEntity<StageResponse>` |
| GET | `/api/stages/{id}/participants` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<StageParticipantResponse>>` |
| GET | `/api/stagiaires/{id}/participations` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `java.util.List<StageParticipantResponse>` |
| GET | `/api/stage-participants` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<StageParticipantResponse>>` |
| GET | `/api/stage-participants/{id}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants` | ADMIN | 201 | `ResponseEntity<StageParticipantResponse>` |
| PATCH | `/api/stage-participants/{id}` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| PATCH | `/api/stage-participants/{id}/statut` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/prolonger` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/suspendre` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/reprendre` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/abandonner` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/annuler` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |
| POST | `/api/stage-participants/{id}/cloturer` | ADMIN | 200 | `ResponseEntity<StageParticipantResponse>` |

- `listerStages` : `aucun paramètre`.
- `trouverStage` : `@PathVariable Integer id`.
- `creerStage` : `@RequestBody StageRequest request`.
- `modifierStage` : `@PathVariable Integer id, @RequestBody StageRequest request`.
- `listerParticipantsStage` : `@PathVariable Integer id`.
- `participationsStagiaire` : `@PathVariable Integer id`.
- `listerParticipations` : `aucun paramètre`.
- `trouverParticipation` : `@PathVariable Integer id`.
- `creerParticipation` : `@RequestBody StageParticipantRequest request`.
- `modifierParticipation` : `@PathVariable Integer id, @RequestBody StageParticipantRequest request`.
- `changerStatutParticipation` : `@PathVariable Integer id, @RequestBody ChangerStatutParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `prolongerParticipation` : `@PathVariable Integer id, @RequestBody ProlongerParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `suspendreParticipation` : `@PathVariable Integer id, @RequestBody EvenementParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `reprendreParticipation` : `@PathVariable Integer id, @RequestBody EvenementParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `abandonnerParticipation` : `@PathVariable Integer id, @RequestBody EvenementParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `annulerParticipation` : `@PathVariable Integer id, @RequestBody EvenementParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `cloturerParticipation` : `@PathVariable Integer id, @RequestBody EvenementParticipationRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.

## [StageParticipantEvenementController](../backend/src/main/java/mg/mef/stagemanagement/controller/StageParticipantEvenementController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stage-participant-evenements` | denyAll | 200 | `ResponseEntity<List<StageParticipantEvenement>>` |
| GET | `/api/stage-participant-evenements/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantEvenement>` |
| POST | `/api/stage-participant-evenements` | denyAll | 201 | `ResponseEntity<StageParticipantEvenement>` |
| PUT | `/api/stage-participant-evenements/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantEvenement>` |
| DELETE | `/api/stage-participant-evenements/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StageParticipantEvenement stageParticipantEvenement`.
- `update` : `@PathVariable Integer id, @RequestBody StageParticipantEvenement stageParticipantEvenement`.
- `delete` : `@PathVariable Integer id`.

## [StageParticipantFichierController](../backend/src/main/java/mg/mef/stagemanagement/controller/StageParticipantFichierController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stage-participant-fichiers` | denyAll | 200 | `ResponseEntity<List<StageParticipantFichier>>` |
| GET | `/api/stage-participant-fichiers/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantFichier>` |
| POST | `/api/stage-participant-fichiers` | denyAll | 201 | `ResponseEntity<StageParticipantFichier>` |
| PUT | `/api/stage-participant-fichiers/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantFichier>` |
| DELETE | `/api/stage-participant-fichiers/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StageParticipantFichier stageParticipantFichier`.
- `update` : `@PathVariable Integer id, @RequestBody StageParticipantFichier stageParticipantFichier`.
- `delete` : `@PathVariable Integer id`.

## [StageParticipantStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/StageParticipantStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stage-participant-statut-historique` | denyAll | 200 | `ResponseEntity<List<StageParticipantStatutHistorique>>` |
| GET | `/api/stage-participant-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantStatutHistorique>` |
| POST | `/api/stage-participant-statut-historique` | denyAll | 201 | `ResponseEntity<StageParticipantStatutHistorique>` |
| PUT | `/api/stage-participant-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<StageParticipantStatutHistorique>` |
| DELETE | `/api/stage-participant-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StageParticipantStatutHistorique stageParticipantStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody StageParticipantStatutHistorique stageParticipantStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## [StagiaireAcademiqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/StagiaireAcademiqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaires-academiques` | denyAll | 200 | `ResponseEntity<List<StagiaireAcademique>>` |
| GET | `/api/stagiaires-academiques/{id}` | denyAll | 200 | `ResponseEntity<StagiaireAcademique>` |
| POST | `/api/stagiaires-academiques` | denyAll | 201 | `ResponseEntity<StagiaireAcademique>` |
| PUT | `/api/stagiaires-academiques/{id}` | denyAll | 200 | `ResponseEntity<StagiaireAcademique>` |
| DELETE | `/api/stagiaires-academiques/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StagiaireAcademique stagiaireAcademique`.
- `update` : `@PathVariable Integer id, @RequestBody StagiaireAcademique stagiaireAcademique`.
- `delete` : `@PathVariable Integer id`.

## [StagiaireFichierController](../backend/src/main/java/mg/mef/stagemanagement/controller/StagiaireFichierController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaire-fichiers` | denyAll | 200 | `ResponseEntity<List<StagiaireFichier>>` |
| GET | `/api/stagiaire-fichiers/{id}` | denyAll | 200 | `ResponseEntity<StagiaireFichier>` |
| POST | `/api/stagiaire-fichiers` | denyAll | 201 | `ResponseEntity<StagiaireFichier>` |
| PUT | `/api/stagiaire-fichiers/{id}` | denyAll | 200 | `ResponseEntity<StagiaireFichier>` |
| DELETE | `/api/stagiaire-fichiers/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StagiaireFichier stagiaireFichier`.
- `update` : `@PathVariable Integer id, @RequestBody StagiaireFichier stagiaireFichier`.
- `delete` : `@PathVariable Integer id`.

## [StagiaireGestionController](../backend/src/main/java/mg/mef/stagemanagement/controller/StagiaireGestionController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaires` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<StagiaireResponse>>` |
| GET | `/api/stagiaires/{id}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<StagiaireResponse>` |
| POST | `/api/stagiaires` | ADMIN | 201 | `ResponseEntity<StagiaireResponse>` |
| PATCH | `/api/stagiaires/{id}` | ADMIN | 200 | `ResponseEntity<StagiaireResponse>` |
| GET | `/api/stagiaires/{id}/academiques` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<List<StagiaireAcademiqueResponse>>` |
| POST | `/api/stagiaires/{id}/academiques` | ADMIN | 201 | `ResponseEntity<StagiaireAcademiqueResponse>` |

- `listerStagiaires` : `aucun paramètre`.
- `trouverStagiaire` : `@PathVariable Integer id`.
- `creerStagiaire` : `@RequestBody StagiaireRequest request`.
- `modifierStagiaire` : `@PathVariable Integer id, @RequestBody StagiaireRequest request`.
- `listerAcademiques` : `@PathVariable Integer id`.
- `ajouterAcademique` : `@PathVariable Integer id, @RequestBody StagiaireAcademiqueRequest request`.

## [StagiaireStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/StagiaireStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaire-statut-historique` | denyAll | 200 | `ResponseEntity<List<StagiaireStatutHistorique>>` |
| GET | `/api/stagiaire-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<StagiaireStatutHistorique>` |
| POST | `/api/stagiaire-statut-historique` | denyAll | 201 | `ResponseEntity<StagiaireStatutHistorique>` |
| PUT | `/api/stagiaire-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<StagiaireStatutHistorique>` |
| DELETE | `/api/stagiaire-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StagiaireStatutHistorique stagiaireStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody StagiaireStatutHistorique stagiaireStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## [StatutController](../backend/src/main/java/mg/mef/stagemanagement/controller/StatutController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/statuts` | denyAll | 200 | `ResponseEntity<List<Statut>>` |
| GET | `/api/statuts/{id}` | denyAll | 200 | `ResponseEntity<Statut>` |
| POST | `/api/statuts` | denyAll | 201 | `ResponseEntity<Statut>` |
| PUT | `/api/statuts/{id}` | denyAll | 200 | `ResponseEntity<Statut>` |
| DELETE | `/api/statuts/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody Statut statut`.
- `update` : `@PathVariable Integer id, @RequestBody Statut statut`.
- `delete` : `@PathVariable Integer id`.

## [StructureAccueilController](../backend/src/main/java/mg/mef/stagemanagement/controller/StructureAccueilController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/structures-accueil` | denyAll | 200 | `ResponseEntity<List<StructureAccueil>>` |
| GET | `/api/structures-accueil/{id}` | denyAll | 200 | `ResponseEntity<StructureAccueil>` |
| POST | `/api/structures-accueil` | denyAll | 201 | `ResponseEntity<StructureAccueil>` |
| PUT | `/api/structures-accueil/{id}` | denyAll | 200 | `ResponseEntity<StructureAccueil>` |
| DELETE | `/api/structures-accueil/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody StructureAccueil structureAccueil`.
- `update` : `@PathVariable Integer id, @RequestBody StructureAccueil structureAccueil`.
- `delete` : `@PathVariable Integer id`.

## [SuiviIntegrationController](../backend/src/main/java/mg/mef/stagemanagement/controller/SuiviIntegrationController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/referentiels/suivi/types-documents` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<DocumentCatalogueService.TypeDocument>` |
| GET | `/api/referentiels/suivi/types-evenements` | ADMIN, ENCADREUR | 200 | `List<ReferentielWorkflowService.Reference>` |
| GET | `/api/referentiels/suivi/types-alertes` | ADMIN, ENCADREUR | 200 | `List<ReferentielWorkflowService.Reference>` |
| GET | `/api/referentiels/suivi/statuts/{type}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<ReferentielWorkflowService.Reference>` |
| GET | `/api/referentiels/suivi/transitions/{type}/{source}` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<ReferentielWorkflowService.Transition>` |
| GET | `/api/stage-participants/{id}/documents-obligatoires` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<DocumentCatalogueService.DocumentObligatoire>` |
| GET | `/api/stage-participants/{id}/historique-statuts` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<SuiviParticipationService.Historique>` |
| GET | `/api/stage-participants/{id}/evenements` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<SuiviParticipationService.Evenement>` |
| GET | `/api/stage-participants/{id}/periodes-rapports` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<SuiviParticipationService.Periode>` |
| GET | `/api/stage-participants/{id}/actions` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `List<SuiviParticipationService.Action>` |
| GET | `/api/encadreurs/{id}/charge` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `SuiviParticipationService.Charge` |

- `typesDocuments` : `aucun paramètre`.
- `typesEvenements` : `aucun paramètre`.
- `typesAlertes` : `aucun paramètre`.
- `statuts` : `@PathVariable String type`.
- `transitions` : `@PathVariable String type, @PathVariable String source`.
- `documentsObligatoires` : `@PathVariable Integer id`.
- `historique` : `@PathVariable Integer id`.
- `evenements` : `@PathVariable Integer id`.
- `periodes` : `@PathVariable Integer id`.
- `actions` : `@PathVariable Integer id`.
- `charge` : `@PathVariable Integer id`.

## [SyntheseController](../backend/src/main/java/mg/mef/stagemanagement/controller/SyntheseController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/stagiaires/{id}/dossier` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `DossierSyntheseService.Dossier` |
| GET | `/api/dashboard/pilotage` | ADMIN, ENCADREUR | 200 | `PilotageService.Pilotage` |
| GET | `/api/dashboard/priorites` | ADMIN, ENCADREUR | 200 | `List<PilotageService.Priorite>` |
| GET | `/api/stage-participants/{id}/bilan.pdf` | ADMIN, ENCADREUR, STAGIAIRE | 200 | `ResponseEntity<byte[]>` |
| GET | `/api/exports/participations.csv` | ADMIN, ENCADREUR | 200 | `ResponseEntity<byte[]>` |
| GET | `/api/exports/statistiques.csv` | ADMIN, ENCADREUR | 200 | `ResponseEntity<byte[]>` |
| GET | `/api/exports/alertes.csv` | ADMIN, ENCADREUR | 200 | `ResponseEntity<byte[]>` |

- `dossier` : `@PathVariable Integer id`.
- `pilotage` : `aucun paramètre`.
- `priorites` : `aucun paramètre`.
- `bilan` : `@PathVariable Integer id`.
- `participations` : `aucun paramètre`.
- `statistiques` : `aucun paramètre`.
- `alertes` : `aucun paramètre`.

## [TransitionStatutController](../backend/src/main/java/mg/mef/stagemanagement/controller/TransitionStatutController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/transitions-statut` | denyAll | 200 | `ResponseEntity<List<TransitionStatut>>` |
| GET | `/api/transitions-statut/{id}` | denyAll | 200 | `ResponseEntity<TransitionStatut>` |
| POST | `/api/transitions-statut` | denyAll | 201 | `ResponseEntity<TransitionStatut>` |
| PUT | `/api/transitions-statut/{id}` | denyAll | 200 | `ResponseEntity<TransitionStatut>` |
| DELETE | `/api/transitions-statut/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody TransitionStatut transitionStatut`.
- `update` : `@PathVariable Integer id, @RequestBody TransitionStatut transitionStatut`.
- `delete` : `@PathVariable Integer id`.

## [TypeAlerteController](../backend/src/main/java/mg/mef/stagemanagement/controller/TypeAlerteController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/types-alerte` | denyAll | 200 | `ResponseEntity<List<TypeAlerte>>` |
| GET | `/api/types-alerte/{id}` | denyAll | 200 | `ResponseEntity<TypeAlerte>` |
| POST | `/api/types-alerte` | denyAll | 201 | `ResponseEntity<TypeAlerte>` |
| PUT | `/api/types-alerte/{id}` | denyAll | 200 | `ResponseEntity<TypeAlerte>` |
| DELETE | `/api/types-alerte/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody TypeAlerte typeAlerte`.
- `update` : `@PathVariable Integer id, @RequestBody TypeAlerte typeAlerte`.
- `delete` : `@PathVariable Integer id`.

## [TypeEvenementParticipationController](../backend/src/main/java/mg/mef/stagemanagement/controller/TypeEvenementParticipationController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/types-evenement-participation` | denyAll | 200 | `ResponseEntity<List<TypeEvenementParticipation>>` |
| GET | `/api/types-evenement-participation/{id}` | denyAll | 200 | `ResponseEntity<TypeEvenementParticipation>` |
| POST | `/api/types-evenement-participation` | denyAll | 201 | `ResponseEntity<TypeEvenementParticipation>` |
| PUT | `/api/types-evenement-participation/{id}` | denyAll | 200 | `ResponseEntity<TypeEvenementParticipation>` |
| DELETE | `/api/types-evenement-participation/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody TypeEvenementParticipation typeEvenementParticipation`.
- `update` : `@PathVariable Integer id, @RequestBody TypeEvenementParticipation typeEvenementParticipation`.
- `delete` : `@PathVariable Integer id`.

## [TypePieceJointeController](../backend/src/main/java/mg/mef/stagemanagement/controller/TypePieceJointeController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/types-piece-jointe` | denyAll | 200 | `ResponseEntity<List<TypePieceJointe>>` |
| GET | `/api/types-piece-jointe/{id}` | denyAll | 200 | `ResponseEntity<TypePieceJointe>` |
| POST | `/api/types-piece-jointe` | denyAll | 201 | `ResponseEntity<TypePieceJointe>` |
| PUT | `/api/types-piece-jointe/{id}` | denyAll | 200 | `ResponseEntity<TypePieceJointe>` |
| DELETE | `/api/types-piece-jointe/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody TypePieceJointe typePieceJointe`.
- `update` : `@PathVariable Integer id, @RequestBody TypePieceJointe typePieceJointe`.
- `delete` : `@PathVariable Integer id`.

## [TypeStatutController](../backend/src/main/java/mg/mef/stagemanagement/controller/TypeStatutController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/types-statut` | denyAll | 200 | `ResponseEntity<List<TypeStatut>>` |
| GET | `/api/types-statut/{id}` | denyAll | 200 | `ResponseEntity<TypeStatut>` |
| POST | `/api/types-statut` | denyAll | 201 | `ResponseEntity<TypeStatut>` |
| PUT | `/api/types-statut/{id}` | denyAll | 200 | `ResponseEntity<TypeStatut>` |
| DELETE | `/api/types-statut/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody TypeStatut typeStatut`.
- `update` : `@PathVariable Integer id, @RequestBody TypeStatut typeStatut`.
- `delete` : `@PathVariable Integer id`.

## [UtilisateurController](../backend/src/main/java/mg/mef/stagemanagement/controller/UtilisateurController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/utilisateurs` | ADMIN | 200 | `ResponseEntity<List<UtilisateurListResponse>>` |
| GET | `/api/utilisateurs/{id}` | ADMIN | 200 | `ResponseEntity<UtilisateurListResponse>` |
| POST | `/api/utilisateurs` | ADMIN | 201 | `ResponseEntity<UtilisateurListResponse>` |
| PATCH | `/api/utilisateurs/{id}/statut` | ADMIN | 200 | `ResponseEntity<UtilisateurListResponse>` |
| PATCH | `/api/utilisateurs/{id}/mot-de-passe` | ADMIN | 204 | `ResponseEntity<Void>` |
| GET | `/api/utilisateurs/{id}/roles` | ADMIN | 200 | `ResponseEntity<List<String>>` |
| POST | `/api/utilisateurs/{id}/roles` | ADMIN | 200 | `ResponseEntity<List<String>>` |
| DELETE | `/api/utilisateurs/{id}/roles/{roleCode}` | ADMIN | 200 | `ResponseEntity<List<String>>` |
| GET | `/api/utilisateurs/{id}/departements` | ADMIN | 200 | `ResponseEntity<List<DepartementUtilisateurResponse>>` |
| POST | `/api/utilisateurs/{id}/departements` | ADMIN | 200 | `ResponseEntity<List<DepartementUtilisateurResponse>>` |
| DELETE | `/api/utilisateurs/{id}/departements/{departementId}` | ADMIN | 200 | `ResponseEntity<List<DepartementUtilisateurResponse>>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody CreerUtilisateurRequest request`.
- `changerStatut` : `@PathVariable Integer id, @RequestBody ChangerStatutUtilisateurRequest request, @AuthenticationPrincipal AuthMeResponse utilisateurConnecte`.
- `reinitialiserMotDePasse` : `@PathVariable Integer id, @RequestBody ReinitialiserMotDePasseRequest request`.
- `listerRoles` : `@PathVariable Integer id`.
- `ajouterRole` : `@PathVariable Integer id, @RequestBody AjouterRoleUtilisateurRequest request`.
- `retirerRole` : `@PathVariable Integer id, @PathVariable String roleCode`.
- `listerDepartements` : `@PathVariable Integer id`.
- `ajouterDepartement` : `@PathVariable Integer id, @RequestBody AjouterDepartementUtilisateurRequest request`.
- `retirerDepartement` : `@PathVariable Integer id, @PathVariable Integer departementId`.

## [UtilisateurRoleController](../backend/src/main/java/mg/mef/stagemanagement/controller/UtilisateurRoleController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/utilisateurs-role` | denyAll | 200 | `ResponseEntity<List<UtilisateurRole>>` |
| GET | `/api/utilisateurs-role/{id}` | denyAll | 200 | `ResponseEntity<UtilisateurRole>` |
| POST | `/api/utilisateurs-role` | denyAll | 201 | `ResponseEntity<UtilisateurRole>` |
| PUT | `/api/utilisateurs-role/{id}` | denyAll | 200 | `ResponseEntity<UtilisateurRole>` |
| DELETE | `/api/utilisateurs-role/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody UtilisateurRole utilisateurRole`.
- `update` : `@PathVariable Integer id, @RequestBody UtilisateurRole utilisateurRole`.
- `delete` : `@PathVariable Integer id`.

## [UtilisateurStatutHistoriqueController](../backend/src/main/java/mg/mef/stagemanagement/controller/UtilisateurStatutHistoriqueController.java)

| Méthode | URL | Autorisation | Succès | Réponse |
|---|---|---|---|---|
| GET | `/api/utilisateur-statut-historique` | denyAll | 200 | `ResponseEntity<List<UtilisateurStatutHistorique>>` |
| GET | `/api/utilisateur-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<UtilisateurStatutHistorique>` |
| POST | `/api/utilisateur-statut-historique` | denyAll | 201 | `ResponseEntity<UtilisateurStatutHistorique>` |
| PUT | `/api/utilisateur-statut-historique/{id}` | denyAll | 200 | `ResponseEntity<UtilisateurStatutHistorique>` |
| DELETE | `/api/utilisateur-statut-historique/{id}` | denyAll | 204 | `ResponseEntity<Void>` |

- `findAll` : `aucun paramètre`.
- `findById` : `@PathVariable Integer id`.
- `create` : `@RequestBody UtilisateurStatutHistorique utilisateurStatutHistorique`.
- `update` : `@PathVariable Integer id, @RequestBody UtilisateurStatutHistorique utilisateurStatutHistorique`.
- `delete` : `@PathVariable Integer id`.

## DTO — champs exacts

Les contraintes métier se trouvent principalement dans les services, sans @Valid dans les contrôleurs de gestion.

### [AlerteRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/alerte/AlerteRequest.java)

`typeAlerteCode: String`, `stageParticipantId: Integer`, `rapportId: Integer`, `encadreurId: Integer`, `soutenanceId: Integer`, `message: String`, `dateEcheance: LocalDate`, `priorite: String`, `cleDeduplication: String`

### [AlerteResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/alerte/AlerteResponse.java)

`id: Integer`, `typeAlerteCode: String`, `typeAlerteLibelle: String`, `actionRecommandee: String`, `stageParticipantId: Integer`, `rapportId: Integer`, `encadreurId: Integer`, `soutenanceId: Integer`, `message: String`, `dateEcheance: LocalDate`, `priorite: String`, `statut: String`, `dateDetection: LocalDateTime`, `dateTraitement: LocalDateTime`, `traiteParId: Integer`, `traitePar: String`, `natureAlerte: String`

### [JournalAuditResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/audit/JournalAuditResponse.java)

`id: Integer`, `utilisateurId: Integer`, `utilisateur: String`, `action: String`, `entite: String`, `entiteId: Integer`, `anciennesValeurs: Map<String, Object>`, `nouvellesValeurs: Map<String, Object>`, `dateAction: LocalDateTime`

### [AuthMeResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/auth/AuthMeResponse.java)

`utilisateurId: Integer`, `identifiant: String`, `roles: List<String>`

### [LoginRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/auth/LoginRequest.java)

`identifiant: String`, `motDePasse: String`

### [LoginResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/auth/LoginResponse.java)

`token: String`, `utilisateurId: Integer`, `identifiant: String`, `roles: List<String>`

### [DashboardResumeResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/dashboard/DashboardResumeResponse.java)

`totalStagiaires: long`, `totalStages: long`, `totalParticipations: long`, `totalRapports: long`, `totalAlertesATraiter: long`, `totalAlertesTraitees: long`

### [ChangerStatutEncadreurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/encadreur/ChangerStatutEncadreurRequest.java)

`statutCode: String`, `motif: String`

### [EncadreurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/encadreur/EncadreurRequest.java)

`utilisateurId: Integer`, `nom: String`, `prenoms: String`, `fonction: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `departementId: Integer`, `capaciteMaxStagiaires: Integer`

### [EncadreurResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/encadreur/EncadreurResponse.java)

`id: Integer`, `utilisateurId: Integer`, `identifiantUtilisateur: String`, `nom: String`, `prenoms: String`, `fonction: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `statut: String`, `departementId: Integer`, `departement: String`, `capaciteMaxStagiaires: Integer`

### [CritereEvaluationResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/evaluation/CritereEvaluationResponse.java)

`id: Integer`, `code: String`, `libelle: String`, `description: String`, `coefficient: BigDecimal`, `obligatoire: Boolean`, `ordre: Integer`

### [EvaluationCritereRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/evaluation/EvaluationCritereRequest.java)

`critereId: Integer`, `note: BigDecimal`, `commentaire: String`

### [EvaluationCritereResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/evaluation/EvaluationCritereResponse.java)

`id: Integer`, `evaluationId: Integer`, `critereId: Integer`, `critereCode: String`, `critereLibelle: String`, `coefficient: BigDecimal`, `note: BigDecimal`, `commentaire: String`

### [EvaluationFinaleRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/evaluation/EvaluationFinaleRequest.java)

`stageParticipantId: Integer`, `encadreurId: Integer`, `appreciationGlobale: String`, `observationsFinales: String`

### [EvaluationFinaleResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/evaluation/EvaluationFinaleResponse.java)

`id: Integer`, `stageParticipantId: Integer`, `stagiaire: String`, `encadreurId: Integer`, `encadreur: String`, `appreciationGlobale: String`, `observationsFinales: String`, `estFinalisee: Boolean`, `dateEvaluation: LocalDateTime`, `dateFinalisation: LocalDateTime`

### [FichierResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/fichier/FichierResponse.java)

`id: Integer`, `nomOriginal: String`, `typeMime: String`, `tailleOctets: Long`, `typePieceJointeCode: String`, `typePieceJointeLibelle: String`, `dateAjout: LocalDateTime`, `dateAssociation: LocalDateTime`

### [FichierTelechargementResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/fichier/FichierTelechargementResponse.java)

`resource: Resource`, `nomOriginal: String`, `typeMime: String`

### [ChangerStatutRapportRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/rapport/ChangerStatutRapportRequest.java)

`statutCode: String`, `motif: String`

### [RapportActiviteRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/rapport/RapportActiviteRequest.java)

`stageParticipantId: Integer`, `debutSemaine: LocalDate`, `finSemaine: LocalDate`, `activite: String`, `difficultes: String`, `solutions: String`, `moisStageId: Integer`

### [RapportActiviteResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/rapport/RapportActiviteResponse.java)

`peutRediger: boolean`, `peutExaminer: boolean`, `id: Integer`, `stageParticipantId: Integer`, `stagiaire: String`, `encadreur: String`, `debutSemaine: LocalDate`, `finSemaine: LocalDate`, `activite: String`, `difficultes: String`, `solutions: String`, `moisStageId: Integer`, `moisStage: String`, `statut: String`, `dateCreation: LocalDateTime`, `dateModification: LocalDateTime`, `dateSoumission: LocalDateTime`, `dateValidation: LocalDateTime`

### [RapportObservationRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/rapport/RapportObservationRequest.java)

`typeObservation: String`, `commentaire: String`, `visibleStagiaire: Boolean`

### [RapportObservationResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/rapport/RapportObservationResponse.java)

`id: Integer`, `rapportId: Integer`, `auteurId: Integer`, `auteur: String`, `typeObservation: String`, `commentaire: String`, `visibleStagiaire: Boolean`, `dateCreation: LocalDateTime`

### [CadreStageRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/CadreStageRequest.java)

`themeStage: String`, `activiteConfieStagiaire: String`, `objectifStage: String`, `actif: Boolean`

### [CadreStageResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/CadreStageResponse.java)

`id: Integer`, `themeStage: String`, `activiteConfieStagiaire: String`, `objectifStage: String`, `actif: Boolean`

### [DepartementRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/DepartementRequest.java)

`libelle: String`, `statutCode: String`

### [DepartementResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/DepartementResponse.java)

`id: Integer`, `libelle: String`, `statut: String`

### [ReferentielRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/ReferentielRequest.java)

`libelle: String`, `actif: Boolean`

### [ReferentielResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/ReferentielResponse.java)

`id: Integer`, `libelle: String`, `actif: Boolean`

### [StructureAccueilRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/StructureAccueilRequest.java)

`departementId: Integer`, `libelle: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `actif: Boolean`

### [StructureAccueilResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/referentiel/StructureAccueilResponse.java)

`id: Integer`, `departementId: Integer`, `departement: String`, `libelle: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `actif: Boolean`

### [SoutenanceRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/soutenance/SoutenanceRequest.java)

`stageParticipantId: Integer`, `dateSoutenance: LocalDateTime`, `lieu: String`, `observation: String`

### [SoutenanceResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/soutenance/SoutenanceResponse.java)

`id: Integer`, `stageParticipantId: Integer`, `stagiaire: String`, `encadreur: String`, `dateSoutenance: LocalDateTime`, `lieu: String`, `observation: String`, `dateCreation: LocalDateTime`

### [ChangerStatutParticipationRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/ChangerStatutParticipationRequest.java)

`statutCode: String`, `motif: String`

### [EvenementParticipationRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/EvenementParticipationRequest.java)

`motif: String`, `dateEffet: LocalDate`, `ignorerDocumentsManquants: Boolean`, `forcerCloture: Boolean`

### [ProlongerParticipationRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/ProlongerParticipationRequest.java)

`nouvelleDateFin: LocalDate`, `motif: String`

### [StageParticipantRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/StageParticipantRequest.java)

`stageId: Integer`, `stagiaireAcademiqueId: Integer`, `cadreStageId: Integer`, `encadreurId: Integer`, `dateDebutStage: LocalDate`, `dateFinStage: LocalDate`

### [StageParticipantResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/StageParticipantResponse.java)

`peutRedigerRapport: boolean`, `id: Integer`, `stageId: Integer`, `stage: String`, `stagiaireAcademiqueId: Integer`, `stagiaireId: Integer`, `stagiaireNomComplet: String`, `niveauEtude: String`, `filiere: String`, `etablissement: String`, `anneeEtudes: String`, `cadreStageId: Integer`, `themeStage: String`, `encadreurId: Integer`, `encadreurNomComplet: String`, `dateDebutStage: LocalDate`, `dateFinStage: LocalDate`, `statut: String`, `dateCreation: LocalDateTime`, `dateModification: LocalDateTime`, `dateCloture: LocalDateTime`

### [StageRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/StageRequest.java)

`libelle: String`, `structureAccueilId: Integer`

### [StageResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/stage/StageResponse.java)

`id: Integer`, `libelle: String`, `structureAccueilId: Integer`, `structureAccueil: String`, `departementId: Integer`, `departement: String`, `dateCreation: LocalDateTime`, `eligibleNouvelleParticipation: boolean`

### [StagiaireAcademiqueRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stagiaire/StagiaireAcademiqueRequest.java)

`niveauEtudeId: Integer`, `filiereId: Integer`, `etablissementId: Integer`, `anneeEtudes: String`

### [StagiaireAcademiqueResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/stagiaire/StagiaireAcademiqueResponse.java)

`id: Integer`, `stagiaireId: Integer`, `niveauEtudeId: Integer`, `niveauEtude: String`, `filiereId: Integer`, `filiere: String`, `etablissementId: Integer`, `etablissement: String`, `anneeEtudes: String`

### [StagiaireRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/stagiaire/StagiaireRequest.java)

`nom: String`, `prenoms: String`, `adresse: String`, `contact: String`, `email: String`

### [StagiaireResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/stagiaire/StagiaireResponse.java)

`id: Integer`, `nom: String`, `prenoms: String`, `adresse: String`, `contact: String`, `email: String`, `statut: String`, `dateCreation: LocalDateTime`

### [AjouterDepartementUtilisateurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/AjouterDepartementUtilisateurRequest.java)

`departementId: Integer`

### [AjouterRoleUtilisateurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/AjouterRoleUtilisateurRequest.java)

`roleCode: String`

### [ChangerStatutUtilisateurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/ChangerStatutUtilisateurRequest.java)

`statutCode: String`, `motif: String`

### [CreerUtilisateurRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/CreerUtilisateurRequest.java)

`identifiant: String`, `motDePasse: String`, `roleCode: String`

### [DepartementUtilisateurResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/DepartementUtilisateurResponse.java)

`id: Integer`, `libelle: String`, `statut: String`

### [ReinitialiserMotDePasseRequest](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/ReinitialiserMotDePasseRequest.java)

`nouveauMotDePasse: String`

### [UtilisateurListResponse](../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/UtilisateurListResponse.java)

`id: Integer`, `identifiant: String`, `statut: String`

### [CalculPeriodesRapportService.PeriodeRapport](../backend/src/main/java/mg/mef/stagemanagement/service/CalculPeriodesRapportService.java)

`LocalDate debut`, `LocalDate fin`, `LocalDate dateLimite`

### [DocumentCatalogueService.TypeDocument](../backend/src/main/java/mg/mef/stagemanagement/service/DocumentCatalogueService.java)

`Integer id`, `String code`, `String libelle`, `boolean pourStagiaire`, `boolean pourParticipation`, `boolean obligatoireCloture`, `boolean multipleAutorise`

### [DocumentCatalogueService.DocumentObligatoire](../backend/src/main/java/mg/mef/stagemanagement/service/DocumentCatalogueService.java)

`Integer id`, `String code`, `String libelle`, `boolean present`

### [DocumentContentValidator.Validated](../backend/src/main/java/mg/mef/stagemanagement/service/DocumentContentValidator.java)

`String filename`, `String extension`, `String mime`, `byte[] bytes`

### [DossierSyntheseService.Rapport](../backend/src/main/java/mg/mef/stagemanagement/service/DossierSyntheseService.java)

`RapportActiviteResponse rapport`, `List<RapportObservationResponse> observations`

### [DossierSyntheseService.Participation](../backend/src/main/java/mg/mef/stagemanagement/service/DossierSyntheseService.java)

`StageParticipantResponse participation`, `List<Rapport> rapports`, `List<SuiviParticipationService.Periode> periodes`, `List<FichierResponse> fichiers`, `List<DocumentCatalogueService.DocumentObligatoire> documentsObligatoires`, `List<SuiviParticipationService.Historique> historique`, `List<SuiviParticipationService.Evenement> evenements`, `EvaluationFinaleResponse evaluation`, `List<EvaluationCritereResponse> notes`, `List<SoutenanceResponse> soutenances`

### [DossierSyntheseService.Dossier](../backend/src/main/java/mg/mef/stagemanagement/service/DossierSyntheseService.java)

`StagiaireResponse stagiaire`, `List<StagiaireAcademiqueResponse> academiques`, `List<FichierResponse> fichiers`, `List<Participation> participations`

### [PilotageService.Priorite](../backend/src/main/java/mg/mef/stagemanagement/service/PilotageService.java)

`Integer participationId`, `Integer stagiaireId`, `String stagiaire`, `long nombreAlertes`, `long score`, `String priorite`, `String situation`, `String actionRecommandee`

### [PilotageService.Dossier](../backend/src/main/java/mg/mef/stagemanagement/service/PilotageService.java)

`Integer participationId`, `String stagiaire`, `String stage`, `String statut`, `LocalDate dateFin`, `long joursRestants`, `long documentsManquants`, `long rapportsAttendus`, `long rapportsSoumis`, `long rapportsValides`, `long rapportsManquants`

### [PilotageService.Charge](../backend/src/main/java/mg/mef/stagemanagement/service/PilotageService.java)

`Integer encadreurId`, `String encadreur`, `Integer capaciteMax`, `long participationsActives`, `boolean surcharge`

### [PilotageService.Pilotage](../backend/src/main/java/mg/mef/stagemanagement/service/PilotageService.java)

`LocalDate dateCalcul`, `List<Dossier> dossiers`, `List<Priorite> priorites`, `List<Charge> charges`, `List<SoutenanceResponse> soutenancesProchaines`

### [PortailStagiaireService.Compte](../backend/src/main/java/mg/mef/stagemanagement/service/PortailStagiaireService.java)

`Integer utilisateurId`, `String identifiant`

### [PortailStagiaireService.Profil](../backend/src/main/java/mg/mef/stagemanagement/service/PortailStagiaireService.java)

`Integer stagiaireId`

### [ReferentielWorkflowService.Reference](../backend/src/main/java/mg/mef/stagemanagement/service/ReferentielWorkflowService.java)

`Integer id`, `String code`, `String libelle`

### [ReferentielWorkflowService.Transition](../backend/src/main/java/mg/mef/stagemanagement/service/ReferentielWorkflowService.java)

`Integer id`, `String source`, `String cible`, `String libelle`

### [SuiviParticipationService.Historique](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

`Integer id`, `String ancienStatut`, `String statut`, `LocalDateTime dateModification`, `String motif`, `Integer responsableId`

### [SuiviParticipationService.Evenement](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

`Integer id`, `String code`, `String libelle`, `LocalDateTime dateEvenement`, `LocalDate dateEffet`, `LocalDate ancienneDateFin`, `LocalDate nouvelleDateFin`, `String motif`, `Integer responsableId`

### [SuiviParticipationService.Periode](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

`LocalDate debut`, `LocalDate fin`, `LocalDate dateLimite`, `Integer rapportId`, `String statutRapport`, `boolean soumise`, `boolean validee`, `boolean exigible`, `boolean manquante`

### [SuiviParticipationService.Action](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

`String code`, `String label`, `String statutCible`, `boolean dateEffet`

### [SuiviParticipationService.Charge](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

`Integer encadreurId`, `Integer capaciteMax`, `Long participationsActives`, `boolean surcharge`

## Erreurs explicites des services

Catalogue par service, à rapprocher des méthodes appelées par chaque contrôleur. Les exceptions de contraintes PostgreSQL et les erreurs de conversion Spring s’y ajoutent. Les messages dynamiques sont tronqués à leur partie littérale dans ce catalogue.

### [AlerteGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/AlerteGestionService.java)

- `BAD_REQUEST` : Les informations de l'alerte sont obligatoires
- `BAD_REQUEST` : La clé de déduplication est limitée à 255 caractères
- `CONFLICT` : Cette alerte existe déjà
- `NOT_FOUND` : Participation introuvable
- `NOT_FOUND` : Rapport introuvable
- `NOT_FOUND` : Encadreur introuvable
- `NOT_FOUND` : Soutenance introuvable
- `NOT_FOUND` : Alerte introuvable
- `CONFLICT` : Une alerte informative ne peut pas être résolue manuellement
- `CONFLICT` : Cette alerte n'est plus à traiter
- `UNAUTHORIZED` : Utilisateur connecté introuvable
- `CONFLICT` : Le traitement global des alertes est désactivé. 
- `CONFLICT` : Le rapport ne correspond pas à la participation sélectionnée
- `CONFLICT` : La soutenance ne correspond pas au dossier sélectionné
- `BAD_REQUEST` : Associez l'alerte à une participation, un rapport, un encadreur ou une soutenance
- `CONFLICT` : L'encadreur ne correspond pas au dossier sélectionné
- `BAD_REQUEST` : Le type d'alerte est obligatoire
- `NOT_FOUND` : Type d'alerte introuvable
- `BAD_REQUEST` : Priorité d'alerte invalide

### [AuthService](../backend/src/main/java/mg/mef/stagemanagement/service/AuthService.java)

- `UNAUTHORIZED` : Identifiant ou mot de passe incorrect
- `FORBIDDEN` : Ce compte n'est pas actif
- `UNAUTHORIZED` : Session invalide ou expirée

### [DocumentCatalogueService](../backend/src/main/java/mg/mef/stagemanagement/service/DocumentCatalogueService.java)

- `NOT_FOUND` : Participation introuvable
- `CONFLICT` : Documents obligatoires manquants : 

### [DocumentContentValidator](../backend/src/main/java/mg/mef/stagemanagement/service/DocumentContentValidator.java)

- `BAD_REQUEST` : Fichier obligatoire
- `PAYLOAD_TOO_LARGE` : Maximum 10 Mio
- `UNSUPPORTED_MEDIA_TYPE` : Contenu documentaire invalide
- `UNSUPPORTED_MEDIA_TYPE` : Formats autorises : PDF sans contenu actif detecte, JPEG et PNG ; extension, contenu et type MIME doivent correspondre

### [DossierAccessService](../backend/src/main/java/mg/mef/stagemanagement/service/DossierAccessService.java)

- `UNAUTHORIZED` : Session requise
- `FORBIDDEN` : Acces hors de votre perimetre
- `NOT_FOUND` : Ressource introuvable
- `BAD_REQUEST` : Structure obligatoire
- `BAD_REQUEST` : Affectation incomplete

### [EncadreurGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/EncadreurGestionService.java)

- `NOT_FOUND` : Encadreur introuvable
- `INTERNAL_SERVER_ERROR` : Statut ACTIF des encadreurs introuvable
- `BAD_REQUEST` : Le nouveau statut est obligatoire
- `UNAUTHORIZED` : Responsable introuvable
- `BAD_REQUEST` : Statut encadreur invalide
- `BAD_REQUEST` : Les informations de l'encadreur sont obligatoires
- `BAD_REQUEST` : La capacité maximale doit être positive
- `BAD_REQUEST` : Le département est obligatoire
- `NOT_FOUND` : Département introuvable
- `NOT_FOUND` : Utilisateur introuvable
- `CONFLICT` : Ce compte est déjà associé à un encadreur
- `CONFLICT` : Un compte possédant le rôle ENCADREUR actif est requis
- `BAD_REQUEST` : Le texte est trop long

### [EvaluationGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/EvaluationGestionService.java)

- `NOT_FOUND` : Évaluation introuvable
- `NOT_FOUND` : Évaluation introuvable pour cette participation
- `CONFLICT` : Une évaluation existe déjà pour cette participation
- `NOT_FOUND` : Participation introuvable
- `NOT_FOUND` : Encadreur introuvable
- `BAD_REQUEST` : Le critère est obligatoire
- `NOT_FOUND` : Critère introuvable
- `BAD_REQUEST` : Ce critère n'est pas actif
- `CONFLICT` : Tous les critères obligatoires doivent être notés avant finalisation
- `BAD_REQUEST` : Les informations de l'évaluation sont incomplètes
- `CONFLICT` : Une évaluation finalisée ne peut plus être modifiée
- `BAD_REQUEST` : La note est obligatoire
- `BAD_REQUEST` : La note accepte au maximum deux décimales
- `BAD_REQUEST` : La note doit être comprise entre 0 et 5

### [FichierGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/FichierGestionService.java)

- `NOT_FOUND` : Stagiaire introuvable
- `BAD_REQUEST` : Ce type de pièce jointe n'est pas autorisé pour un stagiaire
- `NOT_FOUND` : Participation introuvable
- `BAD_REQUEST` : Ce type de pièce jointe n'est pas autorisé pour une participation
- `NOT_FOUND` : Fichier introuvable
- `NOT_FOUND` : Fichier physique introuvable
- `FORBIDDEN` : Fichier hors du stockage autorise
- `NOT_FOUND` : Fichier indisponible
- `INTERNAL_SERVER_ERROR` : Erreur lors de l'enregistrement du fichier
- `BAD_REQUEST` : Le type de pièce jointe est obligatoire
- `NOT_FOUND` : Type de pièce jointe introuvable
- `UNAUTHORIZED` : Utilisateur connecté introuvable
- `CONFLICT` : Ce type de fichier existe déjà pour ce stagiaire
- `CONFLICT` : Ce type de fichier existe déjà pour cette participation

### [JournalAuditService](../backend/src/main/java/mg/mef/stagemanagement/service/JournalAuditService.java)

- `NOT_FOUND` : Utilisateur introuvable
- `BAD_REQUEST` : L'entité et son identifiant sont obligatoires

### [PortailStagiaireService](../backend/src/main/java/mg/mef/stagemanagement/service/PortailStagiaireService.java)

- `NOT_FOUND` : Compte introuvable
- `CONFLICT` : Un compte stagiaire actif est requis
- `CONFLICT` : Ce compte est deja associe a un autre stagiaire

### [RapportActiviteGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/RapportActiviteGestionService.java)

- `NOT_FOUND` : Rapport introuvable
- `NOT_FOUND` : Participation introuvable
- `BAD_REQUEST` : La fin de semaine doit être après le début de semaine
- `CONFLICT` : Un rapport existe déjà pour cette période
- `NOT_FOUND` : Mois de stage introuvable
- `INTERNAL_SERVER_ERROR` : Statut BROUILLON du rapport introuvable
- `CONFLICT` : Un rapport validé ne peut plus être modifié
- `BAD_REQUEST` : Les informations de l'observation sont obligatoires
- `UNAUTHORIZED` : Auteur introuvable
- `BAD_REQUEST` : Le statut est obligatoire
- `UNAUTHORIZED` : Responsable introuvable
- `BAD_REQUEST` : Statut de rapport invalide
- `BAD_REQUEST` : Les informations du rapport sont incomplètes
- `BAD_REQUEST` : Type d'observation invalide

### [ReferentielAcademiqueService](../backend/src/main/java/mg/mef/stagemanagement/service/ReferentielAcademiqueService.java)

- `CONFLICT` : Cet établissement existe déjà
- `NOT_FOUND` : Établissement introuvable
- `CONFLICT` : Cette filière existe déjà
- `NOT_FOUND` : Filière introuvable
- `CONFLICT` : Ce niveau d'étude existe déjà
- `NOT_FOUND` : Niveau d'étude introuvable
- `CONFLICT` : Ce mois de stage existe déjà
- `NOT_FOUND` : Mois de stage introuvable
- `BAD_REQUEST` : Le libellé est obligatoire
- `BAD_REQUEST` : Le libellé est trop long

### [ReferentielOrganisationService](../backend/src/main/java/mg/mef/stagemanagement/service/ReferentielOrganisationService.java)

- `CONFLICT` : Ce département existe déjà
- `NOT_FOUND` : Département introuvable
- `BAD_REQUEST` : Le département est obligatoire
- `CONFLICT` : Cette structure existe déjà dans ce département
- `NOT_FOUND` : Structure d'accueil introuvable
- `NOT_FOUND` : Cadre de stage introuvable
- `BAD_REQUEST` : Statut de département invalide
- `BAD_REQUEST` : Le texte est trop long

### [ReferentielWorkflowService](../backend/src/main/java/mg/mef/stagemanagement/service/ReferentielWorkflowService.java)

- `NOT_FOUND` : Type de statut introuvable ou inactif
- `NOT_FOUND` : Statut source introuvable ou inactif

### [SoutenanceGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/SoutenanceGestionService.java)

- `NOT_FOUND` : Soutenance introuvable
- `CONFLICT` : Une soutenance existe déjà pour cette participation
- `NOT_FOUND` : Participation introuvable
- `BAD_REQUEST` : Les informations de soutenance sont incomplètes
- `BAD_REQUEST` : Le texte est trop long

### [StageGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/StageGestionService.java)

- `NOT_FOUND` : Stage introuvable
- `BAD_REQUEST` : La structure d'accueil est obligatoire
- `NOT_FOUND` : Structure d'accueil introuvable
- `CONFLICT` : Ce stage existe déjà dans cette structure d'accueil
- `NOT_FOUND` : Participation introuvable
- `BAD_REQUEST` : La date de fin doit être après la date de début
- `NOT_FOUND` : Profil académique introuvable
- `NOT_FOUND` : Cadre de stage introuvable
- `NOT_FOUND` : Encadreur introuvable
- `CONFLICT` : Cette participation existe déjà pour ce stagiaire et cette date de début
- `INTERNAL_SERVER_ERROR` : Statut PLANIFIEE introuvable
- `BAD_REQUEST` : Les informations de participation sont incomplètes
- `BAD_REQUEST` : Le texte est trop long
- `BAD_REQUEST` : Le nouveau statut est obligatoire
- `UNAUTHORIZED` : Responsable introuvable
- `BAD_REQUEST` : Statut de participation invalide
- `BAD_REQUEST` : La nouvelle date de fin est obligatoire
- `BAD_REQUEST` : La nouvelle date de fin doit être après l'ancienne date de fin
- `INTERNAL_SERVER_ERROR` : Type d'événement PROLONGATION introuvable
- `INTERNAL_SERVER_ERROR` : Type d'événement introuvable : 
- `INTERNAL_SERVER_ERROR` : Statut de participation introuvable : 
- `CONFLICT` : Type evenement inactif
- `CONFLICT` : Participation deja cloturee
- `BAD_REQUEST` : La date d'effet est obligatoire pour une suspension ou une reprise
- `BAD_REQUEST` : La date d'effet doit appartenir à la période du stage
- `CONFLICT` : Seule une participation en cours peut être suspendue
- `CONFLICT` : Seule une participation suspendue peut reprendre
- `BAD_REQUEST` : La date d'effet ne peut pas être dans le futur
- `CONFLICT` : La date d'effet doit être postérieure à celle du dernier événement de suspension ou de reprise
- `CONFLICT` : Transition de statut non autorisee

### [StagiaireGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/StagiaireGestionService.java)

- `NOT_FOUND` : Stagiaire introuvable
- `INTERNAL_SERVER_ERROR` : Statut ACTIF des stagiaires introuvable
- `BAD_REQUEST` : Les informations académiques sont incomplètes
- `CONFLICT` : Cette information académique existe déjà pour ce stagiaire
- `NOT_FOUND` : Niveau d'étude introuvable
- `NOT_FOUND` : Filière introuvable
- `NOT_FOUND` : Établissement introuvable
- `BAD_REQUEST` : Les informations du stagiaire sont obligatoires
- `BAD_REQUEST` : L'année d'études est obligatoire
- `BAD_REQUEST` : L'année d'études doit être au format 2025-2026
- `BAD_REQUEST` : Le texte est trop long

### [SuiviParticipationService](../backend/src/main/java/mg/mef/stagemanagement/service/SuiviParticipationService.java)

- `NOT_FOUND` : Participation introuvable
- `NOT_FOUND` : Encadreur introuvable
- `SERVICE_UNAVAILABLE` : Charge indisponible
- `SERVICE_UNAVAILABLE` : Capacite non configuree

### [UtilisateurGestionService](../backend/src/main/java/mg/mef/stagemanagement/service/UtilisateurGestionService.java)

- `BAD_REQUEST` : Informations obligatoires manquantes
- `BAD_REQUEST` : Identifiant, mot de passe ou rôle invalide
- `CONFLICT` : Cet identifiant existe déjà
- `BAD_REQUEST` : Rôle introuvable
- `BAD_REQUEST` : Ce rôle est désactivé
- `INTERNAL_SERVER_ERROR` : Statut ACTIF des utilisateurs introuvable
- `NOT_FOUND` : Utilisateur introuvable
- `BAD_REQUEST` : Le nouveau statut est obligatoire
- `UNAUTHORIZED` : Responsable introuvable
- `BAD_REQUEST` : Statut utilisateur invalide
- `BAD_REQUEST` : Le nouveau mot de passe doit contenir au moins 12 caractères
- `BAD_REQUEST` : Le rôle est obligatoire
- `CONFLICT` : Dissociez d'abord le compte de sa fiche métier avant de retirer ce rôle
- `NOT_FOUND` : Ce rôle n'est pas attribué à cet utilisateur
- `BAD_REQUEST` : Un utilisateur doit conserver au moins un rôle actif
- `BAD_REQUEST` : Le département est obligatoire
- `NOT_FOUND` : Département introuvable
- `NOT_FOUND` : Ce département n'est pas attribué à cet utilisateur
- `BAD_REQUEST` : Rôle attendu : ADMIN, ENCADREUR ou STAGIAIRE

## Modèles — relations et stockage

Ces champs ne sont pas tous des champs de DTO et ne doivent pas être envoyés aux API de gestion.

- [Alerte](../backend/src/main/java/mg/mef/stagemanagement/model/Alerte.java) : `id: Integer`, `typeAlerte: TypeAlerte`, `stageParticipant: StageParticipant`, `rapport: RapportActivite`, `encadreur: Encadreur`, `soutenance: Soutenance`, `message: String`, `dateEcheance: LocalDate`, `priorite: AlertePriorite`, `statut: AlerteStatut`, `dateDetection: LocalDateTime`, `dateTraitement: LocalDateTime`, `traitePar: Utilisateur`, `cleDeduplication: String`
- [CadreStage](../backend/src/main/java/mg/mef/stagemanagement/model/CadreStage.java) : `id: Integer`, `themeStage: String`, `activiteConfieStagiaire: String`, `objectifStage: String`, `actif: Boolean`
- [CritereEvaluation](../backend/src/main/java/mg/mef/stagemanagement/model/CritereEvaluation.java) : `id: Integer`, `code: String`, `libelle: String`, `description: String`, `coefficient: BigDecimal`, `obligatoire: Boolean`, `ordre: Integer`, `actif: Boolean`
- [Departement](../backend/src/main/java/mg/mef/stagemanagement/model/Departement.java) : `id: Integer`, `libelle: String`, `statut: Statut`
- [DepartementStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/DepartementStatutHistorique.java) : `id: Integer`, `departement: Departement`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`
- [Encadreur](../backend/src/main/java/mg/mef/stagemanagement/model/Encadreur.java) : `id: Integer`, `utilisateur: Utilisateur`, `nom: String`, `prenoms: String`, `fonction: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `statut: Statut`, `departement: Departement`, `capaciteMaxStagiaires: Integer`
- [EncadreurStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/EncadreurStatutHistorique.java) : `id: Integer`, `encadreur: Encadreur`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`
- [Etablissement](../backend/src/main/java/mg/mef/stagemanagement/model/Etablissement.java) : `id: Integer`, `libelle: String`, `actif: Boolean`
- [EvaluationCritere](../backend/src/main/java/mg/mef/stagemanagement/model/EvaluationCritere.java) : `id: Integer`, `evaluation: EvaluationFinale`, `critere: CritereEvaluation`, `note: BigDecimal`, `commentaire: String`
- [EvaluationFinale](../backend/src/main/java/mg/mef/stagemanagement/model/EvaluationFinale.java) : `id: Integer`, `stageParticipant: StageParticipant`, `encadreur: Encadreur`, `appreciationGlobale: String`, `observationsFinales: String`, `estFinalisee: Boolean`, `dateEvaluation: LocalDateTime`, `dateFinalisation: LocalDateTime`
- [Fichier](../backend/src/main/java/mg/mef/stagemanagement/model/Fichier.java) : `id: Integer`, `nomOriginal: String`, `nomStockage: String`, `chemin: String`, `typeMime: String`, `tailleOctets: Long`, `utilisateur: Utilisateur`, `dateAjout: LocalDateTime`
- [Filiere](../backend/src/main/java/mg/mef/stagemanagement/model/Filiere.java) : `id: Integer`, `libelle: String`, `actif: Boolean`
- [HistoriqueConnexionUtilisateur](../backend/src/main/java/mg/mef/stagemanagement/model/HistoriqueConnexionUtilisateur.java) : `id: Integer`, `utilisateur: Utilisateur`, `dateConnexion: LocalDateTime`
- [JournalAudit](../backend/src/main/java/mg/mef/stagemanagement/model/JournalAudit.java) : `id: Integer`, `utilisateur: Utilisateur`, `action: String`, `entite: String`, `entiteId: Integer`, `anciennesValeurs: Map<String, Object>`, `nouvellesValeurs: Map<String, Object>`, `dateAction: LocalDateTime`
- [MoisStage](../backend/src/main/java/mg/mef/stagemanagement/model/MoisStage.java) : `id: Integer`, `libelle: String`, `actif: Boolean`
- [NiveauEtude](../backend/src/main/java/mg/mef/stagemanagement/model/NiveauEtude.java) : `id: Integer`, `libelle: String`, `actif: Boolean`
- [ParametresSuivi](../backend/src/main/java/mg/mef/stagemanagement/model/ParametresSuivi.java) : `id: Short`, `delaiValidationRapportJours: Integer`, `capaciteEncadreurParDefaut: Integer`, `tailleMaxFichierOctets: Long`
- [RapportActivite](../backend/src/main/java/mg/mef/stagemanagement/model/RapportActivite.java) : `id: Integer`, `stageParticipant: StageParticipant`, `debutSemaine: LocalDate`, `finSemaine: LocalDate`, `activite: String`, `difficultes: String`, `solutions: String`, `moisStage: MoisStage`, `statut: Statut`, `dateCreation: LocalDateTime`, `dateModification: LocalDateTime`, `dateSoumission: LocalDateTime`, `dateValidation: LocalDateTime`
- [RapportActiviteStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/RapportActiviteStatutHistorique.java) : `id: Integer`, `rapport: RapportActivite`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`
- [RapportObservation](../backend/src/main/java/mg/mef/stagemanagement/model/RapportObservation.java) : `id: Integer`, `rapport: RapportActivite`, `auteur: Utilisateur`, `typeObservation: TypeObservationRapport`, `commentaire: String`, `visibleStagiaire: Boolean`, `dateCreation: LocalDateTime`
- [ResponsableDepartement](../backend/src/main/java/mg/mef/stagemanagement/model/ResponsableDepartement.java) : `id: Integer`, `utilisateur: Utilisateur`, `departement: Departement`
- [Role](../backend/src/main/java/mg/mef/stagemanagement/model/Role.java) : `id: Integer`, `code: String`, `libelle: String`, `actif: Boolean`
- [SessionUtilisateur](../backend/src/main/java/mg/mef/stagemanagement/model/SessionUtilisateur.java) : `id: Integer`, `utilisateur: Utilisateur`, `jetonHash: String`, `dateCreation: LocalDateTime`, `dateExpiration: LocalDateTime`, `dateRevocation: LocalDateTime`
- [SeuilAlerte](../backend/src/main/java/mg/mef/stagemanagement/model/SeuilAlerte.java) : `id: Integer`, `typeAlerte: TypeAlerte`, `joursAvant: Integer`, `priorite: AlertePriorite`, `actif: Boolean`
- [Soutenance](../backend/src/main/java/mg/mef/stagemanagement/model/Soutenance.java) : `id: Integer`, `stageParticipant: StageParticipant`, `dateSoutenance: LocalDateTime`, `lieu: String`, `observation: String`, `dateCreation: LocalDateTime`
- [Stage](../backend/src/main/java/mg/mef/stagemanagement/model/Stage.java) : `id: Integer`, `libelle: String`, `structureAccueil: StructureAccueil`, `dateCreation: LocalDateTime`
- [StageParticipant](../backend/src/main/java/mg/mef/stagemanagement/model/StageParticipant.java) : `id: Integer`, `stage: Stage`, `stagiaireAcademique: StagiaireAcademique`, `cadreStage: CadreStage`, `dateDebutStage: LocalDate`, `dateFinStage: LocalDate`, `encadreur: Encadreur`, `statut: Statut`, `dateCloture: LocalDateTime`, `dateCreation: LocalDateTime`, `dateModification: LocalDateTime`
- [StageParticipantEvenement](../backend/src/main/java/mg/mef/stagemanagement/model/StageParticipantEvenement.java) : `id: Integer`, `stageParticipant: StageParticipant`, `typeEvenement: TypeEvenementParticipation`, `dateEvenement: LocalDateTime`, `dateEffet: LocalDate`, `ancienneDateFin: LocalDate`, `nouvelleDateFin: LocalDate`, `motif: String`, `responsable: Utilisateur`
- [StageParticipantFichier](../backend/src/main/java/mg/mef/stagemanagement/model/StageParticipantFichier.java) : `id: Integer`, `stageParticipant: StageParticipant`, `fichier: Fichier`, `typePieceJointe: TypePieceJointe`, `dateAssociation: LocalDateTime`
- [StageParticipantStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/StageParticipantStatutHistorique.java) : `id: Integer`, `stageParticipant: StageParticipant`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`
- [Stagiaire](../backend/src/main/java/mg/mef/stagemanagement/model/Stagiaire.java) : `id: Integer`, `nom: String`, `prenoms: String`, `adresse: String`, `contact: String`, `email: String`, `statut: Statut`, `utilisateur: Utilisateur`, `photoFichier: Fichier`, `dateCreation: LocalDateTime`
- [StagiaireAcademique](../backend/src/main/java/mg/mef/stagemanagement/model/StagiaireAcademique.java) : `id: Integer`, `stagiaire: Stagiaire`, `niveauEtude: NiveauEtude`, `filiere: Filiere`, `etablissement: Etablissement`, `anneeEtudes: String`
- [StagiaireFichier](../backend/src/main/java/mg/mef/stagemanagement/model/StagiaireFichier.java) : `id: Integer`, `stagiaire: Stagiaire`, `fichier: Fichier`, `typePieceJointe: TypePieceJointe`, `dateAssociation: LocalDateTime`
- [StagiaireStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/StagiaireStatutHistorique.java) : `id: Integer`, `stagiaire: Stagiaire`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`
- [Statut](../backend/src/main/java/mg/mef/stagemanagement/model/Statut.java) : `id: Integer`, `code: String`, `libelle: String`, `typeStatut: TypeStatut`, `actif: Boolean`
- [StructureAccueil](../backend/src/main/java/mg/mef/stagemanagement/model/StructureAccueil.java) : `id: Integer`, `departement: Departement`, `libelle: String`, `localite: String`, `porte: String`, `contact: String`, `courriel: String`, `actif: Boolean`
- [TransitionStatut](../backend/src/main/java/mg/mef/stagemanagement/model/TransitionStatut.java) : `id: Integer`, `statutSource: Statut`, `statutCible: Statut`, `actif: Boolean`
- [TypeAlerte](../backend/src/main/java/mg/mef/stagemanagement/model/TypeAlerte.java) : `id: Integer`, `code: String`, `libelle: String`, `poidsPriorite: Integer`, `actionRecommandee: String`, `actif: Boolean`, `natureAlerte: NatureAlerte`
- [TypeEvenementParticipation](../backend/src/main/java/mg/mef/stagemanagement/model/TypeEvenementParticipation.java) : `id: Integer`, `code: String`, `libelle: String`, `actif: Boolean`
- [TypePieceJointe](../backend/src/main/java/mg/mef/stagemanagement/model/TypePieceJointe.java) : `id: Integer`, `code: String`, `libelle: String`, `pourStagiaire: Boolean`, `pourParticipation: Boolean`, `obligatoireCloture: Boolean`, `multipleAutorise: Boolean`, `actif: Boolean`
- [TypeStatut](../backend/src/main/java/mg/mef/stagemanagement/model/TypeStatut.java) : `id: Integer`, `code: String`, `libelle: String`, `actif: Boolean`
- [Utilisateur](../backend/src/main/java/mg/mef/stagemanagement/model/Utilisateur.java) : `id: Integer`, `identifiant: String`, `motDePasseHash: String`, `statut: Statut`, `dateCreation: LocalDateTime`
- [UtilisateurRole](../backend/src/main/java/mg/mef/stagemanagement/model/UtilisateurRole.java) : `id: Integer`, `utilisateur: Utilisateur`, `role: Role`
- [UtilisateurStatutHistorique](../backend/src/main/java/mg/mef/stagemanagement/model/UtilisateurStatutHistorique.java) : `id: Integer`, `utilisateur: Utilisateur`, `ancienStatut: Statut`, `statut: Statut`, `responsable: Utilisateur`, `motif: String`, `dateModification: LocalDateTime`

## Enums

- [AlertePriorite](../backend/src/main/java/mg/mef/stagemanagement/model/enums/AlertePriorite.java) : INFORMATION, AVERTISSEMENT, URGENT
- [AlerteStatut](../backend/src/main/java/mg/mef/stagemanagement/model/enums/AlerteStatut.java) : A_TRAITER, TRAITEE, IGNOREE, EXPIREE
- [NatureAlerte](../backend/src/main/java/mg/mef/stagemanagement/model/enums/NatureAlerte.java) : INFORMATION, A_TRAITER
- [TypeObservationRapport](../backend/src/main/java/mg/mef/stagemanagement/model/enums/TypeObservationRapport.java) : OBSERVATION, DEMANDE_CORRECTION, VALIDATION
