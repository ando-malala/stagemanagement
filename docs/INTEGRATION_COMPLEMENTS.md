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
