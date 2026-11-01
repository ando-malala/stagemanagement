# Migrations après audit

Ces migrations s'appliquent à une base possédant déjà le schéma MEF–SIIGFP. Elles ne recréent pas la base. **Ne jamais exécuter `conception/Conception2.sql` sur une base contenant des données.** Le lanceur de tests en extrait le schéma uniquement après avoir créé une base vide portant un nom aléatoire contrôlé.

1. Sauvegarder ensemble PostgreSQL et le répertoire documentaire ; vérifier leur restauration sur une cible vide indépendante. Prévoir une fenêtre sans écritures applicatives.
2. Exécuter `checks/preflight.sql` en lecture seule. Faire qualifier les anomalies par un responsable métier. Aucun rattachement, chevauchement ni rapport historique n'est corrigé automatiquement.
3. Appliquer **V001**, puis **V002**, avec `psql -X -v ON_ERROR_STOP=1 -f <migration>`, sur la cible explicitement vérifiée. Fournir les identifiants via le gestionnaire de secrets ou un fichier `pgpass` protégé, jamais dans la commande, Git ou une capture de log.
4. Démarrer la nouvelle version avec Hibernate en mode `validate`. Vérifier les permissions avec des comptes de recette distincts et les données de recette prévues.

| Migration | Modification | Données existantes et vigilance |
|---|---|---|
| V001__stagiaire_department.sql | Colonne nullable `stagiaires.departement_id`, clé étrangère et index | Aucun rattachement déduit du nom ou du courriel. Les dossiers sans département peuvent être consultés via les participations autorisées ; leur administration reste réservée à ADMIN tant que le rattachement n'est pas qualifié. |
| V002__participation_integrity.sql | Garde des participations terminées et des chevauchements par personne ; remplacement de la fonction de contrôle des périodes de rapports | Les écritures futures sont contrôlées, pas l'historique nettoyé. Une période non canonique existante peut empêcher une modification ou une clôture. Les annulations et abandons ne bloquent pas une nouvelle affectation ; les participations clôturées restent dans l'historique des chevauchements. |

Chaque fichier est transactionnel. Aucun `DROP`, `TRUNCATE`, effacement de dossier ou changement automatique d'identité n'est prévu. Il n'y a pas de migration inverse destructive fournie : en cas d'échec, conserver les preuves et préparer une correction additive. Une restauration éventuelle exige une cible et une procédure validées par l'exploitant.

Les tests automatisés appliquent ces migrations sur des bases locales vierges ; cela ne constitue pas une preuve de compatibilité de toutes les données historiques de production. Les migrations ne sont pas exécutées automatiquement au démarrage de l'application.
