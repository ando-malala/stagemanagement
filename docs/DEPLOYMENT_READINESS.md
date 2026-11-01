# Préparation de la recette et du déploiement

État au 1 novembre 2026 : vérifications locales documentées dans [le suivi des corrections](REMEDIATION_STATUS.md). Aucun déploiement ni migration sur la base métier n'a été effectué.

## Configuration

- Java 17 et PostgreSQL 15 sont les versions utilisées par les tests locaux. Le frontend se construit avec les dépendances verrouillées dans `frontend/package-lock.json` (`npm ci`, puis `npm run build`). Le backend se package avec Maven (`mvn package` après les tests d'intégration isolés).
- `MEF_DB_URL`, `MEF_DB_USER`, `MEF_DB_PASSWORD`, `MEF_UPLOAD_DIR` sont les paramètres backend. Exemples sans secret : `backend/.env.example`. Le fichier local `backend/.env.local` est ignoré par Git ; en production, injecter les secrets via l'environnement ou le gestionnaire de secrets du service. Hibernate doit rester en `validate`, jamais en `create`/`create-drop`.
- Utiliser un utilisateur PostgreSQL d'exploitation avec les droits nécessaires à l'application ; réserver l'application des migrations et la création des bases à un autre compte. Le lanceur local exige un compte autorisé à créer une base de test, ce n'est pas le modèle de droits du service en production.
- Fournir un chemin documentaire absolu, persistant et accessible uniquement au compte du service. Servir les fichiers par l'API authentifiée ; ne pas publier le répertoire en statique. Le contrôle de contenu PDF/JPEG/PNG ne remplace pas un antivirus.
- Tout ancien secret ayant été présent dans Git doit être renouvelé par l'exploitant. L'externalisation du fichier courant ne supprime pas l'historique Git. Aucun secret ni historique n'a été reproduit ou réécrit pendant ces corrections.

## Entrée HTTP

La configuration livrée privilégie une **origine unique HTTPS** : le reverse proxy sert `frontend/dist` et relaie `/api` vers Spring. Les routes frontend inconnues du serveur statique reviennent vers `index.html` ; les erreurs `/api` restent les réponses de Spring. `VITE_API_URL` reste vide dans ce cas. Le proxy de Vite (`API_PROXY_TARGET`) ne configure pas un serveur de production.

Une origine API distincte exige une configuration CORS explicite sur le serveur, actuellement non livrée. Ne pas ouvrir `*` pour contourner ce besoin. Configurer le reverse proxy pour accepter l'enveloppe multipart de 11 Mio, avec la limite applicative de 10 Mio par fichier. Ne jamais journaliser les en-têtes Authorization, les mots de passe ou le contenu documentaire. Vérifier les délais HTTP et le comportement 401/403/409/413 sur la configuration cible.

## Migrations et sauvegardes

Suivre [la procédure des migrations](../backend/migrations/README.md). Les scripts V001/V002 et le précontrôle sont non destructifs. Le schéma d'initialisation `Conception2.sql` ne doit jamais être lancé sur une base existante.

Sauvegarder PostgreSQL avec `pg_dump` au format custom et le stockage documentaire dans la même fenêtre de cohérence. Une sauvegarde de la base seule ne conserve pas les octets des fichiers. Sur une instance isolée, créer une base vide, restaurer avec `pg_restore --no-owner --no-acl --exit-on-error`, sans `--clean`, et restaurer une copie des fichiers. Attention : les chemins documentaires enregistrés sont absolus. Préserver ces chemins sur l'instance isolée ou préparer une adaptation explicite des métadonnées dans la copie restaurée ; changer seulement `MEF_UPLOAD_DIR` ne relocalise pas les fichiers historiques. Comparer les volumes, vérifier les clés étrangères, se connecter avec les comptes de recette et télécharger des pièces. La restauration d'une sauvegarde de production, la planification des sauvegardes et la rétention restent à valider par l'exploitant.

## Recette à faire sur la cible

1. Qualifier les départements des dossiers historiques et associer explicitement les comptes stagiaires. Créer deux comptes de départements différents et deux stagiaires de recette.
2. Vérifier les parcours création, affectation, dépôt/correction/validation des rapports, événements, pièces, évaluation, soutenance facultative, alertes et clôture. La clôture exige les pièces obligatoires actives et les rapports validés des périodes achevées. Les périodes futures ne sont pas exigées ; les suspensions n'interrompent pas le calendrier hebdomadaire existant.
3. Vérifier les refus croisés, les exports et téléchargements avec ces comptes. Les bilans du portail stagiaire excluent les observations privées et les évaluations/soutenances administratives, conformément aux permissions existantes.
4. Contrôler le rendu mobile, le clavier, les dialogues, la sélection des fichiers et l'impression. Aucun navigateur n'a été utilisé pendant cette intervention, à la demande de l'utilisateur. Les tests SSR ne prouvent pas ces interactions.
5. Vérifier le déclenchement planifié à 06:00 `Indian/Antananarivo`, les logs opérationnels, la disponibilité du stockage, la restauration et la charge sur des volumes représentatifs. Les détecteurs ont été appelés réellement en test, sans attendre le cron.

## Limites techniques connues

Les listes et synthèses chargent encore les données puis filtrent selon les autorisations. Elles ne sont pas paginées en SQL et n'ont pas fait l'objet d'un test de charge. Les exports CSV reprennent les dossiers accessibles, indépendamment du filtre local de recherche affiché dans une table. Les PDF paginent le contenu ; les caractères non disponibles dans la police PDF standard sont remplacés par `?`. Un rendu PDF avec police Unicode embarquée peut être ajouté si les données de recette le nécessitent. L'impression native reste à vérifier visuellement.

L'administration des seuils de suivi/paramètres techniques par interface, l'industrialisation CI/CD, l'exploitation, la formation et la signature de recette ne sont pas déclarées terminées par les tests de cette version.
