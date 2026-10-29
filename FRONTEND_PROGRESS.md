# Suivi frontend MEF–SIIGFP

Dernière mise à jour : 29 octobre 2026.

## Statut réel

**Phases 1 et 2 implémentées; phase 3 intégrée pour les opérations compatibles avec les API autorisées.** Édition des ressources principales, profils académiques, sélecteurs et actions de participation vérifiés localement. Validation HTTP/PostgreSQL non effectuée : le backend n’était pas joignable lors des sondes précédentes. Les limites de phase 3 et les phases 4 à 6 restent à traiter. L’application complète n’est pas déclarée opérationnelle.

Préférence utilisateur : effectuer les vérifications sans Chrome ni pilotage de navigateur. Utiliser les tests locaux, le rendu React SSR, le lint et le build; signaler les limites de vérification visuelle.

L’arborescence React/Vite JavaScript existante a été conservée. Aucune dépendance ajoutée, aucune modification backend, aucun SQL exécuté et aucun contrat API modifié.

## Livrables de la phase 1

| Fichiers créés/modifiés | Résultat |
|---|---|
| `scripts/audit_backend.py`, `docs/API_INVENTORY.md`, `docs/api-inventory.json` | Inventaire reproductible de 50 contrôleurs et 276 opérations, dont 103 admises par SecurityConfig; DTO, paramètres, droits et erreurs des services. |
| `docs/BACKEND_AUDIT.md` | Audit des workflows, validations, modèle de session, SQL, état frontend et liste des API manquantes/écarts. |
| `frontend/src/api/apiClient.js`, `client.js`, anciens modules auth/utilisateur/alerte | Transport unique, URL configurable, jeton opaque, JSON sans enveloppe supposée, erreurs réseau/HTTP/validation, multipart/binaire, signal d’annulation et notification des 401. Les anciens modules délèguent au client commun; le référentiel fermé des types d’alerte signale explicitement son indisponibilité. |
| `frontend/src/api/*Api.js`, `frontend/.env`, `.env.example`, `vite.config.js` | Options de requête sur les listes, configuration du proxy de développement/prévisualisation et téléchargement authentifié via le transport commun. |
| `context/AuthContext.jsx`, `pages/auth/LoginPage.jsx`, `routes/AppRoutes.jsx`, `utils/permissions.js` | Raccordement de l’expiration de session, synchronisation entre onglets, requête de profil annulable, suppression du double appel /me à la connexion, permissions centralisées. |
| `hooks/useFetch.js`, `components/common/EntityPage.jsx`, `utils/formUtils.js` | Rechargement, annulation et protection contre les réponses obsolètes, validation/normalisation du formulaire, retour des erreurs par champ. |
| `components/common/Table.jsx`, `Input.jsx`, `Modal.jsx`, `ConfirmDialog.jsx`, `ErrorMessage.jsx`, `Loader.jsx`, `styles/components.css` | Recherche/pagination locale, labels et erreurs accessibles, modale native avec focus, confirmation asynchrone, états chargement/erreur/vide. |
| `utils/dateUtils.js` | Dates françaises sans déplacement des LocalDate/LocalDateTime; instants explicites affichés à Madagascar. |
| `pages/alertes/AlertesPage.jsx`, `pages/fichiers/FichiersPage.jsx` | Correction des actions existantes : retrait du traitement global systématiquement refusé, traitement individuel confirmé selon nature/statut, affichage des erreurs de téléchargement. Ces corrections ne terminent pas les modules. |
| `frontend/tests/*.test.js`, `package.json`, `eslint.config.js`, `frontend/README.md` | Infrastructure de tests Node sans dépendance supplémentaire, tests de transport/contrats/rendu React et documentation de lancement. |

## Endpoints raccordés au socle

Les appels existants des modules auth, dashboard, stagiaires, encadreurs, stages/participations, rapports, évaluations, soutenances, fichiers, alertes, utilisateurs, référentiels et audit utilisent le client central. Aucun nouvel endpoint backend n’a été inventé. Les mutations des autres workflows restent à intégrer dans les phases suivantes.

Contrôles de contrat exécutés sur : POST `/api/auth/login`, `/api/stagiaires`, `/api/stage-participants`, `/api/rapports-activite`, `/api/soutenances`, `/api/stage-participants/{id}/fichiers` et PATCH `/api/alertes/{id}/lire`. Ces tests interceptent fetch et comparent méthode, chemin et champs aux sources backend; ils ne sont pas des essais PostgreSQL.

## Vérifications

- Build initial : réussi avec `npm.cmd run build`.
- Tests de phase 1 : **24 réussis / 24** via `npm.cmd test`. Transport, 400/401/403/404/409/413/422/500, ProblemDetail, formulaires, multipart/binaire, annulation, dates, permissions, contrats de services et rendu React SSR des composants.
- `npm.cmd run lint` : **réussi**, sans erreur ni avertissement. `npm.cmd run build` final : **réussi**, 72 modules transformés. Aucun succès supposé pour les parcours métier réels.
- Sonde backend `GET http://localhost:8080/api/auth/me` : connexion impossible. Pas de vérification des données ni des triggers PostgreSQL.
- Pas de vérification visuelle en navigateur, de test d’interaction clavier de la modale ou de test de bout en bout avec comptes réels à ce stade. Le rendu SSR ne remplace pas ces vérifications.
- Sous PowerShell, utiliser `npm.cmd` : `npm.ps1` est bloqué par la politique d’exécution locale. Aucune modification de cette politique.

## Écarts à résoudre

Voir l’[audit](docs/BACKEND_AUDIT.md) pour les preuves et conséquences. Priorités :

1. Clôture incohérente entre Java et SQL : événement CLOTUREE/statut TERMINEE côté service, événement CLOTURE/statut CLOTUREE côté script. Conditions documentaires et autres préconditions non contrôlées par ce service.
2. Historiques/événements, types de pièces, statuts/transitions, types d’alertes et paramètres non accessibles via les droits actuels.
3. Absence d’API de périodes attendues, de préparation de clôture et de charge encadreur; ne pas reproduire les détecteurs côté frontend.
4. Rôle STAGIAIRE sans accès métier, profil de session minimal, absence de périmètre individuel des listes.
5. Backend indisponible pour vérifier les workflows et contraintes réellement installées en base.

## Phases suivantes

### Phase 2 — Authentification et structure

Implémentation réalisée :

- Cycle de session isolé dans `frontend/src/context/sessionStore.js`, raccordé à `AuthContext.jsx` : restauration via /me, validation du profil, connexion, expiration et fermeture locale immédiate. Une panne réseau conserve le jeton et propose « Réessayer ». Les réponses tardives et les requêtes annulées ne rétablissent pas une ancienne session. Synchronisation des changements de session entre onglets.
- `routes/routeConfig.js` et `AppRoutes.jsx` : routes centralisées, paramètres d’identifiant vérifiés, retour à la destination après connexion, conservation du chemin/recherche/ancre, refus des destinations externes, historique navigateur et titre de page. Les permissions sont vérifiées avant de monter une page métier.
- Layout : navigation par rôles avec liens réels et indicateur actif, fil d’Ariane, lien d’évitement vers le contenu, menu mobile en dialogue, identité/rôles de session, accès aux alertes et menu de déconnexion. Aucun département inventé : le profil API ne le fournit pas. Le rôle STAGIAIRE ne voit pas de navigation métier à laquelle il n’a pas droit.
- Fiches en lecture seule et liens « Consulter » depuis les listes : stagiaire, encadreur, stage, participation, rapport, évaluation, soutenance et utilisateur. Chaque fiche interroge son propre endpoint et possède des états de chargement, erreur, actualisation et retour. Les participations renvoient vers leur stagiaire/stage/encadreur; les rapports, évaluations et soutenances renvoient vers leur participation. Les fiches métier complètes, les sous-dossiers et les mutations restent à compléter dans les phases suivantes.
- Finalisation d’évaluation déjà présente : bouton masqué quand finalisée, confirmation et restitution des erreurs serveur. Cette correction ne termine pas le module d’évaluation.
- Tests supplémentaires : cycle de session (réseau, 401/403, réponses tardives, changement de compte, nettoyage StrictMode), routage/permissions, rendu du layout et contrôle des huit contrats de détail contre les DTO backend.

Fichiers concernés : `context/{AuthContext.jsx,sessionStore.js}`, `routes/{AppRoutes.jsx,routeConfig.js}`, `components/layout/*`, `components/common/{AppLink,Button,ResourceDetail}.jsx`, `pages/*/*DetailPage.jsx`, listes correspondantes, `api/{auth,stagiaire,encadreur,stage,rapport,evaluation,soutenance,utilisateur}Api.js`, `styles/{layout,pages}.css`, `tests/{sessionStore,routing,layout,detailApi}.test.js`.

Endpoints intégrés : `POST /api/auth/login`, `GET /api/auth/me`, `POST /api/auth/logout`; GET `/api/stagiaires/{id}`, `/api/encadreurs/{id}`, `/api/stages/{id}`, `/api/stage-participants/{id}`, `/api/rapports-activite/{id}`, `/api/evaluations/{id}`, `/api/soutenances/{id}`, `/api/utilisateurs/{id}`. Les autorisations des lectures correspondent à SecurityConfig, notamment ADMIN uniquement pour les utilisateurs.

Vérifications de phase 2 : **50 tests réussis sur 50**, build réussi (87 modules), lint réussi. Les tests SSR désactivent le serveur WebSocket Vite pour éviter les conflits de ports entre fichiers de tests. Backend toujours inaccessible lors de la sonde de cette phase. Pas de validation visuelle réussie : la tentative initiale n’a pas abouti; le pilotage du navigateur a été arrêté à la demande de l’utilisateur. Le rendu SSR ne vérifie pas les interactions clavier, la mise en page mobile réelle ni les parcours avec PostgreSQL.

### Phase 3 — Ressources principales et participations

Implémentation réalisée :

- Création et édition des stagiaires, encadreurs, stages et participations depuis les listes et fiches. Formulaires communs avec limites de texte, champs obligatoires, entiers positifs, dates valides et fin de stage non antérieure au début. Les PATCH envoient les champs du DTO complet, avec `null` pour effacer les valeurs optionnelles; les champs de réponse et de navigation sont exclus.
- Choix chargés depuis les API autorisées : départements, structures, cadres de stage, établissements, filières, niveaux d’étude, stages, encadreurs et stagiaires. Les choix affichent leurs libellés et identifiants pour distinguer les homonymes; les références explicitement inactives sont signalées. Aucune référence fictive ni calcul de charge locale.
- Association compte/encadreur : sélecteur des utilisateurs pour ADMIN seulement. Le RESPONSABLE_STAGE n’appelle pas `/api/utilisateurs`; son édition conserve l’association existante. L’association d’un nouveau compte depuis cette interface nécessite un administrateur, malgré le droit backend plus large d’écriture d’un encadreur.
- Profils académiques : consultation et ajout depuis la fiche stagiaire, année au format `AAAA-AAAA`. Aucune édition/suppression exposée, ces opérations n’étant pas autorisées par le backend. Pour une participation, les profils sont chargés uniquement pour le stagiaire choisi; changer de stagiaire efface le profil sélectionné et le formulaire vérifie son appartenance avant l’envoi.
- Fiche stage : liste des participants avec liens vers leurs dossiers. Les chargements de choix et de profils transmettent le signal d’annulation; erreurs et réponses malformées bloquent l’enregistrement et proposent une reprise. Les rôles de consultation ne montent pas les formulaires d’écriture.
- Participation : démarrage, suspension, reprise, prolongation, abandon et annulation avec préparation, récapitulatif, confirmation et motif. Le démarrage utilise le PATCH de statut existant pour `PLANIFIEE → EN_COURS`, transition attestée par le SQL. Les autres actions utilisent leurs endpoints dédiés. Les boutons sont filtrés selon les transitions présentes dans le code/SQL; le serveur reste l’autorité pour la configuration installée.
- Suspension/reprise : date d’effet obligatoire, comprise dans le stage, au plus égale au jour courant à Madagascar. La chronologie par rapport aux événements précédents reste contrôlée par le serveur, leur historique étant inaccessible. Prolongation : fin strictement postérieure. Abandon/annulation : pas de date d’effet affichée ou envoyée, car le service actuel ne la conserve que pour suspension/reprise.
- Messages de succès après réponse de mutation, rechargement du dossier, restitution des erreurs de validation et conflits serveur. L’échec d’une relecture ne rejoue pas la mutation.

Fichiers principaux : `api/{businessChoices,stageApi,stagiaireApi}.js`, `utils/{businessForms,participationEvents,dateUtils}.js`, `components/common/{BusinessForm,BusinessEditor,EntityPage,ResourceDetail}.jsx`, pages des stagiaires/encadreurs/stages, `AcademiquesPanel.jsx`, `StageParticipantsPanel.jsx`, `ParticipationActions.jsx`, styles et quatre fichiers de tests métier.

Endpoints intégrés : PATCH `/api/stagiaires/{id}`, `/api/encadreurs/{id}`, `/api/stages/{id}`, `/api/stage-participants/{id}`; GET/POST `/api/stagiaires/{id}/academiques`; GET `/api/stages/{id}/participants`; PATCH `/api/stage-participants/{id}/statut`; POST `/api/stage-participants/{id}/{suspendre,reprendre,prolonger,abandonner,annuler}`. Les créations existantes passent par les nouveaux formulaires.

Vérifications : **63 tests réussis sur 63**, lint réussi, build réussi. Couverture ajoutée : contrats de champs contre les DTO Java, requêtes/permissions contre l’inventaire, effacement des champs optionnels, conservation du compte, validation des dates, fuseau Madagascar, états d’action, erreurs de listes et rendu SSR des sélecteurs/droits. Pas de navigateur utilisé, pas de nouvel essai avec serveur réel ni PostgreSQL; les interactions, le rendu responsive et les contraintes installées restent à vérifier.

Limites restantes : historique des participations/encadreurs inaccessible, changement libre de statut encadreur non exposé faute de catalogue autorisé, clôture toujours indisponible en raison de l’écart Java/SQL, préparation de clôture et charge non exposées. Les formulaires de participation restent alignés sur le PATCH existant, qui permet de modifier directement ses dates; les contraintes de modification d’un dossier terminé doivent être renforcées côté serveur si ce comportement doit être interdit.

Prochaine étape : **phase 4**, workflows des rapports d’activité, évaluations et soutenances. Les documents et la clôture conservent les limites de contrat décrites dans l’audit. Aucun changement backend n’a été effectué.

### Reste à réaliser

| Phase | État et prochaine action |
|---|---|
| 2 — Authentification/structure | Implémentée et vérifiée localement; validation avec serveur réel et vérification visuelle restent non effectuées. Voir le compte rendu ci-dessus. |
| 3 — Ressources principales | Édition, choix par API, profils académiques, participants et événements intégrés et vérifiés localement. Historique/statuts encadreur dépendent des API manquantes; essais réels à effectuer. |
| 4 — Suivi métier | Compléter brouillon/soumission/correction/validation des rapports, édition/finalisation évaluations, soutenances, documents. Traiter séparément le contrat de clôture avec accord utilisateur. |
| 5 — Administration/pilotage | Compléter détails/filtres/liens d’alertes, utilisateurs/rôles/départements, référentiels modifiables, audit et dashboard sans chiffres fictifs. |
| 6 — Finalisation | Tests d’intégration réels avec backend/PostgreSQL et comptes habilités, contrôle visuel/responsive/accessibilité, harmonisation et build final. |

Une fonctionnalité ne sera marquée terminée qu’après vérification de ses écrans, appels, validations et droits. Les changements de contrat backend doivent faire l’objet d’une proposition concrète et de l’accord de l’utilisateur.
