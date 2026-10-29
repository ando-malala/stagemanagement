# Suivi frontend MEF–SIIGFP

Dernière mise à jour : 29 octobre 2026.

## Statut réel

**Phases 1 à 5 intégrées pour les opérations couvertes par les API autorisées; phase 6 commencée.** Le contexte Spring se charge avec PostgreSQL et les tests HTTP de refus d’accès passent sur un serveur temporaire. Un contrôle des lectures API par rôle est disponible en ligne de commande. Les parcours authentifiés avec des comptes réels, les écritures métier, les catalogues manquants, les historiques et la clôture restent à valider ou à traiter. L’application complète n’est pas déclarée opérationnelle.

Préférence utilisateur : effectuer les vérifications sans Chrome ni pilotage de navigateur. Utiliser les tests locaux, le rendu React SSR, le lint et le build; signaler les limites de vérification visuelle.

L’arborescence React/Vite JavaScript existante a été conservée. Aucune dépendance ajoutée, aucun script SQL appliqué et aucun contrat API modifié. La phase 6 étend uniquement les tests du backend, sans modification de son code de production.

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
5. Contexte backend et accès PostgreSQL vérifiés en phase 6; workflows authentifiés, triggers et contraintes métier réellement installées restent à vérifier.

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

### Phase 4 — Rapports, évaluations et soutenances

Implémentation réalisée :

- Rapports : création d’un brouillon et édition depuis la fiche avec participation et mois de stage sélectionnés via les API. Mois obligatoire; activités, difficultés et solutions facultatives conformément au service. Dates réelles et période ordonnée vérifiées. Le contenu d’un rapport validé est verrouillé.
- Cycle de validation : soumission depuis BROUILLON ou A_CORRIGER, demande de correction et validation depuis SOUMIS. Ces quatre transitions correspondent au SQL existant; l’interface ne propose pas de catalogue de statuts inventé. Chaque mutation présente un récapitulatif et un motif facultatif. Les erreurs serveur restent affichées, notamment si les transitions installées diffèrent du script.
- Observations : liste par rapport, auteur, type, commentaire, visibilité et date; ajout avec les trois valeurs de l’enum Java. La visibilité `false` est préservée. Une observation de type DEMANDE_CORRECTION ou VALIDATION ne change pas le statut : l’interface explique cette séparation des opérations backend. Pas de double écriture susceptible de réussir partiellement.
- Participation : accès à la liste de ses rapports, liens vers les fiches et création d’un brouillon avec participation présélectionnée. Aucun calcul de période attendue ni détecteur d’alertes ajouté côté client.
- Évaluations : création et édition des appréciations avec sélecteurs de participation et d’encadreur. Suppression de l’ancien panneau global exigeant les identifiants d’évaluation et de critère : les notes se gèrent dans la fiche de l’évaluation concernée.
- Notation : lecture des notes enregistrées et des critères actifs, coefficients et caractère obligatoire; ajout ou modification par critère via le POST existant (upsert serveur). Notes décimales de 0 à 20, zéro conservé et commentaire facultatif. Les anciennes notes restent visibles même si leur critère n’est plus actif. Pas de moyenne ou de score final inventé.
- Finalisation : liste des critères obligatoires non notés, bouton bloqué pendant une édition de note ou tant que ces critères manquent, confirmation explicite de l’impossibilité de modifier ensuite. En cas d’erreur de chargement des critères/notes, aucune finalisation proposée. Le serveur revérifie les obligations et l’état finalisé. Le dossier est rechargé après réussite; les formulaires et actions d’édition disparaissent lorsqu’il est finalisé.
- Soutenances : création et modification avec sélection de participation, date/heure locale conservée sans conversion UTC, lieu facultatif limité à 250 caractères et observation facultative. Les conflits d’unicité restent validés par le backend.
- Droits : ADMIN, RESPONSABLE_STAGE et ENCADREUR pour rapports/observations/évaluations; ADMIN et RESPONSABLE_STAGE pour soutenances. Le composant d’édition applique aussi les états verrouillés. La confirmation asynchrone dispose d’un verrou immédiat contre les doubles soumissions.

Fichiers principaux : `utils/{businessForms,followUpWorkflows,dateUtils}.js`, `components/common/{BusinessEditor,BusinessForm,ConfirmDialog}.jsx`, `api/{rapportApi,evaluationApi}.js`, pages des rapports/évaluations/soutenances, `ParticipationRapports.jsx` et les tests de suivi.

Endpoints intégrés ou complétés : PATCH `/api/rapports-activite/{id}`, `/api/rapports-activite/{id}/statut`; GET/POST `/api/rapports-activite/{id}/observations`; GET `/api/rapports-activite/participation/{stageParticipantId}`; PATCH `/api/evaluations/{id}`; GET `/api/evaluations/criteres`; GET/POST `/api/evaluations/{id}/notes`; POST `/api/evaluations/{id}/finaliser`; PATCH `/api/soutenances/{id}`. Les créations existantes utilisent les nouveaux formulaires et leurs sélecteurs autorisés.

Vérifications : **72 tests réussis sur 72**, lint réussi, build réussi (100 modules). Contrats comparés aux DTO/inventaire, transitions comparées au SQL, permissions/états verrouillés, booléens, dates locales, critères obligatoires, bornes des notes, erreurs 409, annulation des lectures et rendu SSR. Aucun navigateur utilisé, aucune dépendance ajoutée, aucune modification backend, aucun essai de mutation sur serveur réel. Le rendu SSR ne prouve pas les interactions clavier ou la mise en page; PostgreSQL et les comptes réels restent à tester.

### Suite phase 4 — Documents

- Documents accessibles dans les fiches stagiaire et participation et dans une page dédiée avec sélection de dossier par libellé. Chaque périmètre conserve son endpoint; changement de dossier réinitialise le panneau, les sélections et les transferts de téléchargement. Les lectures utilisent le signal d’annulation et le garde contre les réponses tardives de `useFetch`.
- Liste avec nom original, type/code, format, taille lisible et date. Erreur de liste distincte d’un dossier vide; rechargement et erreurs de transfert visibles. Le sélecteur de fichier est réinitialisé après réussite et le message de succès reste visible pendant la relecture.
- Dépôt multipart pour ADMIN/RESPONSABLE_STAGE, téléchargement pour les rôles métier autorisés. Fichier non vide, code requis et contrôle de taille avant envoi. Le fichier doit rester sous 10 Mio; l’enveloppe multipart peut encore provoquer un 413 près de la limite, le backend limitant aussi la requête entière à 10 MB. Aucun filtre d’extension inventé.
- Catalogue des types inaccessible : saisie d’un **code connu du service**, sans liste simulée issue du SQL. Le serveur vérifie type actif, périmètre et multiplicité. La sélection dynamique du type et le calcul des pièces obligatoires manquantes restent impossibles sans contrat de lecture autorisé; l’interface le signale. Aucun retrait/suppression ajouté.
- Téléchargement par le client authentifié, puis lien Blob temporaire. Support du nom UTF-8 de Content-Disposition, repli de nom, suppression de chemins/caractères de contrôle et nettoyage du lien/URL même si le déclenchement DOM échoue.

Fichiers : `pages/fichiers/{FichiersPage,DocumentsPanel}.jsx`, fiches stagiaire/participation, `api/fichierApi.js`, `utils/documentUtils.js`. Endpoints : GET/POST `/api/stagiaires/{id}/fichiers`, GET/POST `/api/stage-participants/{id}/fichiers`, GET `/api/fichiers/{id}/telecharger`.

### Phase 5 — Comptes et attributions

- Création ADMIN depuis la liste utilisateurs : identifiant de 1 à 50 caractères, confirmation du mot de passe, rôle initial sélectionné parmi les rôles actifs de GET `/api/roles`. Aucun rôle fictif ou choix codé en dur. Les valeurs de mot de passe restent intactes, sans trim, stockage persistant ni affichage dans les récapitulatifs.
- Vérification du service actuel : création avec minimum **6** caractères, réinitialisation avec minimum **12** caractères. Cette différence existante a été corrigée dans l’audit documentaire; aucune règle backend n’a été modifiée.
- Fiche utilisateur : lecture et ajout/retrait de rôles et de départements avec choix serveur, confirmation nominative et rechargement. Retrait du dernier rôle bloqué dans l’interface et par le serveur. Ajouts déjà attribués exclus des choix; les attributions existantes restent visibles.
- Modification de ses propres rôles : `/auth/me` est relu avant de poursuivre les lectures ADMIN. Un retrait de son propre rôle ADMIN indique la perte d’accès dans la confirmation. Le backend ne garantit pas la conservation d’un dernier administrateur global; ce contrôle n’est pas simulé côté client.
- Réinitialisation du mot de passe depuis la fiche avec double saisie, confirmation et effacement des champs après réussite/fermeture du formulaire. Le service actuel ne révoque pas les sessions existantes lors de cette opération; aucune révocation n’est annoncée par l’interface.
- Statut du compte et historique : consultation du statut actuel, pas de changement proposé faute de catalogue de statuts autorisé, ni de faux historique. Pas d’édition générale de profil inventée. Les composants d’administration sont réservés à ADMIN.
- Correction ponctuelle d’un caractère `6` isolé dans `EvaluationNotes.jsx`, qui provoquait une erreur de lint.

Fichiers : `api/{utilisateurApi,referentielApi}.js`, `utils/utilisateurForms.js`, `pages/utilisateurs/{CreateAccount,AccountAdministration,ResetPassword,UtilisateursPage,UtilisateurDetailPage}.jsx`. Endpoints : GET `/api/roles`; POST `/api/utilisateurs`; GET/POST `/api/utilisateurs/{id}/roles`, DELETE `/api/utilisateurs/{id}/roles/{roleCode}`; GET/POST `/api/utilisateurs/{id}/departements`, DELETE `/api/utilisateurs/{id}/departements/{departementId}`; PATCH `/api/utilisateurs/{id}/mot-de-passe`. Lecture des départements via le référentiel d’organisation autorisé.

Vérifications documents/administration : **82 tests réussis sur 82**, lint réussi, build réussi (106 modules). Tests ajoutés : contrats DTO/HTTP, codes de rôles encodés, signaux des lectures, erreurs 403/413, limites de transfert, multipart, téléchargement authentifié et nettoyage DOM simulé, mots de passe préservés/confirmation, dernier rôle, permissions et rendu SSR. Aucune mutation sur serveur réel, aucun navigateur utilisé, aucune dépendance ajoutée ni modification backend. Les essais HTTP/PostgreSQL, les interactions et la validation visuelle restent non effectués.

### Suite phase 5 — Référentiels, alertes et pilotage

- Référentiels : création et modification des sept familles autorisées (départements, structures d’accueil, cadres de stage, établissements, filières, niveaux d’étude, mois de stage). Formulaires alignés sur les DTO et limites des services; sélection de département depuis l’API, effacement des valeurs facultatives, conservation du booléen `false`. Les routes académiques et organisation affichent désormais leurs groupes respectifs.
- Départements : édition du libellé avec `statutCode: null`, qui conserve le statut dans le service existant. La création laisse le serveur choisir son statut par défaut. Aucun catalogue de statuts supposé, ni suppression de référence exposée. Les écritures sont réservées à ADMIN; une nouvelle sélection de référentiel réinitialise le formulaire et la liste.
- Alertes : filtres locaux combinables par type, nature, priorité et statut, recherche/pagination dans la liste filtrée. Détail en dialogue depuis les données du GET de liste : message, action recommandée, dates, auteur du traitement, liens vers participation/rapport/encadreur/soutenance. Aucun GET de détail inventé. Les liens du dashboard peuvent présélectionner un statut ou ouvrir une alerte reçue dans la liste.
- Traitement individuel confirmé, disponible uniquement pour nature A_TRAITER et statut A_TRAITER selon les permissions existantes. Pas de traitement global, d’action « ignorer », de détecteur ou de seuil reproduit côté client. La création manuelle utilise des sélecteurs de dossiers réels; le code du type d’alerte reste saisi, son catalogue étant fermé. Le serveur décide de la nature à partir du type.
- Dashboard : six compteurs strictement issus du DTO, liens vers les modules correspondants et aperçu des six premières alertes renvoyées par `/non-lues`. Libellé corrigé en « Alertes à traiter », conforme au filtre réel du service. Les compteurs absents, négatifs ou de type inattendu provoquent une erreur explicite au lieu d’un zéro fictif. Compteurs et alertes se chargent indépendamment, avec annulation et actualisation; un échec n’efface pas la section ayant réussi.
- Audit ADMIN : lectures globales, par utilisateur sélectionné et par nom exact d’entité/identifiant; filtres appliqués explicitement et recherche locale dans les résultats. Détail de l’action et des anciennes/nouvelles valeurs avec lien utilisateur. Rendu JSON échappé par React et masquage récursif des valeurs associées aux clés de secrets identifiables. Aucune mutation ou suppression du journal.

Fichiers principaux : `utils/{businessForms,referentiels,pilotage}.js`, `components/common/BusinessForm.jsx`, pages des référentiels/alertes/dashboard/audit, `api/auditApi.js`, styles et `tests/pilotage*.test.js`. Les modifications des phases précédentes ont été conservées.

Endpoints intégrés ou complétés : GET/POST/PATCH des référentiels autorisés `/api/referentiels/{academiques,organisation}/…`; GET/POST `/api/alertes`, PATCH `/api/alertes/{id}/lire`, GET `/api/alertes/non-lues`; GET `/api/dashboard/resume`; GET `/api/journal-audit`, `/api/journal-audit/utilisateur/{utilisateurId}`, `/api/journal-audit/entite/{entite}/{entiteId}`. Les catalogues fermés ne sont pas appelés.

Vérifications : **90 tests réussis sur 90**, lint réussi, build réussi (109 modules). Ajouts : contrats de champs des nouveaux formulaires contre les DTO, sept PATCH de référentiels contre l’inventaire, booléens/limites/statut conservé, filtres d’alertes et liens, six champs du dashboard contre le DTO, absence de zéro de repli, filtres d’audit et encodage des chemins, masquage récursif, rendu SSR des groupes/droits/liens/valeurs. Aucune dépendance ajoutée ni modification backend, aucun navigateur utilisé. Ces tests ne prouvent pas les interactions de dialogue, le rendu mobile ni les transactions PostgreSQL.

### Phase 6 — Premiers contrôles d’intégration

- Correction du routage : un changement des paramètres d’URL remonte la page et réinitialise ses filtres. Passer d’alertes à traiter aux alertes traitées ne conserve plus le filtre précédent. Un changement d’ancre seul conserve les formulaires en cours.
- `frontend/scripts/{apiReadiness,check-api}.js` et `npm.cmd run check:api` : contrôle HTTP en lecture seule, cible configurable, délai par requête, redirections refusées. Vérification des 401 publics, du profil, des droits métier/ADMIN, des six compteurs, des tableaux et d’un échantillon de fiches/sous-dossiers disponibles. Les résultats affichent chemins, statuts et tailles de listes, jamais les jetons ni le contenu des dossiers.
- Tests du contrôleur de vérification sur un serveur HTTP Node local simulant les réponses selon l’inventaire : ADMIN, RESPONSABLE_STAGE, ENCADREUR et STAGIAIRE. Ces réponses simulées ne valident pas les droits des comptes PostgreSQL réels.
- Test Spring existant étendu : serveur HTTP temporaire sur boucle locale, schéma Hibernate en mode `validate`, orchestration planifiée remplacée par un mock. Six GET anonymes refusés et un jeton inconnu refusé; aucune connexion utilisateur ni écriture métier déclenchée par ces tests.
- Guide reproductible : [validation d’intégration](docs/FRONTEND_VALIDATION.md), avec portée des contrôles, codes de sortie et parcours restants.

Résultats : **97 tests frontend réussis sur 97**, lint réussi, build réussi (109 modules). `mvn.cmd -o test` **réussi, 2 tests**, connexion PostgreSQL 15.15 et validation Hibernate du schéma existant réussies. Cela ne vérifie pas l’installation des triggers, les vues métier ni les transactions d’écriture. Le serveur temporaire s’arrête à la fin du test; aucun service permanent n’est lancé. `npm.cmd run check:api` sur le port 8080 a retourné le code 1 attendu pour une connexion impossible; les lectures authentifiées réelles n’ont pas été exécutées. Aucun navigateur utilisé.

Prochaine étape : exécuter les lectures avec des sessions de test habilitées, puis les parcours d’écriture sur une base dédiée. Préparer ensuite les changements de contrats manquants pour accord utilisateur. Les catalogues manquants, historiques et clôture ne sont pas déclarés opérationnels.

### Reste à réaliser

| Phase | État et prochaine action |
|---|---|
| 2 — Authentification/structure | Implémentée et vérifiée localement; validation avec serveur réel et vérification visuelle restent non effectuées. Voir le compte rendu ci-dessus. |
| 3 — Ressources principales | Édition, choix par API, profils académiques, participants et événements intégrés et vérifiés localement. Historique/statuts encadreur dépendent des API manquantes; essais réels à effectuer. |
| 4 — Suivi métier | Rapports, évaluations, soutenances et transferts de documents intégrés, vérifiés localement. Catalogue des types/complétude documentaire et clôture dépendent des écarts backend. |
| 5 — Administration/pilotage | Comptes, attributions, réinitialisation, sept référentiels modifiables, alertes filtrables avec détail/liens, dashboard et audit intégrés et vérifiés localement. Statuts/catalogues fermés et essais réels restent en attente. |
| 6 — Finalisation | En cours : Spring/PostgreSQL et refus HTTP vérifiés, outil de contrôle des lectures livré. Comptes réels, écritures métier, triggers et contrôle visuel/responsive/accessibilité restent à valider. |

Une fonctionnalité ne sera marquée terminée qu’après vérification de ses écrans, appels, validations et droits. Les changements de contrat backend doivent faire l’objet d’une proposition concrète et de l’accord de l’utilisateur.
