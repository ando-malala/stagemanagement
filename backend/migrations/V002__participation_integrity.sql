-- Non-destructive: checks future writes; does not rewrite historical records.
BEGIN;
CREATE OR REPLACE FUNCTION verifier_integrite_participation_v2()
RETURNS TRIGGER AS $$
DECLARE personne INTEGER; code_nouveau TEXT; code_ancien TEXT;
BEGIN
    SELECT code INTO code_nouveau FROM statuts WHERE id=NEW.statut_id;
    IF TG_OP='UPDATE' THEN
        SELECT code INTO code_ancien FROM statuts WHERE id=OLD.statut_id;
        IF (OLD.date_cloture IS NOT NULL OR code_ancien IN ('CLOTUREE','ANNULEE','ABANDONNEE'))
            AND ROW(NEW.stage_id,NEW.stagiaire_academique_id,NEW.encadreur_id,NEW.cadre_stage_id,NEW.date_debut_stage,NEW.date_fin_stage,NEW.statut_id)
            IS DISTINCT FROM ROW(OLD.stage_id,OLD.stagiaire_academique_id,OLD.encadreur_id,OLD.cadre_stage_id,OLD.date_debut_stage,OLD.date_fin_stage,OLD.statut_id) THEN
            RAISE EXCEPTION 'Participation terminee non modifiable';
        END IF;
    END IF;
    SELECT stagiaire_id INTO personne FROM stagiaire_academique WHERE id=NEW.stagiaire_academique_id;
    PERFORM pg_advisory_xact_lock(73401, personne);
    IF code_nouveau NOT IN ('ANNULEE','ABANDONNEE') AND EXISTS (
        SELECT 1 FROM stage_participant p JOIN stagiaire_academique sa ON sa.id=p.stagiaire_academique_id
        JOIN statuts s ON s.id=p.statut_id WHERE sa.stagiaire_id=personne AND p.id<>COALESCE(NEW.id,-1)
        AND s.code NOT IN ('ANNULEE','ABANDONNEE')
        AND p.date_debut_stage<=NEW.date_fin_stage AND p.date_fin_stage>=NEW.date_debut_stage
    ) THEN RAISE EXCEPTION 'Chevauchement de participations pour le meme stagiaire'; END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trg_integrite_participation_v2
BEFORE INSERT OR UPDATE ON stage_participant
FOR EACH ROW EXECUTE FUNCTION verifier_integrite_participation_v2();

CREATE OR REPLACE FUNCTION verifier_periode_rapport()
RETURNS TRIGGER AS $$
DECLARE debut DATE; fin DATE; cloture TIMESTAMP; statut_participation TEXT; lundi DATE; dimanche DATE;
BEGIN
    SELECT p.date_debut_stage,p.date_fin_stage,p.date_cloture,s.code
    INTO debut,fin,cloture,statut_participation
    FROM stage_participant p JOIN statuts s ON s.id=p.statut_id
    WHERE p.id=NEW.stage_participant_id FOR UPDATE OF p;
    IF debut IS NULL THEN RAISE EXCEPTION 'Participation inexistante'; END IF;
    IF cloture IS NOT NULL OR statut_participation IN ('CLOTUREE','ANNULEE','ABANDONNEE') THEN
        RAISE EXCEPTION 'Participation terminee non modifiable';
    END IF;
    lundi=NEW.debut_semaine-(EXTRACT(ISODOW FROM NEW.debut_semaine)::INTEGER-1);
    dimanche=lundi+6;
    IF NEW.debut_semaine<>GREATEST(debut,lundi) OR NEW.fin_semaine<>LEAST(fin,dimanche)
        OR NEW.debut_semaine<debut OR NEW.fin_semaine>fin OR NEW.fin_semaine<NEW.debut_semaine THEN
        RAISE EXCEPTION 'Periode non conforme au calendrier hebdomadaire';
    END IF;
    IF EXISTS(SELECT 1 FROM rapports_activite r WHERE r.stage_participant_id=NEW.stage_participant_id
        AND r.id<>COALESCE(NEW.id,-1) AND daterange(r.debut_semaine,r.fin_semaine,'[]') && daterange(NEW.debut_semaine,NEW.fin_semaine,'[]')) THEN
        RAISE EXCEPTION 'Chevauchement des rapports';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
COMMIT;
