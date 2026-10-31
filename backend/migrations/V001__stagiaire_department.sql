-- Non-destructive, explicit migration. Existing dossiers are not assigned by guesswork.
BEGIN;
ALTER TABLE stagiaires ADD COLUMN IF NOT EXISTS departement_id INTEGER REFERENCES departements(id);
CREATE INDEX IF NOT EXISTS idx_stagiaires_departement ON stagiaires(departement_id);
COMMIT;
