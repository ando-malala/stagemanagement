# Design system — MEF–SIIGFP

## Fondations

Source des tokens : `frontend/src/styles/global.css`. Les composants utilisent ces variables ; aucune police distante ni bibliothèque UI supplémentaire n'est nécessaire. La famille Segoe UI / system-ui privilégie la lisibilité et le fonctionnement hors connexion.

| Usage | Token | Valeur |
| --- | --- | --- |
| Identité, actions principales, liens | `--primary` | `#174A7E` |
| Survol principal, fond du bandeau | `--primary-dark` | `#10365D` |
| Interaction et focus | `--interaction`, `--focus` | `#2563C4` |
| Accompagnement, repères de section | `--accent` | `#117B75` |
| Participations / KPI secondaire | `--violet` | `#6554A4` |
| Succès | `--success` | `#116D55` |
| Attente et avertissement | `--warning` | `#8A570B` |
| Correction | `--correction` | `#A14613` |
| Erreur et urgence | `--danger` | `#B53539` |
| Fond de l'espace de travail | `--bg` | `#F3F6FA` |
| Surface principale | `--surface` | `#FFFFFF` |
| Texte / texte secondaire | `--text` / `--muted` | `#20334B` / `#52657C` |

Les couleurs de texte sémantique sont plus sombres que les pistes initiales pour conserver le contraste sur des badges clairs et des boutons pleins. Chaque famille possède un fond doux. Les états restent explicitement nommés ; la couleur ne porte jamais seule l'information. Le monogramme MEF est conservé comme repère applicatif, sans prétendre remplacer un emblème officiel.

Échelle typographique : 12, 13, 14, 15, 18 et 22 px ; titres principaux fluides de 22 à 30 px ; valeurs KPI de 30 à 36 px avec chiffres tabulaires. Espacements de base : 4, 8, 12, 16, 20, 24, 32, 40 et 48 px. Rayons de 7, 10, 14 et 22 px. Ombres distinctes pour les cartes, survols, menus et dialogues. Transition commune de 160 ms, désactivée par `prefers-reduced-motion` avec les animations de chargement et de dialogue.

## Composants à réutiliser

- `Button` : primaire pour l'action principale, secondaire pour la consultation et l'actualisation, danger pour une action sensible existante. Hauteur de 44 px ; actions de tableau compactes à 36 px. Ne pas modifier les conditions de permission pour changer leur présentation.
- `Input` : labels associés, contrôle natif, champ obligatoire signalé par une étoile, description et erreur reliées par `aria-describedby`. Les attributs natifs et validations existantes sont conservés.
- `Card` : surface commune avec bordure, rayon et ombre. Les fiches imbriquées ont une ombre réduite.
- `StatusBadge` : présentation commune des codes réels de PostgreSQL, des priorités d'alerte et des états hebdomadaires. Le texte fourni est affiché sans traduction ni transformation. Une valeur inconnue reste visible en neutre, une valeur absente reste « — ». Aucun statut n'est interprété comme une autorisation.
- `Table` : recherche, filtres, tri, pagination et rendu personnalisé conservés. Les colonnes de statut sont décorées automatiquement, sauf lorsqu'un rendu personnalisé existe. Les données brutes restent utilisées pour le tri et la recherche. Région défilante nommée et focalisable au clavier ; aucune colonne ni action masquée sur mobile.
- `EmptyState`, `Loader`, `ErrorMessage` : iconographie existante, textes et rôles accessibles ; aucune donnée de remplacement en cas d'erreur.
- `Modal`, `ConfirmDialog` : dialogue natif et restauration du focus conservés. Animation brève uniquement lors de l'ouverture.
- `ResourceDetail` : icône de module, référence du dossier, détails en deux colonnes puis une, dossiers liés et actions conservés.

Les tableaux, formulaires et fiches partagés couvrent stagiaires, encadreurs, stages, participations, rapports, évaluations, soutenances, documents, alertes, référentiels, comptes et journal d'audit. Le portail stagiaire réutilise ces mêmes éléments.

## Responsive

Les seuils sont documentés ici et dans `global.css`. Les media queries emploient des nombres littéraux car les variables CSS ne peuvent pas être utilisées dans leurs conditions.

| Largeur | Présentation prévue | Validation navigateur |
| --- | --- | --- |
| 375 px | Menu mobile, compte sous le titre, KPI sur deux colonnes, formulaires sur une colonne, connexion empilée et tableaux défilants | À effectuer |
| 768 px | Menu mobile, KPI sur trois colonnes, formulaires fluides et connexion en deux panneaux | À effectuer |
| 1366 px | Sidebar de 256 px, contenu flexible, KPI sur trois colonnes, formulaires auto-fit | À effectuer |
| 1920 px | Contenu plafonné à 1480 px, KPI sur six colonnes | À effectuer |

Seuils : 480 px (petit mobile), 720 px (composition empilée), 980 px (navigation mobile), 1200 px (raccourcis compacts), 1600 px (six KPI). Les formulaires utilisent `auto-fit` et des pistes dont la taille minimale ne dépasse pas le conteneur.

## Contrastes et recette

`npm.cmd run check:contrast` lit les vraies valeurs de `global.css` et calcule la luminance relative sRGB. Il vérifie 51 couples : texte courant et sémantique à 4,5:1 minimum, bordures de champ et focus à 3:1 minimum. Les fonds dégradés principaux sont contrôlés à leurs deux extrémités. Résultats : minimum texte **4,54:1**, minimum champs/focus **3,14:1**.

Ce contrôle de palette ne remplace pas la mesure des styles calculés, l'examen visuel ou un audit WCAG complet. L'accès au navigateur ayant été refusé, les contrôles suivants restent à réaliser sur les données de recette :

1. Examiner et capturer connexion, dashboard, listes, fiche participation, formulaire, alertes et portail aux quatre largeurs ci-dessus.
2. Vérifier le zoom 200 % et 400 %, les libellés longs, les menus, l'absence de débordement de page et le défilement des tableaux.
3. Parcourir au clavier le lien d'évitement, le menu mobile, le compte, les filtres, la pagination et les dialogues ; vérifier le focus et son retour à la fermeture.
4. Contrôler avec un lecteur d'écran les états de chargement, les erreurs, les labels et descriptions des champs.
5. Vérifier les préférences de mouvement réduit, l'impression longue du dossier consolidé et les sauts de page.
6. Rejouer les parcours ADMIN, ENCADREUR et STAGIAIRE sur le backend réel, notamment enregistrement, soumission et validation, sans prendre les tests SSR pour une recette d'intégration.

Les exports serveur CSV/PDF restent inchangés. Seule la présentation HTML imprimable est harmonisée ; aucune ligne du dossier consolidé n'est supprimée.
