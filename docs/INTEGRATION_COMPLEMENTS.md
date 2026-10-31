# Compléments d’intégration — 31 octobre 2026

## Audit des écarts et décisions

Les changements backend et frontend ci-dessous sont autorisés par la demande du 31 octobre. Les CRUD bruts restent fermés. Les nouveaux endpoints renvoient des DTO, sans entités JPA ni informations de session.

| Fonction | Sources concernées | Correction / complément |
|---|---|---|
| Clôture | `StageGestionService`, `StageParticipantRepository`, `TransitionStatutRepository`, SQL `maj_date_cloture_participation`, seeds événements/statuts/transitions | Événement `CLOTURE`, statut `CLOTUREE`; verrouillage participation, transition active, date dans la même transaction, contrôle documentaire y compris depuis PATCH statut. |
| Documents | `TypePieceJointeRepository`, `StageParticipantFichierRepository`, `DocumentCatalogueService`, `DocumentsPanel` | Catalogue actif avec périmètres/multiplicité; pièces obligatoires et présence, sélection issue du serveur. |
| Historiques | Repositories événements/historiques, fiche participation | DTO chronologiques avec codes, dates, motif, identifiant du responsable; aucune exposition de l’utilisateur complet. |
| Rapports attendus | `CalculPeriodesRapportService`, `RapportActiviteRepository`, `AlerteDetectionService`, fiche participation | Réutiliser les semaines et échéances serveur; même notion de remis (SOUMIS/VALIDE/A_CORRIGER avec date de soumission), retard après le lundi. |
| Charge | `EncadreurRepository`, SQL `vue_charge_encadreurs`, fiche encadreur | Lire la vue existante : capacité effective, EN_COURS/A_CLOTURER comptés, surcharge strictement au-delà de la capacité. |
| Référentiels/transitions | `StatutRepository`, `TransitionStatutRepository`, types d’événements/alertes, formulaires React, `SecurityConfig` | Catalogues actifs en lecture, transitions depuis le statut courant, droits métier explicites. |

## Règles de clôture confirmées

Le SQL initialise `A_CLOTURER → CLOTUREE` et l’événement `CLOTURE`. `TERMINEE` est seulement une catégorie de la vue des échéances, pas un statut de participation. Les types actifs `pour_participation` et `obligatoire_cloture` définissent la complétude documentaire. Le service applique cette obligation explicite avant clôture.

Aucune règle existante n’impose une soutenance, une évaluation finalisée ou tous les rapports validés avant clôture : ces conditions ne sont pas inventées. Le présent contrôle conserve les règles identifiées, sans ajouter un seuil de date arbitraire.

Le script `conception/Conception2.sql` commence par une suppression/recréation de base : **ne pas l’exécuter sur la base courante**. Les essais d’écriture doivent utiliser une base dédiée, créée séparément. Aucun changement des données métier de production n’est nécessaire pour ces compléments.

## Contrats de lecture ajoutés

Ces onze GET sont explicitement autorisés aux rôles ADMIN, RESPONSABLE_STAGE et ENCADREUR. Sans session : 401; rôle STAGIAIRE : 403. Les écritures conservent leurs règles existantes, notamment clôture et statut encadreur réservés à ADMIN/RESPONSABLE_STAGE. Les DTO sont des records Java sérialisés, listés avec leurs champs dans l’inventaire généré.

| GET (préfixe `/api`) | Réponse / usage React |
|---|---|
| `/referentiels/suivi/types-documents` | Tableau : id, code, libellé, périmètres stagiaire/participation, obligation de clôture, multiplicité. Types actifs seulement. Sélecteur dans `DocumentsPanel`. |
| `/referentiels/suivi/types-evenements` | Tableau de références actives. Utilisé par le service de calcul des actions. |
| `/referentiels/suivi/types-alertes` | Tableau de références actives; sélecteur de création d’alerte. |
| `/referentiels/suivi/statuts/{type}` | Statuts actifs du type actif; formulaires de département et changement de statut encadreur. Type absent/inactif : 404. |
| `/referentiels/suivi/transitions/{type}/{source}` | Transitions actives, source/cible et libellé de cible; actions du rapport. Type/source invalide : 404. |
| `/stage-participants/{id}/documents-obligatoires` | Types actifs obligatoires pour une participation, avec booléen `present`; actualisation après dépôt. |
| `/stage-participants/{id}/historique-statuts` | Historique ordonné par date puis id : ancien/nouveau code, motif, date et responsableId. |
| `/stage-participants/{id}/evenements` | Événements ordonnés par date puis id, dates d’effet et de prolongation, motif et responsableId. |
| `/stage-participants/{id}/periodes-rapports` | Début, fin, échéance, rapportId/statutRapport et indicateurs soumise/validee/exigible/manquante. Tableau et aide à la saisie d’un rapport. |
| `/stage-participants/{id}/actions` | Opération, libellé, statut cible et besoin de date d’effet. Aucune matrice de transitions de repli dans React. |
| `/encadreurs/{id}/charge` | Capacité effective, nombre de participations actives et surcharge; fiche encadreur et formulaire d’affectation. Capacité absente de la configuration : 503. |

Les lectures par identifiant retournent 404 pour un dossier inexistant. Un catalogue vide reste vide; une erreur réseau/serveur est affichée avec possibilité de relancer la lecture. Les identifiants de responsables sont exposés sans charger les comptes administratifs dans l’interface métier.

## Défauts supplémentaires confirmés par les essais PostgreSQL

- `StagiaireGestionService.remplirStagiaire` traitait l’adresse comme facultative malgré `nullable=false` et `NOT NULL` en SQL. Le premier essai réel retournait 500. L’adresse est désormais obligatoire dans le service et le formulaire; le test vérifie 400 si elle manque.
- `EvaluationGestionService.verifierNote`, `followUpWorkflows.js` et `EvaluationNotes.jsx` autorisaient 20, mais `evaluation_criteres_note_check` impose 0 à 5. Le second essai réel a confirmé le refus SQL. Java et React sont alignés sur 5, sans modifier le schéma ni les notes existantes. Le scénario vérifie 400 pour 15 et l’enregistrement de 5.
- `DatabaseConflictHandler` retourne un ProblemDetail 409 pour les violations d’intégrité et refus de triggers; les autres erreurs JPA restent 500. Aucun SQL, secret ou contenu de ligne fautive n’est repris dans la réponse. Un rapport chevauchant une période existante vérifie ce comportement sur PostgreSQL.
- `scripts/audit_backend.py` ignore désormais les classes sans mappings HTTP, dont le gestionnaire d’erreurs, et recense aussi les DTO records des nouveaux services.

## Exécution et isolation

Depuis la racine :

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-integration.ps1
```

Le script lit la configuration de connexion locale sans l’afficher, exige une cible locale, crée une base au nom aléatoire `mef_integration_<32 caractères hexadécimaux>`, puis importe uniquement les tables/vues/fonctions/seeds du script de conception. Les commandes initiales de suppression/recréation de base sont exclues et un garde refuse leur présence dans l’extrait importé. Aucun enregistrement métier existant n’est copié.

Maven reçoit la connexion isolée par environnement. `CompleteIntegrationTests` refuse de s’activer sans URL conforme à ce préfixe. Les autres tests Spring utilisent également la base isolée. Le serveur HTTP écoute sur boucle locale avec port aléatoire. Seule l’orchestration planifiée est remplacée par un mock pour empêcher les déclenchements horaires; l’authentification, Spring Security, les contrôleurs, services, repositories et PostgreSQL sont réels.

La base créée est supprimée dans `finally`; les variables d’environnement sont restaurées. Les fichiers téléversés par le scénario sont dans un répertoire de test sous `backend/target`, supprimé après vérification de son chemin. Les rapports Surefire restent sous `backend/target/surefire-reports` (non versionnés). Le lanceur utilise le `psql` local PostgreSQL 15 et Maven hors ligne; les dépendances doivent déjà être en cache.

## Vérifications et limites

- Compilation après le groupe clôture, puis après les nouvelles API; builds frontend après raccordement.
- **101 tests frontend réussis**, lint réussi, build réussi (**113 modules**). Couverture supplémentaire : chemins/permissions des nouvelles API, annulation, catalogue invalide, propagation des 409, absence d’actions inventées avant chargement, sélecteurs et état de chargement SSR.
- **10 tests backend réussis**, dont le scénario HTTP authentifié complet, les refus publics et sept tests unitaires de clôture/documents/périodes.
- Parcours HTTP : connexion, création des référentiels/comptes/stagiaire/profil/encadreur/stage, affectation, démarrage, suspension/reprise avec date invalide refusée, prolongation, rapports avec correction/validation/observation, chevauchement refusé, notation/finalisation et modification interdite après finalisation, soutenance, alerte et traitement, dépôt/téléchargement/doublon documentaire, clôture bloquée puis réussie, relecture du statut/date/événement/historique, double clôture refusée, déconnexion et session révoquée.
- Droits des onze GET vérifiés avec RESPONSABLE_STAGE, ENCADREUR et STAGIAIRE, en plus des lectures ADMIN du scénario. Clôture refusée à ENCADREUR/STAGIAIRE; le responsable atteint les contrôles métier. Les écritures SQL directes tentant une transition interdite ou une date de fin invalide sont refusées par PostgreSQL.
- Dernière passe : annulation d’une participation planifiée, abandon de participations en cours, capacité par défaut de l’encadreur, surcharge avec deux participations pour une capacité de un puis disparition de la surcharge, changement du statut encadreur. Les dix tests backend restent réussis après ces ajouts.

Les tests créent des comptes réels dans une base de test, pas dans la base métier. Ils vérifient le schéma du dépôt; ils ne prouvent pas que les triggers, seeds et vues installés en production sont identiques. Aucun navigateur utilisé : interactions de dialogue, clavier, responsive et accessibilité visuelle restent à recetter. Les détecteurs automatiques ne sont pas déclenchés par ce scénario; la création et le traitement d’alertes le sont.

La matrice de droits existante reste inchangée quant au périmètre des dossiers : pas de filtrage individuel ou départemental, ni portail STAGIAIRE. L’administration des catalogues techniques/seuils/paramètres, les suppressions non autorisées et l’édition de profils académiques ne sont pas ajoutées par ces compléments.
