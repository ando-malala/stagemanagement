# Protections de cohérence métier

Ces scripts ne sont pas exécutés automatiquement au démarrage Spring Boot. Le schéma de référence reste `conception/Conception2.sql`. Les migrations n'ajoutent aucune colonne et ne réécrivent aucune donnée métier.

## État de validation

- V002 a été exécutée avec succès dans une base PostgreSQL 15 isolée ; les tests HTTP/SQL des associations et références ont passé.
- V003 a été exécutée après autorisation de la relance le 11 novembre 2026 : **66 tests réussis, dont 13 tests PostgreSQL, aucun échec ni test ignoré**. Le préflight SQL s'est exécuté sans erreur.
- Les trois anomalies de la première suite PostgreSQL sont corrigées et la suite passe désormais. V002 et V003 restent appliquées uniquement sur les bases isolées de test, jamais sur la base métier.

## Ordre de vérification et d'application

1. Exécuter `scripts/test-integration.ps1` depuis la racine : création d'une base locale neuve `mef_integration_<uuid>`, installation du schéma puis V002 et V003, tests et préflight. Le lanceur refuse une cible non locale et ne restaure jamais sur la base configurée. Il conserve sa base de diagnostic.
2. Après succès, exécuter `checks/preflight.sql` en lecture seule sur la base cible et examiner les comptages. Ce script ne révèle pas les noms des personnes. Les associations historiquement inactives ne sont pas assimilées automatiquement à des erreurs.
3. Résoudre explicitement les anomalies héritées ou convenir de leur conservation historique. Ne jamais supprimer les données pour faire passer une contrainte.
4. Dans une fenêtre adaptée aux verrous DDL, appliquer V002 puis V003 avec un client arrêté à la première erreur (`psql -X -v ON_ERROR_STOP=1 -f …`). Chaque migration est transactionnelle. Les fonctions/triggers sont recréables ; aucune action DELETE/UPDATE sur les lignes métier n'est incluse.
5. Vérifier les scénarios prioritaires compte STAGIAIRE/ENCADREUR, comptes déjà utilisés, références inactives, semaines des rapports et dossiers historiques.

Les fonctions SQL protègent les relations et les périodes ciblées ; elles ne remplacent ni l'authentification ni les permissions Spring. La suppression des triggers retirerait ces protections : aucun retour arrière automatique destructif n'est fourni. Voir `AUDIT_COHERENCE_METIER.md` à la racine pour les tests exacts, les risques résiduels et les décisions métier.
