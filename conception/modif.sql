BEGIN;

ALTER TABLE alertes
DROP CONSTRAINT alertes_statut_check;

ALTER TABLE alertes
ADD CONSTRAINT alertes_statut_check
CHECK (
    statut IN (
        'A_TRAITER',
        'TRAITEE',
        'IGNOREE',
        'EXPIREE'
    )
);

COMMIT;