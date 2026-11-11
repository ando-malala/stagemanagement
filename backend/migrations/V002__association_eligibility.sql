-- Forward-only protection: no existing row is changed or removed.
-- Run checks/preflight.sql first; review legacy anomalies separately.
BEGIN;

CREATE OR REPLACE FUNCTION verifier_compte_metier() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE eligible boolean;
BEGIN
    IF NEW.utilisateur_id IS NULL THEN RETURN NEW; END IF;
    IF TG_OP = 'UPDATE' AND NEW.utilisateur_id IS NOT DISTINCT FROM OLD.utilisateur_id THEN RETURN NEW; END IF;
    -- The same row lock is used by account association and role mutation services.
    PERFORM 1 FROM utilisateurs WHERE id = NEW.utilisateur_id FOR UPDATE;
    SELECT s.code = 'ACTIF' AND s.actif AND EXISTS (
        SELECT 1 FROM utilisateurs_role ur JOIN roles r ON r.id = ur.role_id
        WHERE ur.utilisateur_id = u.id AND r.code = TG_ARGV[0] AND r.actif
    ) INTO eligible FROM utilisateurs u JOIN statuts s ON s.id = u.statut_id WHERE u.id = NEW.utilisateur_id;
    IF eligible IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'Compte non eligible pour cette association' USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_stagiaire_compte_eligible ON stagiaires;
CREATE TRIGGER trg_stagiaire_compte_eligible BEFORE INSERT OR UPDATE OF utilisateur_id ON stagiaires
FOR EACH ROW EXECUTE FUNCTION verifier_compte_metier('STAGIAIRE');
DROP TRIGGER IF EXISTS trg_encadreur_compte_eligible ON encadreurs;
CREATE TRIGGER trg_encadreur_compte_eligible BEFORE INSERT OR UPDATE OF utilisateur_id ON encadreurs
FOR EACH ROW EXECUTE FUNCTION verifier_compte_metier('ENCADREUR');

CREATE OR REPLACE FUNCTION proteger_role_associe() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE code_role text;
BEGIN
    IF TG_OP = 'UPDATE' AND NEW.utilisateur_id = OLD.utilisateur_id AND NEW.role_id = OLD.role_id THEN RETURN NEW; END IF;
    PERFORM 1 FROM utilisateurs WHERE id = OLD.utilisateur_id FOR UPDATE;
    SELECT code INTO code_role FROM roles WHERE id = OLD.role_id;
    IF (code_role = 'STAGIAIRE' AND EXISTS (SELECT 1 FROM stagiaires WHERE utilisateur_id = OLD.utilisateur_id))
        OR (code_role = 'ENCADREUR' AND EXISTS (SELECT 1 FROM encadreurs WHERE utilisateur_id = OLD.utilisateur_id)) THEN
        RAISE EXCEPTION 'Role requis par une association existante' USING ERRCODE = '23514';
    END IF;
    IF TG_OP = 'DELETE' THEN RETURN OLD; END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_role_associe ON utilisateurs_role;
CREATE TRIGGER trg_role_associe BEFORE DELETE OR UPDATE ON utilisateurs_role
FOR EACH ROW EXECUTE FUNCTION proteger_role_associe();

-- Arguments: FK column, referenced table, eligibility mode (boolean or status).
-- Identifiers come only from the trigger definitions below, never from HTTP input.
CREATE OR REPLACE FUNCTION verifier_reference_active() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE reference_id integer; eligible boolean;
BEGIN
    reference_id := (to_jsonb(NEW)->>TG_ARGV[0])::integer;
    IF reference_id IS NULL THEN RETURN NEW; END IF;
    IF TG_OP = 'UPDATE' AND to_jsonb(OLD)->>TG_ARGV[0] IS NOT DISTINCT FROM to_jsonb(NEW)->>TG_ARGV[0] THEN RETURN NEW; END IF;
    IF TG_ARGV[2] = 'status' THEN
        EXECUTE format('SELECT s.code = ''ACTIF'' AND s.actif FROM %I r JOIN statuts s ON s.id = r.statut_id WHERE r.id = $1 FOR SHARE OF r, s', TG_ARGV[1]) INTO eligible USING reference_id;
    ELSE
        EXECUTE format('SELECT actif FROM %I WHERE id = $1 FOR SHARE', TG_ARGV[1]) INTO eligible USING reference_id;
    END IF;
    IF eligible IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'Reference inactive ou inexistante pour une nouvelle association' USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;

DO $$
DECLARE item text[];
BEGIN
    FOREACH item SLICE 1 IN ARRAY ARRAY[
        ['stagiaire_academique','niveau_etude_id','niveaux_etude','boolean'],
        ['stagiaire_academique','filiere_id','filieres','boolean'],
        ['stagiaire_academique','etablissement_id','etablissements','boolean'],
        ['encadreurs','departement_id','departements','status'],
        ['structures_accueil','departement_id','departements','status'],
        ['responsable_departements','departement_id','departements','status'],
        ['stages','structure_accueil_id','structures_accueil','boolean'],
        ['stage_participant','cadre_stage_id','cadres_stage','boolean'],
        ['stage_participant','encadreur_id','encadreurs','status'],
        ['rapports_activite','mois_stage_id','mois_stage','boolean']
    ] LOOP
        EXECUTE format('DROP TRIGGER IF EXISTS %I ON %I', 'trg_reference_' || item[2], item[1]);
        EXECUTE format('CREATE TRIGGER %I BEFORE INSERT OR UPDATE OF %I ON %I FOR EACH ROW EXECUTE FUNCTION verifier_reference_active(%L,%L,%L)',
            'trg_reference_' || item[2], item[2], item[1], item[2], item[3], item[4]);
    END LOOP;
END $$;

CREATE OR REPLACE FUNCTION verifier_encadreur_evaluation() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE affectation integer;
BEGIN
    IF TG_OP = 'UPDATE' AND NEW.stage_participant_id = OLD.stage_participant_id AND NEW.encadreur_id = OLD.encadreur_id THEN RETURN NEW; END IF;
    SELECT encadreur_id INTO affectation FROM stage_participant WHERE id = NEW.stage_participant_id FOR UPDATE;
    IF affectation IS DISTINCT FROM NEW.encadreur_id THEN
        RAISE EXCEPTION 'Encadreur different de celui de la participation' USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_evaluation_encadreur ON evaluations_finales;
CREATE TRIGGER trg_evaluation_encadreur BEFORE INSERT OR UPDATE OF stage_participant_id, encadreur_id ON evaluations_finales
FOR EACH ROW EXECUTE FUNCTION verifier_encadreur_evaluation();

CREATE OR REPLACE FUNCTION proteger_affectation_evaluee() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.encadreur_id IS DISTINCT FROM OLD.encadreur_id AND EXISTS (
        SELECT 1 FROM evaluations_finales WHERE stage_participant_id = NEW.id AND encadreur_id <> NEW.encadreur_id
    ) THEN
        RAISE EXCEPTION 'Une evaluation depend de cette affectation' USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;
DROP TRIGGER IF EXISTS trg_affectation_evaluee ON stage_participant;
CREATE TRIGGER trg_affectation_evaluee BEFORE UPDATE OF encadreur_id ON stage_participant
FOR EACH ROW EXECUTE FUNCTION proteger_affectation_evaluee();
COMMIT;
