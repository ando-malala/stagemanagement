# Refonte visuelle MEF–SIIGFP — 4 novembre 2026

## Audit initial (avant modification)

Audit du code React, des cinq feuilles de style actives, des routes et des composants partagés. Aucune charte officielle trouvée dans le dépôt. La marque existante est le monogramme textuel MEF et la mention SIIGFP · Madagascar ; elle est conservée. `public/logo.png` n'est pas une image exploitable par l'outil d'inspection et le favicon est encore celui de Vite. Aucune armoirie officielle n'est inventée.

| Ensemble examiné | Constat initial | Intervention prévue |
| --- | --- | --- |
| Layout, sidebar, header | Navigation déjà regroupée et filtrée par rôle ; accents très discrets, compte peu identifiable | Bleu institutionnel, état actif renforcé, avatar textuel, hiérarchie et espaces harmonisés |
| Connexion, erreurs | Petit formulaire isolé ; erreurs génériques sans repère visuel | Composition institutionnelle responsive, repères d'état, formulaire et authentification conservés |
| Dashboard, pilotage | Six KPI étroits, sans icônes ; actions et sections de même importance | Bandeau de synthèse, icônes existantes, accents par indicateur, grilles adaptées à la largeur |
| Stagiaires, stages, participations, encadreurs | Tables partagées solides ; statuts en texte brut, fiches uniformes | Badges à libellé inchangé, en-tête de dossier, sections et détails mieux délimités |
| Rapports, évaluations, soutenances | Composants de formulaire, grille hebdomadaire et confirmations existants | Champs, erreurs, états de rapport et actions harmonisés sans changer les transitions |
| Documents, alertes | Sélecteurs et filtres existants ; priorités visuellement peu distinctes | Zones de sélection cohérentes et badges de priorité ; téléchargements inchangés |
| Portail stagiaire | Réutilise les fiches et rapports ; attente de rattachement peu visible | Identité commune, introduction personnelle, état sans dossier explicite |
| Référentiels, comptes, audit | Tables, boutons segmentés et formulaires réutilisés | Même système de composants, onglets sélectionnés plus explicites |
| Responsive | Quatre colonnes de formulaire d'au moins 150 px peuvent déborder ; tables défilantes non focalisables | Grilles fluides, actions repliables, région de tableau accessible au clavier |
| Accessibilité | Labels, dialogues natifs et réduction du mouvement déjà présents ; couleurs et focus dispersés | Tokens sémantiques, contraste calculé, focus commun, conservation des labels et dialogues |
| Impression | Synthèse consolidée exhaustive et règles dédiées | Présentation imprimée sobre ; conservation de toutes les lignes et des sauts de page |

Avant refonte : 122 tests réussis, ESLint réussi, build Vite réussi.

## Limite de l'audit visuel

L'inventaire des applications a fonctionné, mais l'accès au navigateur Microsoft Edge a été refusé par l'outil (« Computer Use was not approved to use Microsoft Edge »). Aucun contournement effectué. Pas de capture avant/après ni de validation visuelle dans un navigateur. Les vérifications SSR ne démontrent ni l'absence de débordement rendu ni la conformité WCAG complète.

## Périmètre de non-régression

Modifications exclusivement dans la présentation du frontend et sa documentation. Services API, contexte d'authentification, permissions, DTO, validations métier, routes et workflows conservés. Les composants existants sont réutilisés ; aucune bibliothèque ajoutée. Les exports CSV/PDF générés par le serveur ne sont pas modifiés.

## Réalisation

1. Tokens et composants : palette centralisée, typographie système, champs/focus/erreurs, boutons, badges, cartes et états vides. Vérification du groupe : 122 tests, ESLint et build réussis.
2. Layout et pages d'entrée : navigation bleue avec sélection turquoise, compte avec initiales, lien Alertes sans compteur inventé, connexion institutionnelle à deux panneaux, dashboard à KPI illustrés et raccourcis. Vérification du groupe : 122 tests, ESLint et build réussis.
3. Modules métier : badges communs dans les tableaux et détails, tableaux focalisables et consigne de défilement, formulaires fluides, en-têtes de dossier, portail stagiaire, pages 403/404 et synthèse imprimable. Vérification du groupe : 122 tests, ESLint et build réussis.
4. Validation complémentaire : calcul de 51 combinaisons de contraste, tests de conservation des libellés/statuts inconnus/rendus de cellule personnalisés et des champs de connexion. Voir le [design system et les contrôles manuels restants](DESIGN_SYSTEM.md).

## Résultat des contrôles finaux

- 124 tests frontend réussis (122 existants et 2 complémentaires).
- ESLint et build Vite réussis après chaque groupe.
- 51 combinaisons de contraste vérifiées : minimum texte 4,54:1, champs/focus 3,14:1.
- Aucun fichier modifié dans le backend, le schéma SQL, les services API, les hooks, l'authentification, les routes ou les utilitaires de permissions/validation métier.
- Pas de nouvelle dépendance. Les tests de contrats HTTP et de permissions existants restent réussis ; aucune recette sur un backend réel n'a été exécutée pour cette refonte.
- Recette visuelle, clavier, lecteur d'écran, zoom, impression et largeurs 375/768/1366/1920 px à effectuer : accès navigateur refusé, aucune page déclarée visuellement validée.
