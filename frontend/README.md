# Frontend MEF–SIIGFP

Application React/Vite en JavaScript, adossée au backend Spring Boot du dépôt.
Voir [le suivi réel](../FRONTEND_PROGRESS.md), [l’audit](../docs/BACKEND_AUDIT.md) et [l’inventaire API](../docs/API_INVENTORY.md). L’authentification, le layout et les fiches de consultation sont raccordés; les workflows métier restent à compléter dans les phases 3 à 6.

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

Les historiques et la clôture ne sont pas disponibles dans cette phase. Les changements de statut encadreur attendent un catalogue autorisé. Voir `../FRONTEND_PROGRESS.md` et `../docs/BACKEND_AUDIT.md` pour les limites backend et les vérifications restant à effectuer. Aucun navigateur n’est lancé par les commandes de test, lint ou build.

## Suivi métier

- Rapports : créer un brouillon depuis la liste ou une participation, puis ouvrir la fiche pour modifier le contenu, soumettre, demander une correction ou valider. Un rapport validé ne peut plus être modifié. Les observations sont gérées séparément du changement de statut.
- Évaluations : ouvrir la fiche pour modifier les appréciations et noter les critères actifs. La finalisation est proposée une fois les critères obligatoires notés et nécessite une confirmation; le dossier devient ensuite non modifiable.
- Soutenances : sélectionner une participation et saisir la date/heure locale. Le lieu et l’observation sont facultatifs. ADMIN et RESPONSABLE_STAGE peuvent créer ou modifier une soutenance; les encadreurs la consultent.

Rapports et évaluations sont modifiables par les trois rôles métier autorisés. Les listes ne sont pas limitées aux dossiers de l’utilisateur connecté par le backend actuel. Les contrôles frontend ne remplacent jamais les autorisations ni les validations serveur.
