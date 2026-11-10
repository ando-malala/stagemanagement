# Glossaire

API — Interface de programmation exposant les opérations du serveur à l’interface web.

BCrypt — Fonction de hachage adaptative utilisée pour vérifier les mots de passe des comptes.

CRUD — Opérations de création, lecture, mise à jour et suppression. La présence d’un contrôleur CRUD ne signifie pas que ses routes sont autorisées.

DTO — Objet de transfert qui définit les données reçues ou retournées par une opération de l’API.

HTTP — Protocole des échanges entre le navigateur et le serveur. HTTPS ajoute une protection du transport.

JPA — API de persistance Java employée pour représenter les tables par des entités et accéder aux données.

JSON — Format des messages structurés échangés par les opérations courantes de l’API.

MEF — Ministère de l’Économie et des Finances de Madagascar, organisme d’accueil indiqué pour ce stage.

MCD, MLD et MPD — Modèle conceptuel, modèle logique et modèle physique des données. Les schémas de ce mémoire sont des vues du modèle relationnel physique observé.

Mio — Mébioctet, soit 1 048 576 octets. La limite applicative des pièces jointes est de 10 Mio.

SIIGFP — Sigle du contexte d’accueil fourni pour le projet ; développement officiel et rattachement administratif [À CONFIRMER].

SQL — Langage utilisé pour définir les tables, leurs contraintes et les vues, et interroger la base relationnelle.

SSR — Rendu de composants React côté serveur utilisé dans certains tests. Sa présence dans les tests ne signifie pas que l’application est livrée comme site à rendu serveur.

Transaction — Ensemble d’opérations sur la base dont la validation ou l’annulation forme une unité.

Trigger — Fonction déclenchée par la base lors d’une opération sur les données afin d’appliquer une règle complémentaire.

# Avant-propos

Ce mémoire porte sur la conception et la réalisation d’une application web de gestion et de suivi des stagiaires dans le cadre d’un stage au Ministère de l’Économie et des Finances de Madagascar, au sein du contexte SIIGFP indiqué pour le projet. Il est préparé en vue de l’obtention d’une Licence en Informatique à IT University, option Développement d’applications. L’intitulé administratif exact de la formation, la promotion et les dates officielles du stage restent [À CONFIRMER].

IT University constitue le cadre universitaire de ce travail. Le mémoire met en relation les compétences de développement d’applications, de conception relationnelle et de validation logicielle avec un système métier concret. Une présentation institutionnelle détaillée de l’établissement pourra être ajoutée à partir de la notice officielle applicable à la promotion. Les mémoires fournis en référence servent de repères de structure et de présentation ; leurs descriptions institutionnelles ne sont pas reprises comme des informations administratives actuelles.

Le MEF est l’organisme d’accueil désigné dans le sujet. Le nom complet de l’unité SIIGFP, sa place dans l’organigramme, les fonctions de ses responsables et le périmètre précis de son intervention dans l’accueil des stagiaires restent [À CONFIRMER]. Le présent travail décrit le logiciel du dépôt Stagemanagement ; il ne prétend pas documenter l’ensemble du système d’information du ministère ni démontrer une intégration avec un système financier existant.

La version du projet examinée est identifiée par la révision Git 9e1a7d8. Les constats techniques et les nouvelles vérifications consignées dans ce document sont arrêtés au 10 novembre 2026. Cette date correspond à la préparation du mémoire, et non à une date de soutenance ou à une borne de stage. Les affirmations importantes renvoient à des preuves repérées P01 à P36, présentées en annexe et détaillées dans la matrice documentaire associée.

# Remerciements

[À CONFIRMER — texte personnel à valider avant dépôt]

Je remercie IT University pour le cadre de formation dans lequel s’inscrit ce mémoire, ainsi que le Ministère de l’Économie et des Finances pour l’accueil associé à ce stage.

Je souhaite également remercier mon encadreur pédagogique, [À CONFIRMER : nom et fonction], et mon encadreur professionnel, [À CONFIRMER : nom et fonction]. Leur contribution précise à ce travail sera décrite après validation personnelle.

[À CONFIRMER : ajouter les personnes effectivement concernées et personnaliser les remerciements selon l’expérience vécue.]
