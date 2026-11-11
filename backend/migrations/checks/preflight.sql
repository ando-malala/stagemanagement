-- Read-only audit. Nonzero counts require review, never automatic correction.
SELECT 'stagiaire_compte_sans_role' AS anomalie, count(*) AS nombre FROM stagiaires t
WHERE utilisateur_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM utilisateurs_role ur JOIN roles r ON r.id=ur.role_id WHERE ur.utilisateur_id=t.utilisateur_id AND r.code='STAGIAIRE' AND r.actif)
UNION ALL
SELECT 'encadreur_compte_sans_role', count(*) FROM encadreurs e
WHERE utilisateur_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM utilisateurs_role ur JOIN roles r ON r.id=ur.role_id WHERE ur.utilisateur_id=e.utilisateur_id AND r.code='ENCADREUR' AND r.actif)
UNION ALL
SELECT 'evaluation_encadreur_incompatible', count(*) FROM evaluations_finales e JOIN stage_participant p ON p.id=e.stage_participant_id WHERE e.encadreur_id<>p.encadreur_id
UNION ALL
SELECT 'alerte_dossiers_incompatibles', count(*) FROM alertes a
LEFT JOIN rapports_activite r ON r.id=a.rapport_id LEFT JOIN soutenances s ON s.id=a.soutenance_id
WHERE (a.stage_participant_id IS NOT NULL AND r.stage_participant_id<>a.stage_participant_id)
OR (a.stage_participant_id IS NOT NULL AND s.stage_participant_id<>a.stage_participant_id)
OR (r.stage_participant_id IS NOT NULL AND s.stage_participant_id<>r.stage_participant_id);
