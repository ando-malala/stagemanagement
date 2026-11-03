import { isLocalDate, isLocalDateTime } from './dateUtils.js';
import { can } from './permissions.js';

// Fields mirror the Gestion request DTOs. Empty optional values must be sent as
// null on PATCH: these endpoints replace the whole editable resource.
const text = (name, label, maxLength, required = false) => ({ name, label, maxLength, required });
const choice = (name, label, path, labelKeys = ['libelle'], required = true) => ({
  name, label, type: 'select', valueType: 'number', required, path, labelKeys
});
const ref = (group, name) => `/api/referentiels/${group}/${name}`;
const participant = () => choice('stageParticipantId', 'Participation', '/api/stage-participants', ['stagiaireNomComplet', 'stage', 'dateDebutStage', 'dateFinStage']);
const paragraph = (name, label, required = false) => ({ name, label, type: 'textarea', required });
const activeField = () => ({ name: 'actif', label: 'Actif', type: 'select', valueType: 'boolean', required: true,
  options: [{ value: 'true', label: 'Oui' }, { value: 'false', label: 'Non' }] });

export const businessFields = {
  reference: [text('libelle', 'Libellé', 150, true), activeField()],
  departement: [text('libelle', 'Libellé', 250, true), { name: 'statutCode', label: 'Statut', type: 'select', path: '/api/referentiels/suivi/statuts/DEPARTEMENT', valueKey: 'code' }],
  structure: [choice('departementId', 'Département', ref('organisation', 'departements')), text('libelle', 'Libellé', 150, true),
    text('localite', 'Localité', 150), text('porte', 'Porte', 30), text('contact', 'Contact', 150), { ...text('courriel', 'Courriel', 150), type: 'email' }, activeField()],
  cadre: [text('themeStage', 'Thème du stage', 250, true), paragraph('activiteConfieStagiaire', 'Activités confiées'), paragraph('objectifStage', 'Objectif'), activeField()],
  alerte: [{ name: 'typeAlerteCode', label: 'Type d’alerte', type: 'select', required: true, path: '/api/referentiels/suivi/types-alertes', valueKey: 'code' }, paragraph('message', 'Message', true),
    { name: 'dateEcheance', label: 'Échéance', type: 'date' }, { name: 'priorite', label: 'Priorité', type: 'select', required: true,
      options: ['INFORMATION', 'AVERTISSEMENT', 'URGENT'].map((value) => ({ value, label: value })) },
    { ...participant(), required: false }, choice('rapportId', 'Rapport', '/api/rapports-activite', ['stagiaire', 'debutSemaine', 'finSemaine'], false),
    choice('encadreurId', 'Encadreur', '/api/encadreurs', ['nom', 'prenoms'], false),
    choice('soutenanceId', 'Soutenance', '/api/soutenances', ['stagiaire', 'dateSoutenance'], false), text('cleDeduplication', 'Clé de déduplication facultative')],
  rapport: [participant(), { name: 'debutSemaine', label: 'Début de période', type: 'date', required: true },
    { name: 'finSemaine', label: 'Fin de période', type: 'date', required: true }, paragraph('activite', 'Activités'),
    paragraph('difficultes', 'Difficultés'), paragraph('solutions', 'Solutions'), choice('moisStageId', 'Mois de stage', ref('academiques', 'mois-stage'))],
  evaluation: [participant(), choice('encadreurId', 'Encadreur évaluateur', '/api/encadreurs', ['nom', 'prenoms', 'departement']),
    paragraph('appreciationGlobale', 'Appréciation globale'), paragraph('observationsFinales', 'Observations finales')],
  soutenance: [participant(), { name: 'dateSoutenance', label: 'Date et heure de soutenance', type: 'datetime-local', step: 'any', required: true },
    text('lieu', 'Lieu', 250), paragraph('observation', 'Observation')],
  observation: [{ name: 'typeObservation', label: 'Type d’observation', type: 'select', required: true,
    options: [{ value: 'OBSERVATION', label: 'Observation' }, { value: 'DEMANDE_CORRECTION', label: 'Demande de correction' }, { value: 'VALIDATION', label: 'Validation' }] },
    paragraph('commentaire', 'Commentaire', true), { name: 'visibleStagiaire', label: 'Visibilité stagiaire', type: 'select', valueType: 'boolean', required: true,
      options: [{ value: 'true', label: 'Visible' }, { value: 'false', label: 'Non visible' }] }],
  stagiaire: [text('nom', 'Nom', 150, true), text('prenoms', 'Prénoms', 250, true),
    text('adresse', 'Adresse', 250, true), text('contact', 'Contact', 100), { ...text('email', 'Courriel', 150), type: 'email' }],
  encadreur: [choice('utilisateurId', 'Compte associé', '/api/utilisateurs', ['identifiant'], false),
    text('nom', 'Nom', 250, true), text('prenoms', 'Prénoms', 250, true), text('fonction', 'Fonction', 150),
    text('localite', 'Localité', 150), text('porte', 'Porte', 30), text('contact', 'Contact', 150),
    { ...text('courriel', 'Courriel', 150), type: 'email' },
    choice('departementId', 'Département', ref('organisation', 'departements')),
    { name: 'capaciteMaxStagiaires', label: 'Capacité maximale déclarée', type: 'number', min: 1, max: 2147483647, step: 1 }],
  stage: [text('libelle', 'Libellé', 150, true), choice('structureAccueilId', 'Structure d’accueil', ref('organisation', 'structures-accueil'), ['libelle', 'departement'])],
  academique: [choice('niveauEtudeId', 'Niveau d’étude', ref('academiques', 'niveaux-etude')),
    choice('filiereId', 'Filière', ref('academiques', 'filieres')),
    choice('etablissementId', 'Établissement', ref('academiques', 'etablissements')),
    { name: 'anneeEtudes', label: 'Année d’études', required: true, pattern: '[0-9]{4}-[0-9]{4}', placeholder: '2026-2027', maxLength: 9 }],
  participation: [choice('stageId', 'Stage', '/api/stages', ['libelle', 'structureAccueil']),
    choice('stagiaireAcademiqueId', 'Profil académique', null, ['niveauEtude', 'filiere', 'etablissement', 'anneeEtudes']),
    choice('cadreStageId', 'Cadre de stage', ref('organisation', 'cadres-stage'), ['themeStage']),
    choice('encadreurId', 'Encadreur', '/api/encadreurs', ['nom', 'prenoms', 'departement']),
    { name: 'dateDebutStage', label: 'Début du stage', type: 'date', required: true },
    { name: 'dateFinStage', label: 'Fin du stage', type: 'date', required: true }]
};

export function updateBusinessField(kind, values, name, value) {
  const next = { ...values, [name]: value };
  if (kind === 'rapport' && name === 'stageParticipantId' && String(value) !== String(values.stageParticipantId)) {
    next.debutSemaine = '';
    next.finSemaine = '';
  }
  return next;
}

export function businessPayload(kind, values) {
  const fieldErrors = {};
  const payload = Object.fromEntries(businessFields[kind].map((field) => {
    const raw = values[field.name];
    const value = typeof raw === 'string' ? raw.trim() : raw;
    if (value == null || value === '') {
      if (field.required) fieldErrors[field.name] = 'Ce champ est obligatoire.';
      return [field.name, null];
    }
    if (field.type === 'number' || field.valueType === 'number') {
      const number = Number(value);
      if (!Number.isInteger(number) || number < 1 || number > 2147483647) fieldErrors[field.name] = 'Saisissez un entier positif valide.';
      return [field.name, number];
    }
    if (field.valueType === 'boolean') {
      if (![true, false, 'true', 'false'].includes(value)) fieldErrors[field.name] = 'Choisissez une valeur valide.';
      return [field.name, value === true || value === 'true'];
    }
    if (field.options && !field.options.some((option) => option.value === value)) fieldErrors[field.name] = 'Choisissez une valeur valide.';
    if (field.maxLength && value.length > field.maxLength) fieldErrors[field.name] = `Maximum : ${field.maxLength} caractères.`;
    if (field.type === 'date' && !isLocalDate(value)) fieldErrors[field.name] = 'Saisissez une date valide.';
    if (field.type === 'datetime-local' && !isLocalDateTime(value)) fieldErrors[field.name] = 'Saisissez une date et une heure valides.';
    if (field.pattern && !new RegExp(`^${field.pattern}$`).test(value)) fieldErrors[field.name] = 'Format attendu : 2026-2027.';
    return [field.name, value];
  }));
  if (kind === 'participation' && payload.dateFinStage < payload.dateDebutStage) fieldErrors.dateFinStage = 'La fin ne peut pas précéder le début du stage.';
  if (kind === 'rapport' && payload.finSemaine < payload.debutSemaine) fieldErrors.finSemaine = 'La fin ne peut pas précéder le début de période.';
  if (Object.keys(fieldErrors).length) throw Object.assign(new Error('Vérifiez les champs du formulaire.'), { fieldErrors });
  return payload;
}

export function canEditBusiness(user, kind, initial) {
  const permissions = { stagiaire: 'gererStagiaires', academique: 'gererStagiaires', encadreur: 'gererEncadreurs', stage: 'gererStages',
    reference: 'modifierReferentiels', departement: 'modifierReferentiels', structure: 'modifierReferentiels', cadre: 'modifierReferentiels', alerte: 'creerAlertes',
    participation: 'gererStages', rapport: 'gererRapports', observation: 'validerRapports', evaluation: 'gererEvaluations', soutenance: 'gererSoutenances' };
  if (kind === 'participation' && (initial?.dateCloture || ['CLOTUREE', 'ANNULEE', 'ABANDONNEE'].includes(initial?.statut))) return false;
  if (kind === 'rapport' && initial?.id && (initial.peutRediger !== true || !['BROUILLON', 'A_CORRIGER'].includes(initial.statut))) return false;
  return can(user, permissions[kind]) && !(kind === 'rapport' && initial?.statut === 'VALIDE') && !(kind === 'evaluation' && initial?.estFinalisee);
}

export function toOptions(rows, keys) {
  return rows.map((row) => ({ value: row.id, label: `${keys.map((key) => row[key]).filter(Boolean).join(' · ') || 'Sans libellé'} (n° ${row.id})${row.actif === false ? ' — inactif' : ''}` }));
}

export function requireList(value) {
  if (!Array.isArray(value)) throw new Error('Le serveur n’a pas renvoyé la liste attendue.');
  return value;
}
