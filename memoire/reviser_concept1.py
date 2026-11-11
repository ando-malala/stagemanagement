"""Révision éditoriale du mémoire à partir de concept1, sans rendu bureautique."""
from pathlib import Path
from copy import deepcopy
import re, json, hashlib
from zipfile import ZipFile
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.text.paragraph import Paragraph

ROOT=Path(__file__).resolve().parent
SRC=Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_concept1.docx')
OUT=ROOT/'Memoire_Licence_MEF_SIIGFP_concept1_revise.docx'
D=Document(SRC); P=list(D.paragraphs); T=list(D.tables)
assert len(P)==242 and len(T)==10
source_hash=hashlib.sha256(SRC.read_bytes()).hexdigest()
changes={
6: 'Novembre 2026',
7: 'Encadreur professionnel : RAJOELISOLO Mitanjo',
17: 'CRUD — Opérations de création, de consultation, de modification et de suppression d’une donnée.',
23: 'MCD, MLD et MPD — Modèle conceptuel, modèle logique et modèle physique des données.',
25: 'SIIGFP — Système Intégré Informatisé de Gestion des Finances Publiques.',
27: 'SSR — Rendu de composants côté serveur ; technique utilisée pour certains tests de l’interface React.',
31: 'Ce mémoire s’inscrit dans le cadre de mon stage de fin d’études de Licence en Informatique, option Web, à IT University. D’une durée de trois mois, le stage s’est déroulé au sein de la Cellule d’Exploitation SIIGFP de la Direction du Budget, au Ministère de l’Économie et des Finances de Madagascar. Le projet porte sur la conception et la réalisation d’une application web de suivi des stagiaires et de leurs stages.',
32: 'Le suivi des stagiaires associe des informations administratives, des activités professionnelles et des échéances. Leur organisation dans un système commun demande de comprendre les responsabilités des utilisateurs avant de les traduire en données, en règles de gestion et en interfaces. Ce travail met ainsi en relation la formation informatique et les besoins de la structure d’accueil.',
33: 'IT University constitue le cadre universitaire de cette formation. Les enseignements en développement web, bases de données et génie logiciel trouvent leur application dans la construction du système : analyse du besoin, modélisation relationnelle, réalisation des services, développement de l’interface et validation des traitements.',
34: 'La Cellule d’Exploitation SIIGFP de la Direction du Budget accueille des stagiaires issus de différents établissements et filières. Le projet concerne leur suivi au sein de cette structure. Il prend en charge les dossiers, les affectations, les productions hebdomadaires et les décisions de fin de stage. Son périmètre reste celui de la gestion des stages, distinct des traitements financiers du ministère. [1]',
35: 'Le cahier des charges fixe les fonctions attendues et les responsabilités des acteurs. La liste des tâches organise leur réalisation en quatre phases : préparation, développement, tests et livraison. Ces documents servent de fil conducteur à la présentation du projet, depuis l’expression du besoin jusqu’au bilan de la solution. [1, 2]',
38: 'Je remercie IT University et l’équipe pédagogique pour la formation reçue au cours de ma Licence. Je remercie également le Ministère de l’Économie et des Finances, ainsi que la Cellule d’Exploitation SIIGFP de la Direction du Budget, pour l’accueil de ce stage.',
39: 'J’adresse mes remerciements à RAJOELISOLO Mitanjo, mon encadreur professionnel, ainsi qu’à RAKOTOARIMALALA Tsinjo Tony pour l’accompagnement de ce travail.',
40: '',
42: 'Le suivi d’un stagiaire commence par la constitution de son dossier et se poursuit tout au long de sa présence dans la structure d’accueil. Il implique des informations personnelles et académiques, une affectation, un encadreur, des activités à réaliser et des documents à conserver. Lorsque ces informations sont dispersées, il devient difficile de connaître rapidement l’état d’un dossier, de repérer un rapport manquant ou de préparer la fin d’une participation.',
43: 'À la Cellule d’Exploitation SIIGFP de la Direction du Budget, le cahier des charges met en évidence un besoin de centralisation et de suivi régulier. Il souligne notamment les difficultés de consultation des dossiers complets, le manque de visibilité sur les échéances et l’absence de suivi hebdomadaire centralisé. La problématique du projet est donc la suivante : comment centraliser et sécuriser la gestion des stagiaires tout en facilitant le suivi de leurs activités, la traçabilité des décisions et l’identification des dossiers nécessitant une intervention ? [1]',
44: 'Pour répondre à ce besoin, le projet consiste à développer une application web couvrant le cycle de vie d’une participation à un stage. La solution doit réunir les données administratives et académiques, organiser les affectations, gérer les rapports et les pièces jointes, puis préparer l’évaluation et la clôture. Les alertes, les indicateurs et les actions recommandées doivent aider les responsables à traiter les situations prioritaires. La sécurité repose sur des droits adaptés au rôle de chaque utilisateur et à son lien avec le dossier.',
45: 'La réalisation s’appuie sur une architecture séparant l’interface React, les services Java développés avec Spring Boot et la base PostgreSQL. L’analyse des règles métier guide le modèle de données et les traitements de l’API. Des tests unitaires, des contrôles d’intégration et des tests de l’interface accompagnent cette réalisation. Le bilan présente leurs résultats ainsi que les limites fonctionnelles et techniques de la solution.',
46: 'Ce mémoire comprend trois chapitres. Le premier présente la structure d’accueil, les besoins, l’organisation du travail et les choix techniques. Le deuxième détaille la conception et la réalisation de l’application, en particulier les données, la sécurité et les parcours de suivi. Le troisième expose les résultats, les apports du projet et les perspectives d’évolution. Les annexes réunissent les principales règles de gestion, l’inventaire des tables, les scénarios de recette et le détail de l’estimation des tâches.',
49: 'Le projet est réalisé dans le contexte de l’accueil des stagiaires au Ministère de l’Économie et des Finances. La structure concernée est la Cellule d’Exploitation SIIGFP de la Direction du Budget. Les stagiaires proviennent de différents établissements et filières ; leur suivi nécessite de relier leurs informations personnelles à un parcours académique, une structure d’accueil, un encadreur et une période de stage. Le cahier des charges définit une application commune pour organiser ces relations. [1]',
50: 'L’objectif général est d’assurer la gestion et le suivi d’un stage, depuis l’enregistrement du stagiaire jusqu’à la clôture de sa participation. Une même personne peut effectuer plusieurs participations au cours de son parcours. Son identité générale doit donc être conservée indépendamment des dates, de l’encadreur et du thème propres à chaque stage. Cette distinction constitue le point de départ du modèle de données.',
51: 'Les objectifs spécifiques portent sur la centralisation des dossiers, la gestion des affectations, la saisie des rapports hebdomadaires et le suivi des pièces jointes. La solution doit également conserver les changements de statut, détecter les échéances et produire un bilan de fin de stage. Le tableau de bord regroupe les situations à examiner : rapports manquants, validations en attente, dossiers incomplets ou participations arrivant à leur terme. [1]',
52: '1.2 Analyse de l’existant et périmètre du projet',
53: 'Le cahier des charges décrit des informations réparties entre plusieurs supports et un suivi qui manque de centralisation. Les documents papier, classeurs isolés ou fichiers non synchronisés compliquent la recherche d’un dossier complet et la conservation de son historique. Les difficultés concernent aussi la visibilité sur les stages en cours, à venir ou terminés, ainsi que l’identification des rapports attendus et des documents manquants. [1, §1]',
54: 'Ces difficultés se répercutent sur plusieurs activités. La consultation d’un dossier exige de rapprocher les informations d’identité, d’études et d’affectation. Le suivi hebdomadaire demande de comparer les périodes attendues avec les rapports effectivement soumis. La préparation de la clôture suppose de vérifier les pièces et les productions avant de prendre une décision. Une application centralisée doit donc conserver les relations entre ces éléments et rendre leur état accessible aux personnes concernées.',
55: 'Le périmètre fonctionnel comprend les référentiels, les comptes, les stagiaires, les profils académiques, les encadreurs, les structures d’accueil, les cadres de stage et les participations. Il couvre aussi les rapports, la gestion documentaire, les évaluations, les soutenances facultatives, les alertes et les exports. La recherche, les contrôles d’accès et l’historisation interviennent dans l’ensemble de ces modules. Les fonctions de pilotage complètent la consultation individuelle par une vue des dossiers nécessitant une action. [1, §3]',
57: 'Le cahier des charges distingue quatre acteurs, présentés au tableau 1. L’administrateur gère l’application et les habilitations. Le responsable des stages organise le suivi administratif dans son périmètre départemental. L’encadreur suit les participants qui lui sont affectés et examine leurs rapports. Le stagiaire consulte son dossier et renseigne ses activités. Dans la version réalisée, les fonctions sont réparties entre les rôles ADMIN, ENCADREUR et STAGIAIRE, illustrés à la figure 1 ; la gestion distincte du responsable départemental constitue une évolution du contrôle d’accès. [1, §4]',
62: 'Source : conception de l’application, à partir de SecurityConfig et DossierAccessService.',
63: 'Les besoins s’organisent autour de quatre ensembles. La constitution du dossier rassemble les données personnelles, académiques et documentaires. L’organisation du stage définit la structure, le cadre de travail, la période et l’encadreur. Le suivi professionnel comprend les rapports, les observations et l’évaluation. Enfin, le pilotage regroupe les échéances, les alertes, les indicateurs et les bilans. Chaque ensemble utilise les données des précédents : un rapport appartient à une participation, elle-même reliée à un stagiaire et à un encadreur.',
64: 'Les exigences non fonctionnelles concernent la confidentialité, la fiabilité, les performances et la facilité d’utilisation. Les comptes doivent être protégés et les données accessibles selon les responsabilités. Les formulaires doivent contrôler les saisies et expliquer les erreurs. Le cahier des charges demande une navigation cohérente sur ordinateur et tablette, la pagination des listes, la compatibilité avec les navigateurs récents et une procédure de sauvegarde et de restauration. La traçabilité des opérations sensibles complète ces exigences. [1, §9–11]',
66: 'Le planning prévisionnel répartit le travail sur 65 jours estimés : 11 jours de préparation, 42 jours de réalisation, 8 jours de tests et 4 jours de livraison. Cette répartition est commune au cahier des charges et au classeur de tâches. La préparation couvre la compréhension du contexte, l’analyse des besoins, les règles de gestion, la conception des données et les interfaces. L’annexe D présente la répartition détaillée. [1, §14 ; 2]',
67: 'La phase de réalisation commence par le socle technique et la sécurité, puis aborde les référentiels, les dossiers et les affectations. Les rapports hebdomadaires, la gestion documentaire et les alertes s’appuient sur ces premières fonctions. L’évaluation, la soutenance facultative, le tableau de bord et les exports complètent les parcours. Cet ordre tient compte des dépendances métier : la saisie d’un rapport suppose, par exemple, l’existence d’une participation et l’association du compte stagiaire à son dossier.',
68: 'La phase de tests prévoit les contrôles unitaires, l’intégration avec PostgreSQL, les autorisations et les scénarios reliant le frontend au backend. La livraison comprend la préparation du déploiement, la sauvegarde et la restauration, la documentation et la présentation de l’application. Les durées du classeur sont prévisionnelles ; elles ne constituent pas un relevé du temps réellement consommé. Elles servent à répartir l’effort entre les activités et à réserver une place aux vérifications et à la mise en service. [2]',
70: 'La solution utilise Java 17 et Spring Boot 4.1.0 pour le backend, React 19.2.8 pour l’interface et PostgreSQL pour la persistance. Vite 8.2.2 assure le serveur de développement et la construction du frontend. PDFBox 3.0.8 intervient dans le contrôle des documents PDF et la génération des bilans. Le tableau 2 précise le rôle de chaque technologie dans l’architecture. [7]',
73: 'React permet de décomposer l’interface en composants réutilisables : formulaires, tableaux, messages d’erreur et dialogues de confirmation. Les pages métier les assemblent selon le parcours de l’utilisateur. L’état des composants gère les données affichées, les chargements et les actions en cours. Cette organisation facilite l’évolution de plusieurs pages sans dupliquer chaque élément de l’interface. [5]',
74: 'PostgreSQL répond au caractère relationnel du domaine. Une participation référence un profil académique, un stage et un encadreur ; ses rapports, documents et événements doivent rester liés à cette même participation. Les clés étrangères préservent ces liens, tandis que les contraintes d’unicité et de vérification contrôlent les doublons et certaines valeurs. Les services Java complètent ces contraintes par les règles temporelles et les autorisations. [4]',
76: 'L’application suit une architecture client-serveur, représentée à la figure 2. Le navigateur exécute l’interface React et échange avec l’API du backend. Les services Java appliquent les règles métier avant d’accéder à PostgreSQL par l’intermédiaire des repositories JPA. Les documents sont conservés dans un stockage de fichiers distinct ; leurs métadonnées et leurs associations au dossier sont enregistrées en base.',
79: 'Source : architecture de l’application, clients API, configuration Spring et service de stockage.',
81: 'En développement, le proxy Vite relaie les appels vers le backend. L’architecture prévue pour l’exploitation utilise une origine HTTPS commune : le serveur web distribue les ressources de l’interface et transmet les requêtes /api à Spring Boot. Cette organisation centralise la protection du transport et simplifie la configuration du navigateur. La base de données et le répertoire documentaire nécessitent chacun un stockage persistant et une stratégie de sauvegarde.',
84: 'Le modèle distingue le stagiaire, son profil académique et sa participation à un stage. La table stagiaires contient l’identité et les coordonnées. La table stagiaire_academique associe cette personne à un niveau d’étude, une filière, un établissement et une année d’études. La table stage_participant relie ensuite ce profil à un stage, un cadre de stage, un encadreur et une période. Les données propres à un stage restent ainsi séparées des informations générales de la personne.',
85: 'La figure 3 présente les principales relations du modèle physique PostgreSQL. Les flèches relient une table porteuse d’une clé étrangère à la table référencée. Cette vue met en évidence la position centrale de la participation dans le suivi : les rapports, les événements, les documents et les évaluations s’y rattachent. Les tables de référence complètent le modèle pour les niveaux d’étude, les établissements, les filières et les statuts.',
90: 'L’organisation administrative relie le stage à une structure d’accueil, elle-même rattachée à un département. Le département ne figure donc pas directement dans l’identité du stagiaire. Cette organisation permet à une personne de participer à des stages dans des contextes différents, tout en conservant son dossier. Les permissions de la version réalisée reposent sur le rôle administratif, l’affectation de l’encadreur et l’association du compte stagiaire à cette personne.',
95: 'Le backend est organisé en contrôleurs, services, repositories et entités, comme l’illustre la figure 4. Les contrôleurs reçoivent les requêtes HTTP et transmettent les données aux services. Les objets de transfert, ou DTO, définissent les informations échangées avec l’interface. Les repositories assurent l’accès aux entités persistées par JPA. Des services communs prennent en charge les droits sur les dossiers, les règles des participations et le calcul du suivi.',
98: 'Source : organisation des contrôleurs, services, DTO et repositories du backend.',
99: 'Les services coordonnent les opérations qui mobilisent plusieurs données. Lors d’une affectation, le service résout les références, vérifie leur admissibilité, contrôle les dates et recherche un éventuel chevauchement. Il détermine ensuite le statut initial de la participation. Lors d’une clôture, il contrôle la transition, examine la complétude et enregistre la décision avec son historique. Les routes d’accès direct non autorisées sont fermées par la configuration de sécurité afin que ces règles restent appliquées.',
100: 'Les transactions regroupent les écritures qui appartiennent à une même opération métier. Une modification de statut, son événement et son historique doivent rester cohérents. L’annotation @Transactional délimite ces traitements dans les services Spring. Le mécanisme transactionnel repose sur l’interception des appels par Spring et sur la transaction de la base de données. Il permet de valider les écritures de l’opération ensemble ou de les annuler lorsqu’une erreur entraîne un retour arrière. [3]',
101: 'Le verrouillage pessimiste complète les transactions pour les opérations concurrentes. Avant de modifier une participation, ParticipationRulesService relit et verrouille l’entité avec PESSIMISTIC_WRITE. Les participations clôturées, annulées ou abandonnées ne peuvent plus être modifiées par les opérations soumises à cette règle. La création d’une affectation utilise également un verrou sur le stagiaire afin de limiter les chevauchements résultant de deux demandes simultanées.',
}

def setp(p,text):
    props=deepcopy(p.runs[0]._r.rPr) if p.runs and p.runs[0]._r.rPr is not None else None
    p.clear();r=p.add_run(text)
    if props is not None:r._r.insert(0,props)

def delete(p):
    assert not p._p.xpath('./w:pPr/w:sectPr'), 'Conserver le saut de section'
    p._p.getparent().remove(p._p)

def before(i,text,style='Normal'):
    p=P[i].insert_paragraph_before(text,style)
    if style=='Normal':p.alignment=WD_ALIGN_PARAGRAPH.JUSTIFY
    return p

# Les dictionnaires suivants prolongent la révision du corps du mémoire.
changes.update({
103: 'L’authentification repose sur une session applicative. Lors de la connexion, AuthService recherche le compte, vérifie son statut ACTIF et compare le mot de passe à son empreinte BCrypt. Le service génère ensuite un jeton opaque à partir de 32 octets aléatoires. Seule l’empreinte SHA-256 de ce jeton est conservée dans la table des sessions. La session expire huit heures après sa création. Le navigateur utilise le jeton reçu pour authentifier les requêtes suivantes.',
104: 'À chaque requête protégée, SessionAuthenticationFilter lit le jeton transmis dans l’en-tête Authorization avec le schéma Bearer. Le filtre recherche la session valide et reconstitue l’identité de l’utilisateur ainsi que ses rôles. Spring fonctionne sans session HTTP serveur, tandis que les sessions applicatives restent persistées dans PostgreSQL. Cette organisation permet de révoquer une session, notamment lors de la déconnexion ou d’une réinitialisation du mot de passe.',
105: 'Les autorisations interviennent à deux niveaux. SecurityConfig définit les rôles admis selon la méthode HTTP et le chemin de la requête, puis refuse les accès qui ne correspondent à aucune règle autorisée. DossierAccessService contrôle ensuite le lien entre le compte et la ressource demandée. Un encadreur doit être affecté à la participation ; un stagiaire doit être associé au dossier. Ce second contrôle protège aussi les accès effectués directement par une URL ou par un identifiant transmis à l’API. [6]',
106: 'La version réalisée utilise les rôles ADMIN, ENCADREUR et STAGIAIRE. L’administrateur dispose d’une gestion globale des dossiers. L’encadreur intervient sur les participations qui lui sont affectées, tandis que le stagiaire accède à son propre suivi. Les anciennes attributions RESPONSABLE_STAGE sont interprétées comme ADMIN. Pour les rapports, la propriété et l’affectation restent déterminantes même lorsqu’un utilisateur cumule plusieurs rôles : l’auteur soumet son rapport et l’encadreur affecté l’examine, sans pouvoir valider sa propre production.',
107: 'Le frontend conserve le jeton dans localStorage et le joint aux appels de l’API. Cette persistance maintient la session entre les chargements de page. Elle impose de limiter le risque d’exécution de scripts non autorisés dans l’application, car ceux-ci pourraient accéder au jeton. La sécurité du déploiement comprend donc la protection HTTPS, la configuration du serveur et la maîtrise des dépendances, en complément des contrôles d’accès appliqués par le backend.',
110: 'Le compte utilisateur est associé explicitement au dossier du stagiaire. Cette liaison permet au portail personnel de retrouver les informations de la bonne personne. Les listes de comptes éligibles tiennent compte du rôle, du statut actif et de l’unicité de l’association. Le service verrouille le compte pendant l’opération afin de contrôler les affectations concurrentes. Un parcours administratif analogue permet de relier un compte à un encadreur. Le dossier peut ainsi être constitué avant l’ouverture de l’accès utilisateur.',
115: 'La clôture comporte trois modalités, résumées au tableau 4 et à la figure 5. La clôture ordinaire exige les pièces obligatoires actives et la validation des rapports correspondant aux périodes achevées. Les périodes futures ne sont pas exigées. La dérogation documentaire autorise l’absence de pièces tout en maintenant le contrôle des rapports. Enfin, la clôture forcée permet une décision administrative malgré des pièces ou des rapports manquants, avec conservation de ces éléments dans la trace de décision.',
121: 'Le mode forcé est activé par le paramètre forcerCloture. Les contrôles d’accès, de transition et de clôture antérieure restent appliqués. Pendant le verrouillage de la participation, le serveur recalcule les listes de pièces absentes et de périodes dont les rapports manquent ou ne sont pas validés. Il les ajoute au motif de l’événement, enregistre la date de clôture et affecte le statut CLOTUREE. Le dossier conserve ainsi les conditions dans lesquelles la décision a été prise.',
122: 'Dans l’interface, l’administrateur accède à un dialogue de confirmation qui signale la possibilité de clôturer un dossier incomplet. La confirmation transmet le paramètre de clôture forcée. Le motif saisi accompagne la décision ; lorsqu’il est vide, le serveur ajoute une mention générique de clôture administrative. Cette trace conserve l’état du dossier au moment de sa fermeture, sans modifier la présence des pièces ni le statut des rapports.',
123: 'Les tests de clôture vérifient les transitions refusées, la clôture d’une participation déjà terminée, les pièces et rapports obligatoires, ainsi que les deux formes de dérogation. Ils contrôlent aussi le maintien des droits d’accès et l’enregistrement des éléments manquants. Les treize scénarios de ClotureParticipationTests ont réussi lors de la campagne unitaire du 10 novembre 2026. Les résultats globaux des campagnes de validation sont présentés en section 2.12. [7]',
130: 'Source : parcours de traitement des rapports dans les services métier et l’interface React.',
131: 'Les changements d’état utilisent une requête PATCH sur le statut du rapport. Le passage à SOUMIS enregistre la date de soumission ; le passage à VALIDE renseigne la date de validation. Les règles serveur et les contrôles PostgreSQL encadrent les périodes et les transitions. Un rapport déposé reste donc distinct d’un rapport validé : le premier attend l’examen de l’encadreur, tandis que le second a terminé le circuit de validation. Cette différence intervient dans les indicateurs et dans le contrôle de clôture.',
132: 'Chaque observation conserve son auteur, son type, son commentaire et son niveau de visibilité. Les observations privées sont masquées dans le parcours d’un compte possédant seulement le rôle STAGIAIRE. L’encadreur doit être affecté au dossier pour intervenir sur le rapport. Les services de synthèse appliquent les mêmes droits lors de la restitution des informations, afin que les règles de consultation restent cohérentes entre la page du dossier et ses documents de sortie.',
134: 'Les pièces jointes sont rattachées soit au dossier général du stagiaire, soit à une participation précise. Le catalogue documentaire indique le périmètre de chaque type de pièce, sa multiplicité et son caractère obligatoire pour la clôture. Le service consulte ce catalogue lors du dépôt et du calcul de complétude. Cette organisation permet, par exemple, de distinguer un document d’identité lié à la personne d’un document propre à une période de stage.',
135: 'DocumentContentValidator vérifie le nom, l’extension, le type MIME, la taille et le contenu du fichier. Les formats admis sont PDF, JPEG et PNG, avec une limite applicative de 10 Mio par fichier. Les images doivent pouvoir être décodées et respecter la limite de pixels. Les documents PDF sont analysés avec PDFBox ; les fichiers chiffrés, certains contenus actifs et les nombres de pages hors des bornes prévues sont refusés. Ces contrôles réduisent les risques liés aux formats et complètent les autorisations de dépôt.',
137: 'La base conserve les métadonnées alors que le fichier lui-même est écrit sur disque. Ces deux opérations demandent une coordination : si la transaction n’est pas validée, le service prévoit le nettoyage du fichier ajouté. Pour la sauvegarde, la même dépendance impose de conserver ensemble la base et les pièces. La restauration doit retrouver les documents aux emplacements correspondant aux métadonnées, faute de quoi un dossier pourrait référencer un fichier absent.',
139: 'L’évaluation finale est rattachée à une participation et à son encadreur. Elle contient une appréciation générale et des notes par critère, comprises entre 0 et 5. Le catalogue définit les critères actifs, leur ordre, leur coefficient et leur caractère obligatoire. La restitution présente les notes enregistrées et les observations de l’évaluation. Les contraintes d’unicité empêchent la création de plusieurs évaluations finales pour une même participation.',
140: 'La finalisation vérifie que chaque critère actif obligatoire possède une note. Le service marque ensuite l’évaluation comme finalisée et enregistre la date correspondante. Les modifications ultérieures sont bloquées, et l’auteur de la finalisation est conservé dans la journalisation. L’opération est accessible à l’administrateur ou à l’encadreur affecté dans les conditions prévues par le service. La grille finalisée peut alors être intégrée au bilan de la participation.',
141: 'La soutenance est facultative. Lorsqu’elle est prévue, elle est enregistrée pour une participation avec une date, un lieu et une observation éventuelle. Une contrainte d’unicité limite la participation à une soutenance enregistrée. Les alertes de soutenance concernent uniquement les rendez-vous planifiés. Le dossier peut donc être suivi sans soutenance, conformément au cahier des charges. La saisie administrative est distincte de la consultation des informations par les autres acteurs. [1, §5.21]',
143: 'Un traitement quotidien est programmé à 06 h 00 dans le fuseau Indian/Antananarivo. Il enchaîne sept contrôles : fins de stage proches, participations dépassées, rapports manquants, validations en retard, dossiers incomplets, surcharge des encadreurs et soutenances proches. Ces calculs s’appuient sur les dates, les statuts et les paramètres de suivi. Leur résultat alimente les alertes et les informations présentées dans le tableau de bord.',
144: 'Une alerte possède un type, une priorité, un statut et une clé de déduplication. Cette clé évite de multiplier les alertes pour une même situation lors des passages successifs du traitement. Le statut A_TRAITER désigne une alerte active ; les états TRAITEE, IGNOREE et EXPIREE décrivent la suite de son traitement. La nature de l’alerte et son niveau de priorité sont distincts : la première décrit la situation, tandis que le second aide à déterminer son urgence.',
145: 'La clôture d’une participation provoque l’expiration des alertes actives liées à cette participation, à ses rapports ou à sa soutenance. Un traitement au démarrage rapproche également les alertes anciennes avec les dossiers déjà clôturés. L’historique est conservé : le service modifie l’état et la date de traitement sans supprimer l’alerte. Cette règle évite que le tableau de bord continue à proposer une action sur une participation déjà fermée.',
146: 'PilotageService sélectionne les alertes accessibles à l’utilisateur, puis les regroupe par participation. Le score d’un dossier correspond au cumul des poids associés aux types d’alerte. Les seuils de 20 et de 50, complétés par les priorités AVERTISSEMENT et URGENT, permettent de classer le dossier dans les niveaux NORMALE, HAUTE ou URGENTE. L’action recommandée accompagne ce classement. Le mécanisme repose sur des règles déterministes, compréhensibles à partir des alertes actives du dossier.',
147: 'Le tableau de bord présente les échéances, les rapports attendus ou manquants, les documents absents, la charge des encadreurs et les soutenances prévues à trente jours. Ces informations sont calculées à partir des dossiers accessibles au compte connecté. La vue de pilotage réunit ainsi les éléments utiles pour passer d’un constat à une action : ouvrir une participation, examiner un rapport ou compléter un dossier.',
148: 'Les services de synthèse réunissent les données nécessaires aux documents de sortie. Les listes sont exportées en CSV et le bilan de fin de stage en PDF. ExportService échappe les cellules CSV et neutralise les chaînes susceptibles d’être interprétées comme des formules par un tableur. PDFBox assure la pagination du bilan. L’export utilise le périmètre autorisé par le serveur ; les filtres locaux appliqués à un tableau de l’interface ne sont pas systématiquement transmis à l’export global.',
150: 'Le frontend est organisé en pages métier, composants communs et clients API. Les composants Table, BusinessForm, ConfirmDialog, ErrorMessage et Loader prennent en charge les interactions récurrentes. Les pages de détail regroupent les informations d’un dossier et les opérations possibles. Les formulaires utilisent les référentiels pour proposer des choix adaptés, tandis que les messages d’erreur précisent les données à corriger ou la règle empêchant l’opération.',
151: 'La navigation repose sur la History API du navigateur. AppRoutes utilise pushState, replaceState et popstate pour changer de page et gérer les déplacements dans l’historique. Le fichier routeConfig associe chaque chemin à une page, un titre et des rôles autorisés. Les routes de détail vérifient le format de l’identifiant et les destinations de retour après connexion sont limitées aux chemins internes de l’application.',
153: 'Le tableau 5 rassemble les principaux parcours de l’interface. L’admission conduit au dossier et aux informations académiques ; l’affectation ouvre le suivi d’une participation ; la grille hebdomadaire donne accès aux rapports ; le pilotage permet de rejoindre les dossiers prioritaires. Les pages de détail constituent des points de consultation communs, à partir desquels les actions disponibles dépendent du rôle et de l’état du dossier.',
157: 'Le script de référence Conception2.sql définit 44 tables, 6 vues et 16 triggers. Les vues regroupent notamment les échéances, la charge des encadreurs, les rapports en attente, les pièces manquantes et les actions prioritaires. Le backend utilise également des calculs applicatifs, notamment pour filtrer les données selon les autorisations avant de construire les synthèses. L’inventaire des tables figure en annexe B.',
159: 'Les entités JPA représentent les tables et leurs relations dans le code Java. La cohérence de leurs noms de tables et de colonnes avec le script SQL conditionne le démarrage de l’application. Les validations interviennent à plusieurs niveaux : une colonne peut accepter une valeur nulle pour conserver un état intermédiaire, alors qu’une opération de finalisation exige cette valeur. Le modèle de persistance et le service métier expriment ainsi des contraintes complémentaires.',
160: 'Hibernate fonctionne en mode validate et vérifie un schéma préexistant au démarrage. La création initiale de la base est séparée de son évolution. Les migrations V002__association_eligibility.sql et V003__participation_report_consistency.sql ajoutent des protections sur les associations et les périodes des rapports. Un précontrôle SQL permet d’examiner certaines incohérences avant leur application. Ces scripts sont appliqués explicitement ; le démarrage de Spring Boot ne les installe pas automatiquement. [7]',
162: 'La validation associe plusieurs niveaux de test. Les tests unitaires isolent les règles des services : droits, dates, clôture, documents ou alertes. Les tests d’intégration vérifient les échanges HTTP et les contraintes PostgreSQL. Les tests frontend portent sur les fonctions, les contrats des clients API et le rendu de composants. Le tableau 6 présente les résultats des campagnes des 10 et 11 novembre 2026, en indiquant leur périmètre. [7]',
165: 'La campagne du 10 novembre a abouti à 36 tests unitaires backend réussis et à 126 tests frontend réussis. Le contrôle ESLint et la construction Vite ont également réussi. Le premier lancement frontend avait rencontré onze échecs liés au chargement de React depuis des chemins dont la casse différait. La relance depuis le chemin canonique du projet a permis de terminer la suite sans modifier le code source. Cet incident a conduit à distinguer un problème d’environnement d’un défaut fonctionnel.',
166: 'Le compte rendu du 11 novembre indique 66 tests backend réussis, dont 13 tests PostgreSQL, avec les migrations V002 et V003 appliquées sur une base isolée. Le précontrôle SQL s’est également terminé sans erreur. Cette campagne couvre des contrôles HTTP, SQL et de concurrence ciblés. Les nombres de tests correspondent à des campagnes distinctes et ne sont pas additionnés. [7]',
167: 'La recette utilisateur complète ces contrôles par des parcours réalisés dans le navigateur. Elle porte notamment sur l’enregistrement d’un stagiaire, l’affectation, le cycle complet d’un rapport, le dépôt et le téléchargement d’un document, puis la clôture. Les essais de charge, la restauration et l’exécution du traitement quotidien relèvent de la validation d’exploitation. Les scénarios de l’annexe C décrivent les conditions et les résultats attendus de ces vérifications.',
168: '2.13 Préparation du déploiement',
169: 'Le dépôt contient des Dockerfile pour le frontend et le backend, un fichier docker-compose.yml et une configuration Nginx. Les paramètres de connexion PostgreSQL et le répertoire documentaire sont externalisés. La mise en service prévoit un serveur web pour distribuer les ressources React et relayer les requêtes vers l’API. La configuration de l’environnement cible doit aussi assurer la persistance des données, la protection HTTPS et la journalisation des erreurs.',
170: 'La procédure d’intégration crée une base locale isolée, installe le schéma, applique les migrations V002 et V003, puis exécute les tests et le précontrôle. Pour une base contenant déjà des dossiers, les migrations constituent une opération distincte de l’initialisation. Le script Conception2.sql comporte une suppression et une création de base ; il est destiné à un environnement neuf. L’évolution d’une installation existante nécessite la préservation des données et l’examen des incohérences éventuelles avant application des nouvelles protections.',
171: 'L’exploitation repose sur une sauvegarde coordonnée de PostgreSQL et du répertoire des pièces jointes. La procédure de restauration doit rétablir les deux ensembles et vérifier l’accès aux documents depuis les dossiers. Le compte de connexion à la base doit disposer des droits nécessaires à l’application, tandis que le stockage doit rester inaccessible en téléchargement public direct. La surveillance des erreurs et des traitements planifiés complète cette préparation.',
172: '2.14 Difficultés techniques et solutions',
173: 'Une difficulté de cohérence a concerné le mapping d’un département directement associé au stagiaire. L’application attendait une colonne departement_id absente du schéma retenu, ce qui provoquait un échec de validation Hibernate. La suppression du mapping et du champ de formulaire correspondant a rétabli la cohérence. Le rattachement organisationnel reste porté par le stage et sa structure d’accueil. Cette correction souligne l’importance de maintenir ensemble le schéma SQL, les entités JPA et les formulaires.',
174: 'La gestion des décisions de fin de stage a nécessité de distinguer les contrôles bloquants et les dérogations. Un simple changement de statut aurait fait disparaître le contexte d’une clôture incomplète. Les traitements conservent donc la modalité utilisée, les pièces absentes et les périodes dont les rapports manquent ou ne sont pas validés. Le verrouillage de la participation permet de recalculer ces informations au moment de la décision, afin que la trace corresponde à l’état réellement traité.',
175: 'La cohérence des associations a également demandé des contrôles spécifiques. Un compte déjà lié à un dossier ne doit pas être affecté simultanément à un autre dossier. Une référence devenue inactive peut rester dans l’historique, tout en étant exclue des nouvelles affectations. Enfin, un changement d’encadreur doit tenir compte d’une évaluation déjà enregistrée. Les listes de choix, les validations serveur, les verrous et les protections SQL participent ensemble au traitement de ces situations. [7]',
176: '3 Bilan du projet et apports',
178: 'La réalisation rassemble les principales informations d’un stage autour de la participation. Le dossier personnel, le profil académique, l’affectation, les rapports et les documents sont reliés dans un même modèle. Le responsable du suivi peut consulter l’état d’une participation, tandis que le stagiaire et l’encadreur disposent des actions propres à leur rôle. Le tableau de bord et les alertes complètent cette consultation par une vue des échéances et des situations à traiter.',
179: 'Le tableau 7 présente les principales fonctions réalisées et les compléments nécessaires au regard du cahier des charges. Le suivi des participations et le workflow des rapports constituent le cœur de la solution. Les écarts concernent surtout la séparation du rôle de responsable départemental, certains droits du stagiaire et les conditions de la clôture administrative. [1, 7]',
182: 'Le contrôle d’accès par dossier constitue un apport important de la réalisation. Les droits dépendent de l’affectation de l’encadreur et de l’association du compte stagiaire, en complément du rôle général. La même politique intervient dans la lecture, la modification et le téléchargement. Le cahier des charges prévoit toutefois un responsable limité à un ou plusieurs départements ; ce périmètre distinct n’est pas pris en charge par l’assimilation actuelle du rôle RESPONSABLE_STAGE au rôle ADMIN.',
184: 'La séparation entre interface, services métier et persistance facilite l’évolution du système. Une règle commune peut être centralisée dans un service au lieu d’être reproduite dans plusieurs pages. Les contrôles SQL préservent les relations, tandis que les services Java prennent en charge les droits et les décisions dépendant du contexte. Les tests de clôture, de rapports et de fichiers vérifient des cas de refus et des conditions limites, en complément du parcours nominal.',
185: 'Les campagnes automatisées ont obtenu les résultats présentés au chapitre précédent. Elles couvrent les scénarios exécutés, mais la validation reste à étendre aux parcours complets dans le navigateur, aux volumes réels et aux incidents d’exploitation. Les essais d’intégration sur une base isolée apportent une vérification des contraintes SQL et des échanges avec le backend ; l’acceptation du produit nécessite aussi une recette métier sur la version destinée aux utilisateurs.',
186: 'La montée en charge constitue une première limite technique. Plusieurs services chargent des listes avant de les filtrer en mémoire selon les droits. Ce fonctionnement devient plus coûteux lorsque le nombre de dossiers et de relations augmente. Des requêtes filtrées et paginées directement dans PostgreSQL réduiraient les données chargées. Leur mise en place doit conserver les mêmes restrictions d’accès et être accompagnée de mesures sur un volume représentatif.',
187: 'La clôture administrative présente également une limite fonctionnelle. Le cahier des charges prévoit une confirmation motivée pour une clôture incomplète et le contrôle de l’évaluation finale lorsqu’elle est requise. L’interface actuelle permet un motif vide et le traitement de clôture ne comporte pas de condition correspondant à une évaluation obligatoire. La politique de dérogation doit donc préciser les cas admis, l’exigence du motif et les éléments qui restent bloquants. [1, §5.19]',
188: 'D’autres compléments concernent les parcours du stagiaire et les restitutions. Le dépôt de fichiers reste réservé à l’administrateur, alors que le cahier des charges prévoit le dépôt des pièces autorisées par le stagiaire. La consultation de certaines informations de soutenance et des historiques de connexion doit aussi être complétée. Les sorties comprennent le CSV et le bilan PDF ; l’ensemble des fiches et statistiques demandées reste à qualifier. La couverture des caractères dans les PDF et la qualité de l’impression font partie de cette validation.',
189: '3.3 Apports pour la structure d’accueil',
190: 'La centralisation fournit un point de consultation commun pour les informations du stagiaire et ses participations. Les rapports et les documents restent associés à leur contexte, et les historiques conservent les changements de statut et les décisions. Les alertes rendent visibles les retards et les échéances, tandis que le classement des dossiers oriente le suivi vers les situations les plus urgentes. Ces fonctions répondent aux difficultés de dispersion et de visibilité décrites dans le cahier des charges.',
191: 'L’effet de la solution sur le travail quotidien devra être évalué après sa mise en service. Le temps de recherche d’un dossier, le délai entre soumission et examen d’un rapport et la proportion de dossiers complets à la clôture constituent des indicateurs pertinents. Leur comparaison sur une période d’utilisation permettrait de mesurer les améliorations et d’identifier les parcours qui restent difficiles. À ce stade, aucun gain de temps chiffré n’est établi.',
192: 'La traçabilité apporte un autre support au suivi administratif. Une prolongation conserve l’ancienne et la nouvelle date de fin, un rapport conserve son état d’examen et une clôture avec dérogation garde la liste des éléments manquants. Ces informations permettent de comprendre l’évolution d’un dossier après la décision. Elles facilitent aussi la discussion entre les acteurs lorsqu’une situation nécessite une justification ou un complément.',
193: '3.4 Apports pédagogiques',
195: 'Le projet mobilise les acquis de la formation en modélisation des données et en développement web. La distinction entre personne, profil académique et participation montre comment traduire un besoin métier en relations stables. Les règles de période, de chevauchement et de clôture mettent en évidence les conséquences d’un choix de modèle sur les traitements. Une modification administrative peut affecter plusieurs productions déjà enregistrées ; sa validation doit donc tenir compte du dossier dans son ensemble.',
196: 'L’architecture illustre la complémentarité des compétences frontend et backend. L’interface doit proposer des choix compréhensibles et expliquer les refus, tandis que le serveur doit appliquer les règles même lorsque la requête ne provient pas du formulaire prévu. La distinction entre authentification et autorisation est particulièrement importante : un utilisateur connecté n’a pas nécessairement le droit de consulter tous les dossiers ou de réaliser toutes les actions.',
197: 'Le travail de validation complète la réalisation technique. Les tests unitaires vérifient une règle dans un contexte isolé ; les tests d’intégration contrôlent les échanges et la persistance ; la recette utilisateur évalue un parcours complet. L’incident lié aux chemins de modules et les corrections de mapping montrent également la place de l’environnement d’exécution dans le diagnostic. Ces situations relient les notions de programmation, de base de données et de génie logiciel à la mise au point d’une application métier.',
199: 'La première évolution concerne les habilitations. Un rôle de responsable des stages limité à ses départements permettrait de séparer la gestion administrative courante de l’administration globale. Cette évolution doit être appliquée aux listes, aux fiches, aux exports et aux documents, avec des tests utilisant plusieurs départements. Les droits de dépôt du stagiaire et la visibilité des informations de soutenance peuvent être complétés dans le même travail sur les parcours.',
200: 'L’amélioration des performances passe par la pagination SQL et les requêtes filtrées selon le périmètre autorisé. Des mesures de temps de réponse et de consommation des ressources permettraient de comparer le fonctionnement actuel avec ces optimisations. Le tableau de bord mérite une attention particulière, car il agrège plusieurs informations par participation. La réduction des lectures répétées peut améliorer son fonctionnement lorsque le volume de dossiers augmente.',
201: 'La qualité d’usage et l’exploitation constituent un second axe de progression. Les essais sur petits écrans, la navigation au clavier, l’impression et l’utilisation d’une police Unicode dans les PDF peuvent améliorer les restitutions. Des tests automatisés de navigateur compléteraient les tests de composants. La préparation de la mise en service comprend enfin la surveillance du traitement quotidien, les sauvegardes coordonnées et un exercice de restauration.',
202: 'Le paramétrage du suivi gagnerait à disposer d’une interface d’administration dédiée aux seuils d’alerte et aux éléments obligatoires. Les modifications devraient être contrôlées et historisées, car elles influencent les alertes, la complétude et les décisions de clôture. Une chaîne d’intégration continue pourrait ensuite exécuter les tests et produire des versions identifiées à chaque évolution, afin de faciliter la maintenance.',
204: 'Le projet de suivi des stagiaires répond au besoin de centralisation exprimé par la Cellule d’Exploitation SIIGFP de la Direction du Budget. Il organise les informations personnelles et académiques, les affectations et les productions autour d’un dossier commun. Le modèle distingue le stagiaire de chacune de ses participations, ce qui permet de conserver le contexte propre à chaque période de stage.',
205: 'La solution repose sur React, Spring Boot et PostgreSQL. Les services métier prennent en charge les autorisations, les rapports hebdomadaires, la gestion documentaire et les décisions de suivi. Les évaluations, les soutenances facultatives, les alertes et le bilan complètent ces parcours. L’historisation conserve les changements importants, tandis que le pilotage rapproche les échéances et les situations qui nécessitent une intervention.',
206: 'Les tests automatisés ont permis de vérifier des règles de gestion, des contrôles d’accès et des traitements de persistance. La réalisation reste à compléter sur certains points du cahier des charges, notamment le périmètre départemental, les droits de dépôt du stagiaire et les conditions de clôture. La recette utilisateur et les vérifications d’exploitation sont les étapes suivantes pour préparer une utilisation régulière dans la structure d’accueil.',
207: 'Ce travail met en relation l’analyse d’un besoin administratif et la construction d’une application web métier. Il montre l’importance des liens entre les données, des responsabilités de chaque acteur et de la cohérence des décisions au cours du stage. Les évolutions proposées prolongent cette démarche en renforçant les habilitations, les performances et les conditions de maintenance du système.',
210: '[1] Cellule d’Exploitation SIIGFP de la Direction du Budget. Cahier des charges : Application web de suivi des stagiaires. Document de projet, 21 pages. CDC_suivi_stagiaires.pdf.',
211: '[2] Projet de suivi des stagiaires. Estimation des tâches. Classeur Estimation_taches_ETU003156.xlsm, feuille Estimation, lignes 1 à 53. Estimation totale : 65 jours.',
212: '[3] Spring. Using @Transactional. Documentation du Spring Framework. https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html. Consultation : 10 novembre 2026.',
213: '[4] PostgreSQL Global Development Group. PostgreSQL 15 Documentation, Constraints. https://www.postgresql.org/docs/15/ddl-constraints.html. Consultation : 10 novembre 2026.',
214: '[5] React. Thinking in React. Documentation de React. https://react.dev/learn/thinking-in-react. Consultation : 10 novembre 2026.',
215: '[6] Spring. Authorize HttpServletRequests. Documentation de Spring Security. https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html. Consultation : 10 novembre 2026.',
216: '[7] Projet Stagemanagement. Sources Java, React et PostgreSQL ; journaux de tests du 10 novembre 2026 ; compte rendu de cohérence métier et documentation des migrations du 11 novembre 2026. Révisions 9e1a7d8 et d8819ad.',
220: 'Annexe A Principales règles de gestion',
221: 'Le tableau 8 présente les règles qui structurent les principaux parcours. Les codes RG renvoient au cahier des charges. Les règles de référence sont appliquées par les contraintes relationnelles et les services, tandis que les droits dépendent du rôle et du lien entre le compte et le dossier. [1]',
225: 'Le tableau 9 regroupe les 44 tables du schéma de référence par domaine fonctionnel. Les tables de statut, d’historique et de paramétrage sont distinguées des données de suivi. Les clés étrangères relient ces ensembles autour du stagiaire, de la participation et des comptes utilisateurs.',
228: 'Annexe C Scénarios de recette fonctionnelle',
229: 'Le tableau 10 décrit des scénarios de recette et leurs résultats attendus. Les critères CR renvoient au cahier des charges. La recette utilise des comptes de rôles différents et des dossiers de test afin de vérifier le parcours nominal ainsi que les refus d’accès et les situations incomplètes. [1, §12]',
238: 'Annexe D Estimation détaillée des tâches',
239: 'Le planning prévisionnel comprend 36 tâches chiffrées, regroupées en quatre phases. Le tableau 11 présente les sous-totaux et le tableau 12 reprend les tâches avec leurs estimations. Les numéros de ligne permettent de retrouver chaque entrée dans la feuille Estimation du classeur de suivi. [2]',
240: 'La réalisation représente 42 jours sur les 65 jours estimés, soit environ 64,6 % du total. Les tests et la livraison réunissent 12 jours. Cette répartition réserve du temps à la validation, à la documentation et à la préparation de la mise en service, en complément du développement des fonctions.',
241: 'Les durées sont exprimées en jours estimés. Le planning distingue les activités prévues sans fixer de calendrier daté ni présenter un état d’avancement réel.',
})

for i,text in changes.items():setp(P[i],text)
for i in [9,11,13,37,194,209,217,218,232,233,234,235,236,237]:delete(P[i])

# Conservation des informations techniques et retrait de l'appareil d'audit interne.
for p in D.paragraphs:
    if re.search(r'\[P\d+',p.text):
        setp(p,re.sub(r'\s*\[P\d+[^\]]*\]','',p.text))
    if re.search(r'\[R[3-6]\]',p.text):
        setp(p,re.sub(r'\[R([3-6])\]',r'[\1]',p.text))

before(40,'Résumé','Heading 1')
before(40,'Ce mémoire présente la conception et la réalisation d’une application web de suivi des stagiaires au sein de la Cellule d’Exploitation SIIGFP de la Direction du Budget du Ministère de l’Économie et des Finances. Le projet répond au besoin de centraliser les dossiers et de suivre les affectations, les activités hebdomadaires et les échéances. La solution utilise React, Spring Boot et PostgreSQL. Son modèle distingue le stagiaire, son profil académique et sa participation à un stage. Les services gèrent les autorisations, les rapports, les documents, les évaluations, les soutenances facultatives et les alertes. Le planning prévisionnel répartit les tâches sur 65 jours. Les campagnes de tests vérifient des règles métier et des traitements de persistance ; le bilan identifie les compléments nécessaires sur les habilitations, les conditions de clôture et la préparation de l’exploitation.')
before(40,'Mots-clés : suivi des stagiaires, participation, rapports hebdomadaires, SIIGFP, application web, PostgreSQL.')
before(40,'Abstract','Heading 1')
before(40,'This dissertation presents the design and implementation of a web application for internship monitoring within the SIIGFP Operations Unit of the Budget Directorate at Madagascar’s Ministry of Economy and Finance. The project addresses the need to centralize trainee records and track assignments, weekly activities and deadlines. The application uses React, Spring Boot and PostgreSQL. Its data model separates trainees, academic profiles and individual internship participations. Application services manage access rights, reports, documents, evaluations, optional defenses and alerts. The estimated work plan spans 65 days. Test campaigns verify selected business rules and persistence operations. The project assessment identifies further work on authorization, closure conditions and operational deployment.')
before(40,'Keywords: internship monitoring, participation, weekly reports, SIIGFP, web application, PostgreSQL.')

# Compléments explicatifs issus des besoins, sans consignes de rédaction.
before(69,'La durée estimée de la réalisation tient compte des dépendances entre modules. Les référentiels et les comptes sont nécessaires avant les affectations ; les participations sont nécessaires avant les rapports ; les pièces et les rapports alimentent la complétude et les alertes. Les tests accompagnent ces enchaînements pour vérifier qu’une modification dans un module ne rend pas incohérentes les informations utilisées par un autre. La liste des tâches prévoit enfin une intégration générale avant la recette.')
before(94,'Les données de référence sont séparées des dossiers. Un établissement, une filière ou un niveau d’étude peut être utilisé par plusieurs profils académiques. Lorsqu’une valeur n’est plus proposée pour de nouvelles saisies, sa conservation permet de préserver les liens historiques. Les contrôles d’activité s’appliquent aux nouvelles associations, tandis que les données anciennes restent consultables dans leur contexte. Ce principe évite de supprimer une référence encore utilisée dans le suivi.')
before(113,'Les choix proposés par les formulaires dépendent de l’état des références. Une structure d’accueil ou un cadre de stage inactif ne doit pas être sélectionné pour une nouvelle affectation. Le serveur vérifie ces conditions même si la liste a été chargée auparavant. Le changement d’encadreur tient également compte de l’évaluation existante : il est refusé lorsqu’il rendrait incohérente l’évaluation déjà liée à l’encadreur précédent. [7]')
before(124,'Les événements de cycle de vie complètent les statuts. Une prolongation explique pourquoi la date de fin a changé ; une suspension conserve sa date d’effet ; une reprise permet de situer la poursuite de la participation. Chaque événement est rattaché à son auteur et à sa date d’enregistrement. Le statut courant indique la situation du dossier, alors que la suite d’événements permet d’en comprendre l’évolution.')
before(133,'Le calcul des semaines fournit également un repère commun au stagiaire et à l’encadreur. Une période attendue sans rapport peut être identifiée avant l’ouverture d’une fiche de rapport. Une période soumise mais non examinée correspond à une autre action : l’intervention de l’encadreur. La distinction entre absence de dépôt et attente de validation permet donc d’adresser l’alerte au bon moment du parcours.')
before(149,'La priorisation se lit au niveau du dossier. Plusieurs alertes peuvent concerner simultanément une participation : une fin proche, une pièce manquante et un rapport en attente. Leur regroupement évite de traiter ces informations comme des situations sans lien. Le score donne un ordre de traitement, tandis que le détail des alertes explique les raisons de cet ordre. L’utilisateur peut ensuite ouvrir la participation concernée pour examiner la situation avant de décider.')
before(161,'La cohérence des données demande aussi de traiter les modifications après création. Le changement d’une date ne doit pas exclure un rapport déjà rattaché à la participation. Le changement d’un encadreur ne doit pas laisser une évaluation incompatible avec la nouvelle affectation. Les validations de service et les migrations de cohérence couvrent ces dépendances. Elles complètent les clés étrangères, qui garantissent l’existence d’une référence mais ne suffisent pas à exprimer toutes les relations temporelles. [7]')

bid=max([int(x.get(qn('w:id'))) for x in D.element.iter(qn('w:bookmarkStart'))]+[0])
def field(p,code,value):
    for kind in ('begin','instruction','separate','value','end'):
        r=p.add_run()
        if kind in ('begin','separate','end'):
            e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),kind)
            if kind=='begin':e.set(qn('w:dirty'),'true')
            r._r.append(e)
        elif kind=='instruction':
            e=OxmlElement('w:instrText');e.set(qn('xml:space'),'preserve');e.text=' '+code+' ';r._r.append(e)
        else:r.text=value

def caption(p,kind,num,title):
    global bid
    p.clear();p.style='Caption';p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.keep_with_next=True
    p.add_run(kind+' ');bid+=1
    start=OxmlElement('w:bookmarkStart');start.set(qn('w:id'),str(bid));start.set(qn('w:name'),kind.lower()+'_'+str(num));p._p.append(start)
    field(p,'SEQ '+kind.lower()+' \\* ARABIC',str(num))
    end=OxmlElement('w:bookmarkEnd');end.set(qn('w:id'),str(bid));p._p.append(end)
    p.add_run(' — '+title)

caption(P[58],'Tableau',1,'Acteurs et responsabilités définis par le cahier des charges')
caption(P[61],'Figure',1,'Acteurs et parcours de la version réalisée')
caption(P[116],'Tableau',4,'Modalités de clôture des participations')
caption(P[163],'Tableau',6,'Résultats des campagnes de tests')
caption(P[180],'Tableau',7,'Bilan des principales fonctionnalités')
caption(P[222],'Tableau',8,'Règles de gestion des parcours de suivi')
caption(P[230],'Tableau',10,'Scénarios et résultats attendus de la recette')

def filltable(t,rows,widths):
    for row in list(t.rows):t._tbl.remove(row._tr)
    t.autofit=False;t.alignment=WD_TABLE_ALIGNMENT.CENTER
    for col,w in zip(t.columns,widths):col.width=Cm(w)
    for i,values in enumerate(rows):
        row=t.add_row();pr=row._tr.get_or_add_trPr();pr.append(OxmlElement('w:cantSplit'))
        if i==0:pr.append(OxmlElement('w:tblHeader'))
        for j,(c,text,w) in enumerate(zip(row.cells,values,widths)):
            c.width=Cm(w);c.vertical_alignment=WD_CELL_VERTICAL_ALIGNMENT.CENTER
            p=c.paragraphs[0];p.paragraph_format.line_spacing=1.05
            p.paragraph_format.space_before=Pt(4);p.paragraph_format.space_after=Pt(4)
            p.alignment=WD_ALIGN_PARAGRAPH.CENTER if w<=2 else WD_ALIGN_PARAGRAPH.LEFT
            r=p.add_run(str(text));r.font.name='Times New Roman';r.font.size=Pt(10.5)
            r.bold=i==0;r.font.color.rgb=RGBColor.from_string('FFFFFF' if i==0 else '000000')
            cp=c._tc.get_or_add_tcPr();sh=OxmlElement('w:shd');sh.set(qn('w:fill'),'354F60' if i==0 else ('F3F5F7' if i%2==0 else 'FFFFFF'));cp.append(sh)
            borders=OxmlElement('w:tcBorders');mar=OxmlElement('w:tcMar')
            for side in ('top','left','bottom','right'):
                e=OxmlElement('w:'+side);e.set(qn('w:val'),'single');e.set(qn('w:sz'),'4');e.set(qn('w:color'),'D9D9D9');borders.append(e)
                e=OxmlElement('w:'+side);e.set(qn('w:w'),'85');e.set(qn('w:type'),'dxa');mar.append(e)
            cp.append(borders);cp.append(mar)

filltable(T[0],[
['Acteur','Périmètre attendu','Responsabilités'],
['Administrateur','Ensemble de l’application','Administrer les comptes, les rôles, les référentiels et les droits d’accès.'],
['Responsable des stages','Départements auxquels il est affecté','Constituer les dossiers, organiser les affectations, suivre les alertes et décider de la clôture.'],
['Encadreur professionnel','Participations qui lui sont affectées','Examiner les rapports, formuler des observations, demander des corrections et réaliser l’évaluation.'],
['Stagiaire','Son dossier et ses participations','Consulter son suivi, rédiger et soumettre les rapports, consulter les observations et déposer les pièces autorisées.'],
],[3.5,4.3,8.0])

tech=[[c.text for c in row.cells] for row in T[1].rows]
tech[0]=['Technologie','Version ou environnement','Utilisation']
tech[1]=['Java et Spring Boot','Java 17 ; Spring Boot 4.1.0','Services métier, API HTTP et assemblage des composants.']
tech[3]=['PostgreSQL','PostgreSQL 15 dans l’environnement d’intégration','Persistance relationnelle, contraintes, triggers et vues.']
tech[4][1]='React et React DOM 19.2.8'
tech[-1][1]='Outils de développement et de validation'
filltable(T[1],tech,[3.6,4.4,7.8])

filltable(T[4],[
['Parcours','Principales pages','Fonctions'],
['Admission','StagiairesPage, StagiaireDetailPage, AcademiquesPanel','Enregistrer la personne, renseigner son parcours académique et consulter le dossier.'],
['Suivi individuel','MonDossierPage, ParticipationDetailPage','Consulter l’affectation, les dates, les rapports et les informations accessibles.'],
['Rapports hebdomadaires','MesRapports, RapportWeeks, RapportActions','Saisir, soumettre, corriger et examiner les productions.'],
['Cycle de vie','ParticipationActions, ClotureConfirmation','Enregistrer les événements et confirmer la décision de clôture.'],
['Pilotage','DashboardPage, PilotagePanel, AlertesPage','Consulter les échéances, les alertes et les dossiers prioritaires.'],
],[3.4,5.1,7.3])

filltable(T[5],[
['Campagne','Résultat','Périmètre'],
['10 novembre 2026 — backend unitaire','36 tests réussis','Accès aux dossiers, clôture, périodes, fichiers, exports et alertes de clôture.'],
['10 novembre 2026 — frontend','126 tests réussis après relance','Fonctions, clients API et rendu de composants ; relance depuis le chemin canonique.'],
['10 novembre 2026 — ESLint','Contrôle réussi','Analyse statique du frontend.'],
['10 novembre 2026 — Vite','Construction réussie','Production des ressources distribuables de l’interface.'],
['11 novembre 2026 — backend et PostgreSQL','66 tests réussis, dont 13 PostgreSQL','Résultat du compte rendu d’intégration ; migrations V002 et V003 sur base isolée, précontrôle SQL réussi.'],
],[4.3,4.0,7.5])

filltable(T[6],[
['Domaine','Fonctions réalisées','Compléments'],
['Dossiers et affectations','Informations personnelles et académiques, participations, encadreurs et périodes.','Compléter le workflow d’archivage de la personne et la gestion départementale.'],
['Rapports','Grille des semaines, brouillon, soumission, correction, validation et observations.','Recette des parcours complets avec les comptes concernés.'],
['Documents','Stockage, classement, contrôles de format et de taille, téléchargement autorisé.','Ouvrir le dépôt des pièces autorisées au stagiaire.'],
['Clôture','Contrôles des pièces et rapports, dérogations et historique de la décision.','Préciser le motif obligatoire et le contrôle de l’évaluation lorsqu’elle est requise.'],
['Évaluation et soutenance','Grille d’évaluation finalisable et planification facultative de soutenance.','Compléter les informations visibles dans le parcours du stagiaire.'],
['Alertes et pilotage','Détection des échéances, regroupement des alertes et classement des dossiers.','Valider les indicateurs, les seuils et le déclenchement planifié en exploitation.'],
['Exports','Listes CSV et bilan PDF.','Compléter les documents prioritaires et la restitution de tous les caractères.'],
['Exploitation','Configurations, migrations et fichiers de conteneurisation.','Recette cible, sauvegarde, restauration et mise en service.'],
],[3.1,6.6,6.1])

filltable(T[7],[
['Référence','Objet','Règle'],
['RG01, RG18','Comptes','Rôles multiples, identifiants de connexion uniques et mots de passe hachés.'],
['RG21','Périmètre départemental','Le responsable gère les données des départements auxquels il est affecté, sauf administration globale.'],
['RG22','Documents','Chaque pièce possède un type et un fichier identifié ; son format et sa taille sont contrôlés.'],
['RG23','Affectation','Une participation ne doit pas créer de chevauchement incompatible pour le même stagiaire.'],
['RG24, RG25','Suivi hebdomadaire','Une période attendue est déterminée par semaine ; une participation ne dispose pas de deux rapports sur la même période.'],
['RG26','Validation','Un rapport validé est verrouillé ; un rapport retourné à corriger redevient modifiable.'],
['RG27 à RG29','Échéances','Signaler les rapports manquants, les fins proches et les participations dépassées non clôturées.'],
['RG30, RG31','Fin de participation','Contrôler les éléments obligatoires avant clôture et conserver les dates et motifs des prolongations.'],
['RG35 à RG38','Évaluation et soutenance','Rattacher l’évaluation à la participation ; gérer la soutenance et ses alertes uniquement lorsqu’elle existe.'],
['RG39, RG40','Priorisation','Associer des poids et actions aux alertes actives et calculer un score par participation.'],
],[2.8,3.5,9.5])

filltable(T[9],[
['Critères','Scénario','Résultat attendu'],
['CR01','Connexion et accès à un dossier d’autrui','Accès réservé au rôle et au périmètre autorisés ; refus des accès directs non admis.'],
['CR03 à CR05','Création du dossier et affectation','Données liées au bon stagiaire ; période cohérente, références admises et chevauchements contrôlés.'],
['CR06, CR14','Cycle complet d’un rapport','Brouillon, soumission, retour à corriger, nouvelle soumission et validation ; rapport validé verrouillé.'],
['CR13','Dépôt et téléchargement de pièces','Format et taille admis, rattachement correct et contrôle des droits sur le document.'],
['CR15, CR20','Échéances et priorisation','Alertes correspondant aux dates et statuts ; classement cohérent avec les poids et les actions recommandées.'],
['CR16','Clôture d’un dossier incomplet','Application des contrôles et de la politique de dérogation ; décision et éléments manquants historisés.'],
['CR17, CR19','Évaluation et soutenance facultative','Finalisation selon les critères obligatoires ; participation valide sans soutenance, alerte si rendez-vous planifié.'],
['CR08, CR18','Indicateurs sur un jeu de dossiers connu','Comptages et taux cohérents avec les rapports, documents et affectations du périmètre.'],
['CR09 à CR11','Recherche, historiques et documents de sortie','Filtres et pagination cohérents, traces consultables et exports conformes aux droits et aux données.'],
],[2.6,5.2,8.0])

def append_table(number,title,rows,widths):
    p=D.add_paragraph(style='Caption');caption(p,'Tableau',number,title)
    t=D.add_table(rows=0,cols=len(widths));filltable(t,rows,widths)
    D.add_paragraph()

append_table(11,'Répartition du planning prévisionnel',[
['Phase','Contenu','Jours'],
['Préparation','Contexte, besoins, règles, données et interfaces.','11'],
['Réalisation','Socle technique et développement des modules métier.','42'],
['Tests','Règles, intégration, sécurité et recette.','8'],
['Livraison','Déploiement, restauration, documentation et formation.','4'],
['Total','Ensemble des tâches prévues.','65'],
],[3.6,10.7,1.5])
rows=json.loads((ROOT/'tmp/complements/taches.json').read_text(encoding='utf-8'))['Estimation']['rows']
tasks=[]
for row in rows:
    vals={c['cell'][0]:c['value'] for c in row}
    try:days=int(float(vals.get('D','')))
    except (ValueError,TypeError):continue
    line=int(re.search(r'\d+',row[0]['cell']).group())
    phase='Préparation' if line<8 else 'Réalisation' if line<42 else 'Tests' if line<48 else 'Livraison'
    tasks.append([str(line),phase,vals['B'],str(days)])
assert len(tasks)==36 and sum(int(t[-1]) for t in tasks)==65
append_table(12,'Liste des tâches et estimations',[['Ligne','Phase','Tâche','Jours']]+tasks+[['','Total','', '65']],[1.3,2.9,10.1,1.5])

# Refaire les caches de navigation sans texte d'instruction ni pagination périmée.
headings=[(1 if p.style.name=='Heading 1' else 2,p.text) for p in D.paragraphs if p.style.name in ('Heading 1','Heading 2') and p.text not in ('Table des matières','Liste des tableaux','Liste des figures')]
caps={kind:[p.text for p in D.paragraphs if re.match(r'^'+kind+r' \d+ — ',p.text)] for kind in ('Figure','Tableau')}
for sdt in D.element.body.findall(qn('w:sdt')):
    instruction=''.join(x.text or '' for x in sdt.iter(qn('w:instrText')))
    if not instruction.strip().startswith('TOC'):continue
    content=sdt.find(qn('w:sdtContent'))
    for e in list(content):content.remove(e)
    def np(text='',style='Normal'):
        e=OxmlElement('w:p');content.append(e);p=Paragraph(e,D._body);p.style=style
        if text:p.add_run(text)
        return p
    kind=next((k for k in ('Tableau','Figure') if '\\c "'+k+'"' in instruction),None)
    entries=[(1,t) for t in caps[kind]] if kind else headings
    code='TOC \\c "'+kind+'" \\h \\z' if kind else 'TOC \\o "1-2" \\h \\z'
    p=np()
    for k in ('begin','instruction','separate'):
        r=p.add_run()
        if k=='instruction':e=OxmlElement('w:instrText');e.text=' '+code+' '
        else:e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),k)
        if k=='begin':e.set(qn('w:dirty'),'true')
        r._r.append(e)
    for level,text in entries:
        p=np(text,'TOC '+str(level));p.paragraph_format.space_after=Pt(4);p.paragraph_format.line_spacing=1.05
    p=np();e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),'end');p.add_run()._r.append(e)

update=D.settings.element.find(qn('w:updateFields'))
if update is None:update=OxmlElement('w:updateFields');D.settings.element.append(update)
update.set(qn('w:val'),'true')
D.core_properties.author='RAKOTONIAINA Andomalala Erica'
D.core_properties.title='Gestion et suivi des stagiaires au Ministère de l’Économie et des Finances'
D.core_properties.subject='Mémoire de Licence en Informatique — Option Web'
D.core_properties.comments=''
for p in D.paragraphs:
    if p.style.name.startswith('Heading'):
        p.paragraph_format.keep_with_next=True
        for r in p.runs:r.font.color.rgb=RGBColor(0,0,0)
D.save(OUT)

text='\n'.join(p.text for p in D.paragraphs)+'\n'+'\n'.join(c.text for t in D.tables for row in t.rows for c in row.cells)
for unwanted in ['À CONFIRMER','CAPTURE À FOURNIR','MATRICE_PREUVES','POINTS_A_CONFIRMER','pour cette rédaction','l’auteur pourra','Le mémoire doit','Champ automatique','[P01','[R1']:
    assert unwanted.casefold() not in text.casefold(),unwanted
check=Document(OUT)
assert len(check.sections)==3 and len(check.inline_shapes)==8 and len(check.tables)==12
assert len(caps['Figure'])==6 and len(caps['Tableau'])==12
names={e.get(qn('w:name')) for e in check.element.iter(qn('w:bookmarkStart'))}
refs=[e.text.strip().split()[1] for e in check.element.iter(qn('w:instrText')) if (e.text or '').strip().startswith('REF ')]
assert all(n in names for n in refs)
with ZipFile(SRC) as a,ZipFile(OUT) as z:
    media=lambda q: sorted(hashlib.sha256(q.read(n)).hexdigest() for n in q.namelist() if n.startswith('word/media/'))
    assert z.testzip() is None and media(a)==media(z)
    from lxml import etree
    ns={'w':'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
    for part in z.namelist():
        if part.startswith('word/') and part.endswith('.xml'):
            root=etree.fromstring(z.read(part))
            story=' '.join(root.xpath('//w:t/text()',namespaces=ns))
            for marker in ('À CONFIRMER','CAPTURE À FOURNIR','MATRICE_PREUVES','POINTS_A_CONFIRMER','Champ automatique'):
                assert marker not in story,(part,marker)
assert hashlib.sha256(SRC.read_bytes()).hexdigest()==source_hash
summary={'output':str(OUT),'words':len(text.split()),'paragraphs':len(check.paragraphs),'tables':12,'figures':6,'images':8,'sections':3,'tasks':36,'days':65,'source_preserved':True,'visual_review':False}
(ROOT/'tmp/revision_concept1_controle.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
(ROOT/'tmp/revision_concept1_texte.txt').write_text(text,encoding='utf-8')
print(json.dumps(summary,ensure_ascii=False))
