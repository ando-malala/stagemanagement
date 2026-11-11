# Audit de cohérence métier — MEF–SIIGFP

Date : 11 novembre 2026. Audit du code présent dans le dépôt ; aucune écriture dans la base métier. Les modifications UI des alertes déjà présentes sont conservées.

**Mise à jour — relance autorisée du 11 novembre 2026 à 14:47 : 66 tests backend réussis, dont les 13 tests PostgreSQL, aucun échec ni test ignoré. V002 et V003 ont été appliquées sur la nouvelle base isolée `mef_integration_a8d83bc31384434987f80816152c8bd3`. Le préflight SQL s'est exécuté sans erreur. Les trois échecs précédents et le scénario de concurrence des comptes sont désormais vérifiés avec succès.** Les mentions de relance refusée/non exécutée ci-dessous décrivent le point de contrôle antérieur et sont remplacées par ce résultat. Aucune migration appliquée à la base métier.

## 1. Cartographie

Les formulaires partagent `BusinessForm`, `businessFields`, `loadBusinessChoices`, `useFetch`, `Input` et les clients API. Les PATCH métier remplacent généralement tous les champs éditables. Les contrôleurs CRUD bruts ne constituent pas des points d'entrée autorisés : `SecurityConfig` refuse par défaut les routes non admises. Les services de gestion et `DossierAccessService` sont les points de contrôle réels.

| Pages / formulaire | Sélections et dépendances | API / services responsables | Protection existante et historique |
|---|---|---|---|
| Utilisateurs : création, rôles, mot de passe | Rôle actif supporté ; API supplémentaires de département et statut | UtilisateurController, UtilisateurGestionService | ADMIN, identifiant unique, dernier rôle actif conservé, sessions révoquées au reset ; statut et département non exposés par la page actuelle |
| Stagiaires : fiche et création | Identité et coordonnées | StagiaireGestionController / Service | ADMIN en écriture, ACTIF à la création, longueurs SQL |
| CompteStagiaire (fonctionnalité existante) | Compte utilisateur facultatif ; dépend du stagiaire édité | PortailStagiaireController / Service | STAGIAIRE actif, unicité utilisateur ; aucun endpoint de choix éligibles initialement |
| AcademiquesPanel | Niveau, filière, établissement, année | StagiaireGestionService | Combinaison unique par stagiaire ; consultation historique ; pas d'édition de profil exposée |
| Encadreurs | Compte facultatif, département, capacité | EncadreurGestionController / Service | ADMIN, capacité positive, compte unique en SQL, statut ENCADREUR |
| Stages | Structure d'accueil | StageGestionController / Service | ADMIN, unicité libellé/structure |
| Participations | Stagiaire → profils académiques ; stage, cadre, encadreur, dates | StageGestionService, ParticipationRulesService | Pas de chevauchement pour une personne ; verrou stagiaire ; périodes des rapports préservées ; événements dédiés après démarrage |
| ParticipationActions / clôture | Transitions, date d'effet, prolongation, motif, dérogations de clôture | StageGestionService, DocumentCatalogueService | Transitions actives et triggers, participation verrouillée ; dérogations administratives existantes conservées |
| Rapports / MesRapports / RapportWeeks | Participation de l'auteur → semaines serveur ; mois de stage | RapportActiviteGestionService, CalculPeriodesRapportService | Rédaction par le stagiaire propriétaire ; correction/validation par son encadreur ; pas d'auto-validation ; VALIDE immuable |
| RapportObservations | Type d'observation, visibilité, commentaire | RapportActiviteGestionService | Auteur issu de session ; périmètre du rapport |
| Évaluations / EvaluationNotes | Participation → encadreur affecté ; critères actifs ; notes | EvaluationGestionService, DossierAccessService | Une évaluation par participation ; 0 à 5 ; critères obligatoires ; finalisation verrouillée |
| Soutenances | Participation, date/heure, lieu | SoutenanceGestionService | ADMIN ; une soutenance par participation ; dossiers terminés verrouillés |
| DocumentsPanel / Fichiers | Propriétaire, type de pièce selon portée, fichier | FichierGestionService, DocumentCatalogueService, DocumentContentValidator | PDF contrôlé, taille, multiplicité, propriétaire, accès à toutes les associations d'un fichier |
| Alertes / dashboard | Type réel, priorité, participation, rapport, encadreur, soutenance | AlerteGestionService / Detection / Cloture, DashboardService | Nature distincte de priorité ; information non traitable manuellement ; déduplication ; périmètres |
| Référentiels académiques | Établissements, filières, niveaux, mois | ReferentielAcademiqueService | ADMIN en écriture ; actif booléen ; historiques conservés |
| Référentiels organisation | Département/statut, structure/département, cadre | ReferentielOrganisationService | ADMIN en écriture ; référentiels listés même inactifs pour administration |
| Référentiels de suivi | Statuts, transitions, types pièces/alertes/événements, critères | ReferentielWorkflowService, DocumentCatalogueService, EvaluationGestionService | Catalogues spécialisés ; CRUD techniques interdits |
| Audit / exports / synthèses | Utilisateur ou entité/id (recherche), format d'export | JournalAuditService, ExportService, DossierSyntheseService | Lecture autorisée par rôle et périmètre ; aucun lien métier créé par ces filtres |

### Grille commune des sélections

Pour chaque association : identifier l'entité attendue par le DTO ; charger uniquement les données autorisées ; exclure les nouvelles références inactives ; conserver la référence courante historique si l'opération ne la change pas ; recalculer les choix dépendants ; afficher chargement, erreur et absence de choix ; conserver les saisies sur rejet ; revalider côté serveur au moment de l'écriture. Une liste valide à l'ouverture ne garantit pas une écriture ultérieure. Les contraintes UNIQUE et les verrous doivent couvrir les courses concurrentes.

## 2. Anomalies et analyse

| Module | Champ | Comportement initial | Comportement attendu / preuve | Risque | Fichiers concernés | Priorité |
|---|---|---|---|---|---|---|
| Stagiaire | utilisateurId | Tous les utilisateurs ACTIF proposés ; sélection initialement vide | STAGIAIRE actif libre ou lié à ce stagiaire ; compte courant présélectionné (mission + validation serveur existante) | Mauvais choix, dissociation involontaire | CompteStagiaire, PortailStagiaireService | Élevée |
| Encadreur | utilisateurId | Compte existant quelconque accepté ; doublon vérifié seulement en création | Compte ENCADREUR actif ; unicité aussi en modification | Association incompatible | EncadreurGestionService, businessChoices | Élevée |
| Références | IDs des référentiels | Existence souvent suffisante dans les services et sélecteurs | Référence active pour nouvelle association ; référence historique inchangée conservée | Affectation invalide | Stagiaire/Stage/Encadreur/ReferentielOrganisation/Rapport services, BusinessForm | Élevée |
| Utilisateurs | rôles concurrents | Dernier rôle vérifié sans verrou utilisateur | Sérialiser les changements de rôles et associations | Retrait simultané laissant aucun rôle ; association invalidée | UtilisateurGestionService, UtilisateurRepository | Critique |
| Évaluation | participation/encadreur | Serveur contrôle la correspondance, UI propose tous les encadreurs | Liste dépendante de la participation ; éviter dossiers terminés | Rejets évitables | businessChoices, BusinessForm | Moyenne |
| Alerte | références multiples | Existence et périmètre vérifiés indépendamment | Rapport et soutenance du même dossier ; encadreur cohérent si dossier indiqué | Fausse alerte associée à plusieurs dossiers | AlerteGestionService, BusinessForm | Élevée |
| Participation | affectations modifiées | Encadreur remplaçable même avec une évaluation attribuée à l'ancien | Ne pas invalider une évaluation existante | Relation évaluation/affectation incohérente | StageGestionService | Élevée |
| Évaluation | note | Bornes 0–5 mais précision SQL NUMERIC à vérifier | Refuser précision non représentable sans arrondi silencieux | Donnée enregistrée différente | EvaluationGestionService, EvaluationNotes | Moyenne |
| Coordonnées | email/courriel | Longueur serveur uniquement | Format conforme au champ email de l'interface | Contournement de validation | DTO, services de gestion | Moyenne |
| Vérification | lanceur intégration | Référence un répertoire migrations absent | Exécuter le schéma courant et migrations présentes uniquement | Tests impossibles | scripts/test-integration.ps1 | Moyenne |
| Rapports | période SQL | SQL vérifie les bornes et chevauchements, mais accepte une fin de semaine décalée | Même découpage lundi–dimanche, tronqué aux bornes du stage, que CalculPeriodesRapportService | Écart entre service et base | Conception2.sql, migration V003 | Élevée |
| Participations | chevauchement SQL | UNIQUE(stage, profil, début) ne couvre ni les débuts différents ni plusieurs profils d'une personne | Protection conforme à ParticipationRulesService avec verrou stagiaire | Contournement par import SQL, collision | Migration V003 | Élevée |
| Tests | anciens rôles | Fixtures supposent que RESPONSABLE_STAGE est présent dans un schéma neuf | Créer explicitement cette donnée de test historique ; conserver les trois rôles de production | Tests de compatibilité invalides | CompleteIntegrationTests | Moyenne |
| Évaluations | affichage des notes | Colonnes « Note / 20 » alors que saisie, API et CHECK imposent 0–5 | Afficher « Note / 5 » sans transformer les valeurs | Mauvaise interprétation de la note | EvaluationNotes.jsx | Moyenne |
| Référentiels | longueur libelle | Limite générique 150 ; niveaux limités à 100 et mois à 50 en SQL | Même maximum dans le formulaire et le service | Rejet SQL tardif d'une saisie acceptée | ReferentielAcademiqueService, BusinessForm, referentiels.js | Moyenne |

## 3. Règles nécessitant une décision métier

- Aucun alignement forcé entre département de l'encadreur et structure du stage : aucune règle de ce type démontrée.
- La capacité est actuellement une source d'alerte de surcharge, pas un quota bloquant : conserver ce comportement.
- Identifiants utilisateur : unicité sensible à la casse selon SQL et authentification ; ne pas imposer arbitrairement une normalisation différente.
- Homonymes stagiaires et courriels partagés : pas de clé métier d'identité suffisante pour bloquer des doublons sur nom/prénoms.
- Année académique : format YYYY-YYYY démontré ; durée exacte d'une année et cas de chevauchement nécessitent confirmation.
- Mois de stage : référentiel de libellés, sans ordinal/calendrier imposé ; ne pas déduire arbitrairement le mois à partir de la semaine.
- Date de soutenance : pas de règle démontrée imposant une date après la fin du stage. Une règle supplémentaire doit être validée.
- Comptes multirôles : présence du rôle attendu, jamais égalité à une liste d'un seul rôle. Ne pas interdire globalement un compte multirôle lié aux deux catégories sans règle explicite ; l'auto-validation des rapports reste interdite.
- Désactivation d'un compte déjà lié : préserver le lien historique et bloquer son accès via l'authentification ; la dissociation automatique serait une nouvelle règle.

## 4. Corrections et vérifications

### Corrections présentes dans les fichiers

- **Compte du stagiaire** : la fonctionnalité existait dans `CompteStagiaire.jsx`. Ajout de `GET /api/stagiaires/{id}/comptes-eligibles`, réservé ADMIN. La requête teste la présence du rôle STAGIAIRE actif, le statut utilisateur ACTIF actif et l'absence de lien vers un autre stagiaire. Le compte courant éligible reste proposé. La réponse ne contient que l'identifiant du compte et son nom de connexion. La sélection initiale reprend le compte associé ; un ancien compte inéligible reste visible, désactivé, jusqu'à une dissociation ou un remplacement explicite. Le composant est réinitialisé quand on change de fiche.
- **Encadreur** : endpoint analogue `GET /api/encadreurs/comptes-eligibles?encadreurId=…`, rôle ENCADREUR, activité et unicité contrôlés dans le service à la création et au remplacement. Un compte historique inchangé n'est pas supprimé automatiquement. Aucun filtre arbitraire de département commun au stage n'a été ajouté.
- **Concurrence des comptes** : verrou pessimiste sur l'utilisateur pour les associations, ajouts/retraits de rôles, changement de statut et reset. Le retrait du rôle nécessaire à une fiche encore associée est refusé. Les contraintes UNIQUE existantes restent en place.
- **Références actives** : vérifications lors de nouvelles associations académiques, de départements, de structures, de cadres, de stagiaires, d'encadreurs et de mois de rapport. Les références historiques inchangées sont conservées. Le DTO de stage fournit `eligibleNouvelleParticipation`, calculé d'après sa structure réelle.
- **Formulaires** : filtrage partagé par `eligibleChoices`, dépendance participation → encadreur évaluateur, cohérence des liens d'alerte, réinitialisation des sélections dépendantes, messages de liste vide, actualisation des choix, blocage des choix requis absents et contrôle d'appartenance à la liste avant envoi. Le backend revalide indépendamment. Les saisies restent en mémoire en cas de rejet.
- **Évaluations** : refus des notes à plus de deux décimales ; validation du DTO avant contrôle de référence ; nouvelle vérification des droits et de l'affectation après verrouillage. Réaffecter une participation à un encadreur différent de son évaluation existante est refusé. Correction des colonnes de notes sur 5.
- **Rapports** : contrôle du mois actif pour une nouvelle référence ; relecture du rapport après verrouillage de la participation pour ne pas modifier un état périmé ; recontrôle des droits ; cible de statut active.
- **Alertes** : refus des rapports/soutenances de dossiers différents, de l'encadreur incompatible, d'une alerte sans référence et d'une clé de déduplication dépassant 255 caractères.
- **Coordonnées et statuts** : validation serveur du format email/courriel dans stagiaires, encadreurs et structures ; contrôle d'activité des statuts cibles utilisateur, encadreur et département.
- **Longueurs des référentiels** : niveaux limités à 100 et mois à 50 caractères, dans les formulaires et le service, conformément aux colonnes PostgreSQL ; établissements et filières restent à 150. Tests aux bornes et au-delà.
- **UI alertes antérieure conservée** : mini-synthèse, filtres, tri priorité puis échéance, distinction nature/priorité/statut, chiffres exacts dans les statistiques et `9+` dans les seuls badges de notification. Les compteurs utilisent les réponses réelles. Le total « À traiter » est explicitement le statut toutes natures, sans rendre les informations traitables manuellement.

### Résultats réellement exécutés

| Commande / contrôle | Résultat observé | Limite |
|---|---|---|
| `python scripts/audit_backend.py` | 53 contrôleurs, 300 endpoints, 127 admis ; inventaires Markdown/JSON actualisés | Analyse statique des mappings et règles de sécurité |
| Dernier `node --test --test-concurrency=1 tests/*.test.js` dans frontend | **140 réussites, 0 échec** | Tests fonctionnels JS et rendu SSR, pas de parcours navigateur interactif |
| `npm.cmd run lint` et `npm.cmd run build` | Réussis | Build de production frontend |
| Premier `mvn.cmd test` | 38 réussites, 9 tests PostgreSQL ignorés | État intermédiaire avant ajout de tests |
| `scripts/test-integration.ps1` avec V002 sur une base locale neuve | **63 tests : 60 réussites, 2 échecs et 1 erreur, aucun ignoré** | Les nouveaux scénarios compte/références/alertes ont réussi ; les trois erreurs sont détaillées ci-dessous |
| Dernier `mvn.cmd test`, sans variable de base de test | **53 réussites, 13 tests PostgreSQL ignorés, 0 échec** | Ne valide pas V003 ni le nouveau scénario de concurrence des comptes |
| Tests ciblés businessEligibility/businessForms | 11 réussites | Cas de choix éligibles, historique, dépendances et payload |
| `git diff --check` | Pas d'erreur de patch/blancs | Avertissements Git LF/CRLF uniquement |

Les trois anomalies de l'exécution PostgreSQL étaient : deux fixtures utilisant un rôle historique absent du schéma neuf, et l'acceptation SQL d'une période de rapport qui ne correspond pas au découpage serveur. Les fixtures créent désormais explicitement leur rôle historique. V003 prépare les protections SQL de périodes et de chevauchement. **La relance PostgreSQL après ces changements a été refusée dans l'outil d'exécution ; ces corrections ne sont donc pas déclarées validées en intégration.** La compilation et les tests hors PostgreSQL ont ensuite été exécutés séparément avec succès.

Les essais ont créé uniquement `mef_integration_7d7e2a64b7844d88b0644e57baacf251`, conservée par le lanceur pour diagnostic ; les fichiers d'upload de test ont été nettoyés par ce lanceur. Aucune migration n'a été exécutée sur la base métier. Aucun accès interactif au navigateur n'a été validé pendant cette reprise.

### Matrice des cas préparés

| Correction | Validité / invalidité / référence | Autorisation / contournement / concurrence | Historique |
|---|---|---|---|
| Compte stagiaire | actif valide, multirôle, ADMIN seul refusé, compte absent/inactif, rôle inactif, compte utilisé | unitaires + HTTP ; SQL direct ADMIN refusé ; concurrence des revendications et retrait de rôle ajoutée, dernier scénario non exécuté | même compte accepté, dissociation acceptée, liste contextualisée |
| Compte encadreur | mauvais rôle et doublon refusés en HTTP ; liste contextualisée | endpoint ADMIN, autres rôles 403 ; trigger V002 exécuté | compte courant présent ; inéligible inchangé préservé par code, cas HTTP dédié à compléter |
| Référentiels / participation | structure inactive/absente, cadre inactif, niveau inactif, stage éligible calculé | HTTP forgé et INSERT SQL sur structure inactive refusés | stage et cadre inactifs inchangés modifiables ; profil historique lisible |
| Évaluation | DTO incomplet, note 4.123 refusée et 4.25 acceptée, réaffectation incompatible refusée | affectation contrôlée sous verrou ; changement direct SQL refusé | garde de finalisation existante couverte par le workflow complet |
| Rapports | semaines calculées, chevauchement, correction, finalisation, mois réel | tests de propriété, auto-validation interdite et concurrence existants ; V003 à exécuter | VALIDE immuable côté API, protection SQL ajoutée à vérifier |
| Alertes | sans lien, dossier incompatible, référence absente, clé trop longue, cas valide | STAGIAIRE ne peut créer ; accès croisé et clôture déjà couverts | nature informative conservée ; alertes historiques non réécrites |
| Emails | valide, optionnel, sans @, double @, espaces | contrôle service indépendant du frontend ; HTTP stagiaire invalide refusé | aucune normalisation des adresses déjà stockées |

Cette matrice ne signifie pas que chaque permutation de référence inactive, mutation concurrente et rôle a été exécutée pour chaque formulaire. Les limites ci-dessous restent ouvertes.

## 5. Audit détaillé des sélections

Pour répondre aux dix questions de la mission, chaque ligne précise la source et l'entité attendue (1–2), les critères de statut et d'association (3–5), le contexte (6), la modification historique (7) et le service responsable (8). Les comportements 9–10 sont communs : `useFetch` affiche le chargement et l'erreur sans les convertir en liste vide ; `BusinessForm` affiche l'absence de choix, bloque une sélection obligatoire vide et préserve la saisie sur rejet. Un choix devenu invalide est rejeté par le service, avec 404 s'il a disparu, 409 s'il est incompatible/inactif/déjà utilisé, 403 hors périmètre. L'utilisateur peut actualiser les choix. Les composants spécialisés ont leurs propres confirmations et états de chargement.

| Formulaire / champ | Données chargées et entité | Éligibilité et contexte | Modification / service serveur |
|---|---|---|---|
| CreateAccount / roleCode | GET `/roles`, rôles supportés | actif, rôle initial ; ADMIN | pas d'édition par création ; UtilisateurGestionService |
| MembershipPanel / roleCode | `/roles` + `/utilisateurs/{id}/roles` | actifs non déjà attribués ; ADMIN ; plusieurs rôles possibles | retrait du dernier rôle interdit, rôle de fiche liée protégé ; UtilisateurGestionService |
| Utilisateur / departementId | API organisation et départements attribués | département ACTIF ; **branche de composant non montée dans la page** | API contrôlée, attribution existante idempotente ; UtilisateurGestionService |
| Utilisateur / statutCode | API statuts UTILISATEUR | catalogue actif, ADMIN ; **aucun sélecteur monté** | changement et historique backend ; UtilisateurGestionService |
| CompteStagiaire / utilisateurId | `/stagiaires/{id}/comptes-eligibles`, projection compte | STAGIAIRE actif, compte ACTIF, libre ou lié au stagiaire courant | compte courant préselectionné ; PortailStagiaireService |
| AcademiquesPanel / niveauEtudeId | référentiel niveaux-etude | niveau actif ; ADMIN du dossier | anciens profils consultables ; StagiaireGestionService |
| AcademiquesPanel / filiereId | référentiel filieres | filière active ; ADMIN du dossier | idem ; aucune dépendance établissement-filière inventée |
| AcademiquesPanel / etablissementId | référentiel etablissements | établissement actif ; ADMIN du dossier | idem ; combinaison de profil unique |
| Encadreur / utilisateurId | `/encadreurs/comptes-eligibles`, projection compte | ENCADREUR actif, compte ACTIF et libre ou courant ; ADMIN | compte historique inchangé conservé ; EncadreurGestionService |
| Encadreur / departementId | organisation/departements | département ACTIF pour un nouveau rattachement | département historique inchangé conservé ; EncadreurGestionService |
| EncadreurStatut / statutCode | statuts/ENCADREUR | actif, différent du courant ; ADMIN | pas de nouvelle transition inventée ; historique serveur |
| Stage / structureAccueilId | organisation/structures-accueil | structure active, libellé et département lisibles | historique inchangé accepté ; StageGestionService |
| Participation / stagiaireId | `/stagiaires` | ACTIF, ou stagiaire courant ; choix d'aide non envoyé dans le DTO | déclenche chargement des profils ; contrôle serveur via le profil réel |
| Participation / stagiaireAcademiqueId | `/stagiaires/{id}/academiques` | profil du stagiaire choisi ; aucun booléen actif sur le profil | profil existant conservé ; références académiques anciennes consultables |
| Participation / stageId | `/stages` | structure réelle active pour nouvelle participation | stage courant conservé ; après démarrage, changement interdit par service |
| Participation / cadreStageId | organisation/cadres-stage | cadre actif pour nouvelle association | cadre courant historique conservé ; StageGestionService |
| Participation / encadreurId | `/encadreurs` | ACTIF ; capacité informative ; aucun filtre de département inventé | affectation protégée si évaluation existante ; StageGestionService |
| ParticipationActions / action | `/stage-participants/{id}/actions` | transitions et types d'événements actifs, état mutable | dates et événements spécifiques contrôlés ; StageGestionService |
| Rapport / stageParticipantId | `/stage-participants`, filtré peutRedigerRapport | propriété de l'auteur, rôle STAGIAIRE, dossier non terminal | fixe en édition ou depuis grille ; RapportActiviteGestionService |
| Rapport / semaine | `/stage-participants/{id}/periodes-rapports` | semaines serveur sans rapport, ou rapport courant | réinitialisée en changeant de stage ; périodes revérifiées sous verrou |
| Rapport / moisStageId | academiques/mois-stage | mois actif ; aucune correspondance calendaire arbitraire | mois historique inchangé conservé ; RapportActiviteGestionService |
| RapportActions / statut | transitions RAPPORT / permissions DTO | auteur soumet ; encadreur affecté examine ; pas d'auto-validation | VALIDE sans transition de sortie ; relecture après verrou |
| RapportObservations / typeObservation, visibleStagiaire | enum et booléen | trois valeurs connues, commentaire obligatoire, périmètre rapport | auteur issu de session ; valeurs invalides rejetées par conversion/validation |
| Évaluation / stageParticipantId | `/stage-participants` | dossier non terminal ; rôle/périmètre de suivi ; unicité serveur | évaluation finalisée non éditable ; EvaluationGestionService |
| Évaluation / encadreurId | `/encadreurs` filtré par participation | uniquement encadreur réellement affecté ; nul tant que stage non choisi | remis à vide si participation change ; contrôle serveur après verrou |
| EvaluationNotes / critereId | `/evaluations/criteres` actifs | sélection par bouton lisible, pas saisie d'un ID ; critères obligatoires | notes historiques listées séparément ; doublon traité comme modification |
| Soutenance / stageParticipantId | `/stage-participants` | dossier non terminal, ADMIN, soutenance unique | conflit si nouvelle participation déjà pourvue ; SoutenanceGestionService |
| DocumentsPanel / typePieceJointeCode | catalogue documentaire actif | portée stagiaire/participation et multiplicité ; propriétaire issu de fiche | documents anciens consultables ; FichierGestionService valide portée, contenu et droits |
| Alertes / typeAlerteCode | suivi/types-alertes | types actifs réels ; ADMIN en création | nature héritée du type, pas de nature inventée par UI |
| Alertes / priorite | enum INFORMATION/AVERTISSEMENT/URGENT | priorité distincte de nature et statut | enum contrôlé par service et CHECK |
| Alertes / stageParticipantId | participations autorisées | lien facultatif, mais au moins une référence exigée | changer de participation efface les liens d?pendants ; AlerteGestionService |
| Alertes / rapportId, soutenanceId | rapports et soutenances autorisés | même participation que les autres références indiquées | filtrage contextuel, refus 409 serveur si incompatible |
| Alertes / encadreurId | encadreurs autorisés | encadreur affecté si un dossier est indiqué ; lien seul autorisé | ancien encadreur ne disparaît pas des données historiques |
| Référentiels / actif | booléen | Oui/Non réel ; ADMIN | désactivation ne supprime pas les liens existants |
| Département / statutCode | suivi/statuts/DEPARTEMENT | statut de bon type et actif ; ADMIN | valeur facultative préserve l'existant en modification |
| Structure / departementId | organisation/departements | département ACTIF pour nouveau lien | historique inchangé conservé ; ReferentielOrganisationService |
| Audit / utilisateur, entiteId | filtre utilisateur et identifiant numérique d'entité | **recherche**, pas création de relation ; ADMIN | ID technique conservé car filtre d'audit, pas choix d'affectation |
| Listes / filtres, pagination, export | lignes et référentiels accessibles | aucune écriture d'association | filtres locaux, compteurs exacts, exports avec contrôle serveur |

Les champs identité/adresse/contact, libellés, thèmes, commentaires, appréciations, motifs, mots de passe, dates, année académique, capacité et note sont des saisies, pas des sélections de références. Les limites du DTO/SQL sont couvertes par les validateurs existants et `businessPayload` ; les identifiants métier numériques sont convertis en entiers positifs avant envoi. Les identifiants de propriétaire sont issus des fiches et restent recontrôlés côté serveur.

`UtilisateursPanel.jsx` est un ancien composant sans import dans les routes actives. `MembershipPanel` possède une branche départements, mais seul `kind="roles"` est monté actuellement. Ni leur présence dans les fichiers ni une API existante ne constituent une fonctionnalité effectivement affichée. Le changement de statut **stagiaire** reste absent de la gestion métier exposée : les statuts ACTIF/ARCHIVE existent en SQL, mais aucun workflow d'archivage de personne n'est déduit de la clôture d'une participation.

## 6. PostgreSQL, déploiement et points restant ouverts

Les FK et contraintes UNIQUE/CHECK du schéma ont été rapprochées des modèles JPA : compte unique par fiche, profil académique unique, une évaluation et une soutenance par participation, un critère par évaluation, notes 0–5, dates ordonnées, enums d'alertes et référence minimale. Les triggers existants contrôlent le type de statut, certaines transitions, les bornes des rapports et la portée des documents. Ils ne remplacent pas les droits utilisateur vérifiés par Spring.

- `backend/migrations/V002__association_eligibility.sql` : nouvelles associations compte/rôle/statut, protection du rôle lié, références actives, encadreur d'évaluation et réaffectation. **Exécutée seulement sur la base isolée.**
- `backend/migrations/V003__participation_report_consistency.sql` : semaines exactes, rapports validés, chevauchements entre profils et cohérence des changements de dates. **Préparée, non exécutée après refus de la relance.**
- `backend/migrations/checks/preflight.sql` : comptages d'anomalies historiques sans données personnelles ; lecture seule. Les migrations n'effacent ni ne corrigent silencieusement des lignes. Le préflight final du lanceur n'a pas été atteint parce que la suite d'intégration échouait.
- Aucun changement de table/colonne n'est requis ; les migrations ajoutent des fonctions et triggers. Elles doivent être appliquées explicitement après validation : Spring Boot ne les installe pas automatiquement. Voir `backend/migrations/README.md`.

**Vérification automatisée réussie après relance autorisée.** Les 13 tests PostgreSQL avec V002+V003 passent, y compris les contrôles HTTP, SQL et de concurrence ciblés. Une recette navigateur, l'audit des données déployées et les risques résiduels ci-dessous restent hors de cette vérification rapide ; les tests ne constituent pas une preuve exhaustive d'absence d'incohérence.

Risques résiduels identifiés, à traiter avant de conclure à une couverture totale :

1. Certains référentiels et libellés utilisent `IgnoreCase` dans les services mais des UNIQUE sensibles à la casse en SQL. Deux créations concurrentes ne différant que par la casse peuvent passer. Des index normalisés exigent d'abord un inventaire des collisions et une stratégie explicite pour les données existantes ; aucune fusion automatique n'a été effectuée.
2. La cohérence des liens d'alerte est désormais vérifiée à la création API ; le déplacement ultérieur d'une soutenance déjà référencée ou une réaffectation d'encadreur peut encore modifier son contexte historique. La politique de rattachement historique de ces alertes doit être fixée avant un trigger inverse ou une réécriture automatique.
3. Les documents sont protégés par portée, multiplicité, contrôle PDF et verrous dans les services. Le SQL seul ne porte pas toutes les règles de contenu/multiplicité pour des imports réalisés hors application. Les imports doivent passer par les services ou être contrôlés séparément.
4. La matrice de tests n'exécute pas encore toutes les permutations de désactivation concurrente de référentiels, changement de droits en cours de requête et conservation d'un ancien compte encadreur inactif. Les contrôles API et verrous présents ne constituent pas une preuve pour tous ces entrelacements.
5. Le statut de stagiaire, la règle de date des soutenances, l'identité utilisée pour détecter les homonymes et le mois de stage restent des décisions métier ; aucune automatisation arbitraire ajoutée.

### Principaux fichiers modifiés

Backend : `UtilisateurRepository`, `SecurityConfig`, `PortailStagiaireController/Service`, `EncadreurGestionController/Service`, `AssociationRules`, `UtilisateurGestionService`, `StagiaireGestionService`, `StageGestionService`, `StageResponse`, `ParticipationRulesService`, `RapportActiviteGestionService`, `EvaluationGestionService`, `ReferentielOrganisationService`, `AlerteGestionService`.

Les limites de libellés concernent aussi `ReferentielAcademiqueService`, `referentiels.js` et `ReferentielsPage.jsx`, avec tests dans `ReferentielValidationTests` et `businessEligibility.test.js`.

Frontend : `businessChoices.js`, `businessForms.js`, `businessEligibility.js`, `followUpWorkflows.js`, `BusinessForm.jsx`, `Input.jsx`, `CompteStagiaire.jsx`, `StagiaireDetailPage.jsx`, `EvaluationNotes.jsx` ; composants et utilitaires d'alertes de la demande précédente conservés.

Tests/docs : `CompteEligibilityTests.java`, `CompleteIntegrationTests.java`, `businessEligibility.test.js`, `businessForms.test.js`, tests d'alertes, inventaires API régénérés, présent audit et migrations. Le lanceur existant est réutilisé : les répertoires de migrations et de préflight qu'il attend sont maintenant présents.
