# Frontend MEF–SIIGFP

Application React/Vite en JavaScript, adossée au backend Spring Boot du dépôt.
Voir [le suivi réel](../FRONTEND_PROGRESS.md), [l’audit](../docs/BACKEND_AUDIT.md) et [l’inventaire API](../docs/API_INVENTORY.md). Les phases 1 à 5 sont intégrées dans les limites des contrats autorisés; la validation d’intégration de phase 6 est en cours.

## Lancement local

Depuis `frontend/`, avec Node compatible avec les dépendances installées (vérifié ici avec Node 24) :

```powershell
npm.cmd ci
npm.cmd run dev
```

`npm.cmd ci` est nécessaire uniquement si les dépendances ne sont pas déjà présentes ou si le lockfile change. Sur un shell autre que PowerShell, `npm` convient également.

Démarrer le backend séparément avec sa configuration PostgreSQL. Le serveur attendu est `http://localhost:8080`. Le frontend ne remplace jamais une réponse indisponible par des données de démonstration.

## Configuration

Le fichier `.env.example` décrit les variables. Les ajustements locaux peuvent être placés dans `.env.local` (ignoré par Git).

- `VITE_API_URL=` : URL de base vue du navigateur, vide par défaut pour utiliser `/api` sur la même origine. Un suffixe `/api` est accepté sans être doublé.
- `API_PROXY_TARGET=http://localhost:8080` : cible du proxy Vite de développement et de prévisualisation.

Le backend actuel ne configure pas CORS. Privilégier le proxy local. En production, configurer le serveur web pour servir `dist/`, rediriger `/api` vers Spring Boot et retourner `index.html` pour les routes frontend. Vite preview n’est pas un serveur de production. Une URL API externe requiert une politique CORS compatible côté serveur. Aucune variable VITE ne doit contenir un secret.

## Vérification

```powershell
npm.cmd test
npm.cmd run lint
npm.cmd run build
```

Les tests Node utilisent les dépendances déjà installées. Ils couvrent le transport et les contrats de services avec fetch intercepté, les utilitaires, les permissions et le rendu SSR React. Ils ne remplacent pas les essais avec PostgreSQL ou dans un navigateur.

`npm.cmd run check:api` contrôle un backend déjà démarré sans navigateur ni écriture. La cible est `http://localhost:8080` par défaut (`MEF_API_URL` pour la changer). Sans `MEF_TEST_TOKEN`, seuls les refus d’accès anonymes sont contrôlés et le code de sortie est 2 si ces contrôles passent. Avec un jeton existant de test, les lectures et droits associés sont vérifiés; une erreur produit le code 1. Voir le [guide d’intégration](../docs/FRONTEND_VALIDATION.md) pour la portée exacte et les parcours restants.

Pour régénérer l’inventaire, depuis la racine du dépôt :

```powershell
python scripts/audit_backend.py
```

## Conventions d’intégration

- Utiliser `src/api/apiClient.js` dans chaque service. Les chemins commencent par `/api/` et doivent exister dans l’inventaire.
- La session est un jeton opaque Bearer, pas un JWT; ni décodage ni refresh token. Le backend vérifie sa validité. Les 401 protégés déclenchent la fermeture locale; les 403 métier restent des erreurs de droits.
- Ne pas ajouter un Content-Type aux FormData. Les téléchargements utilisent `responseType: 'blob'` et le même client authentifié.
- Les listes backend sont des tableaux, sans enveloppe data ou Page. Recherche et pagination se font localement sur les résultats reçus.
- Pour `useFetch`, fournir une fonction stable (fonction de module ou useCallback). Le hook transmet `{ signal }`, annule l’ancienne requête et ignore ses résultats tardifs. Les services ou closures doivent transmettre ce signal pour interrompre effectivement le transport.
- `useFetch.reload()` affiche les erreurs dans l’état du hook et retourne undefined en cas d’échec; les mutations doivent gérer leurs propres erreurs et afficher le succès seulement après confirmation serveur.
- Les fonctions de permissions guident l’interface; Spring Security reste l’autorité.
- Préserver les dates sans offset renvoyées par le serveur; utiliser les fonctions de date communes pour l’affichage.

## Ressources principales

Les listes proposent la création et les fiches proposent la modification aux rôles ADMIN et RESPONSABLE_STAGE. Les formulaires chargent les référentiels autorisés et les choix métier depuis le serveur. Le compte associé à un encadreur est sélectionnable par ADMIN; pour le responsable, l’association existante est conservée.

Depuis la fiche stagiaire, consulter ou ajouter les profils académiques. Pour affecter un stagiaire, le sélectionner puis choisir l’un de ses profils. La fiche stage donne accès à ses participants. La fiche participation propose les actions compatibles avec son état, avec confirmation et contrôles des dates d’effet à Madagascar.

La fiche participation affiche les historiques, événements et périodes attendues. Les actions et transitions sont chargées depuis le serveur : passer à « À clôturer », compléter les pièces obligatoires puis confirmer la clôture. La fiche encadreur permet le changement de statut et affiche sa charge réelle; celle-ci apparaît aussi dans le formulaire d’affectation. Aucun navigateur n’est lancé par les commandes de test, lint ou build.

## Suivi métier

- Rapports : créer un brouillon depuis la liste ou une participation, puis ouvrir la fiche pour modifier le contenu, soumettre, demander une correction ou valider. Un rapport validé ne peut plus être modifié. Les observations sont gérées séparément du changement de statut.
- Évaluations : ouvrir la fiche pour modifier les appréciations et noter les critères actifs **sur 5**, conformément à PostgreSQL. La finalisation est proposée une fois les critères obligatoires notés et nécessite une confirmation; le dossier devient ensuite non modifiable.
- Soutenances : sélectionner une participation et saisir la date/heure locale. Le lieu et l’observation sont facultatifs. ADMIN et RESPONSABLE_STAGE peuvent créer ou modifier une soutenance; les encadreurs la consultent.

Rapports et évaluations sont modifiables par les trois rôles métier autorisés. Les listes ne sont pas limitées aux dossiers de l’utilisateur connecté par le backend actuel. Les contrôles frontend ne remplacent jamais les autorisations ni les validations serveur.

## Documents et comptes

Les documents se consultent depuis une fiche stagiaire/participation ou la page Documents. ADMIN et RESPONSABLE_STAGE peuvent déposer un fichier non vide, sous la limite de 10 Mio (requête multipart comprise). Le sélecteur utilise le catalogue actif, filtré par périmètre et multiplicité; les pièces obligatoires présentes/manquantes viennent du serveur. La clôture revérifie ces pièces. Les téléchargements restent authentifiés.

ADMIN peut créer un compte avec un rôle actif chargé depuis le serveur, gérer ses rôles/départements depuis sa fiche et réinitialiser son mot de passe avec confirmation. Le backend actuel exige 6 caractères à la création et 12 à la réinitialisation. Aucun secret n’est affiché dans un récapitulatif ou stocké dans le navigateur. Le compte doit conserver au moins un rôle; une modification de ses propres rôles relit la session et actualise les accès.

## Référentiels et pilotage

ADMIN peut créer ou modifier les sept référentiels exposés dans Référentiels. Les structures choisissent leur département par libellé. Le statut des départements est sélectionnable dans le catalogue serveur; les autres références exposent leur indicateur actif. Les types de pièces, d’alertes, d’événements et les transitions sont accessibles en lecture aux trois rôles métier. Leur administration brute reste fermée.

La page Alertes permet de filtrer les données reçues et de consulter le message, l’action recommandée et les dossiers liés. Seules les alertes traitables proposent une confirmation de traitement. Le dashboard affiche les six compteurs serveur et donne accès aux dossiers et alertes; une réponse incomplète reste une erreur visible.

Le journal d’audit est consultable par ADMIN, globalement ou par utilisateur/entité. Le détail affiche les anciennes et nouvelles valeurs, avec masquage des champs de secrets identifiables. Aucune opération d’écriture sur le journal n’est proposée.

## Tests réels d’intégration

Depuis la racine, `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-integration.ps1` crée une base locale `mef_integration_<identifiant aléatoire>`, charge le schéma, exécute les tests Maven puis supprime cette seule base. Il requiert PostgreSQL local, `psql` et des droits de création de base. Aucune donnée de production n’est copiée. Les [parcours et limites](../docs/INTEGRATION_COMPLEMENTS.md) précisent la validation réalisée.
