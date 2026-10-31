# Audit d’intégration — actualisé le 31 octobre 2026

Les écarts initiaux et leurs corrections sont détaillés dans [INTEGRATION_COMPLEMENTS.md](INTEGRATION_COMPLEMENTS.md). Onze endpoints de lecture ont été ajoutés et les parcours HTTP ont été exécutés sur PostgreSQL isolé, sans modification des données métier de production.

## Périmètre

Audit des sources locales, sans modification du code de production backend ni application de script SQL. L’[inventaire](API_INVENTORY.md) couvre 50 contrôleurs et 276 opérations : signatures, paramètres, DTO, réponses, succès HTTP déclarés, permissions Spring et erreurs explicites des services. La [version JSON](api-inventory.json) permet les contrôles de contrat. 103 opérations sont admises par SecurityConfig, dont quatre routes techniques de détection. Une opération admise peut encore être refusée par le service ou PostgreSQL.

Sources : controller, dto, model/enums, service, repository, config, security, scheduler, application.properties, pom.xml et conception/Conception2.sql. Le générateur relit tous les mappings, DTO, champs des modèles et erreurs explicites des services; les conclusions métier ci-dessous résultent de l’inspection des services de gestion et du SQL.

Les sondes initiales sur `http://localhost:8080` n’ont pas répondu. En phase 6, le contexte Spring a été chargé avec PostgreSQL 15.15 et Hibernate `validate`; deux tests passent sur un serveur HTTP temporaire : six routes protégées refusent les accès anonymes et `/api/auth/me` refuse un jeton inconnu. Les tests neutralisent l’orchestration planifiée. Cela ne valide pas les parcours authentifiés, les triggers ni les vues métier. Aucun compte ni dossier de test n’a été créé. Voir le [guide de validation](FRONTEND_VALIDATION.md).

## État initial du frontend

React 19, Vite 8, JavaScript ESM, CSS classique. Seules dépendances d’exécution : React et React DOM. Routage History API existant, formulaires React, HTTP fetch. Pas d’infrastructure de tests configurée; node_modules présent.

Les modules ont des pages de liste et de création, mais les routes de détail pointent sur les listes. Plusieurs fichiers DetailPage ne font que réafficher leur liste. Les formulaires demandent encore des identifiants numériques. Les événements, profils académiques et transitions des rapports sont absents des écrans. Aucun module complet ne peut être déclaré terminé sur cette seule base.

## Transport, session et permissions

- Java 17, Spring Boot 4.1.0, port 8080, préfixe `/api`, pas de context-path observé.
- Pas de configuration CORS ni de @CrossOrigin : proxy Vite en développement, reverse proxy `/api` en production. Une origine API externe demande une configuration serveur appropriée.
- **Session opaque, pas JWT** : 32 octets aléatoires encodés Base64 URL, empreinte SHA-256 en base, validité huit heures. Ni refresh token, ni date d’expiration dans la réponse.
- Login : identifiant/motDePasse → token/utilisateurId/identifiant/roles. Compte ACTIF requis. `/auth/me` renvoie utilisateurId/identifiant/roles uniquement. Logout révoque la session, réponse 204.
- Le filtre revalide session et rôles à chaque requête. 401 signifie session absente/invalide; 403 signifie accès refusé. Pas de nom complet ou département disponible dans le profil de session.
- Rôles métier : ADMIN, RESPONSABLE_STAGE, ENCADREUR. Le SQL initialise aussi **STAGIAIRE**, sans accès aux dossiers dans SecurityConfig. Les GET des référentiels académiques/organisation sont accessibles à toute session authentifiée.
- Gestion utilisateurs, écriture référentiels et consultation audit : ADMIN. Écriture stagiaires/stages/participations/encadreurs/soutenances/fichiers : ADMIN ou RESPONSABLE_STAGE. Rapports/évaluations/traitement alertes : les trois rôles métier.
- Les listes utilisent généralement findAll : pas de périmètre « mes dossiers » ou « mon département ». Un filtre frontend ne constitue pas un contrôle d’accès.
- Les CRUD bruts de sessions, associations de rôles, historiques et de nombreux référentiels sont fermés, même à ADMIN. Utiliser les contrôleurs de gestion avec DTO.

## Contrats et validations

Listes : tableaux JSON directs, sans enveloppe data, pagination, recherche ou filtre serveur. Pagination/recherche frontend sur les lignes reçues uniquement. Les PATCH reçoivent souvent un DTO complet. Validation surtout manuelle dans les services, sans @Valid dans les contrôleurs de gestion. Pas de ControllerAdvice personnalisé; les exceptions SQL ne sont pas systématiquement traduites en 409. Le client prévoit les erreurs JSON, ProblemDetail, HTML ou vides sans afficher de page HTML comme message.

| Domaine | Règles confirmées dans les services |
|---|---|
| Stagiaire | Nom obligatoire ≤150, prénoms obligatoires ≤250; adresse obligatoire ≤250, contact ≤100, email ≤150. Statut initial ACTIF. |
| Profil académique | Niveau, filière, établissement, année obligatoires; année YYYY-YYYY. Combinaison unique pour le stagiaire. Ajout/liste via `/stagiaires/{id}/academiques`, pas de modification autorisée. |
| Encadreur | Nom/prénoms obligatoires ≤250; département obligatoire. Fonction/localité/contact/courriel ≤150, porte ≤30. Capacité facultative strictement positive. Utilisateur facultatif, association unique à la création. |
| Stage | Libellé obligatoire ≤150, structure existante obligatoire; unicité libellé/structure. Département via la structure. |
| Participation | Stage, profil académique, cadre, encadreur et dates obligatoires; fin ≥ début. Unicité stage/profil/date de début à la création. Statut initial PLANIFIEE. |
| Rapport | Participation, mois, début/fin obligatoires; fin ≥ début; doublon de période refusé à la création. Activité/difficultés/solutions facultatives. Initial BROUILLON; modification interdite si VALIDE. |
| Observation | Commentaire obligatoire; type de l’enum, défaut OBSERVATION; visibleStagiaire par défaut true. Auteur issu de la session. |
| Évaluation | Une par participation; participation/encadreur obligatoires. Critère actif, note obligatoire de 0 à 5, alignée sur le CHECK PostgreSQL après échec confirmé du test réel. POST note crée ou remplace. Finalisation exige les critères actifs obligatoires notés et rend l’évaluation non modifiable. Pas de score global exposé. |
| Soutenance | Participation/dateSoutenance obligatoires, lieu ≤150, observation facultative. LocalDateTime sans offset. |
| Utilisateur | Identifiant unique ≤50, mot de passe initial ≥6 caractères dans le service actuel; rôle initial actif. Réinitialisation avec nouveauMotDePasse ≥12 caractères. Retrait du dernier rôle interdit. Pas de PATCH général du profil. |
| Référentiels | DTO dédiés pour établissements/filières/niveaux/mois/départements/structures/cadres. Lecture authentifiée; création/modification ADMIN. Rôles en lecture ADMIN seulement. |

Les champs exacts figurent dans l’inventaire. Les colonnes SQL apportent des contraintes supplémentaires et ne sont pas toutes des champs de requête HTTP.

## Événements et clôture

POST dédiés : prolonger, suspendre, reprendre, abandonner, annuler, cloturer. PATCH simple de statut : statutCode/motif. Les actions dédiées ne doivent pas être remplacées par le PATCH.

Suspension EN_COURS → SUSPENDUE et reprise SUSPENDUE → EN_COURS : dateEffet obligatoire, dans la période du stage, non future à Madagascar, strictement après la dernière suspension/reprise. Le DTO contient dateEffet et motif. L’événement stocke dateEvenement/dateEffet; l’historique distinct stocke ancien/nouveau statut et responsable/motif. Leurs routes de lecture brutes restent bloquées.

Prolongation : nouvelleDateFin strictement après la fin actuelle, événement PROLONGATION avec anciennes/nouvelles dates. Abandon cible ABANDONNEE; annulation cible ANNULEE. Le Java ne vérifie pas toutes les transitions; le SQL contient le trigger verifier_transition_autorisee, dont l’installation réelle reste à confirmer.

**Clôture corrigée le 31 octobre** : événement **CLOTURE**, statut **CLOTUREE**, transition active contrôlée, participation verrouillée et date enregistrée dans la même transaction. Les pièces actives obligatoires de participation doivent être présentes. Le PATCH vers CLOTUREE passe par le même workflow. Deux GET exposent les actions autorisées et les pièces obligatoires avec leur présence. Aucun prérequis supplémentaire sur les rapports, évaluations ou soutenances n’a été inventé. Persistance et refus vérifiés sur PostgreSQL isolé.

## Rapports et périodes

CalculPeriodesRapportService produit des semaines finissant le dimanche, tronquées aux bornes du stage, avec échéance le lundi suivant ce dimanche. GET `/api/stage-participants/{id}/periodes-rapports` expose ces périodes, le rapport associé et les indicateurs soumis/validé/exigible/manquant. Comme le détecteur, SOUMIS/VALIDE/A_CORRIGER avec dateSoumission compte comme remis; BROUILLON ne compte pas. Le retard commence après le lundi. Le calcul existant des semaines est conservé, y compris pendant une suspension.

Soumission/validation via PATCH `/rapports-activite/{id}/statut`, sans routes `/soumettre` ou `/valider`. SOUMIS renseigne dateSoumission, VALIDE dateValidation. SQL : BROUILLON → SOUMIS; SOUMIS → A_CORRIGER ou VALIDE; A_CORRIGER → SOUMIS. Le service vérifie l’existence du statut mais pas cette matrice. Les triggers SQL contrôlent transitions, appartenance de la période au stage et absence de chevauchement. Leur installation PostgreSQL reste non vérifiée.

## Documents

Multipart POST `/stagiaires/{id}/fichiers` ou `/stage-participants/{id}/fichiers` : parties **fichier**, **typePieceJointeCode**, réponse 200 FichierResponse. Type actif, périmètre et multiplicité vérifiés. Téléchargement authentifié `/fichiers/{id}/telecharger`, sans lien public.

Limites configurées : 10 MB par fichier **et par requête**, comprenant l’enveloppe multipart. Pas de liste blanche MIME/extension dans le service; seule l’extension de stockage est nettoyée. Le paramètre de taille en base n’est pas utilisé par ce service. Catalogue des types de pièces bloqué : sélection dynamique indisponible. Les codes du SQL (CV, CONVENTION_STAGE, RAPPORT_FINAL, ATTESTATION_STAGE, AUTRE) ne prouvent pas la configuration active de la base.

## Alertes, dashboard et audit

Scheduler : sept contrôles à 06:00 Indian/Antananarivo, exclusivement serveur. Types d’alertes stockés en base; les sept codes demandés sont confirmés par SQL/détecteurs.

Priorités : INFORMATION, AVERTISSEMENT, URGENT. Statuts : A_TRAITER, TRAITEE, EXPIREE, IGNOREE. **NatureAlerte distincte : INFORMATION ou A_TRAITER**. `/non-lues` liste les A_TRAITER. `/{id}/lire` traite une alerte et refuse nature INFORMATION ou statut différent de A_TRAITER. `/tout-lire` retourne **toujours 409** malgré son mapping 204 déclaré; bouton retiré. Pas d’endpoint pour ignorer ou lire un détail individuel (détail possible depuis les données de liste).

Dashboard : uniquement totalStagiaires, totalStages, totalParticipations, totalRapports, totalAlertesATraiter, totalAlertesTraitees. Pas de compteurs actifs/terminés/en attente exposés. Charge encadreur calculée par une vue SQL utilisée par la détection, sans DTO de charge.

Audit : lecture ADMIN globale, par utilisateur, par entité/id; utilisateur, action, ressource, horodatage et anciennes/nouvelles valeurs disponibles. Pas d’écriture/suppression frontend.

## API manquantes / écarts d’intégration

| Écart | Conséquence / suite proposée |
|---|---|
| Historique/événements bloqués | Prévoir GET avec DTO sûrs par participation; ne pas appeler les CRUD fermés. |
| Clôture Java/SQL incohérente, préconditions absentes | Réconcilier codes et règles serveur, proposer une API de préparation. Accord utilisateur requis avant changement backend. |
| Types de pièces/statuts/transitions/événements/alertes/seuils/paramètres fermés | Proposer des API de lecture et des droits d’administration explicites; pas de référentiels inventés. |
| Périodes attendues et charge non exposées | Utiliser les alertes existantes; pas de détection ou capacité de remplacement en JavaScript. |
| Suppression/désactivation stagiaire et retrait documentaire non autorisés | Masquer ces actions. |
| Profil académique seulement ajout/liste | Pas d’édition inventée. |
| Profil session minimal et rôle STAGIAIRE non habilité | Ne pas inventer département, nom ou portail stagiaire. |
| Aucun périmètre métier individuel | Les filtres locaux ne protègent pas les ressources. |
| Pas de contrat uniforme d’erreurs SQL | Message serveur si disponible, repli HTTP sinon. |
| Parcours authentifiés non vérifiés | Spring/PostgreSQL et refus HTTP vérifiés; utiliser le contrôle des lectures avec des sessions de test, puis valider les écritures et triggers sur une base dédiée. |

Aucun changement du code de production backend n’est inclus; seuls ses tests ont été étendus en phase 6. Les phases suivantes peuvent intégrer les opérations autorisées et documenter séparément les propositions de contrat à soumettre à l’utilisateur.
