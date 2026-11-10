# 2 Conception et réalisation

## 2.1 Organisation des données

La conception observée repose sur trois niveaux distincts : la personne stagiaire, son profil académique et sa participation à un stage. La table stagiaires porte l’identité et les coordonnées. La table stagiaire_academique associe cette personne à un niveau, une filière, un établissement et une année d’études. La table stage_participant relie ensuite ce profil à un stage, un cadre de stage, un encadreur, des dates et un statut. Cette organisation évite de placer les données d’une participation particulière dans l’identité générale. [P09, P10]

La figure 3 représente les principales clés étrangères du modèle physique. Elle ne constitue pas un MCD reconstruit : les noms sont ceux des tables SQL et les flèches vont de la table porteuse de la clé étrangère vers la table référencée. Les relations accessoires vers les référentiels, les comptes et les statuts sont volontairement regroupées dans le texte pour préserver la lisibilité. L’inventaire complet des tables figure en annexe.

:::figure donnees.png|Relations principales du modèle physique PostgreSQL|Source : conception/Conception2.sql. Chaque flèche représente une clé étrangère ; les relations représentées sont un extrait du schéma.

Un stagiaire peut posséder plusieurs profils académiques ; chaque profil se rattache à un seul stagiaire. Plusieurs participations peuvent référencer un même stage, mais chacune porte ses propres dates et son encadreur. Les rapports, événements et associations de fichiers référencent la participation. Les évaluations finales et les soutenances disposent chacune d’une contrainte d’unicité sur stage_participant_id : le schéma admet donc au plus une ligne de chacune de ces catégories par participation.

L’organisation administrative suit une autre chaîne : le stage référence une structure d’accueil, elle-même rattachée à un département. L’entité Stagiaire actuelle ne possède pas de champ departement, conformément à la table stagiaires du script examiné. Il faut donc éviter d’expliquer les permissions comme un filtrage fondé sur un département directement enregistré sur la personne. Le code courant utilise l’administration globale, l’affectation de l’encadreur et l’association du compte stagiaire. [P07, P10]

Le tableau 3 résume les ensembles du modèle. Les tables d’historique conservent des transitions et leurs responsables, tandis que les événements décrivent les actes tels qu’une suspension ou une prolongation. Ces objets sont complémentaires : un changement de date de fin peut être un événement sans constituer, à lui seul, un changement de statut.

:::table Ensembles du modèle relationnel
Ensemble | Tables représentatives | Fonction
Identité et études | stagiaires, stagiaire_academique | Identifier la personne et contextualiser son parcours académique.
Organisation | departements, structures_accueil, encadreurs, stages, cadres_stage | Définir le cadre d’accueil et les responsabilités.
Participation | stage_participant, stage_participant_evenements | Porter les dates, les affectations et les actes de gestion.
Suivi pédagogique | rapports_activite, rapport_observations, evaluations_finales, evaluation_criteres | Conserver les productions, observations et notes.
Pièces et échéances | fichiers, stage_participant_fichiers, soutenances, alertes | Relier les documents, rendez-vous et situations à traiter.
Sécurité et traçabilité | utilisateurs, utilisateurs_role, sessions_utilisateur, journal_audit | Authentifier, habiliter et conserver certaines traces d’opération.
:::

## 2.2 Architecture backend et transactions

La figure 4 présente l’organisation logicielle. Les contrôleurs de gestion reçoivent les requêtes et transmettent les données aux services. Les DTO définissent le contrat échangé avec le navigateur. Les repositories encapsulent les opérations de persistance sur les entités JPA. Des services transversaux portent la politique d’accès, les règles des participations et les calculs de suivi. Cette répartition peut être observée dans StageGestionController, StageGestionService, StageParticipantRepository et StageParticipant. [P10, P11]

:::figure composants.png|Composants de traitement d’une requête métier|Sources : contrôleurs de gestion, DTO, DossierAccessService, ParticipationRulesService et repositories. Le schéma présente des responsabilités, pas des services déployés séparément.

Les services de gestion apportent une valeur qui dépasse les opérations CRUD. La création d’une participation vérifie l’affectation, résout les références, contrôle les dates, recherche les chevauchements et détermine le statut initial. Une clôture examine la transition, applique les règles de complétude ou la décision administrative, puis écrit le statut et les traces associées. Les contrôleurs CRUD historiques encore présents ne doivent pas être considérés comme des chemins alternatifs autorisés : la configuration ferme les accès qui ne sont pas explicitement admis.

Les annotations transactionnelles délimitent les opérations métier qui doivent être validées ensemble. Dans Spring, leur portée dépend de l’interception des appels par l’infrastructure transactionnelle [R3]. Pour le projet, l’intention transactionnelle est visible sur les méthodes de gestion. La présence de l’annotation ne suffit toutefois pas à prouver tous les scénarios d’annulation en base : les tests unitaires exécutés ne simulent pas un échec réel au moment d’un commit PostgreSQL.

Le verrouillage pessimiste de la participation et le verrou par personne pour les affectations réduisent les possibilités de concurrence incohérente dans les chemins applicatifs concernés. ParticipationRulesService rafraîchit notamment l’entité avec PESSIMISTIC_WRITE avant de vérifier sa mutabilité. L’état clôturé, annulé ou abandonné, ainsi que la présence d’une date de clôture, bloque les modifications soumises à cette règle. La protection d’une opération dépend donc de son passage effectif par ces services. [P11]

## 2.3 Authentification et contrôle d’accès

Le mécanisme d’authentification repose sur une session opaque. Lors de la connexion, AuthService recherche le compte, vérifie le mot de passe avec BCrypt et contrôle que le statut est ACTIF. Il génère ensuite 32 octets aléatoires, les encode pour former le jeton transmis au client et enregistre uniquement son empreinte SHA-256 dans la table des sessions. La date d’expiration est fixée à huit heures après la création. Le jeton n’est pas un JWT : aucune charge utile signée contenant les droits n’est utilisée par ce mécanisme. [P04]

À chaque requête protégée, SessionAuthenticationFilter exploite le jeton Bearer et rétablit l’identité et les autorités à partir des données valides. La configuration STATELESS concerne la session HTTP gérée par Spring ; elle n’interdit pas l’existence de sessions applicatives persistées dans PostgreSQL. Cette distinction explique pourquoi l’application peut révoquer une session et prendre en compte les rôles actifs sans attendre la fin d’un jeton autonome.

La première barrière d’autorisation est la route. SecurityConfig ordonne les règles par méthode et chemin, ouvre la connexion, autorise les opérations prévues aux rôles concernés et termine par un refus par défaut. La documentation Spring Security décrit ce niveau d’autorisation par requête [R6]. La seconde barrière est la ressource : DossierAccessService vérifie si le compte peut accéder à la participation ou à l’objet lié. Un identifiant valide transmis dans l’URL ne constitue donc pas une preuve de droit. [P06, P07]

Les rôles actifs supportés sont ADMIN, ENCADREUR et STAGIAIRE. Les anciennes attributions RESPONSABLE_STAGE sont interprétées comme ADMIN en lecture. Cette compatibilité ne crée pas un quatrième rôle dans le fonctionnement courant. Pour les rapports, le cumul des rôles ne dispense pas du contrôle de propriété : seul le stagiaire associé rédige et soumet, et seul l’encadreur affecté peut examiner, à condition de ne pas être lui-même propriétaire du dossier. [P05, P13]

L’interface conserve le jeton dans localStorage et le joint aux appels authentifiés. Cette décision simplifie la persistance de session entre chargements, mais rend nécessaire la maîtrise des scripts exécutés dans l’origine web. L’absence de JWT ou l’usage de BCrypt ne suffisent pas à conclure à une sécurité exhaustive. La protection du transport, les paramètres de l’environnement cible, la politique de mots de passe et les essais adverses doivent être validés en exploitation. La réinitialisation du mot de passe comporte une révocation des sessions dans le code ; le parcours HTTP correspondant n’a pas été rejoué pour cette rédaction. [P03, P33]

## 2.4 Admission et affectation

L’admission commence par la création du stagiaire et l’ajout de son profil académique. Les services vérifient les champs requis et résolvent les références académiques. Le schéma impose l’unicité de la combinaison personne, niveau, filière, établissement et année d’études. Cette règle évite d’enregistrer plusieurs fois le même contexte académique, tout en autorisant des profils différents pour une même personne. Les formulaires React utilisent les catalogues de référence pour produire les identifiants attendus par l’API. [P32]

L’association d’un compte au dossier constitue une opération explicite. L’identité numérique n’est pas déduite d’une ressemblance de nom ou d’adresse électronique. PortailStagiaireService et les pages du dossier personnel s’appuient sur cette liaison. La séparation entre compte et personne permet aussi de représenter un dossier administratif avant l’ouverture d’un accès au portail. Les conditions d’ouverture des comptes dans le service d’accueil restent à confirmer sur le plan organisationnel. [P08]

L’affectation crée une participation comportant les références du stage, du profil académique, du cadre et de l’encadreur, ainsi que les dates de début et de fin. La fin ne peut précéder le début. Le contrôle de chevauchement s’effectue par personne, y compris lorsque deux participations utilisent des profils académiques différents. La création prend un verrou sur le stagiaire avant la vérification afin de sérialiser les affectations concurrentes empruntant ce chemin. Le statut initial recherché est PLANIFIEE. [P10, P11]

La modification des dates doit également tenir compte des rapports déjà saisis. Si une nouvelle période rend une semaine de rapport incompatible avec le calendrier calculé, le service refuse la modification. Cette règle relie une donnée administrative, la durée du stage, à une production pédagogique déjà conservée. Elle évite qu’un changement apparemment isolé rende incohérent le suivi existant.

## 2.5 Événements et clôture des participations

Le cycle de vie comporte des opérations dédiées de prolongation, suspension, reprise, abandon, annulation et clôture. La prolongation conserve l’ancienne et la nouvelle date de fin. La suspension et la reprise exigent une date d’effet contrôlée par rapport à la période et aux événements antérieurs. Les services vérifient les transitions autorisées dans le référentiel. Les historiques de statut et les événements permettent ensuite de distinguer le résultat courant de la décision qui l’a produit.

La clôture mérite une distinction entre trois modalités présentes dans le code, présentées au tableau 4 et dans la figure 5. En mode ordinaire, le service exige les pièces obligatoires actives et les rapports validés pour les périodes achevées. Le calcul des rapports attendus retient les périodes dont la fin est au plus égale à la date courante à Madagascar. Les périodes futures ne sont pas exigées. La soutenance n’est pas un prérequis appelé par la branche de clôture examinée. [P14, P35]

:::table Modalités de clôture présentes dans le code
Modalité | Pièces manquantes | Rapports achevés non validés | Trace
Ordinaire | Bloquantes | Bloquants | Événement CLOTURE et historique de statut.
Dérogation documentaire | Autorisées si ignorerDocumentsManquants vaut vrai | Restent bloquants | Liste réelle des pièces manquantes ajoutée au motif.
Forcée | Autorisées | Autorisés | Décision administrative et listes des pièces et périodes manquantes ajoutées au motif.
:::

:::figure cloture.png|Décision de clôture et contrôles conservés|Sources : StageGestionService, ParticipationRulesService, EvenementParticipationRequest et ClotureParticipationTests.

Le mode forcé est activé par forcerCloture. Il maintient les contrôles d’accès, de transition et de participation déjà terminée, mais remplace l’exigence de complétude par une trace de décision administrative. Les listes de pièces et de périodes manquantes sont recalculées côté serveur pendant le verrouillage ; elles ne sont pas simplement recopiées depuis un affichage susceptible d’être ancien. Le statut devient CLOTUREE, une date de clôture est enregistrée et les traces associées sont conservées. [P15, P16]

L’interface actuelle transmet forcerCloture=true lorsque l’administrateur confirme le dialogue de clôture. Le texte de confirmation indique que des documents ou rapports peuvent rester manquants ou non validés. Le mémoire doit donc expliquer ce comportement effectif, même si un rapport ancien décrit seulement une clôture stricte. La présence d’une trace technique ne vaut pas approbation institutionnelle de cette dérogation : les conditions administratives d’emploi et les justificatifs à exiger restent [À CONFIRMER].

Les tests unitaires couvrent notamment le refus d’une transition, la clôture déjà effectuée, le mode ordinaire, la dérogation documentaire et le mode forcé. Ils vérifient également que les dérogations ne suppriment pas les gardes d’accès. Les 13 scénarios de ClotureParticipationTests ont réussi dans la campagne actuelle. Cette observation porte sur les services testés avec leurs doubles ; la persistance complète sur PostgreSQL doit être rejouée après rétablissement des prérequis d’intégration.

## 2.6 Rapports hebdomadaires et observations

CalculPeriodesRapportService découpe la durée de la participation en semaines terminant le dimanche. La première et la dernière période sont tronquées aux bornes du stage. L’échéance correspond au lundi suivant le dimanche utilisé pour le calcul, y compris lorsque la dernière période s’arrête avant ce dimanche. Le calendrier est calculé à partir des dates du stage ; la suspension n’introduit pas, dans ce calcul, une exclusion automatique de semaines. [P12]

L’interface présente les périodes attendues dans une grille. Une période vide permet d’ouvrir un formulaire prérempli avec les dates et la participation. Une période déjà renseignée renvoie au rapport correspondant. Les indicateurs de droits transmis par le serveur déterminent les actions proposées. Cette approche réduit la divergence entre la page et la règle métier : le navigateur n’a pas à reconstituer seul le propriétaire ou l’encadreur autorisé.

La figure 6 décrit le parcours principal. Le stagiaire propriétaire crée un brouillon, puis demande la transition vers SOUMIS. L’encadreur affecté examine le rapport soumis et peut demander une correction ou valider. Après correction, le stagiaire soumet à nouveau. Le service interdit de déplacer un rapport existant vers une autre participation et restreint l’édition du contenu aux états BROUILLON et A_CORRIGER. [P13]

:::figure rapports.png|Séquence simplifiée de soumission et d’examen d’un rapport|Sources : RapportActiviteGestionService, DossierAccessService, clients API et pages de rapports. Les contrôles et écritures sont regroupés pour la lisibilité.

Les transitions utilisent le PATCH de statut du rapport ; il n’est pas nécessaire d’inventer des routes distinctes de soumission ou de validation. La date de soumission est renseignée lors du passage à SOUMIS, et la date de validation lors du passage à VALIDE. Les contraintes et triggers PostgreSQL encadrent également les dates, les transitions et les chevauchements de périodes. Un rapport « remis » et un rapport « validé » correspondent donc à des états différents qui ne doivent pas être confondus dans les indicateurs.

Les observations sont liées à un auteur, un type, un commentaire et un indicateur de visibilité au stagiaire. Le parcours du stagiaire seul filtre les observations privées. La rédaction de commentaires et l’examen restent soumis à l’affectation réelle de l’encadreur. Les synthèses et exports doivent conserver cette même restriction afin que le téléchargement ne devienne pas un moyen de contourner ce qui est masqué à l’écran. [P07, P13, P34]

## 2.7 Gestion documentaire

Les fichiers sont associés soit à un stagiaire, soit à une participation. Le catalogue des types de pièce indique leur périmètre, leur caractère obligatoire pour la clôture et la multiplicité autorisée. Le service consulte ce catalogue au moment du dépôt et lors du contrôle de complétude. Une liste codée dans le navigateur ne remplace donc pas le référentiel effectif de la base. [P17, P18]

DocumentContentValidator contrôle la taille, le nom, l’extension, le type MIME et le contenu. Les formats admis sont PDF, JPEG et PNG. La limite est de 10 Mio par fichier ; l’enveloppe multipart est configurée à 11 MB côté Spring. Les images doivent être décodables et respecter la limite de pixels du validateur. Les PDF sont analysés avec PDFBox, avec notamment un refus des fichiers chiffrés, des nombres de pages hors des bornes prévues et de structures actives détectées. Ces contrôles ciblés ne constituent pas un antivirus exhaustif. [P17]

Le stockage attribue un nom fondé sur un UUID. Les métadonnées SQL conservent le lien avec l’objet métier et le chemin de stockage. Le téléchargement vérifie les droits, l’existence et la lisibilité du fichier, puis que son chemin réel reste dans le répertoire autorisé. Lorsqu’un fichier est lié à plusieurs dossiers, la politique exige l’accès à toutes ses associations pour un utilisateur non administrateur. Ce choix évite qu’une association accessible expose indirectement un autre dossier. [P07, P18]

Une transaction SQL ne restaure pas automatiquement un fichier écrit sur disque. Le service prévoit donc un nettoyage si l’opération transactionnelle n’est pas validée. Le mécanisme est présent dans le code, mais une panne disque ou un arrêt du processus à un instant critique n’a pas été injecté pendant les vérifications actuelles. En exploitation, la sauvegarde doit couvrir conjointement les tables et les octets des pièces ; une sauvegarde de PostgreSQL seule serait incomplète.

## 2.8 Évaluations et soutenances

L’évaluation finale se rattache à une participation et à l’encadreur correspondant. Les notes sont enregistrées par critère, dans l’intervalle de 0 à 5. Le catalogue précise notamment les critères actifs, leur caractère obligatoire, leur ordre et leur coefficient. La présence d’un coefficient dans le modèle ne justifie pas d’affirmer qu’une moyenne générale est calculée et présentée : cette restitution n’est pas démontrée par les réponses examinées. [P19]

La finalisation vérifie que tous les critères actifs obligatoires disposent d’une note. Elle marque ensuite l’évaluation comme finalisée et renseigne la date correspondante. Les écritures ultérieures sur cette évaluation sont refusées par les gardes du service. L’auteur de la finalisation est transmis à la journalisation. La politique d’évaluation autorise l’administrateur ou l’encadreur affecté dans les conditions du service ; elle ne doit pas être confondue avec la règle plus restrictive d’examen des rapports. [P07, P19, P36]

La soutenance représente un rendez-vous lié à la participation, avec une date, un lieu et une observation. Sa saisie et sa consultation utilisent les services et routes dédiés. Les éléments administratifs d’évaluation et de soutenance ne sont pas automatiquement exposés au portail d’un compte possédant seulement le rôle STAGIAIRE. Le bilan de cette séparation doit être confirmé avec les utilisateurs, mais le mémoire décrit le périmètre effectivement codé. [P34, P35]

## 2.9 Alertes et pilotage

Le planificateur appelle l’orchestration quotidienne à 06 h 00 dans le fuseau Indian/Antananarivo. Sept contrôles sont enchaînés : fins de stage proches, stages dépassés, rapports manquants, validations en retard, dossiers incomplets, surcharge des encadreurs et soutenances proches. Cette planification est un fait du code ; son déclenchement effectif à l’heure prévue sur la cible n’a pas été observé pendant la rédaction. [P20]

Les alertes disposent d’un type, d’une priorité, d’un statut et d’une clé de déduplication unique. Le statut A_TRAITER représente une situation active, tandis que TRAITEE, IGNOREE ou EXPIREE décrivent d’autres états du cycle. La nature de l’alerte est une notion distincte de sa priorité. Une information n’est donc pas nécessairement une action à acquitter. Les contrôles métier, et non seulement les libellés de l’interface, déterminent les actions admises.

AlerteClotureService expire les alertes à traiter liées à une participation clôturée, directement ou par l’intermédiaire d’un rapport ou d’une soutenance. Un traitement au démarrage rapproche également les anciennes alertes encore actives avec les dossiers déjà clôturés. L’historique n’est pas supprimé : l’état et la date de traitement sont modifiés. Les tests unitaires de cette règle ont réussi dans la campagne actuelle. [P21]

PilotageService filtre les alertes selon les accès avant de les regrouper par participation. Il additionne les poids du type d’alerte et applique les seuils de 20 et 50, en tenant également compte des priorités AVERTISSEMENT et URGENT. Le résultat est une classification déterministe NORMALE, HAUTE ou URGENTE. Il ne s’agit pas d’une prédiction statistique ni d’un système d’intelligence artificielle. Les alertes sans participation identifiable ne sont pas transformées artificiellement en ligne de dossier prioritaire. [P22]

Le pilotage expose aussi les échéances, les rapports attendus ou manquants, les pièces manquantes, la charge accessible des encadreurs et les soutenances à trente jours. Ces éléments sont construits à partir des dossiers autorisés. Les listes sont encore souvent chargées avant filtrage en mémoire, ce qui impose une réserve sur les volumes importants. Le dépôt ne fournit pas de campagne actuelle permettant de chiffrer cette limite.

Les synthèses réunissent les données du dossier et les exports produisent des CSV ou des bilans PDF. ExportService échappe les cellules CSV et neutralise les chaînes susceptibles d’être interprétées comme des formules. Les PDF sont paginés avec PDFBox, mais la police standard ne couvre pas tous les caractères ; les caractères indisponibles peuvent être remplacés. Les filtres locaux d’une table ne sont pas nécessairement repris par l’export global des dossiers accessibles. [P23, P34]

## 2.10 Interface utilisateur et parcours

Le frontend est organisé autour de pages métier, de composants communs et de clients API. Les composants Table, BusinessForm, ConfirmDialog, ErrorMessage et Loader structurent les interactions récurrentes. Les pages de détail regroupent les informations d’une ressource et ses actions, tandis que les formulaires réutilisent les référentiels pour proposer des choix métier. Cette architecture vise une cohérence des parcours ; elle ne constitue pas une validation ergonomique par les utilisateurs.

Le routage est implémenté avec la History API du navigateur. AppRoutes utilise pushState, replaceState et popstate ; routeConfig associe chemins, pages, titres et rôles. Il n’y a pas lieu de présenter React Router comme une dépendance de cette version. Les routes de détail contrôlent le format de l’identifiant, et les destinations de retour après connexion sont limitées aux chemins internes admis. [P24]

La session possède des états d’initialisation, de disponibilité et d’authentification. Une indisponibilité du serveur peut produire une proposition de réessai au lieu d’une fausse confirmation de connexion. Le traitement du 401 évite également qu’une réponse tardive concernant un ancien jeton déconnecte une session plus récente. Les contrôles d’interface améliorent le parcours, tandis que l’autorité finale reste le serveur. [P03]

Le tableau 5 présente les parcours à illustrer lors de la recette visuelle. Les emplacements d’images demandés figurent en annexe sous la forme [CAPTURE À FOURNIR]. Aucune maquette ou image artificielle n’est présentée comme un écran réel de l’application.

:::table Parcours et pages existantes
Parcours | Pages ou composants | Vérification complémentaire
Admission et consultation | StagiairesPage, StagiaireDetailPage, AcademiquesPanel | Saisie, validation et navigation avec données de recette.
Suivi individuel | MonDossierPage, ParticipationDetailPage | Portée du compte et visibilité des informations.
Production hebdomadaire | MesRapports, RapportWeeks, RapportActions | Préremplissage, soumission, correction et validation.
Décision administrative | ParticipationActions, ClotureConfirmation | Information comprise et confirmation de la clôture forcée.
Pilotage | DashboardPage, PilotagePanel, AlertesPage | Lisibilité des échéances et accès aux dossiers concernés.
:::

## 2.11 Contraintes SQL et cohérence avec JPA

Le script Conception2.sql déclare 44 tables, 6 vues et 16 triggers. Les vues couvrent notamment la charge des encadreurs, les échéances de participation, les rapports en attente, les pièces manquantes, les soutenances prochaines et les actions prioritaires. Le fait qu’une vue soit définie ne signifie pas qu’elle est utilisée directement pour chaque réponse : le calcul des priorités est repris côté service après filtrage des accès. [P09, P22]

Les contraintes UNIQUE empêchent des doublons identifiés, les clés étrangères préservent les références et les CHECK valident certaines valeurs. Les triggers complètent ces mécanismes pour vérifier le type d’un statut, la cohérence d’une transition, la période d’un rapport ou le périmètre d’une pièce. La table des rapports limite notamment une période à sept jours inclus au maximum. Le contrôle d’une semaine canonique calculée par le serveur constitue une exigence applicative supplémentaire.

La comparaison automatique des 44 entités avec les déclarations SQL n’a relevé aucune table ni colonne explicitement nommée dans les annotations qui soit absente du script. Cette observation porte sur les noms ; elle ne prouve pas une équivalence exhaustive des types, longueurs, valeurs par défaut et règles. Par exemple, la base autorise une note NULL alors que le service de notation exige une valeur. Les deux niveaux n’ont pas nécessairement la même responsabilité de validation.

Hibernate est configuré en validate, avec open-in-view désactivé. L’application attend donc un schéma préexistant compatible, au lieu de demander sa création automatique au démarrage. Le script d’initialisation contient néanmoins des instructions de suppression et création de base. Il n’a pas été exécuté pour ce travail et ne doit pas être assimilé à une migration additive. Les migrations et précontrôles référencés dans les anciens documents sont absents de cette copie ; leurs garanties ne sont pas attribuées au schéma courant sans récupération et vérification. [P25, P26]

## 2.12 Tests et validation

La validation est présentée à trois niveaux : les tests présents, les tests exécutés pour ce mémoire et les essais restant à réaliser. Le tableau 6 rapporte les résultats observés le 10 novembre 2026. Les tests backend sélectionnés sont unitaires et n’utilisent pas la campagne Spring/HTTP/PostgreSQL complète. Les tests frontend associent fonctions, contrats HTTP et rendu de composants ; ils ne pilotent pas un navigateur avec les interactions d’un utilisateur. [P27, P28]

:::table Résultats des vérifications réalisées
Vérification | Résultat observé | Portée et limite
Backend unitaire | 36 réussis ; 0 échec ; 0 erreur ; 0 ignoré | Accès, clôture, périodes, documents, exports et alertes de clôture ; sans recette PostgreSQL complète.
Frontend premier essai | 115 réussis ; 11 échecs | Échecs React useContext dans les tests de rendu ; chemins de casse différente dans les traces.
Frontend chemin canonique | 126 réussis ; 0 échec ; 0 ignoré | Relance sans modification du code depuis stagemanagement.
ESLint | Code de sortie 0 | Vérification statique du frontend, sans garantie fonctionnelle exhaustive.
Construction Vite | Réussie | Ressources web produites ; ne prouve pas le déploiement de ces ressources.
Intégration complète | Non exécutée pour cette rédaction | Précontrôles et migrations attendus absents de la copie.
Recette navigateur et exploitation | Non exécutée pour cette rédaction | Responsive, clavier, impression, charge, restauration et cron restent à vérifier.
:::

Le premier lancement frontend a utilisé une graphie du chemin différente de celle observée dans certaines résolutions de modules. Les traces montrent React et React DOM chargés au travers de chemins variant par la casse. Une relance depuis le chemin canonique a permis aux 126 tests de réussir sans changement de source. La casse du chemin constitue une explication étayée de l’incident ; une instrumentation complète de la résolution des modules n’a pas été effectuée. L’échec initial reste conservé dans le journal au lieu d’être effacé du bilan. [P29]

Les documents antérieurs rapportent des campagnes d’intégration et d’autres nombres de tests à des dates précédentes. Ces résultats historiques ne sont pas additionnés aux tests actuels et ne sont pas présentés comme un nouveau succès de la version auditée. Les fichiers préexistants de rapports Surefire ne suffisent pas davantage à attester une campagne actuelle complète. La sélection, la commande et les journaux des vérifications présentes sont conservés avec les livrables documentaires.

Les scénarios à compléter doivent notamment vérifier une création de dossier, une affectation, le cycle complet du rapport, les refus croisés entre comptes, le téléchargement des pièces et les modalités de clôture. Leur exécution doit utiliser une base isolée et des identités de recette. Les campagnes de charge, de reprise et d’ergonomie répondent à des questions différentes des tests unitaires ; elles doivent donc produire leurs propres observations.

## 2.13 État du déploiement

La construction frontend a réussi et le projet fournit des paramètres externes pour la connexion PostgreSQL et le répertoire documentaire. La documentation prépare une installation avec origine HTTPS unique, des sauvegardes et une recette cible. Aucun de ces éléments ne constitue une preuve de service accessible en production au moment de la rédaction. Le statut réel de livraison et d’utilisation par l’organisme reste [À CONFIRMER]. [P25, P31]

Le répertoire backend/migrations mentionné par la procédure et par le lanceur d’intégration n’est pas présent. Avant de rejouer cette procédure, il faut récupérer les scripts nécessaires ou faire valider une procédure mise à jour. Exécuter le script d’initialisation sur une base existante serait une opération différente et potentiellement destructrice ; cette opération n’a pas été réalisée.

L’exploitation doit en outre définir les droits du compte PostgreSQL, le stockage persistant, le reverse proxy, les règles de sauvegarde et les essais de restauration. Les chemins des fichiers enregistrés dans les métadonnées doivent rester cohérents avec les octets restaurés. La rotation des anciens secrets évoquée dans la documentation, la charge et l’observation du traitement planifié sont des travaux à vérifier, sans reproduire de secrets dans le mémoire.

## 2.14 Difficultés attestées et solutions

Une difficulté historique documentée concerne l’attente d’une colonne departement_id dans la table stagiaires. Le compte rendu de recette décrit un échec de validation Hibernate, suivi de la suppression du mapping et du champ de formulaire devenus incompatibles avec le schéma retenu. L’inspection actuelle confirme l’absence de ce mapping. Cette correction illustre l’importance de distinguer le modèle attendu par l’application du schéma effectivement livré.

Une autre difficulté réside dans l’évolution des règles. Les anciens rapports et le code actuel ne décrivent pas la même modalité de clôture. La solution documentaire consiste à exposer les trois branches réellement présentes, leurs contrôles et leur traçabilité, puis à isoler la validation institutionnelle encore requise. Modifier le code pour le faire correspondre au récit aurait masqué l’état du projet ; aucun fichier applicatif n’a été modifié pour ce mémoire.

Enfin, les essais actuels ont révélé une sensibilité des tests de rendu à l’environnement de résolution des modules et un accès initialement insuffisant au cache Maven. La relance depuis le chemin canonique et l’accès autorisé au cache local ont permis de terminer les vérifications annoncées. Ces résultats montrent pourquoi un compte rendu de test doit préciser son environnement et sa portée, au lieu de résumer toute validation par un unique message de construction réussie.
