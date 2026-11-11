-- REPARATION DE L'INITIALISATION - BASE suivi_stagiaire
-- Base du journal d'erreurs du 11/11/2026 et de Conception2(7).sql
-- ATTENTION : ne cree ni ne supprime de base ou table; ne supprime aucune donnee metier.
-- A lancer depuis psql : \i 'C:/Users/Andomalala/Downloads/reparer_initialisation.sql'
-- IMPORTANT : enregistrer ce fichier en UTF-8 et NE PAS coller les lignes dans la console.
\encoding UTF8
\set ON_ERROR_STOP on

BEGIN;

-- Reparations des fonctions/triggers dont la creation avait echoue dans le journal.
CREATE OR REPLACE FUNCTION verifier_transition_meme_type()
RETURNS TRIGGER AS $$
DECLARE
    type_source INT;
    type_cible INT;
BEGIN
    SELECT type_statut_id INTO type_source
    FROM statuts
    WHERE id=NEW.statut_source_id;

    SELECT type_statut_id INTO type_cible
    FROM statuts
    WHERE id=NEW.statut_cible_id;

    IF type_source<>type_cible THEN
        RAISE EXCEPTION 'Une transition ne peut pas relier deux types de statut différents';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_transition_meme_type ON transitions_statut;
CREATE TRIGGER trg_transition_meme_type
BEFORE INSERT OR UPDATE ON transitions_statut
FOR EACH ROW EXECUTE FUNCTION verifier_transition_meme_type();

CREATE OR REPLACE FUNCTION verifier_transition_autorisee()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.statut_id=OLD.statut_id THEN
        RETURN NEW;
    END IF;

    IF NOT EXISTS(
        SELECT 1
        FROM transitions_statut
        WHERE statut_source_id=OLD.statut_id
        AND statut_cible_id=NEW.statut_id
        AND actif=TRUE
    ) THEN
        RAISE EXCEPTION 'Transition de statut non autorisée: % -> %',OLD.statut_id,NEW.statut_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_transition_participation ON stage_participant;
CREATE TRIGGER trg_transition_participation
BEFORE UPDATE OF statut_id ON stage_participant
FOR EACH ROW EXECUTE FUNCTION verifier_transition_autorisee();

DROP TRIGGER IF EXISTS trg_transition_rapport ON rapports_activite;
CREATE TRIGGER trg_transition_rapport
BEFORE UPDATE OF statut_id ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION verifier_transition_autorisee();

CREATE OR REPLACE FUNCTION verifier_periode_rapport()
RETURNS TRIGGER AS $$
DECLARE
    date_debut_participation DATE;
    date_fin_participation DATE;
BEGIN
    SELECT date_debut_stage,date_fin_stage
    INTO date_debut_participation,date_fin_participation
    FROM stage_participant
    WHERE id=NEW.stage_participant_id;

    IF date_debut_participation IS NULL THEN
        RAISE EXCEPTION 'Participation % inexistante',NEW.stage_participant_id;
    END IF;

    IF NEW.debut_semaine<date_debut_participation OR NEW.fin_semaine>date_fin_participation THEN
        RAISE EXCEPTION 'La période du rapport doit appartenir à la période de participation';
    END IF;

    IF EXISTS(
        SELECT 1
        FROM rapports_activite r
        WHERE r.stage_participant_id=NEW.stage_participant_id
        AND r.id<>COALESCE(NEW.id,-1)
        AND daterange(r.debut_semaine,r.fin_semaine,'[]') &&
            daterange(NEW.debut_semaine,NEW.fin_semaine,'[]')
    ) THEN
        RAISE EXCEPTION 'Un rapport existe déjà sur une période qui chevauche cette semaine';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_verifier_periode_rapport ON rapports_activite;
CREATE TRIGGER trg_verifier_periode_rapport
BEFORE INSERT OR UPDATE ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION verifier_periode_rapport();

CREATE OR REPLACE FUNCTION verifier_piece_stagiaire()
RETURNS TRIGGER AS $$
DECLARE
    autorise BOOLEAN;
BEGIN
    SELECT pour_stagiaire INTO autorise
    FROM types_piece_jointe
    WHERE id=NEW.type_piece_jointe_id;

    IF autorise IS DISTINCT FROM TRUE THEN
        RAISE EXCEPTION 'Ce type de pièce ne peut pas être associé directement à un stagiaire';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_piece_stagiaire ON stagiaire_fichiers;
CREATE TRIGGER trg_piece_stagiaire
BEFORE INSERT OR UPDATE ON stagiaire_fichiers
FOR EACH ROW EXECUTE FUNCTION verifier_piece_stagiaire();

CREATE OR REPLACE FUNCTION verifier_piece_participation()
RETURNS TRIGGER AS $$
DECLARE
    autorise BOOLEAN;
BEGIN
    SELECT pour_participation INTO autorise
    FROM types_piece_jointe
    WHERE id=NEW.type_piece_jointe_id;

    IF autorise IS DISTINCT FROM TRUE THEN
        RAISE EXCEPTION 'Ce type de pièce ne peut pas être associé à une participation';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_piece_participation ON stage_participant_fichiers;
CREATE TRIGGER trg_piece_participation
BEFORE INSERT OR UPDATE ON stage_participant_fichiers
FOR EACH ROW EXECUTE FUNCTION verifier_piece_participation();

-- Initialisation des referentiels incomplets.
-- ON CONFLICT permet de relancer sans creer de doublons.
INSERT INTO types_statut(code,libelle) VALUES
('UTILISATEUR','Utilisateur'),
('DEPARTEMENT','Département'),
('STAGIAIRE','Stagiaire'),
('ENCADREUR','Encadreur'),
('PARTICIPATION','Participation au stage'),
('RAPPORT','Rapport d''activité')
ON CONFLICT DO NOTHING;

INSERT INTO statuts(type_statut_id,code,libelle)
SELECT ts.id,v.code_statut,v.libelle
FROM(
    VALUES
    ('UTILISATEUR','ACTIF','Actif'),
    ('UTILISATEUR','INACTIF','Inactif'),
    ('UTILISATEUR','BLOQUE','Bloqué'),
    ('DEPARTEMENT','ACTIF','Actif'),
    ('DEPARTEMENT','INACTIF','Inactif'),
    ('STAGIAIRE','ACTIF','Actif'),
    ('STAGIAIRE','ARCHIVE','Archivé'),
    ('ENCADREUR','ACTIF','Actif'),
    ('ENCADREUR','INACTIF','Inactif'),
    ('PARTICIPATION','PLANIFIEE','Planifiée'),
    ('PARTICIPATION','EN_COURS','En cours'),
    ('PARTICIPATION','SUSPENDUE','Suspendue'),
    ('PARTICIPATION','A_CLOTURER','À clôturer'),
    ('PARTICIPATION','CLOTUREE','Clôturée'),
    ('PARTICIPATION','ABANDONNEE','Abandonnée'),
    ('PARTICIPATION','ANNULEE','Annulée'),
    ('RAPPORT','BROUILLON','Brouillon'),
    ('RAPPORT','SOUMIS','Soumis'),
    ('RAPPORT','A_CORRIGER','À corriger'),
    ('RAPPORT','VALIDE','Validé')
) AS v(type_code,code_statut,libelle)
JOIN types_statut ts ON ts.code=v.type_code
WHERE TRUE
ON CONFLICT DO NOTHING;

INSERT INTO roles(code,libelle) VALUES
('ADMIN','Administrateur'),
('ENCADREUR','Encadreur professionnel'),
('STAGIAIRE','Stagiaire')
ON CONFLICT DO NOTHING;

INSERT INTO types_piece_jointe(code,libelle,pour_stagiaire,pour_participation,obligatoire_cloture,multiple_autorise) VALUES
('CV','Curriculum Vitae',TRUE,FALSE,FALSE,FALSE),
('CONVENTION_STAGE','Convention de stage',FALSE,TRUE,TRUE,FALSE),
('RAPPORT_FINAL','Rapport final de stage',FALSE,TRUE,TRUE,FALSE),
('ATTESTATION_STAGE','Attestation de stage',FALSE,TRUE,FALSE,FALSE),
('AUTRE','Autre document',TRUE,TRUE,FALSE,TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO types_evenement_participation(code,libelle) VALUES
('PROLONGATION','Prolongation du stage'),
('SUSPENSION','Suspension du stage'),
('REPRISE','Reprise du stage'),
('ABANDON','Abandon du stage'),
('ANNULATION','Annulation du stage'),
('CLOTURE','Clôture du stage')
ON CONFLICT DO NOTHING;

INSERT INTO types_alerte(code,libelle,poids_priorite,action_recommandee) VALUES
('FIN_STAGE_PROCHE','Fin de stage proche',10,'Préparer l''évaluation et la clôture du stage'),
('STAGE_DEPASSE','Date de fin dépassée sans clôture',50,'Régulariser la clôture'),
('RAPPORT_MANQUANT','Rapport hebdomadaire manquant',30,'Relancer le stagiaire'),
('VALIDATION_EN_RETARD','Rapport en attente de validation',20,'Relancer l''encadreur'),
('DOSSIER_INCOMPLET','Dossier incomplet',15,'Compléter les éléments manquants du dossier'),
('ENCADREUR_SURCHARGE','Encadreur en surcharge',20,'Réexaminer les affectations de l''encadreur'),
('SOUTENANCE_PROCHE','Soutenance prochaine',25,'Vérifier que le dossier de soutenance est prêt')
ON CONFLICT DO NOTHING;

INSERT INTO parametres_suivi(id,delai_validation_rapport_jours,capacite_encadreur_par_defaut,taille_max_fichier_octets)
VALUES(1,3,5,10485760)
ON CONFLICT DO NOTHING;

INSERT INTO seuils_alerte(type_alerte_id,jours_avant,priorite)
SELECT ta.id,v.jours_avant,v.priorite
FROM(
    VALUES
    ('FIN_STAGE_PROCHE',15,'INFORMATION'),
    ('FIN_STAGE_PROCHE',7,'AVERTISSEMENT'),
    ('FIN_STAGE_PROCHE',3,'URGENT'),
    ('SOUTENANCE_PROCHE',15,'INFORMATION'),
    ('SOUTENANCE_PROCHE',7,'AVERTISSEMENT'),
    ('SOUTENANCE_PROCHE',1,'URGENT')
) AS v(type_code,jours_avant,priorite)
JOIN types_alerte ta ON ta.code=v.type_code
WHERE TRUE
ON CONFLICT DO NOTHING;

INSERT INTO criteres_evaluation(code,libelle,ordre) VALUES
('PONCTUALITE','Ponctualité',1),
('AUTONOMIE','Autonomie',2),
('QUALITE_TRAVAIL','Qualité du travail',3),
('COMMUNICATION','Communication',4),
('RESPECT_CONSIGNES','Respect des consignes',5),
('PROGRESSION','Progression',6)
ON CONFLICT DO NOTHING;

WITH etats AS(
    SELECT s.id,s.code,ts.code AS type_code
    FROM statuts s
    JOIN types_statut ts ON ts.id=s.type_statut_id
)
INSERT INTO transitions_statut(statut_source_id,statut_cible_id)
SELECT source.id,cible.id
FROM(
    VALUES
    ('PARTICIPATION','PLANIFIEE','EN_COURS'),
    ('PARTICIPATION','PLANIFIEE','ANNULEE'),
    ('PARTICIPATION','EN_COURS','SUSPENDUE'),
    ('PARTICIPATION','SUSPENDUE','EN_COURS'),
    ('PARTICIPATION','EN_COURS','A_CLOTURER'),
    ('PARTICIPATION','EN_COURS','ABANDONNEE'),
    ('PARTICIPATION','EN_COURS','ANNULEE'),
    ('PARTICIPATION','SUSPENDUE','ABANDONNEE'),
    ('PARTICIPATION','SUSPENDUE','ANNULEE'),
    ('PARTICIPATION','A_CLOTURER','CLOTUREE'),
    ('PARTICIPATION','A_CLOTURER','EN_COURS')
) AS v(type_code,source_code,cible_code)
JOIN etats source ON source.type_code=v.type_code AND source.code=v.source_code
JOIN etats cible ON cible.type_code=v.type_code AND cible.code=v.cible_code
WHERE TRUE
ON CONFLICT DO NOTHING;

WITH etats AS(
    SELECT s.id,s.code,ts.code AS type_code
    FROM statuts s
    JOIN types_statut ts ON ts.id=s.type_statut_id
)
INSERT INTO transitions_statut(statut_source_id,statut_cible_id)
SELECT source.id,cible.id
FROM(
    VALUES
    ('RAPPORT','BROUILLON','SOUMIS'),
    ('RAPPORT','SOUMIS','A_CORRIGER'),
    ('RAPPORT','SOUMIS','VALIDE'),
    ('RAPPORT','A_CORRIGER','SOUMIS')
) AS v(type_code,source_code,cible_code)
JOIN etats source ON source.type_code=v.type_code AND source.code=v.source_code
JOIN etats cible ON cible.type_code=v.type_code AND cible.code=v.cible_code
WHERE TRUE
ON CONFLICT DO NOTHING;

-- Verification integree (doit afficher 6 types et 20 statuts).
SELECT (SELECT count(*) FROM types_statut) AS types_statut,
       (SELECT count(*) FROM statuts) AS statuts,
       (SELECT count(*) FROM types_alerte) AS types_alerte,
       (SELECT count(*) FROM transitions_statut) AS transitions_statut;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM statuts s
    JOIN types_statut ts ON ts.id = s.type_statut_id
    WHERE ts.code='STAGIAIRE' AND s.code='ACTIF' AND s.actif IS TRUE
  ) THEN
    RAISE EXCEPTION 'Le statut STAGIAIRE/ACTIF manque apres reparation';
  END IF;
END $$;

COMMIT;
\echo 'Reparation effectuee. Vous pouvez lancer data_clean_reel_2026.sql avec \i.'
