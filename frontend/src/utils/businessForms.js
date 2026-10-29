import { isLocalDate } from './dateUtils.js';

// Fields mirror the Gestion request DTOs. Empty optional values must be sent as
// null on PATCH: these endpoints replace the whole editable resource.
const text = (name, label, maxLength, required = false) => ({ name, label, maxLength, required });
const choice = (name, label, path, labelKeys = ['libelle'], required = true) => ({
  name, label, type: 'select', valueType: 'number', required, path, labelKeys
});
const ref = (group, name) => `/api/referentiels/${group}/${name}`;

export const businessFields = {
  stagiaire: [text('nom', 'Nom', 150, true), text('prenoms', 'Prénoms', 250, true),
    text('adresse', 'Adresse', 250), text('contact', 'Contact', 100), { ...text('email', 'Courriel', 150), type: 'email' }],
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
    if (field.maxLength && value.length > field.maxLength) fieldErrors[field.name] = `Maximum : ${field.maxLength} caractères.`;
    if (field.type === 'date' && !isLocalDate(value)) fieldErrors[field.name] = 'Saisissez une date valide.';
    if (field.pattern && !new RegExp(`^${field.pattern}$`).test(value)) fieldErrors[field.name] = 'Format attendu : 2026-2027.';
    return [field.name, value];
  }));
  if (kind === 'participation' && payload.dateFinStage < payload.dateDebutStage) fieldErrors.dateFinStage = 'La fin ne peut pas précéder le début du stage.';
  if (Object.keys(fieldErrors).length) throw Object.assign(new Error('Vérifiez les champs du formulaire.'), { fieldErrors });
  return payload;
}

export function toOptions(rows, keys) {
  return rows.map((row) => ({ value: row.id, label: `${keys.map((key) => row[key]).filter(Boolean).join(' · ') || 'Sans libellé'} (n° ${row.id})${row.actif === false ? ' — inactif' : ''}` }));
}

export function requireList(value) {
  if (!Array.isArray(value)) throw new Error('Le serveur n’a pas renvoyé la liste attendue.');
  return value;
}
