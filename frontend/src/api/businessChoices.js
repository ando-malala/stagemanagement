import { apiRequest } from './apiClient.js';
import { businessFields, requireList } from '../utils/businessForms.js';

export async function loadBusinessChoices(kind, isAdmin, options = {}, fixedReport = false) {
  const sources = businessFields[kind].filter((field) => field.path && (field.name !== 'utilisateurId' || isAdmin)
    && !(kind === 'rapport' && fixedReport && field.name === 'stageParticipantId'));
  if (kind === 'participation') sources.push({ name: 'stagiaireId', path: '/api/stagiaires' });
  return Object.fromEntries(await Promise.all(sources.map(async (field) => {
    const rows = requireList(await apiRequest(field.path, options));
    return [field.name, kind === 'rapport' && field.name === 'stageParticipantId' ? rows.filter(row => row.peutRedigerRapport === true) : rows];
  })));
}
