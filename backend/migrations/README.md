# Migrations après audit

Ces migrations s'appliquent à une base possédant déjà le schéma MEF–SIIGFP. Elles ne recréent pas la base. **Ne jamais exécuter `conception/Conception2.sql` sur une base contenant des données.** Le lanceur de tests en extrait le schéma uniquement après avoir créé une base vide portant un nom aléatoire contrôlé.

1. Sauvegarder ensemble PostgreSQL et le répertoire documentaire ; vérifier leur restauration sur une cible vide indépendante. Prévoir une fenêtre sans écritures applicatives.
2. Exécuter `checks/preflight.sql` en lecture seule. Faire qualifier les anomalies par un responsable métier. Aucun rattachement, chevauchement ni rapport historique n'est corrigé automatiquement.
3. **V001 est retirée du parcours d'installation depuis le 2 novembre 2026 : ne pas l'appliquer.** Si le renforcement des contraintes SQL est décidé, appliquer seulement **V002**, avec `psql -X -v ON_ERROR_STOP=1 -f <migration>`, sur la cible explicitement vérifiée. Fournir les identifiants via le gestionnaire de secrets ou un fichier `pgpass` protégé, jamais dans la commande, Git ou une capture de log.
4. Démarrer la nouvelle version avec Hibernate en mode `validate`. Vérifier les permissions avec des comptes de recette distincts et les données de recette prévues.

| Migration | Modification | Données existantes et vigilance |
|---|---|---|
| V001__stagiaire_department.sql — historique, ne plus appliquer | Ancien ajout de `stagiaires.departement_id` | Le modèle Java et les DTO n'utilisent plus cette colonne. Si elle existe déjà sur une base de test, la laisser en place : aucune suppression n'est nécessaire. Les nouveaux tests omettent V001. |
| V002__participation_integrity.sql | Garde des participations terminées et des chevauchements par personne ; remplacement de la fonction de contrôle des périodes de rapports | Les écritures futures sont contrôlées, pas l'historique nettoyé. Une période non canonique existante peut empêcher une modification ou une clôture. Les annulations et abandons ne bloquent pas une nouvelle affectation ; les participations clôturées restent dans l'historique des chevauchements. |

Chaque fichier est transactionnel. Aucun `DROP`, `TRUNCATE`, effacement de dossier ou changement automatique d'identité n'est prévu. Il n'y a pas de migration inverse destructive fournie : en cas d'échec, conserver les preuves et préparer une correction additive. Une restauration éventuelle exige une cible et une procédure validées par l'exploitant.

Les tests automatisés appliquent V002 sur des bases locales vierges, sans V001 ; cela ne constitue pas une preuve de compatibilité de toutes les données historiques de production. Les migrations ne sont pas exécutées automatiquement au démarrage de l'application. V002 reste un renforcement des contraintes PostgreSQL indépendant de l'erreur de colonne : cette correction et la fusion des rôles ne nécessitent aucune migration.

## Diagnostic de démarrage

L'erreur `missing column [departement_id] in table [stagiaires]` provenait de l'ancien mapping Java. Il est supprimé. Arrêter l'ancien backend et relancer la version recompilée (`mvn spring-boot:run` depuis `backend`). Si un ancien JAR est utilisé, le reconstruire. Conserver `ddl-auto=validate` ; aucune création de colonne n'est nécessaire. Voir [la recette finale](../../docs/RECETTE_FINALE.md) pour la preuve de démarrage sur une base isolée dépourvue de cette colonne.

## Trois rôles sans migration de données

Les rôles applicatifs sont `ADMIN`, `ENCADREUR` et `STAGIAIRE`. Les attributions actives historiques `RESPONSABLE_STAGE` sont interprétées comme `ADMIN` à la connexion et à chaque validation de session. Elles donnent donc les droits administratifs globaux, y compris l'administration des comptes, sans restriction départementale. Les lignes SQL restent inchangées. Le catalogue et les nouvelles attributions n'exposent que les trois rôles retenus. Retirer `ADMIN` via l'API retire aussi son ancien alias, afin de ne pas conserver involontairement l'accès administratif.
