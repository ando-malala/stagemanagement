# Recette finale MEF–SIIGFP — 2 novembre 2026

## Mise à jour du 2 novembre — schéma stagiaire conservé et trois rôles

Cette mise à jour remplace les conclusions précédentes sur V001 et le rôle RESPONSABLE_STAGE. Les tableaux de la campagne initiale ci-dessous conservent leurs observations historiques ; ils ne prouvent pas les nouveaux droits à eux seuls.

- Le mapping `Stagiaire.departement`, les champs `departementId` des DTO stagiaire et le champ de formulaire ont été retirés. Aucun changement de `application.properties` ni de `ddl-auto=validate`.
- `ADMIN` regroupe la gestion des stages et l'administration globale. Un dossier sans participation est administré par ADMIN. `ENCADREUR` reste limité aux participations qui lui sont affectées ; `STAGIAIRE` à son compte associé. Le département d'une participation reste celui de la structure d'accueil du stage.
- Les attributions actives historiques `RESPONSABLE_STAGE` deviennent `ADMIN` dans les réponses de connexion, `/auth/me` et les autorités Spring, sans modification SQL. Le catalogue et les créations/ajouts de rôles sont limités à ADMIN, ENCADREUR, STAGIAIRE. Un retrait d'ADMIN enlève aussi l'alias historique pour empêcher la persistance de droits administratifs.
- Le panneau de rattachement départemental des comptes n'est plus présenté dans l'interface ; les anciennes données et API ADMIN correspondantes restent conservées pour compatibilité, sans effet sur le périmètre global d'ADMIN.

| Préconditions / rôle | Actions exécutées | Attendu | Observé / verdict |
|---|---|---|---|
| Base vierge isolée `mef_integration_c6227645b345445b9bb04ecb332acf25`, schéma initial + V002, sans V001 | Lancer Spring avec Hibernate validate ; vérifier `information_schema.columns` ; créer/modifier/lire un stagiaire sans département ni participation | Démarrage, colonne absente, HTTP 201/200 | Réussi, `CompleteIntegrationTests.originalTraineeSchemaAndThreeRolesWithLegacyAdministration` |
| Ancien compte RESPONSABLE_STAGE actif | Connexion, profil, catalogue, consultation et modification des dossiers des deux départements | ADMIN, catalogue à trois rôles, droits globaux | Réussi, test précédent et `mergedAdministrationAndAssignmentIsolationOverHttp` |
| ADMIN | Créer/ajouter un rôle RESPONSABLE_STAGE ou inconnu | HTTP 400 | Réussi |
| Compte avec ADMIN et son alias historique | Retirer ADMIN seul, puis ajouter ENCADREUR et retirer ADMIN | Refus du dernier rôle effectif ; retrait des deux alias ; ancienne session privée d'administration | Réussi : 400 puis 200 ; `/api/utilisateurs` renvoie 403 |
| Deux encadreurs et deux stagiaires distincts | Listes, détails, fichiers, rapports, exports, écritures sur les dossiers d'autrui | Refus 403 et filtrage | Réussi, tests HTTP et `DossierAccessTests` |
| Suites complètes | `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-integration.ps1 -KeepUploads` ; `npm.cmd test` | Aucune régression | 33 tests backend réussis (25 unitaires, 8 Spring/HTTP/PostgreSQL), 106 tests frontend réussis ; aucun ignoré |
| Contrôles de construction | `npm.cmd run lint`, `npm.cmd run build`, `mvn.cmd -o -DskipTests package` | Constructions réussies | Réussi ; le packaging ne réexécute pas les tests. Les 6 tests apiReadiness ont également été relancés après adaptation de son script aux trois rôles |

Le premier essai backend a révélé un scénario historique qui tentait encore de créer RESPONSABLE_STAGE ; son attente a été adaptée et toute la suite relancée avec succès. Le cache Maven exigeait un accès hors bac à sable. Le packaging a d'abord rencontré un verrou Windows : les deux anciens processus de recette ont été arrêtés après vérification du manifeste, des PID et des heures de démarrage, puis le JAR a été reconstruit avec succès. Ces anciens serveurs ne sont donc plus en cours d'exécution. Les bases et pièces de test ont été conservées.

Aucune donnée ni structure de la base métier n'a été modifiée. V001 n'est plus requise et ne doit pas être appliquée. V002, utilisée seulement dans les bases isolées, reste une décision séparée de renforcement SQL sur la cible. Aucun test navigateur n'a été exécuté pour cette mise à jour ; les essais visuels restent à faire avec les trois rôles retenus.

## Avis de recette

**Les parcours automatisés passent. La livraison finale et la mise en production ne sont pas validées.** Les interactions dans le navigateur restent à tester. Le blocage de colonne transmis par l'utilisateur est corrigé dans le code et vérifié sur PostgreSQL isolé ; le redémarrage sur sa propre base reste à effectuer par l'utilisateur.

Références examinées : [état des corrections](REMEDIATION_STATUS.md), [préparation du déploiement](DEPLOYMENT_READINESS.md), [migrations](../backend/migrations/README.md). Aucun code applicatif n'a été modifié pendant cette recette. Seuls des outils de recette et ce rapport ont été ajoutés ; le lanceur de tests accepte maintenant de conserver les documents fictifs pour les essais manuels.

## Environnement et isolation

| Élément | Vérification observée |
|---|---|
| Windows / Java / Maven | Windows 11, Java 17.0.12, Maven 3.9.11 |
| Frontend | Node 24.11.1, npm 11.6.2, Vite 8.2.2 ; dépendances déjà installées |
| PostgreSQL | Client 15.15 ; connexion réelle et contraintes exercées par les tests Spring |
| Base dédiée créée pour cette recette | `mef_integration_f5bf43b0567943f08f6018319cd4aec7` |
| Backend dédié | JAR reconstruit ; écoute locale `127.0.0.1:18080`, Hibernate `validate`, stockage des pièces de cette seule base |
| Frontend dédié | Vite sur `http://127.0.0.1:15173`, proxy `/api` vers `127.0.0.1:18080` ; paramètres de recette transmis aux processus, sans modifier les `.env` métier |
| Navigateur | Edge disponible comme application Windows ; accès direct aux onglets indisponible. Tentative interrompue par le contrôle de sécurité du pilotage avant confirmation du chargement de l'application |
| Secrets | Aucun identifiant PostgreSQL sensible ou jeton enregistré dans les rapports. Identifiants et sessions utilisés en mémoire par les outils ; les noms de comptes ci-dessous sont des fixtures fictives |

Le schéma initial a été importé uniquement après création d'une nouvelle base vide au nom contrôlé. Les instructions de création/suppression de base du fichier de conception ont été exclues avant import. Aucun script de conception, migration ou changement de données n'a été exécuté sur `suivi_stagiaire` pendant cette recette.

Les documents fictifs sont conservés dans `backend/target/integration-uploads/<base>/`. Contrairement aux anciennes exécutions qui les nettoyaient, cette instance permet de reprendre des téléchargements manuels. Certains dossiers de test ont volontairement des états terminés, des alertes résolues et un fichier partagé entre deux dossiers : ils servent aux tests négatifs, pas à une démonstration de données métier réalistes.

## A. Tests automatisés réellement exécutés

**Résultats : 30 tests backend, 106 tests frontend, 32 contrôles HTTP supplémentaires réussis.** ESLint, build Vite et packaging du JAR réussis. Les tests frontend comprennent des contrats et du SSR ; ils ne constituent pas une recette navigateur.

Préconditions communes des scénarios backend : base vierge isolée, schéma de référence puis V001/V002, comptes et données fictifs créés par les tests, serveur Spring temporaire. Les refus attendus 400/401/403/409 sont des résultats réussis lorsqu'ils sont explicitement vérifiés.

| Parcours | Préconditions / compte | Actions exécutées | Résultat attendu | Résultat observé | Erreurs éventuelles | Verdict |
|---|---|---|---|---|---|---|
| Initialisation et migrations | Base isolée vide ; compte technique local | Import du schéma sans opérations de base destructives, V001/V002, démarrage Hibernate validate | Schéma accepté | Contexte Spring démarré, suite exécutée | Aucune erreur finale | Réussi |
| Création et affectation | ADMIN ; référentiels de test | Créer département, structure, cadre, stagiaire, études, encadreur, stage et participation | Créations et liens persistés | Assertions HTTP de création réussies | Requêtes invalides refusées comme attendu | Réussi |
| Événements | ADMIN ; participation EN_COURS | Suspension, reprise, prolongation ; tentative de contournement par PATCH ; lecture des historiques | Actions dédiées tracées, contournement refusé | Historique/événements vérifiés ; PATCH événementiel → 409 | Refus attendus | Réussi |
| Autorisation RESPONSABLE_STAGE | Deux départements et responsables distincts | Listes/détails, traitement d'alerte propre, accès et mutations vers l'autre département | Périmètre propre uniquement | Listes filtrées, accès croisés → 403 | Refus attendus | Réussi |
| Autorisation ENCADREUR | Deux encadreurs et affectations distinctes | Lire ses participations, revoir les rapports ; tenter les dossiers de l'autre | Affectation requise | Lectures autorisées, demandes hors périmètre refusées | Refus attendus ; même département sans affectation aussi testé unitairement | Réussi |
| Portail STAGIAIRE | Deux comptes associés explicitement | Lire son dossier, fichiers, rapports ; accès au second stagiaire et opérations administratives | Seulement son dossier ; opérations administratives interdites | Accès croisés → 403 ; observations privées masquées | Refus attendus | Réussi |
| Rapports et validations | STAGIAIRE, encadreur affecté ; périodes backend | Créer, soumettre, demander correction, corriger, resoumettre, valider | Workflow et droits respectés | Parcours complet ; auto-validation et modification d'un SOUMIS refusées | Refus attendus | Réussi |
| Documents | ADMIN et comptes de périmètres distincts | Déposer de vrais PDF, télécharger, tester doublon et partage entre dossiers | Octets valides, contrôle des droits | Téléchargement autorisé → 200 ; autre propriétaire → 403 ; métadonnées partagées masquées | Refus attendus | Réussi |
| Formats documentaires | Tests unitaires avec fichiers fabriqués | MIME incohérent, faux PDF, JavaScript compressé, images, taille | Refus des contenus invalides | 4 tests de validation réussis | Pas de contrôle antivirus exhaustif ni panne disque injectée | Réussi |
| Évaluations | ADMIN / encadreur ; participation et critères actifs | Noter, refuser note invalide, finaliser, tenter une modification après finalisation | Notes conformes et verrouillage | Assertions HTTP réussies ; refus attendus | Panne entre écriture et commit non injectée | Réussi |
| Soutenances | ADMIN ; participation de test | Créer et modifier ; refuser modification après clôture et hors périmètre | Autorisations et état respectés | 200/201 nominaux, 403/409 négatifs vérifiés | Refus attendus | Réussi |
| Sept détecteurs d'alertes | Services Spring réels et fixtures dédiées | Exécuter sept contrôles, répéter, résoudre les causes | Sept types, déduplication et résolution | Types attendus produits, aucun doublon à la répétition, résolution/expiration vérifiées | Cron non attendu ; bean planifié neutralisé dans les tests | Réussi |
| Clôture | ADMIN ; participation A_CLOTURER | Essayer sans pièces/rapports ; ajouter pièces et rapports validés ; clôturer puis tenter d'écrire | Refus incomplet, succès complet, dossier verrouillé | Événement CLOTURE, statut CLOTUREE, date/historique ; écritures interdites → 409 | Périodes futures non exigées selon règle actuelle | Réussi |
| Concurrence et contraintes SQL | Deux requêtes simultanées sur le même stagiaire / période | Affectations sur deux profils ; rapports concurrents ; SQL non canonique | Un succès et un conflit ; SQL invalide refusé | 201/409 ; chevauchements et dates invalides rejetés par PostgreSQL | Pas de test de charge | Réussi |
| Dossier et priorités | ADMIN, responsables/encadreurs distincts, STAGIAIRE | Lire synthèse, score, traiter alerte | Données filtrées, score conforme, classement actualisé | Comparaison à la vue SQL réussie ; retrait de la priorité traitée | Gros volumes non mesurés | Réussi |
| PDF et CSV | Données fictives accessibles et hors périmètre | Appeler les exports ; analyser PDF, CSV, pagination, neutralisation des formules | Document valide et aucune fuite croisée | Signatures/types, texte du PDF final, notes, pagination et périmètres vérifiés | Ouverture visuelle dans un lecteur PDF/tableur non exécutée | Réussi |
| Sessions | ADMIN, utilisateur de test | Réinitialiser le mot de passe, réutiliser l'ancien jeton | Ancienne session invalide | 401 vérifié | Aucune valeur sensible consignée dans ce rapport | Réussi |
| Frontend automatisé | Node ; tests/fixtures locaux | Contrats API, réseau, rôles, formulaires, tableaux, SSR | Assertions et compilation réussies | 106 tests, lint et build réussis ; 120 modules compilés | Ne prouve pas le rendu interactif | Réussi |
| JAR + proxy Vite réels | Serveurs dédiés 18080/15173 ; quatre rôles | Login, me, listes, utilisateurs, pilotage, CSV, PDF, logout via le proxy | Droits effectifs et réponses binaires valides | 32 contrôles réussis ; STAGIAIRE interdit aux exports globaux ; PDF propre autorisé | Première sonde corrigée : comparaison de casse des noms d'en-têtes HTTP, pas une anomalie serveur | Réussi |
| Rejeu des migrations sur données de test | Base isolée déjà remplie | Empreinte des lignes de 9 tables, réappliquer V001/V002, comparer | Aucun changement des lignes | 9 empreintes identiques ; notices « déjà existant » normales | Ne prouve pas la qualité des données historiques réelles | Réussi |

Preuves détaillées locales : `backend/target/surefire-reports/TEST-*.xml` (23 unitaires et 7 tests Spring/HTTP), `backend/target/recette/http-results.json`, `backend/target/recette/migrations-results.json`. Ces fichiers générés restent dans `target`, pas dans Git. Le répertoire `target` peut être effacé par une compilation propre : ce rapport conserve la synthèse et les commandes reproductibles.

### Commandes exécutées

Depuis la racine :

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-integration.ps1 -KeepUploads
mvn.cmd -o -f backend/pom.xml -DskipTests package
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-recette.ps1 -Database mef_integration_f5bf43b0567943f08f6018319cd4aec7
python scripts/check-recette-http.py
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/check-recette-migrations.ps1
```

Depuis `frontend` :

```powershell
node --test tests/*.test.js
node node_modules/eslint/bin/eslint.js .
node node_modules/vite/bin/vite.js build
```

Le packaging utilise `-DskipTests` après la réussite de la suite complète ; il ne constitue pas une seconde exécution des tests. Les migrations ne sont pas appliquées au démarrage du backend.

## B. Tests dans un navigateur réel

| Préconditions / rôle | Actions effectivement tentées | Résultat attendu | Résultat observé | Erreur / limite | Verdict |
|---|---|---|---|---|---|
| Edge installé, fenêtre Windows sélectionnée ; aucun compte connecté à la recette | Restaurer Edge, créer un nouvel onglet de recette, tenter d'ouvrir `127.0.0.1:15173` | Page de connexion de la recette visible | Onglet créé ; chargement de l'application non confirmé | L'outil a arrêté le pilotage car il ne pouvait pas déterminer l'URL courante avec assez de certitude pour appliquer sa politique de sécurité. Aucun contournement tenté | Non testé |
| ADMIN, RESPONSABLE_STAGE, ENCADREUR, STAGIAIRE | Connexions UI, navigation, formulaires, responsive, tableaux, impressions | Rendu correct et parcours utilisables | Aucune de ces interactions applicatives vérifiée dans Edge | Arrêt du pilotage avant la recette applicative | Non testé |

Ni les réponses HTML 200 du serveur Vite, ni le SSR, ni les assertions HTTP n'ont été présentés comme une vérification visuelle. Aucune capture d'écran de réussite de l'application n'est revendiquée.

## C. Procédure manuelle à exécuter

### Préparation

1. Ouvrir **uniquement** `http://127.0.0.1:15173` pour cette campagne, pas le serveur métier 8080. Les anciens processus ont été arrêtés pour reconstruire le JAR. Démarrer une recette isolée avec `scripts/start-recette.ps1 -Database <base-isolée>` avant reprise ; vérifier les paramètres du script.
2. Sur l'ancienne base de recette `mef_integration_f5bf43b0567943f08f6018319cd4aec7`, les noms des comptes fictifs sont : ADMIN `integration-admin`, ancien RESPONSABLE_STAGE devenu ADMIN `scope-63f6522b-manager0`, ENCADREUR `scope-63f6522b-mentor0`, STAGIAIRE `scope-63f6522b-trainee0`. Le département adverse possède les comptes de même préfixe finissant par `manager1`, `mentor1`, `trainee1`. Les mots de passe de fixtures ne sont pas recopiés ici ; les obtenir dans le cadre de la préparation locale autorisée, sans les mettre dans les preuves.
3. Pour refaire un parcours neuf, créer des comptes et dossiers fictifs propres à la campagne UI dans cette seule base. Prévoir deux départements A/B, un ADMIN global et un encadreur affecté par département, deux stagiaires et leurs associations de comptes. Ne pas déplacer les associations des fixtures sans noter ce changement de test. Utiliser des noms préfixés `RECETTE-UI-` et des coordonnées fictives.
4. Prévoir un PDF simple lisible et des images JPEG/PNG fictives, un faux PDF et un fichier dépassant 10 Mio. Ne pas utiliser de pièce personnelle. Choisir pour la nouvelle participation un début J−21 et une fin J+7, sans chevauchement avec un autre dossier du même stagiaire.
5. Préparer les résolutions 1440×900, 768×1024 et 390×844 via le navigateur ou un appareil adapté, zoom 100 %, puis un contrôle séparé à 200 %. Noter les dimensions réellement utilisées.
6. Dans les preuves, relever heure, rôle, identifiants de dossiers de test, action, statut HTTP et capture expurgée. Ne pas capturer les champs de mot de passe, Authorization, cookies ou jetons de stockage.

Dans le tableau suivant, « non exécuté » est le résultat observé de cette intervention. Le recettiste complétera chaque ligne avec une observation et un verdict datés.

| Parcours | Préconditions / compte | Actions manuelles à exécuter | Résultat attendu | Résultat observé | Erreurs éventuelles | Verdict |
|---|---|---|---|---|---|---|
| Connexion et déconnexion | Chaque rôle ; compte de test actif | Tester identifiants invalides, puis valides ; recharger ; se déconnecter ; retour arrière | Message lisible pour échec, navigation adaptée au rôle, session restaurée puis fermée | Non exécuté | Pilotage navigateur arrêté | Non testé |
| ADMIN et comptes | ADMIN ; nouveau jeu UI | Créer les comptes fictifs, attribuer les trois rôles retenus, associer les stagiaires ; essayer une association déjà utilisée | Choix réels, messages lisibles, doublon refusé sans écran cassé | Non exécuté | À relever | Non testé |
| Dossier stagiaire | ADMIN | Créer un dossier et ses études ; laisser un champ requis vide, corriger ; rouvrir | Validation accessible, données persistées, pas de doublon au double-clic | Non exécuté | À relever | Non testé |
| Stage et affectation | ADMIN ; références de A | Créer un stage et affecter stagiaire/encadreur ; essayer dates inversées et période chevauchante | Choix cohérents, erreurs 400/409 lisibles ; affectation correcte conservée | Non exécuté | À relever | Non testé |
| Événements | Participation EN_COURS | Suspendre, reprendre, prolonger ; ouvrir l'historique après chaque action ; annuler un dialogue | Statuts/événements actualisés ; annulation du dialogue sans écriture | Non exécuté | À relever | Non testé |
| Portail STAGIAIRE | Propriétaire A connecté | Ouvrir Mon dossier, participations, documents, observations ; saisir l'URL d'un dossier B | Données propres visibles, privé masqué, accès B refusé par API et message clair | Non exécuté | À relever sans conserver le jeton | Non testé |
| Rapport | Propriétaire A ; période disponible | Choisir une période serveur, saisir activité/difficultés/solutions, enregistrer, soumettre | Dates canoniques, contenu conservé, édition SOUMIS interdite | Non exécuté | À relever | Non testé |
| Correction et validation | Encadreur A puis stagiaire A | Ajouter observations publique/privée ; demander correction ; vérifier visibilité côté stagiaire ; corriger/resoumettre ; valider | Privé absent du portail, workflow complet et actualisation après action | Non exécuté | À relever | Non testé |
| Documents | ADMIN puis lecteur autorisé | Déposer PDF/JPEG/PNG ; essayer faux PDF, taille excessive, doublon ; télécharger | Succès/erreurs lisibles, sélection conservée ou réinitialisée correctement, fichier ouvrable | Non exécuté | À relever | Non testé |
| Évaluation | Encadreur affecté ; critères actifs | Noter chaque critère, essayer valeur hors 0–5, finaliser avec puis sans critère manquant ; rouvrir | Validations claires, note persistée, finalisation bloquant l'édition | Non exécuté | À relever | Non testé |
| Soutenance | ADMIN | Créer/modifier date et lieu ; ouvrir depuis la participation ; essayer depuis STAGIAIRE | Vue actualisée ; administration interdite au STAGIAIRE | Non exécuté | À relever | Non testé |
| Alertes et priorités | Données avec alertes ; ADMIN pour déclenchement global | Exécuter la détection autorisée, actualiser ; traiter une alerte ; vérifier score et lien dossier | Pas de doublons, classement actualisé ; détection globale refusée aux autres rôles | Non exécuté | Cron distinct de ce test manuel | Non testé |
| Clôture | ADMIN ; dossier A_CLOTURER | Essayer sans pièces/rapports ; compléter les pièces et valider les périodes achevées ; clôturer ; tenter modifications liées | 409 expliqué avant complétude ; CLOTUREE ensuite ; écritures refusées | Non exécuté | À relever | Non testé |
| PDF/CSV | ADMIN / encadreur ; données fictives | Cliquer chaque export, ouvrir PDF dans Edge/lecteur et CSV dans tableur ; contrôler accents, colonnes, multipages et valeurs commençant par = | Fichiers lisibles, données du seul périmètre, formule traitée comme texte | Non exécuté | Police PDF standard et outils bureautiques à vérifier | Non testé |
| Impression | Dossier consolidé assez long | Cliquer Imprimer la synthèse ; inspecter toutes les pages de l'aperçu ; annuler avant impression physique | Pas de menus/boutons, aucune ligne tronquée ni page blanche parasite | Non exécuté | Mise en page non vérifiée | Non testé |
| Tableaux et recherche | Listes > 10 dossiers/rapports de test | Chercher, changer taille de page, paginer, ouvrir un détail, revenir | Compteur et lignes cohérents, retour utilisable, pas de perte d'état surprenante | Non exécuté | À relever | Non testé |
| Responsive et clavier | Trois résolutions prévues ; trois rôles | Ouvrir menu mobile, formulaires et tableaux ; naviguer Tab/Shift+Tab/Entrée/Escape ; zoom 200 % | Aucun chevauchement, actions atteignables, focus visible, libellés lisibles | Non exécuté | À relever par taille réelle | Non testé |
| Réseau et session | Compte de test ; outils réseau du navigateur | Mettre hors ligne, tenter lecture/enregistrement, rétablir ; tester session expirée | Erreur claire, aucune fausse confirmation, reprise possible, redirection correcte après 401 | Non exécuté | Ne pas exposer jetons/cookies dans les captures | Non testé |
| Permissions adverses | Encadreur B, stagiaire B | Réutiliser URL et ID d'une ressource A pour lecture, écriture, téléchargement et export | Backend 403, aucun contenu du dossier A ; interface explique le refus | Non exécuté | Assertions API déjà réussies, interaction UI restante | Non testé |

Pour arrêter uniquement les serveurs de recette créés par les outils : `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/stop-recette.ps1`. Ce script vérifie l'identité et l'heure de démarrage des processus avant de les arrêter. Il conserve la base et les pièces de test. Le script d'arrêt a été exécuté lors de la mise à jour à trois rôles ; les anciennes données de test sont conservées.

## D. Blocage historique de démarrage transmis par l'utilisateur

Le journal du 2 novembre à 10:22 montre une connexion réussie à PostgreSQL puis :

```text
Schema validation: missing column [departement_id] in table [stagiaires]
```

| Préconditions / compte | Actions exécutées | Résultat attendu | Résultat observé | Erreur | Verdict |
|---|---|---|---|---|---|
| Configuration par défaut, base `suivi_stagiaire` ; lancement utilisateur | Analyse du journal fourni de `mvn spring-boot:run` | Serveur démarré si migrations présentes | JPA échoue, Tomcat s'arrête ; le `BUILD SUCCESS` Maven final ne prouve pas le démarrage de l'application | V001 absente ou colonne manquante dans le schéma visé | Échec |

Les erreurs de création de `SessionAuthenticationFilter`/`UtilisateurRepository` étaient des conséquences du mapping Java. Ce mapping a maintenant été retiré, ainsi que le champ DTO/formulaire. `validate` reste conservé. L'application a démarré et servi les parcours HTTP sur la base isolée sans cette colonne.

Pour cette erreur, arrêter l'ancien backend et relancer le code recompilé suffit ; ne pas appliquer V001. Le JAR a également été reconstruit. Voir le [README des migrations](../backend/migrations/README.md) pour distinguer cette correction de V002. Aucune migration métier n'a été exécutée.

## E. Décision et réserves

- **Réussi :** backend et contraintes sur le jeu isolé, quatre rôles via HTTP, exports binaires/contrats, tests frontend, compilations, démarrage isolé et rejeu non destructif des migrations sur données de test.
- **Blocage corrigé dans le code :** la colonne de V001 n'est plus attendue ; démarrage sans colonne confirmé sur PostgreSQL isolé, redémarrage de l'installation utilisateur restant à effectuer.
- **Non testé :** rendu/interactions navigateur, responsive, impression et ouverture visuelle des exports, cron à heure réelle, performances, restauration coordonnée base/fichiers, TLS/proxy de production, qualification des données historiques et rotation des anciens secrets.

L'instance isolée est utilisable pour commencer les essais humains listés. Pour prononcer la recette finale, compléter le tableau manuel et traiter les anomalies effectivement reproduites. Pour préparer la production, lever également les réserves de déploiement. Cette campagne ne remplace ni la validation métier des utilisateurs ni la vérification de la base réelle.
