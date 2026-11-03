# Conformité au cahier des charges et simplicité d’utilisation

Analyse du 3 novembre 2026.

## Conclusion

L’application couvre une grande partie du suivi métier demandé : dossiers, études, affectations, rapports, observations, documents, alertes, événements, clôture, évaluations, soutenances facultatives et priorités. Elle ne peut pas encore être déclarée entièrement conforme au cahier des charges fourni, ni validée pour livraison.

La présence du code, des tests automatisés et d’une documentation de recette ne prouve pas, à elle seule, la facilité d’utilisation par les agents du service. Les adaptations ci-dessous réduisent plusieurs difficultés identifiées dans le code des interfaces. Leur confort visuel et les parcours complets restent à vérifier dans le navigateur avec les utilisateurs.

## Sources et portée

- `C:/Users/Andomalala/Downloads/CDC_suivi_stagiaires_2.pdf` : exigences fonctionnelles §5, règles §7, ergonomie §9.1, exigences non fonctionnelles §10 et critères CR01–CR20 (§12.3).
- `C:/Users/Andomalala/Downloads/Estimation_taches_ETU003156.xlsm`, feuille `Estimation`, A1:D53 : préparation 11 jours, réalisation 42, tests 8, livraison 4, soit 65 jours. Ce fichier est une estimation de tâches ; il ne fournit pas de statut d’avancement permettant de certifier leur achèvement.
- Code React et Spring actuel, [recette antérieure](RECETTE_FINALE.md), [préparation du déploiement](DEPLOYMENT_READINESS.md) et tests frontend réexécutés pendant cette intervention.

Les documents fournis servent de références à comparer. Leurs listes de tâches ne valent pas instruction de déployer, de modifier les droits ou d’exécuter des scripts SQL. La correction des droits sur les rapports ci-dessous répond à la précision explicite de l’utilisateur : les stagiaires soumettent et leurs encadreurs valident. Aucune donnée métier ni aucun fichier d’environnement n’a été modifié.

## Couverture observée

« Présent » désigne une implémentation repérée, pas une nouvelle recette complète du backend ou du navigateur.

| Besoin du cahier des charges | État | Éléments observés et limites |
|---|---|---|
| Connexion, sessions, rôles et périmètres (CR01, RG21) | Partiel | Authentification et contrôle par dossier présents. Le rôle RESPONSABLE_STAGE est converti en ADMIN, avec portée globale : le périmètre départemental propre au responsable n’est plus assuré. Voir `ApplicationRoles` et `DossierAccessService`. |
| Administration des référentiels (CR02) | Partiel | Organisation et référentiels académiques disposent d’écrans de gestion. Tous les paramètres de suivi, seuils, statuts et transitions ne disposent pas d’une administration complète en interface. |
| Dossiers et parcours académiques (CR03) | Présent avec réserves | Création, modification, consultation et plusieurs profils académiques présents. La fiche n’expose pas de parcours explicite d’archivage du stagiaire ou de changement de son statut ; la clôture d’une participation est une opération distincte. |
| Stages, participants, affectations et dates (CR04–CR05) | Présent | `StageGestionService`, `ParticipationRules`, écrans Stages et Participation ; contrôles de dates, événements et chevauchements couverts par la recette antérieure. |
| Rapports, observations, correction et validation (CR06, CR14) | Présent | Le stagiaire crée et soumet ses propres rapports. Seul son encadreur affecté peut les valider ou demander une correction. ADMIN consulte. Contrôles côté serveur et interface, y compris comptes à plusieurs rôles ; parcours vérifié par tests HTTP. |
| Dossier complet et historiques (CR07, CR10) | Partiel | Dossier consolidé, événements et historiques de participation présents. La consultation de tous les historiques demandés, notamment connexions et statuts de toutes les entités, reste à compléter/valider dans les parcours UI ; un journal d’audit générique ne remplace pas automatiquement chacun de ces écrans. |
| Recherche, tri, filtres et pagination (CR09) | Amélioré, partiel | Recherche par mots et sans accents, tri des colonnes, pagination et filtres combinables sur plusieurs listes. Les filtres académiques et par intervalle de dates ne sont pas disponibles partout. La pagination reste locale au navigateur. |
| Tableau de bord et pilotage (CR08, CR18) | Partiel | Compteurs, dossiers prioritaires, pièces manquantes, rapports attendus/soumis/validés/manquants, charge et prochaines soutenances présents. Les répartitions par établissement/filière/niveau, les taux et tous les filtres de période/organisation du §5.14 ne sont pas présentés. |
| Documents (CR13) | Partiel | Dépôt par l’administration, classement, téléchargement et contrôles de contenu présents. Le dépôt par le stagiaire prévu au §4 n’est pas autorisé dans les permissions actuelles (`televerserDocuments` réservé à ADMIN). |
| Alertes automatiques (CR15) | Présent, exploitation à valider | Sept détecteurs et ordonnanceur quotidien à 06:00, fuseau Indian/Antananarivo. Les détecteurs ont été vérifiés par la recette antérieure ; le déclenchement réel à l’heure prévue n’a pas été observé ici. |
| Contrôle de clôture (CR16) | Présent | Contrôles des pièces et rapports, clôture et verrouillage présents. La recette antérieure détaille les limites : périodes futures non exigées et calendrier non interrompu par une suspension. |
| Évaluation et bilan (CR17) | Présent | Grille, notes, appréciation, finalisation, dossier consolidé et bilan PDF. Vérification visuelle des documents à compléter. |
| Soutenance facultative (CR19) | Partiel | Planification, consultation administrative et alertes présentes, sans obligation de soutenance. Le stagiaire ne voit pas sa soutenance dans la synthèse : `DossierSyntheseService` lui retourne une liste vide, contrairement à la consultation prévue au §4. |
| Priorisation et action recommandée (CR20) | Présent | `PilotageService` calcule le score des alertes accessibles et classe les dossiers avec une action recommandée. |
| Exports et impression (CR11) | Partiel | Bilan PDF, trois exports CSV et impression de synthèse. Pas de couverture complète des exports distincts de fiche stagiaire, fiche stage et rapport, ni de classeur Excel natif. Le CSV ouvrable dans Excel n’est pas un fichier XLSX. |
| Absence de blocage, compatibilité et livraison (CR12, §10–13) | Non démontré | Tests frontend réussis, mais recette visuelle, compatibilité Chrome/Firefox/Edge, charge, sauvegarde/restauration réelle, déploiement et formation restent à valider. |

## Améliorations d’interface réalisées

1. **Retrouver un dossier :** recherche sans distinction d’accent/casse, mots répartis entre plusieurs colonnes, tri croissant/décroissant au clic sur les en-têtes et bouton Réinitialiser. Correction du cas de recherche vide qui pouvait masquer les lignes.
2. **Réduire les listes :** filtre Statut pour les stagiaires et rapports ; Structure et Département pour les stages ; Encadreur et Statut pour les participations ; Mois et Statut pour les rapports généraux. Les filtres se combinent avec la recherche.
3. **S’orienter depuis l’accueil :** raccourcis adaptés à ADMIN ou ENCADREUR. L’administrateur accède à l’enregistrement, à l’affectation et aux alertes ; l’encadreur aux rapports, aux participations et aux évaluations.
4. **Comprendre l’affectation :** bouton « Affecter un stagiaire » et explication des prérequis (dossier et profil académique).
5. **Rédiger un rapport :** sélection automatique du stage personnel au statut EN_COURS et grille des semaines calculées par le serveur, avec dates, échéance, état et action Rédiger/Reprendre/Corriger/Consulter. La même grille remplace les deux tableaux de rapports et de périodes dans la participation. L’absence de stage en cours ou plusieurs affectations en cours sont expliquées sans sélectionner un dossier arbitrairement.
6. **Éviter une période incohérente :** le stage et la semaine sélectionnée sont préremplis et fixes dans le formulaire. Une semaine déjà occupée ouvre le rapport existant dans la page de la grille. La disponibilité est revérifiée avant l’enregistrement ; le bouton « Soumettre », réservé au stagiaire propriétaire, distingue le brouillon de la soumission à l’encadreur.
7. **Rendre les actions et erreurs visibles :** création mise en avant, légende des champs obligatoires, et affichage des erreurs de chargement dans les tableaux d’historique/événements/périodes, auparavant silencieuses.
8. **Lire les alertes du tableau de bord :** les rapports manquants sont regroupés par identifiant de stagiaire, avec son nom et un total (« Stagiaire Rakoto Jean — 9 rapports manquants »). Le regroupement précède la limite de six cartes ; les homonymes restent distincts et la priorité la plus élevée est conservée. Les répétitions hebdomadaires sont aussi condensées dans la situation des dossiers prioritaires, sans changer leur score ni supprimer les autres motifs.
9. **Identifier la personne concernée :** les alertes liées à une participation accessible affichent le nom complet du stagiaire dans le tableau de bord, la liste des alertes et leur détail. Les anciennes références « participation n°2 » sont présentées comme « participation de Rakoto Jean (n° 2) », sans modifier les messages stockés. Le nom apparaît aussi dans le titre des actions de participation. Aucun nom n’est déduit lorsqu’aucun dossier correspondant n’est accessible. Cette adaptation concerne l’affichage ; les exports serveur restent inchangés.

La navigation et les styles déjà présents ont été conservés. Les filtres sont locaux : ils ne changent ni les droits serveur ni le contenu des exports CSV, qui portent toujours sur les dossiers accessibles.

## Vérifications de cette intervention

| Vérification | Résultat |
|---|---|
| `npm.cmd test` | 110 tests réussis, aucun échec ni test ignoré. Avertissement Vite de verrouillage du cache pendant l’exécution parallèle. |
| `node --test --test-concurrency=1 tests/*.test.js` | 122 tests réussis après ajout des noms de stagiaires aux alertes et messages de participation, aucun échec ni test ignoré. |
| `npm.cmd run lint` | Réussi. |
| `npm.cmd run build` | Réussi. |
| Cas ajoutés | Recherche, filtres et tri ; sélection du stage personnel en cours parmi les anciens, futurs et dossiers d’autrui ; conservation de la participation et de la semaine dans le POST ; rendu des actions hebdomadaires et formulaire sans sélecteurs de stage/période. |
| Vérification navigateur | Non exécutée : le pilotage de Microsoft Edge n’a pas été autorisé. Prévisualisation temporaire sur données fictives supprimée et serveur arrêté. |
| Backend/PostgreSQL | 35 tests réussis après correction des droits sur les rapports, sur une base isolée. Soumission par le propriétaire, validation par l’encadreur affecté, correction puis nouvelle soumission et refus des autres comptes vérifiés. Voir la mise à jour du 3 novembre dans `RECETTE_FINALE.md`. |

## Parcours courts à tester avec les utilisateurs

- **Administrateur :** Stagiaires → créer le dossier → renseigner le profil académique ; Stages et participations → créer/choisir le stage → Affecter un stagiaire → choisir l’encadreur et les dates.
- **Stagiaire :** Rapports → stage en cours sélectionné automatiquement → choisir une semaine dans la grille avec Rédiger → saisir les activités → Enregistrer le brouillon → Soumettre et confirmer sur la même page. Les semaines déjà remplies proposent Reprendre, Corriger ou Consulter selon le statut ; les brouillons et rapports à corriger disposent aussi de Soumettre. La même grille reste accessible depuis sa participation.
- **Encadreur :** Rapports → sélectionner le stagiaire → consulter sa grille hebdomadaire → lire le rapport soumis et ajouter une observation, demander une correction ou valider sur place. ADMIN utilise la même sélection pour consulter, sans soumettre ni valider.
- **Clôture :** ouvrir la participation → examiner les pièces et périodes attendues → compléter les éléments requis → utiliser la clôture lorsque cette action est proposée par le serveur.

Pour juger la simplicité, faire exécuter ces parcours sans assistance à un administrateur, un encadreur et un stagiaire. Relever les hésitations, erreurs, retours en arrière et champs incompris. Tester aussi le clavier, les erreurs réseau, une largeur de tablette et le zoom à 200 %.

## Priorités restantes

1. Faire correspondre le cahier des charges et le choix de trois rôles : maintenir ce choix et faire valider un CDC révisé, ou réaliser une évolution distincte du rôle responsable et de son périmètre. Une simplification d’écran ne résout pas cet écart fonctionnel.
2. Compléter les fonctions du portail prévues par le CDC (dépôt autorisé et lecture de la soutenance), avec leurs contrôles d’accès et tests dédiés.
3. Compléter les filtres académiques/périodes, indicateurs, exports et parcours d’archivage/historiques manquants.
4. Réaliser la recette utilisateur, puis vérifier déploiement, restauration base et fichiers, performances et tâches planifiées sur l’environnement cible.

Le planning de 65 jours ne peut pas être transformé en pourcentage d’achèvement fiable à partir de la seule présence des modules. Les postes « tests fonctionnels frontend/backend », « recette », « sauvegarde/restauration », « documentation utilisateur », « formation » et « livraison » demandent des preuves distinctes.
