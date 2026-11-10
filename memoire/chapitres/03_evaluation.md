# 3 Évaluation du projet et connaissances acquises

## 3.1 Bilan fonctionnel

L’application rassemble un ensemble cohérent de modules autour de la participation à un stage. Le dossier administratif, le profil académique, l’affectation, les rapports, les pièces et les décisions ne sont pas traités comme des listes indépendantes : ils sont reliés par le modèle de données et par les services métier. Les pages de synthèse et de pilotage rendent ces relations consultables dans un même parcours. Cette capacité est observée dans le code ; sa validation complète en situation d’exploitation reste à réaliser.

Le tableau 7 utilise des catégories explicites pour éviter de confondre présence du code et qualification d’un parcours. « Vérifié » désigne ici un périmètre effectivement exercé par les tests cités. Un module peut être implémenté et disposer de tests partiels sans que son usage complet dans le navigateur et la base cible soit confirmé.

:::table Bilan des réalisations et niveau de validation
Élément | État retenu | Justification et limite
Règles d’accès aux dossiers | Implémentées et vérifiées sur scénarios unitaires | Tests DossierAccessTests réussis ; refus HTTP complets à rejouer.
Clôture et dérogations | Implémentées et vérifiées sur scénarios unitaires et frontend | Trois branches et traces testées ; acceptation administrative à confirmer.
Calendrier, documents et export | Implémentés et vérifiés sur scénarios ciblés | Tests de services et fonctions réussis ; rendu final des exports à contrôler.
Admission, évaluation et soutenance | Implémentées ; parcours complet non testé ici | Sources, DTO, routes et pages présents ; campagne actuelle d’intégration non exécutée.
Interface React | Implémentée avec tests automatisés réussis | 126 tests ; interactions visuelles et recette humaine non effectuées.
Alertes planifiées | Implémentées ; planification non observée | Cron et orchestration présents ; exécution à heure réelle à vérifier.
Impression et exploitation | Partiellement validées | Code et procédure disponibles ; recette, restauration et environnement cible non confirmés.
Pagination SQL et automatisation CI/CD | Perspectives non réalisées ou non démontrées | Chargements en mémoire et absence de preuve d’une chaîne CI/CD complète.
Déploiement de production | Impossible à confirmer | Aucun procès-verbal de livraison ni observation de la cible dans ce travail.
:::

La réalisation se distingue particulièrement par la prise en compte de l’identité du propriétaire et de l’affectation de l’encadreur. Le contrôle d’une ressource traverse la lecture, la mutation et le téléchargement. La présence de ces gardes dans un service partagé favorise une politique cohérente. Elle n’exonère pas d’ajouter des tests lors de l’ouverture de nouvelles routes, notamment lorsque plusieurs objets peuvent être liés au même document ou à la même alerte.

## 3.2 Bilan technique

Les principales qualités observées sont la séparation des responsabilités, la centralisation de certaines règles transversales et l’association de contrôles Java et SQL. Les tests de clôture montrent que les règles ne se réduisent pas à un simple changement de statut : ils vérifient aussi les conditions d’accès, l’état antérieur, la complétude et les traces. Les tests des exports et des fichiers traitent des cas de contenu, et non uniquement l’existence d’une méthode.

Les résultats actuels ne doivent cependant pas être convertis en pourcentage global de fiabilité. Aucun taux de couverture n’a été calculé et la campagne PostgreSQL complète n’a pas été relancée. La réussite de 36 tests unitaires et de 126 tests frontend atteste les scénarios sélectionnés et leur environnement. Elle ne démontre pas toutes les combinaisons de statuts, la concurrence réelle de plusieurs utilisateurs, les pannes de stockage ou le comportement d’un jeu historique de données.

La première limite structurelle concerne les volumes. Plusieurs services utilisent findAll puis filtrent les objets accessibles. Cette démarche facilite la réutilisation de la politique d’accès, mais son coût peut croître avec les dossiers et leurs relations. Une pagination côté base et des requêtes tenant compte du périmètre pourraient réduire les chargements ; leur efficacité devra être mesurée plutôt que présumée.

Une deuxième limite porte sur la reproductibilité. Le projet dispose de scripts de test et de procédures, mais des fichiers de migration auxquels ils font référence sont absents de la copie. L’explication d’un état validé doit inclure l’ensemble des sources nécessaires pour le reproduire. Récupérer les scripts, fixer la procédure et rejouer les vérifications sur une base vierge isolée sont donc des préalables à une conclusion plus forte sur l’intégration.

Une troisième limite concerne la restitution et l’exploitation : couverture des caractères dans les PDF, qualité de l’impression, comportement mobile, sauvegarde coordonnée et configuration HTTPS. Ces éléments dépassent les seules fonctions métier. Ils déterminent pourtant la qualité d’une livraison utilisable et doivent faire partie de la recette finale.

## 3.3 Apport potentiel à l’organisme d’accueil

L’application peut offrir un point de consultation commun pour les informations d’un stage, une meilleure explicitation des échéances et un historique des décisions. Les contrôles de rôle et d’affectation peuvent limiter l’accès aux dossiers aux acteurs concernés. Les alertes et les synthèses peuvent aider à identifier les actions à examiner. Ces apports découlent du fonctionnement développé ; ils sont formulés comme une valeur attendue tant qu’aucune mesure d’usage n’a été recueillie.

Un gain de productivité ne peut pas être déduit du seul nombre de modules réalisés. Il faudrait comparer des situations observables, par exemple le temps nécessaire pour retrouver un dossier, le délai entre dépôt et examen d’un rapport ou la proportion de dossiers complets au moment d’une décision. Les définitions, la période d’observation, les volumes et la méthode de collecte devraient être approuvés avec le service. Aucun résultat chiffré n’est attribué à ces indicateurs dans le présent mémoire.

La clôture forcée apporte une souplesse administrative, mais demande une règle d’usage claire. Sa trace permet de conserver l’existence d’un manque au moment de la décision ; elle ne transforme pas une pièce absente en pièce reçue ni un rapport non validé en rapport validé. Les responsables doivent donc confirmer qui décide, dans quelles circonstances et avec quel niveau de justification. Cette validation organisationnelle conditionne l’interprétation correcte des bilans.

## 3.4 Bilan personnel à valider

[À CONFIRMER — attribution personnelle des réalisations et formulation définitive]

Le travail présenté mobilise la traduction d’un domaine métier en tables, relations, contrôles d’accès et parcours web. Il permet d’illustrer la conception d’un modèle où une personne conserve son identité au-delà d’une participation particulière. Il met aussi en évidence le lien entre des règles temporelles, comme le découpage hebdomadaire, et des opérations administratives, comme la modification des dates ou la clôture.

Sur le plan du développement, les compétences représentées concernent l’organisation de services Java, les DTO et les repositories, ainsi que la composition de pages React et la gestion des réponses HTTP. La séparation entre authentification, autorisation par route et autorisation par ressource constitue un point d’apprentissage important à expliciter lors de la soutenance. Le même rôle peut conduire à des décisions différentes selon la propriété du dossier ou l’affectation de l’encadreur.

La validation apporte une autre dimension : un test unitaire, un test HTTP avec PostgreSQL et un essai dans le navigateur répondent à des questions différentes. Les écarts observés entre documentation historique et code courant soulignent également l’importance de maintenir des preuves datées. L’auteur pourra préciser ici les parties qu’il a réalisées personnellement, les échanges effectivement menés avec ses encadreurs et les difficultés qu’il a lui-même rencontrées. Aucun sentiment, niveau d’autonomie acquis ou expérience de collaboration n’est présumé.

## 3.5 Perspectives d’évolution

La première perspective consiste à rendre la livraison reproductible : rétablir les migrations et précontrôles, documenter leurs prérequis et exécuter l’intégration complète sur une base isolée. Cette étape doit être accompagnée d’une clarification de la clôture administrative et d’une recette des trois rôles avec des dossiers distincts. L’objectif est de rapprocher l’état du dépôt, les procédures et la décision métier.

La deuxième perspective porte sur les performances et les volumes. Des requêtes filtrées selon les droits, une pagination SQL et une réduction des lectures répétées peuvent être étudiées sur un jeu représentatif. Les tests devront mesurer les temps de réponse et les ressources consommées avant et après modification. La conservation d’une politique de confidentialité identique est une condition de cette optimisation.

La troisième perspective concerne la qualité d’usage et l’exploitation : essais clavier et mobile, contrôle de l’impression, police Unicode dans les exports PDF, surveillance des tâches et restauration coordonnée de la base et des pièces. Des tests de navigateur automatisés pourraient compléter les tests de rendu existants. Une chaîne CI/CD pourrait ensuite rejouer les contrôles et produire des versions identifiées, sans confondre construction automatique et autorisation de mise en production.

Enfin, l’administration de paramètres techniques et de seuils par une interface dédiée peut être envisagée si elle répond à un besoin validé. Elle devrait conserver des droits restreints, des validations et des traces. Les évolutions proposées constituent des perspectives ; elles ne sont pas comptées comme des fonctions déjà livrées.

# Conclusion générale

Ce projet organise la gestion et le suivi des stagiaires autour d’un dossier relié à des participations, des rapports, des documents et des décisions. La séparation entre identité, parcours académique et participation fournit la structure nécessaire à ce suivi. L’architecture React, Spring Boot et PostgreSQL répartit l’interface, les règles métier et la persistance, tandis que la politique d’accès combine rôles, propriété et affectation.

L’étude du code met en évidence des mécanismes concrets : sessions opaques, rapports soumis par leur propriétaire et examinés par l’encadreur affecté, calendrier hebdomadaire calculé, contrôle des pièces, notes par critère, alertes et synthèses. La clôture est décrite selon ses branches réelles, y compris la décision administrative forcée qui conserve la trace des éléments manquants. Cette précision est nécessaire pour expliquer le fonctionnement actuel sans reproduire des règles dépassées.

Les vérifications réalisées pour ce mémoire ont abouti à 36 tests unitaires backend et 126 tests frontend réussis, ainsi qu’à un contrôle statique et une construction frontend réussis. Ces résultats apportent des preuves ciblées. Les migrations absentes, l’intégration complète non rejouée, la recette visuelle et les contrôles d’exploitation limitent la portée de la conclusion. Le projet ne peut pas être présenté ici comme une production entièrement qualifiée ni comme une source de gains chiffrés déjà mesurés.

La suite du travail consiste à rétablir les prérequis de validation, confirmer les règles administratives, recueillir les observations des utilisateurs et qualifier l’environnement de déploiement. Le mémoire peut ainsi défendre les réalisations constatées tout en exposant clairement les conditions restant à satisfaire pour leur utilisation institutionnelle.
