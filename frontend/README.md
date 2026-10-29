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
