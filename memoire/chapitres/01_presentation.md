# Introduction générale

Le suivi d’un stage ne se limite pas à enregistrer l’identité d’une personne. Il associe un parcours académique, une structure d’accueil, un encadreur, une période de présence, des travaux à rendre et des décisions administratives. Dans l’application étudiée, ces informations sont reliées à des documents, à des observations et à des événements qui doivent rester compréhensibles au fil du dossier. Le projet présenté dans ce mémoire organise ces relations au sein d’une application web destinée à la gestion et au suivi des stagiaires du MEF dans le contexte SIIGFP.

La question centrale est la suivante : comment construire un dossier de stage cohérent et traçable, accessible à chaque acteur selon ses responsabilités, tout en conservant les règles et les décisions qui déterminent son évolution ? Cette problématique est formulée à partir du périmètre effectivement développé. Elle ne suppose ni un nombre d’utilisateurs, ni une perte de documents, ni un délai de traitement ancien qui n’auraient pas été mesurés.

L’objectif général consiste à mettre en relation les informations administratives et pédagogiques nécessaires au suivi. Les objectifs techniques sont de séparer l’interface des traitements métier, de préserver l’intégrité relationnelle, de contrôler les accès au niveau des ressources et d’exposer les échéances utiles à l’action. Le code comporte des parcours d’admission, d’affectation, de rédaction et d’examen des rapports, de gestion documentaire, d’évaluation, de soutenance, de clôture et de pilotage. L’authentification et les rôles traversent l’ensemble de ces parcours.

La démarche de présentation s’appuie sur une lecture du schéma PostgreSQL, des entités et services Java, des contrôleurs REST, des composants React et des tests disponibles. Les résultats de campagnes antérieures sont distingués des vérifications effectuées pour cette rédaction. Ces dernières ont permis d’exécuter 36 tests unitaires backend et 126 tests frontend avec succès, ainsi que le contrôle ESLint et la construction Vite. Elles ne constituent pas une recette complète de l’installation cible : les interactions navigateur, l’intégration PostgreSQL complète et l’exploitation restent à documenter dans le périmètre de cette version.

Le mémoire comporte trois chapitres. Le premier présente le projet, les besoins déduits de ses fonctions et l’architecture existante. Le deuxième développe la conception et les principaux mécanismes de réalisation, avec une attention particulière à la sécurité, aux rapports et à la clôture administrative. Le troisième évalue les résultats démontrés, les limites et les évolutions envisageables. Les annexes rassemblent les repères de preuve, l’inventaire relationnel et les vérifications à compléter.

# 1 Présentation du projet

## 1.1 Contexte et objectifs

Le sujet fourni situe le stage au Ministère de l’Économie et des Finances de Madagascar et rattache le projet au SIIGFP. Le produit développé est une application de suivi des stagiaires. Son périmètre observable concerne des dossiers de personnes, leurs participations à des stages, les encadreurs, les productions périodiques et les éléments nécessaires à la clôture. Le rattachement à l’organisme d’accueil fournit le contexte du travail ; aucune connexion à une application financière du ministère n’est démontrée dans le dépôt.

L’application distingue l’existence administrative d’un stagiaire de ses participations successives. Cette distinction permet de conserver une identité générale tout en associant chaque participation à un profil académique, un stage, un cadre de travail et un encadreur. L’objectif n’est donc pas seulement de disposer d’un annuaire : il s’agit de rendre possible un suivi contextualisé, depuis l’affectation jusqu’à la clôture, sans confondre les événements de plusieurs périodes. [P10, P32]

Les services de synthèse et de pilotage poursuivent un objectif complémentaire. Ils regroupent les informations réparties dans les tables pour rendre visibles les échéances, les pièces manquantes, l’avancement des rapports et les alertes. La présence de ces calculs démontre une capacité de consolidation dans le code. L’utilité effectivement constatée auprès du service d’accueil et les critères d’acceptation institutionnels restent [À CONFIRMER]. [P22, P34]

## 1.2 Analyse de l’existant et périmètre documenté

Le dépôt permet d’étudier l’état du logiciel et des corrections techniques, mais ne contient pas de description administrative validée du processus antérieur. Il serait donc infondé d’affirmer que les dossiers étaient tenus sur papier, dans un tableur précis ou dans un autre logiciel. Les supports anciens, les personnes intervenantes, les étapes d’approbation et les difficultés observées sur le terrain restent [À CONFIRMER] à partir du cahier des charges ou d’entretiens documentés.

L’existant technique est mieux documenté. Les rapports d’audit et de recette mentionnent notamment une évolution vers des contrôles d’accès par dossier, l’association explicite des comptes stagiaires, l’enrichissement du pilotage et la correction d’un ancien mapping départemental. Ces éléments décrivent l’histoire du projet informatique. Ils ne constituent pas, à eux seuls, une étude du fonctionnement antérieur du service. Le code actuel est retenu lorsque ces documents décrivent une règle devenue obsolète. [P07, P26, P31]

Le périmètre retenu comprend les fonctions présentes dans les pages et services de gestion. Les routes de manipulation brute des sessions, des associations de rôles ou d’autres données sensibles restent fermées par la configuration de sécurité. À l’inverse, l’existence d’une table ne suffit pas à déduire l’existence d’un écran d’administration. L’administration complète des paramètres de suivi et des seuils techniques n’est pas présentée comme une réalisation achevée.

## 1.3 Acteurs et besoins

Les acteurs applicatifs correspondent aux trois rôles effectivement supportés : ADMIN, ENCADREUR et STAGIAIRE. Le tableau 1 présente leurs principales responsabilités observées. La figure 1 en donne une vue de lecture simplifiée. Les responsabilités précises doivent être interprétées avec les restrictions propres à chaque opération : un droit global de gestion ne donne pas automatiquement le droit de rédiger ou de valider le rapport d’un autre acteur. [P05, P07, P13]

:::table Acteurs et responsabilités observées
Rôle | Périmètre | Opérations caractéristiques
ADMIN | Gestion globale | Créer les dossiers, affecter les participations, administrer les comptes, gérer les pièces et les décisions de clôture.
ENCADREUR | Participations affectées au compte | Consulter les dossiers autorisés, examiner les rapports, gérer les évaluations autorisées et suivre les alertes accessibles.
STAGIAIRE | Dossier associé à son compte | Consulter son dossier, rédiger et soumettre ses rapports, consulter les informations et observations autorisées.
:::

:::figure acteurs.png|Acteurs et principaux parcours observés|Sources : SecurityConfig et DossierAccessService ; vue fonctionnelle simplifiée, hors matrice exhaustive des permissions.

Les besoins fonctionnels se répartissent en quatre ensembles. Le premier rassemble les données de référence, les comptes, l’identité du stagiaire et son parcours académique. Le deuxième organise les affectations et les événements du stage. Le troisième concerne les rapports, les documents, les évaluations et les soutenances. Le quatrième expose les synthèses, les alertes, les bilans et le journal d’audit. Cette répartition est une lecture des modules présents, et non la transcription d’un cahier des charges signé qui n’a pas été fourni.

Les exigences non fonctionnelles observables portent sur la confidentialité des dossiers, la cohérence des données et la traçabilité des décisions. Elles se traduisent respectivement par des contrôles de rôle et de propriété, des contraintes relationnelles complétées par les services, et des historiques associés aux événements. D’autres exigences nécessitent une mesure dans l’environnement réel : disponibilité, temps de réponse, charge admissible, accessibilité et restauration. Aucun seuil de performance contractuel n’est inventé dans ce mémoire.

## 1.4 Démarche de travail et planning

La structure du dépôt met en évidence plusieurs activités : modélisation SQL, réalisation des services et de l’API, développement de l’interface, contrôles automatiques et préparation de la recette. Les documents de suivi conservent des campagnes datées de fin octobre et de début novembre 2026. Ces dates attestent les comptes rendus correspondants ; elles ne permettent pas de reconstituer la durée complète des phases du stage ni le temps consacré individuellement à chaque module.

La démarche retenue pour ce mémoire procède par correspondance entre besoin, règle, implémentation et vérification. Par exemple, la confidentialité d’un rapport est étudiée dans le contrôleur, dans la politique d’accès, dans la réponse DTO et dans les tests. Une lecture limitée au nom d’une classe ne suffirait pas à expliquer le comportement devant un jury. La matrice de preuves permet de retrouver ces points d’appui.

Le calendrier officiel, les jalons approuvés, les périodes de réalisation et les éventuelles itérations avec l’encadreur restent [À CONFIRMER]. Aucun diagramme de Gantt n’est produit sans ces éléments. De même, le projet n’est pas présenté comme ayant appliqué Scrum ou une autre méthode formalisée en l’absence de documents attestant ses rituels et ses livrables.

## 1.5 Technologies et justification de la solution

Le tableau 2 décrit la pile technique du projet. Les versions déclarées sont distinguées des versions d’une installation cible. Java 17, Spring Boot 4.1.0 et PDFBox 3.0.8 sont lus dans le fichier Maven. Les versions frontend sont précisées dans les manifestes et leur fichier de verrouillage. La présence de PostgreSQL 15 dans les procédures locales ne permet pas d’affirmer que toute instance d’exploitation utilise exactement cette version. [P01, P02, P25]

:::table Technologies et responsabilités
Élément | Version ou preuve | Responsabilité dans le projet
Java et Spring Boot | Java 17 ; Spring Boot 4.1.0 dans pom.xml | Exécuter les services, exposer les routes HTTP et assembler les composants.
Spring Security et JPA | Dépendances Spring déclarées | Authentification et autorisations ; mapping des entités et accès relationnel.
PostgreSQL | Scripts SQL ; version 15 dans les procédures locales | Tables, clés étrangères, contraintes, triggers et vues.
React et React DOM | 19.2.8 dans le verrouillage frontend | Composition des pages, formulaires et états de l’interface.
Vite | 8.2.2 dans le verrouillage frontend | Serveur de développement et construction des ressources web.
PDFBox | 3.0.8 dans pom.xml | Analyse des pièces PDF et génération des bilans.
Maven, Node et ESLint | Fichiers de construction et commandes exécutées | Gestion des dépendances, tests et contrôle statique frontend.
:::

La solution existante sépare les responsabilités : React compose les vues et gère les interactions, tandis que les services Java décident des opérations autorisées et valident les règles métier. Cette organisation est cohérente avec la décomposition en composants et états décrite par la documentation React consultée [R5]. Elle ne constitue pas la preuve d’une comparaison historique entre plusieurs frameworks.

L’usage de PostgreSQL correspond à un domaine riche en relations et en contraintes : une participation doit référencer des éléments existants, un rapport doit correspondre à une période, et une évaluation possède une identité propre. Les contraintes SQL empêchent certaines incohérences indépendamment de la page qui a émis la demande ; leur rôle général est décrit dans la documentation PostgreSQL [R4]. La justification présentée est technique et rétrospective. Aucun classement de performances entre bases ou langages n’a été réalisé pour ce travail.

## 1.6 Architecture générale

La figure 2 représente les composants d’exécution observés. Le navigateur charge l’interface React et adresse des requêtes HTTP à l’API. Le backend interroge PostgreSQL par les repositories JPA et accède séparément au stockage des fichiers. Les octets des documents ne sont donc pas contenus intégralement dans les tables de métadonnées. Cette distinction conditionne la sauvegarde et la restauration. [P03, P18]

:::figure architecture.png|Architecture générale de l’application|Sources : apiClient.js, vite.config.js, application.properties, repositories et FichierGestionService. Le reverse proxy de production reste à installer et à vérifier.

Le client HTTP centralise la construction des URL, l’ajout de l’en-tête Authorization, la conversion des réponses et le traitement des erreurs. Les données courantes transitent sous forme JSON. Les téléversements utilisent FormData, tandis que les téléchargements sont traités comme des contenus binaires. Le client distingue notamment les réponses 401, 403, 409 et 413, afin d’expliquer respectivement un problème de session, de droits, de cohérence ou de taille. [P03]

En développement, le proxy Vite permet de relayer les appels vers le backend. Pour l’exploitation, la documentation envisage une origine HTTPS unique servant les ressources web et relayant /api vers Spring. Il s’agit d’une architecture de déploiement proposée, non d’une installation vérifiée. Une API sur une autre origine nécessiterait une configuration serveur adaptée ; le simple changement d’une variable frontend ne démontre pas cette capacité. [P31]
