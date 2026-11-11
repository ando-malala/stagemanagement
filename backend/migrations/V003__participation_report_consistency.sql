-- Mirrors existing ParticipationRulesService and CalculPeriodesRapportService.
-- No data rewrite; unchanged historical associations are not revalidated.
BEGIN;
CREATE OR REPLACE FUNCTION verifier_coherence_participation() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE personne integer; structure_active boolean; personne_active boolean; nouveau_statut text;
BEGIN
    SELECT a.stagiaire_id INTO personne FROM stagiaire_academique a WHERE a.id=NEW.stagiaire_academique_id;
    PERFORM 1 FROM stagiaires WHERE id=personne FOR UPDATE;
    IF TG_OP='INSERT' OR NEW.stagiaire_academique_id IS DISTINCT FROM OLD.stagiaire_academique_id THEN
        SELECT s.code='ACTIF' AND s.actif INTO personne_active FROM stagiaires t JOIN statuts s ON s.id=t.statut_id WHERE t.id=personne;
        IF personne_active IS DISTINCT FROM true THEN RAISE EXCEPTION 'Stagiaire inactif' USING ERRCODE='23514'; END IF;
    END IF;
    IF TG_OP='INSERT' OR NEW.stage_id IS DISTINCT FROM OLD.stage_id THEN
        SELECT sa.actif INTO structure_active FROM stages st JOIN structures_accueil sa ON sa.id=st.structure_accueil_id WHERE st.id=NEW.stage_id FOR SHARE OF st, sa;
        IF structure_active IS DISTINCT FROM true THEN RAISE EXCEPTION 'Structure inactive' USING ERRCODE='23514'; END IF;
    END IF;
    SELECT code INTO nouveau_statut FROM statuts WHERE id=NEW.statut_id;
    IF TG_OP='INSERT' OR NEW.stagiaire_academique_id IS DISTINCT FROM OLD.stagiaire_academique_id
        OR NEW.date_debut_stage IS DISTINCT FROM OLD.date_debut_stage OR NEW.date_fin_stage IS DISTINCT FROM OLD.date_fin_stage
        OR NEW.statut_id IS DISTINCT FROM OLD.statut_id THEN
        IF nouveau_statut NOT IN ('ANNULEE','ABANDONNEE') AND EXISTS (
            SELECT 1 FROM stage_participant p JOIN stagiaire_academique a ON a.id=p.stagiaire_academique_id JOIN statuts s ON s.id=p.statut_id
            WHERE a.stagiaire_id=personne AND p.id<>NEW.id AND s.code NOT IN ('ANNULEE','ABANDONNEE')
            AND p.date_debut_stage<=NEW.date_fin_stage AND p.date_fin_stage>=NEW.date_debut_stage
        ) THEN RAISE EXCEPTION 'Participations qui se chevauchent' USING ERRCODE='23514'; END IF;
    END IF;
    IF TG_OP='UPDATE' AND (NEW.date_debut_stage IS DISTINCT FROM OLD.date_debut_stage OR NEW.date_fin_stage IS DISTINCT FROM OLD.date_fin_stage) AND EXISTS (
        SELECT 1 FROM rapports_activite r WHERE r.stage_participant_id=NEW.id AND (
            r.debut_semaine<NEW.date_debut_stage OR r.fin_semaine>NEW.date_fin_stage
            OR (r.debut_semaine<>NEW.date_debut_stage AND extract(isodow FROM r.debut_semaine)<>1)
            OR r.fin_semaine<>least(NEW.date_fin_stage,r.debut_semaine+(7-extract(isodow FROM r.debut_semaine)::integer))
        )
    ) THEN RAISE EXCEPTION 'Dates incompatibles avec les rapports existants' USING ERRCODE='23514'; END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_coherence_participation ON stage_participant;
CREATE TRIGGER trg_coherence_participation BEFORE INSERT OR UPDATE ON stage_participant
FOR EACH ROW EXECUTE FUNCTION verifier_coherence_participation();

CREATE OR REPLACE FUNCTION verifier_coherence_rapport() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE debut date; fin date; statut_ancien text;
BEGIN
    SELECT date_debut_stage,date_fin_stage INTO debut,fin FROM stage_participant WHERE id=NEW.stage_participant_id FOR UPDATE;
    IF TG_OP='INSERT' OR NEW.stage_participant_id IS DISTINCT FROM OLD.stage_participant_id
        OR NEW.debut_semaine IS DISTINCT FROM OLD.debut_semaine OR NEW.fin_semaine IS DISTINCT FROM OLD.fin_semaine THEN
        IF debut IS NULL OR NEW.debut_semaine<debut OR NEW.fin_semaine>fin
            OR (NEW.debut_semaine<>debut AND extract(isodow FROM NEW.debut_semaine)<>1)
            OR NEW.fin_semaine<>least(fin,NEW.debut_semaine+(7-extract(isodow FROM NEW.debut_semaine)::integer)) THEN
            RAISE EXCEPTION 'Periode differente des semaines attendues' USING ERRCODE='23514';
        END IF;
    END IF;
    IF TG_OP='UPDATE' THEN
        SELECT code INTO statut_ancien FROM statuts WHERE id=OLD.statut_id;
        IF statut_ancien='VALIDE' AND ROW(NEW.stage_participant_id,NEW.debut_semaine,NEW.fin_semaine,NEW.activite,NEW.difficultes,NEW.solutions,NEW.mois_stage_id)
            IS DISTINCT FROM ROW(OLD.stage_participant_id,OLD.debut_semaine,OLD.fin_semaine,OLD.activite,OLD.difficultes,OLD.solutions,OLD.mois_stage_id) THEN
            RAISE EXCEPTION 'Rapport valide non modifiable' USING ERRCODE='23514';
        END IF;
    END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_coherence_rapport ON rapports_activite;
CREATE TRIGGER trg_coherence_rapport BEFORE INSERT OR UPDATE ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION verifier_coherence_rapport();
COMMIT;
