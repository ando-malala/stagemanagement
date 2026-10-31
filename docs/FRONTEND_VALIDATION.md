# Validation d’intégration sans navigateur

Ce guide distingue les contrôles automatisés disponibles et les parcours restant à valider. Aucun de ces scripts ne lance Chrome.

**Actualisation du 31 octobre :** le lanceur `scripts/test-integration.ps1` exécute désormais les parcours authentifiés sur une base PostgreSQL temporaire, créée à partir du schéma du dépôt puis supprimée. Voir [le compte rendu et les endpoints intégrés](INTEGRATION_COMPLEMENTS.md). Les sections de lecture ci-dessous restent utilisables; la liste finale des parcours sert aussi de grille de recette visuelle, non exécutée ici.

## Vérifications locales

Depuis `frontend/` :

```powershell
npm.cmd test
npm.cmd run lint
npm.cmd run build
```

Les tests frontend combinent fetch intercepté, rendu React SSR et serveur HTTP Node local avec réponses simulées selon l’inventaire. Ils vérifient les contrats attendus et les droits simulés, pas les comptes ou transactions PostgreSQL réels.

Depuis `backend/`, avec Java 17, Maven et PostgreSQL configurés :

```powershell
mvn.cmd test
```

Si les dépendances sont déjà en cache, `mvn.cmd -o test` permet de travailler hors ligne. Le test Spring utilise la configuration de connexion existante, force Hibernate `validate` et démarre un serveur temporaire sur `127.0.0.1` avec port aléatoire. Il vérifie six refus anonymes et un jeton inconnu. L’orchestration planifiée est remplacée par un mock pour éviter la création d’alertes à 06:00. Aucune création de compte/session ni mutation métier n’est exécutée. Le schéma doit déjà exister; aucun script de conception n’est appliqué.

## Contrôle d’un backend déjà démarré

Depuis `frontend/` :

```powershell
$env:MEF_API_URL = 'http://localhost:8080'
npm.cmd run check:api
```

La cible accepte une origine HTTP(S), éventuellement suivie de `/api`. Les URL contenant des identifiants, paramètres ou ancres sont refusées. Le script n’utilise pas le proxy Vite et ne valide pas CORS. Il ne démarre pas le backend.

Sans jeton, seuls `/api/auth/me` et `/api/dashboard/resume` sont sondés; ils doivent retourner 401. Pour les lectures authentifiées, fournir un **jeton de session de test existant** via la variable d’environnement `MEF_TEST_TOKEN` du processus, puis relancer la commande. Ne pas enregistrer ce jeton dans le dépôt, les fichiers VITE, les rapports ou une commande contenant sa valeur en clair. Effacer la variable après usage :

```powershell
Remove-Item Env:MEF_TEST_TOKEN -ErrorAction SilentlyContinue
```

Le script n’effectue que des GET, sans login/logout, mutation, détection d’alertes ni téléchargement. Il refuse les redirections et limite chaque requête à cinq secondes. Une session invalide arrête les lectures authentifiées. Les sorties contiennent uniquement chemins avec identifiants, statuts, tailles des listes et messages génériques; aucun contenu de dossier ni secret n’est imprimé.

| Contrôle | Portée |
|---|---|
| Profil `/auth/me` | Structure du profil et rôles utilisés pour les attentes suivantes. |
| Dashboard | Six compteurs entiers non négatifs pour les rôles métier; 403 attendu sinon. |
| Listes métier et critères | Tableaux pour ADMIN, RESPONSABLE_STAGE et ENCADREUR; 403 attendu sinon. |
| Sept référentiels | Tableaux pour toute session authentifiée. |
| Utilisateurs, rôles, audit | Tableaux pour ADMIN; 403 attendu sinon. |
| Fiches et sous-dossiers | Premier identifiant disponible de chaque liste autorisée; correspondance de l’identifiant de fiche et tableaux des sous-dossiers. |

Une liste vide ne permet pas de vérifier sa fiche ni ses sous-dossiers. Une réponse de liste est vérifiée comme tableau, sans validation exhaustive de chaque champ DTO. Un succès ne prouve pas les droits d’écriture, les règles métier, tous les dossiers ou les filtres d’audit. Exécuter séparément avec chaque rôle pour couvrir sa matrice de lectures.

| Code de sortie | Interprétation |
|---|---|
| 0 | Tous les contrôles effectués avec la session fournie ont réussi. |
| 1 | Connexion impossible, configuration invalide ou contrôle en échec. |
| 2 | Contrôles publics réussis; pas de session fournie, lectures métier non vérifiées. |

## Parcours restant à valider sur une base de test dédiée

Ne pas assimiler les tests de lecture à la réussite des écritures suivantes. Pour chaque parcours, conserver les identifiants de test, les statuts HTTP, la relecture et l’effet attendu; ne pas conserver les mots de passe ou jetons.

| Parcours | Résultat à vérifier |
|---|---|
| Session et rôles | Connexion, restauration, expiration, 403 selon rôle, déconnexion; aucun accès métier pour STAGIAIRE. |
| Stagiaire et participation | Création/édition, profil académique du bon stagiaire, rattachements et contrôles de dates. |
| Événements | Démarrage, suspension, reprise, prolongation, abandon et annulation; refus des transitions/dates invalides par la base installée. |
| Rapports | Brouillon, soumission, correction, validation, observations; périodes et chevauchements contrôlés par les triggers. |
| Évaluation | Notes, bornes et critères obligatoires; finalisation puis refus de modification. |
| Soutenance et documents | Dates, dépôt multipart avec code réellement configuré, périmètre/multiplicité, téléchargement et refus selon rôle. |
| Comptes et référentiels | Création, rôles/départements, dernier rôle, réinitialisation, autorisations et conservation des valeurs optionnelles. |
| Pilotage | Alertes traitables seulement, compteurs après mutations, audit cohérent. |

La clôture, les catalogues fermés, les historiques, les périodes attendues et la charge encadreur nécessitent encore les décisions décrites dans l’[audit](BACKEND_AUDIT.md). Aucun changement de contrat n’est appliqué par ce guide.

Le responsive, les interactions de dialogue, le clavier et le focus réel restent à contrôler visuellement. Le rendu SSR ne suffit pas à les valider.
