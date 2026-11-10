# Mémoire de Licence MEF SIIGFP

Le livrable Word modifiable est `Memoire_Licence_MEF_SIIGFP.docx`.

Les sources de rédaction se trouvent dans `chapitres/`. `MEMOIRE_COMPLET.md` les rassemble. Les six illustrations originales et leur générateur se trouvent dans `illustrations/`. `generer_docx.py` reconstruit le document avec python-docx, à partir des chapitres et des logos fournis dans Downloads.

Les preuves sont consignées dans `AUDIT_PROJET.md` et `MATRICE_PREUVES.md`. Le plan est conservé dans `PLAN_MEMOIRE.md`, et les informations restant à valider dans `POINTS_A_CONFIRMER.md`.

## État du contenu

- 3 chapitres, introduction, conclusion, bibliographie et annexes.
- 6 figures originales et 10 tableaux ; numérotation par champs SEQ et renvois REF.
- Table des matières et listes de figures et tableaux sous forme de champs Word automatiques.
- Pagination romaine des pages préliminaires et arabe du corps du mémoire.
- Informations administratives manquantes marquées `[À CONFIRMER]` ; captures réelles à fournir.
- Données personnelles de stagiaires et secrets exclus du texte.

## Vérifications techniques

Campagne du 10 novembre 2026 : 36 tests unitaires backend, 126 tests frontend, ESLint et construction Vite réussis. Les journaux de travail sont dans `tmp/`. La campagne d’intégration complète n’a pas été exécutée : le répertoire `backend/migrations` attendu par la procédure est absent de cette copie. Les essais historiques cités dans les documents du dépôt ne sont pas assimilés à des exécutions actuelles.

## Mise à jour dans Word

Ouvrir le document, sélectionner le contenu et actualiser les champs (Ctrl+A puis F9 sous Word pour Windows). Pour la table des matières, choisir la mise à jour de toute la table. Contrôler ensuite les sauts de page, les légendes, la pagination et les tableaux avant impression. Le contenu doit être personnalisé et validé avant un dépôt universitaire.

## Périmètre du travail

Aucun fichier du code applicatif n’a été modifié. Aucun script d’initialisation de base métier n’a été exécuté. Le mémoire n’atteste pas d’un déploiement en production.
