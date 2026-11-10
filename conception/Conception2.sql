\c postgres

DROP DATABASE IF EXISTS suivi_stagiaire;
CREATE DATABASE suivi_stagiaire;

\c suivi_stagiaire
\encoding UTF8
CREATE TABLE types_statut(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE statuts(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    type_statut_id INT NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(type_statut_id,code),
    UNIQUE(type_statut_id,libelle),
    FOREIGN KEY(type_statut_id) REFERENCES types_statut(id)
);

CREATE TABLE etablissements(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE filieres(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE niveaux_etude(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mois_stage(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE roles(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE types_piece_jointe(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    pour_stagiaire BOOLEAN NOT NULL DEFAULT FALSE,
    pour_participation BOOLEAN NOT NULL DEFAULT FALSE,
    obligatoire_cloture BOOLEAN NOT NULL DEFAULT FALSE,
    multiple_autorise BOOLEAN NOT NULL DEFAULT TRUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK(pour_stagiaire OR pour_participation)
);

CREATE TABLE types_evenement_participation(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE types_alerte (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    poids_priorite INT NOT NULL DEFAULT 0,
    action_recommandee VARCHAR(250),
    nature_alerte VARCHAR(20) NOT NULL DEFAULT 'A_TRAITER',
    actif BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (poids_priorite >= 0),
    CONSTRAINT chk_types_alerte_nature
        CHECK (nature_alerte IN ('INFORMATION', 'A_TRAITER'))
);

CREATE TABLE parametres_suivi(
    id SMALLINT PRIMARY KEY DEFAULT 1,
    delai_validation_rapport_jours INT NOT NULL DEFAULT 3,
    capacite_encadreur_par_defaut INT NOT NULL DEFAULT 5,
    taille_max_fichier_octets BIGINT NOT NULL DEFAULT 10485760,
    CHECK(id=1),
    CHECK(delai_validation_rapport_jours>0),
    CHECK(capacite_encadreur_par_defaut>0),
    CHECK(taille_max_fichier_octets>0)
);

CREATE TABLE seuils_alerte(
    id SERIAL PRIMARY KEY,
    type_alerte_id INT NOT NULL,
    jours_avant INT NOT NULL,
    priorite VARCHAR(20) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(type_alerte_id,jours_avant),
    CHECK(jours_avant>=0),
    CHECK(priorite IN('INFORMATION','AVERTISSEMENT','URGENT')),
    FOREIGN KEY(type_alerte_id) REFERENCES types_alerte(id)
);

CREATE TABLE utilisateurs(
    id SERIAL PRIMARY KEY,
    identifiant VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    statut_id INT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(statut_id) REFERENCES statuts(id)
);

CREATE TABLE utilisateurs_role(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    role_id INT NOT NULL,
    UNIQUE(utilisateur_id,role_id),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY(role_id) REFERENCES roles(id)
);

CREATE TABLE sessions_utilisateur(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    jeton_hash VARCHAR(255) NOT NULL UNIQUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration TIMESTAMP NOT NULL,
    date_revocation TIMESTAMP,
    CHECK(date_expiration>date_creation),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE historique_connexion_utilisateur(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    date_connexion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE utilisateur_statut_historique(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE journal_audit(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT,
    action VARCHAR(100) NOT NULL,
    entite VARCHAR(100) NOT NULL,
    entite_id INT,
    anciennes_valeurs JSONB,
    nouvelles_valeurs JSONB,
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE departements(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(250) NOT NULL UNIQUE,
    statut_id INT NOT NULL,
    FOREIGN KEY(statut_id) REFERENCES statuts(id)
);

CREATE TABLE departement_statut_historique(
    id SERIAL PRIMARY KEY,
    departement_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(departement_id) REFERENCES departements(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE responsable_departements(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    departement_id INT NOT NULL,
    UNIQUE(utilisateur_id,departement_id),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY(departement_id) REFERENCES departements(id)
);

CREATE TABLE structures_accueil(
    id SERIAL PRIMARY KEY,
    departement_id INT NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    localite VARCHAR(150),
    porte VARCHAR(30),
    contact VARCHAR(150),
    courriel VARCHAR(150),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(departement_id,libelle),
    FOREIGN KEY(departement_id) REFERENCES departements(id)
);

CREATE TABLE fichiers(
    id SERIAL PRIMARY KEY,
    nom_original VARCHAR(255) NOT NULL,
    nom_stockage VARCHAR(255) NOT NULL UNIQUE,
    chemin VARCHAR(500) NOT NULL,
    type_mime VARCHAR(100),
    taille_octets BIGINT,
    utilisateur_id INT,
    date_ajout TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK(taille_octets IS NULL OR taille_octets>=0),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE stagiaires(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    prenoms VARCHAR(250) NOT NULL,
    adresse VARCHAR(250) NOT NULL,
    contact VARCHAR(100),
    email VARCHAR(150),
    statut_id INT NOT NULL,
    utilisateur_id INT UNIQUE,
    photo_fichier_id INT UNIQUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY(photo_fichier_id) REFERENCES fichiers(id)
);

CREATE TABLE stagiaire_statut_historique(
    id SERIAL PRIMARY KEY,
    stagiaire_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(stagiaire_id) REFERENCES stagiaires(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE stagiaire_academique(
    id SERIAL PRIMARY KEY,
    stagiaire_id INT NOT NULL,
    niveau_etude_id INT NOT NULL,
    filiere_id INT NOT NULL,
    etablissement_id INT NOT NULL,
    annee_etudes VARCHAR(9) NOT NULL,
    UNIQUE(stagiaire_id,niveau_etude_id,filiere_id,etablissement_id,annee_etudes),
    CHECK(annee_etudes ~ '^[0-9]{4}-[0-9]{4}$'),
    FOREIGN KEY(stagiaire_id) REFERENCES stagiaires(id),
    FOREIGN KEY(niveau_etude_id) REFERENCES niveaux_etude(id),
    FOREIGN KEY(filiere_id) REFERENCES filieres(id),
    FOREIGN KEY(etablissement_id) REFERENCES etablissements(id)
);

CREATE TABLE stagiaire_fichiers(
    id SERIAL PRIMARY KEY,
    stagiaire_id INT NOT NULL,
    fichier_id INT NOT NULL,
    type_piece_jointe_id INT NOT NULL,
    date_association TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stagiaire_id,fichier_id),
    FOREIGN KEY(stagiaire_id) REFERENCES stagiaires(id),
    FOREIGN KEY(fichier_id) REFERENCES fichiers(id),
    FOREIGN KEY(type_piece_jointe_id) REFERENCES types_piece_jointe(id)
);

CREATE TABLE encadreurs(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT UNIQUE,
    nom VARCHAR(250) NOT NULL,
    prenoms VARCHAR(250) NOT NULL,
    fonction VARCHAR(150),
    localite VARCHAR(150),
    porte VARCHAR(30),
    contact VARCHAR(150),
    courriel VARCHAR(150),
    statut_id INT NOT NULL,
    departement_id INT NOT NULL,
    capacite_max_stagiaires INT,
    CHECK(capacite_max_stagiaires IS NULL OR capacite_max_stagiaires>0),
    FOREIGN KEY(departement_id) REFERENCES departements(id),
    FOREIGN KEY(utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id)
);

CREATE TABLE encadreur_statut_historique(
    id SERIAL PRIMARY KEY,
    encadreur_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(encadreur_id) REFERENCES encadreurs(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE cadres_stage(
    id SERIAL PRIMARY KEY,
    theme_stage VARCHAR(250) NOT NULL,
    activite_confie_stagiaire TEXT,
    objectif_stage TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE stages(
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL,
    structure_accueil_id INT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(libelle,structure_accueil_id),
    FOREIGN KEY(structure_accueil_id) REFERENCES structures_accueil(id)
);

CREATE TABLE stage_participant(
    id SERIAL PRIMARY KEY,
    stage_id INT NOT NULL,
    stagiaire_academique_id INT NOT NULL,
    cadre_stage_id INT NOT NULL,
    date_debut_stage DATE NOT NULL,
    date_fin_stage DATE NOT NULL,
    encadreur_id INT NOT NULL,
    statut_id INT NOT NULL,
    date_cloture TIMESTAMP,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stage_id,stagiaire_academique_id,date_debut_stage),
    CHECK(date_fin_stage>=date_debut_stage),
    FOREIGN KEY(stage_id) REFERENCES stages(id),
    FOREIGN KEY(stagiaire_academique_id) REFERENCES stagiaire_academique(id),
    FOREIGN KEY(cadre_stage_id) REFERENCES cadres_stage(id),
    FOREIGN KEY(encadreur_id) REFERENCES encadreurs(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id)
);

CREATE TABLE stage_participant_statut_historique(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE stage_participant_evenements(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL,
    type_evenement_id INT NOT NULL,
    date_evenement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_effet DATE,
    ancienne_date_fin DATE,
    nouvelle_date_fin DATE,
    motif TEXT,
    responsable_id INT NOT NULL,
    CHECK(nouvelle_date_fin IS NULL OR ancienne_date_fin IS NULL OR nouvelle_date_fin>=ancienne_date_fin),
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(type_evenement_id) REFERENCES types_evenement_participation(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE stage_participant_fichiers(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL,
    fichier_id INT NOT NULL,
    type_piece_jointe_id INT NOT NULL,
    date_association TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stage_participant_id,fichier_id),
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(fichier_id) REFERENCES fichiers(id),
    FOREIGN KEY(type_piece_jointe_id) REFERENCES types_piece_jointe(id)
);

CREATE TABLE rapports_activite(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL,
    debut_semaine DATE NOT NULL,
    fin_semaine DATE NOT NULL,
    activite TEXT,
    difficultes TEXT,
    solutions TEXT,
    mois_stage_id INT NOT NULL,
    statut_id INT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_soumission TIMESTAMP,
    date_validation TIMESTAMP,
    UNIQUE(stage_participant_id,debut_semaine,fin_semaine),
    CHECK(fin_semaine>=debut_semaine),
    CHECK(fin_semaine-debut_semaine<=6),
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(mois_stage_id) REFERENCES mois_stage(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id)
);

CREATE TABLE rapports_activite_statut_historique(
    id SERIAL PRIMARY KEY,
    rapport_id INT NOT NULL,
    ancien_statut_id INT,
    statut_id INT NOT NULL,
    responsable_id INT NOT NULL,
    motif TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(rapport_id) REFERENCES rapports_activite(id),
    FOREIGN KEY(ancien_statut_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_id) REFERENCES statuts(id),
    FOREIGN KEY(responsable_id) REFERENCES utilisateurs(id)
);

CREATE TABLE rapport_observations(
    id SERIAL PRIMARY KEY,
    rapport_id INT NOT NULL,
    auteur_id INT NOT NULL,
    type_observation VARCHAR(30) NOT NULL,
    commentaire TEXT NOT NULL,
    visible_stagiaire BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK(type_observation IN('OBSERVATION','DEMANDE_CORRECTION','VALIDATION')),
    FOREIGN KEY(rapport_id) REFERENCES rapports_activite(id),
    FOREIGN KEY(auteur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE transitions_statut(
    id SERIAL PRIMARY KEY,
    statut_source_id INT NOT NULL,
    statut_cible_id INT NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(statut_source_id,statut_cible_id),
    CHECK(statut_source_id<>statut_cible_id),
    FOREIGN KEY(statut_source_id) REFERENCES statuts(id),
    FOREIGN KEY(statut_cible_id) REFERENCES statuts(id)
);

CREATE TABLE criteres_evaluation(
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    coefficient NUMERIC(5,2) NOT NULL DEFAULT 1,
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INT NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK(coefficient>0),
    CHECK(ordre>=0)
);

CREATE TABLE evaluations_finales(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL UNIQUE,
    encadreur_id INT NOT NULL,
    appreciation_globale TEXT,
    observations_finales TEXT,
    est_finalisee BOOLEAN NOT NULL DEFAULT FALSE,
    date_evaluation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_finalisation TIMESTAMP,
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(encadreur_id) REFERENCES encadreurs(id)
);

CREATE TABLE evaluation_criteres(
    id SERIAL PRIMARY KEY,
    evaluation_id INT NOT NULL,
    critere_id INT NOT NULL,
    note NUMERIC(4,2),
    commentaire TEXT,
    UNIQUE(evaluation_id,critere_id),
    CHECK(note IS NULL OR(note>=0 AND note<=5)),
    FOREIGN KEY(evaluation_id) REFERENCES evaluations_finales(id),
    FOREIGN KEY(critere_id) REFERENCES criteres_evaluation(id)
);

CREATE TABLE soutenances(
    id SERIAL PRIMARY KEY,
    stage_participant_id INT NOT NULL UNIQUE,
    date_soutenance TIMESTAMP NOT NULL,
    lieu VARCHAR(250),
    observation TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id)
);

CREATE TABLE alertes(
    id SERIAL PRIMARY KEY,
    type_alerte_id INT NOT NULL,
    stage_participant_id INT,
    rapport_id INT,
    encadreur_id INT,
    soutenance_id INT,
    message TEXT,
    date_echeance DATE,
    priorite VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'A_TRAITER',
    date_detection TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement TIMESTAMP,
    traite_par_id INT,
    cle_deduplication VARCHAR(255) UNIQUE,
    CHECK(priorite IN('INFORMATION','AVERTISSEMENT','URGENT')),
    CHECK (statut IN ('A_TRAITER', 'TRAITEE', 'IGNOREE', 'EXPIREE')),
    CHECK(stage_participant_id IS NOT NULL OR rapport_id IS NOT NULL OR encadreur_id IS NOT NULL OR soutenance_id IS NOT NULL),
    FOREIGN KEY(type_alerte_id) REFERENCES types_alerte(id),
    FOREIGN KEY(stage_participant_id) REFERENCES stage_participant(id),
    FOREIGN KEY(rapport_id) REFERENCES rapports_activite(id),
    FOREIGN KEY(encadreur_id) REFERENCES encadreurs(id),
    FOREIGN KEY(soutenance_id) REFERENCES soutenances(id),
    FOREIGN KEY(traite_par_id) REFERENCES utilisateurs(id)
);

CREATE INDEX idx_statuts_type ON statuts(type_statut_id);
CREATE INDEX idx_utilisateurs_statut ON utilisateurs(statut_id);
CREATE INDEX idx_sessions_utilisateur ON sessions_utilisateur(utilisateur_id);
CREATE INDEX idx_sessions_expiration ON sessions_utilisateur(date_expiration);
CREATE INDEX idx_connexions_utilisateur ON historique_connexion_utilisateur(utilisateur_id);
CREATE INDEX idx_responsable_departements_utilisateur ON responsable_departements(utilisateur_id);
CREATE INDEX idx_responsable_departements_departement ON responsable_departements(departement_id);
CREATE INDEX idx_structures_departement ON structures_accueil(departement_id);
CREATE INDEX idx_stagiaire_academique_stagiaire ON stagiaire_academique(stagiaire_id);
CREATE INDEX idx_encadreurs_departement ON encadreurs(departement_id);
CREATE INDEX idx_stage_structure ON stages(structure_accueil_id);
CREATE INDEX idx_stage_participant_stage ON stage_participant(stage_id);
CREATE INDEX idx_stage_participant_academique ON stage_participant(stagiaire_academique_id);
CREATE INDEX idx_stage_participant_encadreur ON stage_participant(encadreur_id);
CREATE INDEX idx_stage_participant_statut ON stage_participant(statut_id);
CREATE INDEX idx_stage_participant_dates ON stage_participant(date_debut_stage,date_fin_stage);
CREATE INDEX idx_rapport_participant ON rapports_activite(stage_participant_id);
CREATE INDEX idx_rapport_statut ON rapports_activite(statut_id);
CREATE INDEX idx_rapport_dates ON rapports_activite(debut_semaine,fin_semaine);
CREATE INDEX idx_soutenance_date ON soutenances(date_soutenance);
CREATE INDEX idx_alertes_type ON alertes(type_alerte_id);
CREATE INDEX idx_alertes_statut ON alertes(statut);
CREATE INDEX idx_alertes_participant ON alertes(stage_participant_id);
CREATE INDEX idx_alertes_rapport ON alertes(rapport_id);
CREATE INDEX idx_alertes_encadreur ON alertes(encadreur_id);
CREATE INDEX idx_alertes_soutenance ON alertes(soutenance_id);
CREATE INDEX idx_alertes_detection ON alertes(date_detection);

CREATE OR REPLACE FUNCTION verifier_type_statut()
RETURNS TRIGGER AS $$
DECLARE
    code_type_reel VARCHAR(50);
BEGIN
    SELECT ts.code INTO code_type_reel
    FROM statuts s
    JOIN types_statut ts ON ts.id=s.type_statut_id
    WHERE s.id=NEW.statut_id;

    IF code_type_reel IS NULL THEN
        RAISE EXCEPTION 'Statut % inexistant',NEW.statut_id;
    END IF;

    IF code_type_reel<>TG_ARGV[0] THEN
        RAISE EXCEPTION 'Statut incompatible. Type attendu: %, type obtenu: %',TG_ARGV[0],code_type_reel;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_utilisateur_type_statut
BEFORE INSERT OR UPDATE ON utilisateurs
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('UTILISATEUR');

CREATE TRIGGER trg_departement_type_statut
BEFORE INSERT OR UPDATE ON departements
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('DEPARTEMENT');

CREATE TRIGGER trg_stagiaire_type_statut
BEFORE INSERT OR UPDATE ON stagiaires
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('STAGIAIRE');

CREATE TRIGGER trg_encadreur_type_statut
BEFORE INSERT OR UPDATE ON encadreurs
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('ENCADREUR');

CREATE TRIGGER trg_participation_type_statut
BEFORE INSERT OR UPDATE ON stage_participant
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('PARTICIPATION');

CREATE TRIGGER trg_rapport_type_statut
BEFORE INSERT OR UPDATE ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION verifier_type_statut('RAPPORT');

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

CREATE TRIGGER trg_transition_participation
BEFORE UPDATE OF statut_id ON stage_participant
FOR EACH ROW EXECUTE FUNCTION verifier_transition_autorisee();

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

CREATE TRIGGER trg_piece_participation
BEFORE INSERT OR UPDATE ON stage_participant_fichiers
FOR EACH ROW EXECUTE FUNCTION verifier_piece_participation();

CREATE OR REPLACE FUNCTION maj_date_modification()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification=CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_maj_date_participant
BEFORE UPDATE ON stage_participant
FOR EACH ROW EXECUTE FUNCTION maj_date_modification();

CREATE TRIGGER trg_maj_date_rapport
BEFORE UPDATE ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION maj_date_modification();

CREATE OR REPLACE FUNCTION maj_dates_workflow_rapport()
RETURNS TRIGGER AS $$
DECLARE
    code_statut VARCHAR(50);
BEGIN
    SELECT s.code INTO code_statut
    FROM statuts s
    JOIN types_statut ts ON ts.id=s.type_statut_id
    WHERE s.id=NEW.statut_id
    AND ts.code='RAPPORT';

    IF code_statut='SOUMIS' THEN
        NEW.date_soumission=CURRENT_TIMESTAMP;
    END IF;

    IF code_statut='VALIDE' THEN
        NEW.date_validation=CURRENT_TIMESTAMP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_dates_workflow_rapport
BEFORE INSERT OR UPDATE OF statut_id ON rapports_activite
FOR EACH ROW EXECUTE FUNCTION maj_dates_workflow_rapport();

CREATE OR REPLACE FUNCTION maj_date_cloture_participation()
RETURNS TRIGGER AS $$
DECLARE
    code_statut VARCHAR(50);
BEGIN
    SELECT s.code INTO code_statut
    FROM statuts s
    JOIN types_statut ts ON ts.id=s.type_statut_id
    WHERE s.id=NEW.statut_id
    AND ts.code='PARTICIPATION';

    IF code_statut='CLOTUREE' AND NEW.date_cloture IS NULL THEN
        NEW.date_cloture=CURRENT_TIMESTAMP;
    END IF;

    IF code_statut<>'CLOTUREE' THEN
        NEW.date_cloture=NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_date_cloture_participation
BEFORE INSERT OR UPDATE OF statut_id ON stage_participant
FOR EACH ROW EXECUTE FUNCTION maj_date_cloture_participation();

CREATE VIEW vue_charge_encadreurs AS
SELECT
    e.id AS encadreur_id,
    e.nom,
    e.prenoms,
    COALESCE(
        e.capacite_max_stagiaires,
        (SELECT capacite_encadreur_par_defaut FROM parametres_suivi WHERE id=1)
    ) AS capacite_max,
    COUNT(sp.id) FILTER(WHERE s.code IN('EN_COURS','A_CLOTURER')) AS participations_actives,
    CASE
        WHEN COUNT(sp.id) FILTER(WHERE s.code IN('EN_COURS','A_CLOTURER'))>
        COALESCE(
            e.capacite_max_stagiaires,
            (SELECT capacite_encadreur_par_defaut FROM parametres_suivi WHERE id=1)
        )
        THEN TRUE
        ELSE FALSE
    END AS surcharge
FROM encadreurs e
LEFT JOIN stage_participant sp ON sp.encadreur_id=e.id
LEFT JOIN statuts s ON s.id=sp.statut_id
GROUP BY e.id,e.nom,e.prenoms,e.capacite_max_stagiaires;

CREATE VIEW vue_participations_echeances AS
SELECT
    sp.id AS stage_participant_id,
    st.id AS stage_id,
    st.libelle AS stage,
    sg.id AS stagiaire_id,
    sg.nom,
    sg.prenoms,
    sp.date_debut_stage,
    sp.date_fin_stage,
    sp.date_fin_stage-CURRENT_DATE AS jours_restants,
    s.code AS statut,
    CASE
        WHEN s.code IN('CLOTUREE','ANNULEE','ABANDONNEE') THEN 'TERMINEE'
        WHEN sp.date_fin_stage<CURRENT_DATE THEN 'DEPASSEE'
        WHEN seuil.priorite IS NOT NULL THEN seuil.priorite
        ELSE 'NORMAL'
    END AS niveau_echeance
FROM stage_participant sp
JOIN stages st ON st.id=sp.stage_id
JOIN stagiaire_academique sa ON sa.id=sp.stagiaire_academique_id
JOIN stagiaires sg ON sg.id=sa.stagiaire_id
JOIN statuts s ON s.id=sp.statut_id
LEFT JOIN LATERAL(
    SELECT se.priorite
    FROM seuils_alerte se
    JOIN types_alerte ta ON ta.id=se.type_alerte_id
    WHERE ta.code='FIN_STAGE_PROCHE'
    AND se.actif=TRUE
    AND sp.date_fin_stage>=CURRENT_DATE
    AND sp.date_fin_stage-CURRENT_DATE<=se.jours_avant
    ORDER BY se.jours_avant ASC
    LIMIT 1
) seuil ON TRUE;

CREATE VIEW vue_rapports_en_attente AS
SELECT
    r.id AS rapport_id,
    r.stage_participant_id,
    r.debut_semaine,
    r.fin_semaine,
    r.date_soumission,
    CURRENT_DATE-r.date_soumission::DATE AS jours_attente
FROM rapports_activite r
JOIN statuts s ON s.id=r.statut_id
JOIN types_statut ts ON ts.id=s.type_statut_id
WHERE ts.code='RAPPORT'
AND s.code='SOUMIS'
AND r.date_soumission IS NOT NULL;

CREATE VIEW vue_pieces_participation_manquantes AS
SELECT
    sp.id AS stage_participant_id,
    tpj.id AS type_piece_jointe_id,
    tpj.code,
    tpj.libelle
FROM stage_participant sp
CROSS JOIN types_piece_jointe tpj
WHERE tpj.actif=TRUE
AND tpj.pour_participation=TRUE
AND tpj.obligatoire_cloture=TRUE
AND NOT EXISTS(
    SELECT 1
    FROM stage_participant_fichiers spf
    WHERE spf.stage_participant_id=sp.id
    AND spf.type_piece_jointe_id=tpj.id
);

CREATE VIEW vue_soutenances_prochaines AS
SELECT
    so.id AS soutenance_id,
    so.stage_participant_id,
    sg.id AS stagiaire_id,
    sg.nom,
    sg.prenoms,
    so.date_soutenance,
    so.lieu,
    so.date_soutenance::DATE-CURRENT_DATE AS jours_restants,
    CASE
        WHEN so.date_soutenance::DATE<CURRENT_DATE THEN 'PASSEE'
        WHEN seuil.priorite IS NOT NULL THEN seuil.priorite
        ELSE 'NORMALE'
    END AS niveau_alerte
FROM soutenances so
JOIN stage_participant sp ON sp.id=so.stage_participant_id
JOIN stagiaire_academique sa ON sa.id=sp.stagiaire_academique_id
JOIN stagiaires sg ON sg.id=sa.stagiaire_id
LEFT JOIN LATERAL(
    SELECT se.priorite
    FROM seuils_alerte se
    JOIN types_alerte ta ON ta.id=se.type_alerte_id
    WHERE ta.code='SOUTENANCE_PROCHE'
    AND se.actif=TRUE
    AND so.date_soutenance::DATE>=CURRENT_DATE
    AND so.date_soutenance::DATE-CURRENT_DATE<=se.jours_avant
    ORDER BY se.jours_avant ASC
    LIMIT 1
) seuil ON TRUE;

CREATE VIEW vue_actions_prioritaires AS
SELECT
    sp.id AS stage_participant_id,
    sg.id AS stagiaire_id,
    sg.nom,
    sg.prenoms,
    COUNT(a.id) AS nombre_alertes,
    SUM(ta.poids_priorite) AS score_priorite,
    CASE
        WHEN BOOL_OR(a.priorite='URGENT') OR SUM(ta.poids_priorite)>=50 THEN 'URGENTE'
        WHEN BOOL_OR(a.priorite='AVERTISSEMENT') OR SUM(ta.poids_priorite)>=20 THEN 'HAUTE'
        ELSE 'NORMALE'
    END AS priorite,
    STRING_AGG(DISTINCT COALESCE(a.message,ta.libelle),' + ') AS situation,
    STRING_AGG(DISTINCT ta.action_recommandee,' / ')
        FILTER(WHERE ta.action_recommandee IS NOT NULL) AS action_recommandee
FROM alertes a
JOIN types_alerte ta ON ta.id=a.type_alerte_id
LEFT JOIN rapports_activite r ON r.id=a.rapport_id
LEFT JOIN soutenances so ON so.id=a.soutenance_id
JOIN stage_participant sp
ON sp.id=COALESCE(a.stage_participant_id,r.stage_participant_id,so.stage_participant_id)
JOIN stagiaire_academique sa ON sa.id=sp.stagiaire_academique_id
JOIN stagiaires sg ON sg.id=sa.stagiaire_id
WHERE a.statut='A_TRAITER'
GROUP BY sp.id,sg.id,sg.nom,sg.prenoms;

INSERT INTO types_statut(code,libelle) VALUES
('UTILISATEUR','Utilisateur'),
('DEPARTEMENT','Département'),
('STAGIAIRE','Stagiaire'),
('ENCADREUR','Encadreur'),
('PARTICIPATION','Participation au stage'),
('RAPPORT','Rapport d''activité');

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
JOIN types_statut ts ON ts.code=v.type_code;

INSERT INTO roles(code,libelle) VALUES
('ADMIN','Administrateur'),
('RESPONSABLE_STAGE','Responsable des stages'),
('ENCADREUR','Encadreur professionnel'),
('STAGIAIRE','Stagiaire');

INSERT INTO types_piece_jointe(code,libelle,pour_stagiaire,pour_participation,obligatoire_cloture,multiple_autorise) VALUES
('CV','Curriculum Vitae',TRUE,FALSE,FALSE,FALSE),
('CONVENTION_STAGE','Convention de stage',FALSE,TRUE,TRUE,FALSE),
('RAPPORT_FINAL','Rapport final de stage',FALSE,TRUE,TRUE,FALSE),
('ATTESTATION_STAGE','Attestation de stage',FALSE,TRUE,FALSE,FALSE),
('AUTRE','Autre document',TRUE,TRUE,FALSE,TRUE);

INSERT INTO types_evenement_participation(code,libelle) VALUES
('PROLONGATION','Prolongation du stage'),
('SUSPENSION','Suspension du stage'),
('REPRISE','Reprise du stage'),
('ABANDON','Abandon du stage'),
('ANNULATION','Annulation du stage'),
('CLOTURE','Clôture du stage');

INSERT INTO types_alerte(code,libelle,poids_priorite,action_recommandee) VALUES
('FIN_STAGE_PROCHE','Fin de stage proche',10,'Préparer l''évaluation et la clôture du stage'),
('STAGE_DEPASSE','Date de fin dépassée sans clôture',50,'Régulariser la clôture'),
('RAPPORT_MANQUANT','Rapport hebdomadaire manquant',30,'Relancer le stagiaire'),
('VALIDATION_EN_RETARD','Rapport en attente de validation',20,'Relancer l''encadreur'),
('DOSSIER_INCOMPLET','Dossier incomplet',15,'Compléter les éléments manquants du dossier'),
('ENCADREUR_SURCHARGE','Encadreur en surcharge',20,'Réexaminer les affectations de l''encadreur'),
('SOUTENANCE_PROCHE','Soutenance prochaine',25,'Vérifier que le dossier de soutenance est prêt');

INSERT INTO parametres_suivi(id,delai_validation_rapport_jours,capacite_encadreur_par_defaut,taille_max_fichier_octets)
VALUES(1,3,5,10485760);

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
JOIN types_alerte ta ON ta.code=v.type_code;

INSERT INTO criteres_evaluation(code,libelle,ordre) VALUES
('PONCTUALITE','Ponctualité',1),
('AUTONOMIE','Autonomie',2),
('QUALITE_TRAVAIL','Qualité du travail',3),
('COMMUNICATION','Communication',4),
('RESPECT_CONSIGNES','Respect des consignes',5),
('PROGRESSION','Progression',6);

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
JOIN etats cible ON cible.type_code=v.type_code AND cible.code=v.cible_code;

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
JOIN etats cible ON cible.type_code=v.type_code AND cible.code=v.cible_code;

