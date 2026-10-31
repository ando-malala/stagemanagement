# Corrections apres audit contractuel

Reference : Estimation_taches_ETU003156.xlsm, onglet Estimation (36 taches).

## Lot 1 : perimetres

Politique : ADMIN global ; RESPONSABLE_STAGE selon responsable_departements et le departement de la structure du stage ; ENCADREUR selon l'affectation et le compte associe ; STAGIAIRE selon le compte associe au dossier. Les roles cumules donnent l'union des droits. Aucun compte n'est associe a partir d'un nom ou courriel.

Un departement facultatif est ajoute au dossier stagiaire pour les admissions avant affectation. Les dossiers historiques sans rattachement restent lisibles selon leurs participations autorisees, mais seul ADMIN peut les modifier avant qualification du rattachement. Aucune attribution automatique sur les donnees existantes.

Migration V001 : `backend/migrations/V001__stagiaire_department.sql`, ajout nullable et index, sans suppression ni modification de donnees. A appliquer par un administrateur avant le demarrage de cette version. Non appliquee a la base metier pendant le developpement.

Listes, details, creation/modification, fichiers, suivis, evaluations, soutenances, alertes et compteurs utilisent DossierAccessService. Les traitements globaux de detection sont reserves a ADMIN. Les associations de comptes encadreurs ne sont modifiables que par ADMIN.

Validation executee : compilation Java ; 12 tests unitaires ; 16 tests backend au total avec HTTP/PostgreSQL, dont un scenario croise entre deux departements et deux encadreurs. Base isolee conservee : `mef_integration_c57c93a9d3404980b2182be2851ec36b`. Aucun DROP DATABASE execute. Les listes restent filtrees dans les services apres chargement ; pagination SQL a ameliorer pour les grands volumes.

## Lot 2 : valide sur les scenarios executes

Association explicite compte-stagiaire et portail personnel. La contrainte UNIQUE existante sur stagiaires.utilisateur_id est conservee.

Validation lot 2 : 16 tests backend avec scenario HTTP pour deux stagiaires (association unique, refus croises, fichiers, observations masquees, soumission/correction/resoumission/validation). 103 tests frontend, ESLint et build Vite passes. Base isolee conservee : `mef_integration_38a4a97403cc442f8908ce1111d3cfb4`. Aucun navigateur utilise.

## Lot 3 : validation en cours

Refus des transitions evenementielles sur le PATCH generique, fermeture des editions de dossiers termines, calendrier obligatoire pour les rapports et controle des chevauchements au niveau de la personne (y compris profils academiques differents). Annulations et abandons sont exclus du controle de chevauchement. Les participations cloturees restent dans ce controle. La cloture exige les rapports VALIDE des periodes deja achevees ; les semaines futures ne sont pas exigees et la soutenance reste facultative. Aucune interdiction nouvelle de cloture anticipee n'est ajoutee.

Migration V002 : gardes des futures ecritures PostgreSQL, sans nettoyage automatique des anomalies historiques. Les jeux historiques doivent etre controles avant recette. 20 tests backend et 103 tests frontend, ESLint et build ont passe avant le dernier renforcement des verrous, qui sera revalide.

## Lots 4 a 6

Non termines. Le verdict de recette reste suspendu a leur implementation et leurs tests.
