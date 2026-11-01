-- Read-only qualification. Run before migrations; result sets contain identifiers, not personal details.
BEGIN TRANSACTION READ ONLY;

-- Account ownership is explicit, never inferred from names or email addresses.
SELECT s.id AS stagiaire_id, s.utilisateur_id
FROM stagiaires s
WHERE s.utilisateur_id IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM utilisateurs_role ur JOIN roles r ON r.id=ur.role_id
    WHERE ur.utilisateur_id=s.utilisateur_id AND r.code='STAGIAIRE' AND r.actif=TRUE
);

-- Includes different academic profiles of the same trainee.
SELECT a.stagiaire_id, p.id AS participation_a, q.id AS participation_b
FROM stage_participant p JOIN stagiaire_academique a ON a.id=p.stagiaire_academique_id
JOIN stagiaire_academique b ON b.stagiaire_id=a.stagiaire_id
JOIN stage_participant q ON q.stagiaire_academique_id=b.id AND q.id>p.id
JOIN statuts sp ON sp.id=p.statut_id JOIN statuts sq ON sq.id=q.statut_id
WHERE sp.code NOT IN ('ANNULEE','ABANDONNEE') AND sq.code NOT IN ('ANNULEE','ABANDONNEE')
AND p.date_debut_stage<=q.date_fin_stage AND q.date_debut_stage<=p.date_fin_stage;

-- Existing noncanonical periods must be qualified before editing or closing dossiers.
SELECT r.id AS rapport_id, r.stage_participant_id
FROM rapports_activite r JOIN stage_participant p ON p.id=r.stage_participant_id
WHERE r.debut_semaine<>GREATEST(p.date_debut_stage,r.debut_semaine-(EXTRACT(ISODOW FROM r.debut_semaine)::INTEGER-1))
OR r.fin_semaine<>LEAST(p.date_fin_stage,r.debut_semaine-(EXTRACT(ISODOW FROM r.debut_semaine)::INTEGER-1)+6)
OR r.debut_semaine<p.date_debut_stage OR r.fin_semaine>p.date_fin_stage;

SELECT p.id AS participation_id, s.code AS statut
FROM stage_participant p JOIN statuts s ON s.id=p.statut_id
WHERE (s.code='CLOTUREE' AND p.date_cloture IS NULL)
OR (s.code<>'CLOTUREE' AND p.date_cloture IS NOT NULL);

-- A shared file is hidden if any linked dossier is outside the caller's perimeter.
SELECT fichier_id, COUNT(DISTINCT stage_participant_id) AS participations_liees
FROM stage_participant_fichiers GROUP BY fichier_id HAVING COUNT(DISTINCT stage_participant_id)>1;

COMMIT;
