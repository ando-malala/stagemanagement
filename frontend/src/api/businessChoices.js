import { apiRequest } from './apiClient.js';
import { businessFields, requireList } from '../utils/businessForms.js';

export async function loadBusinessChoices(kind, isAdmin, options = {}, fixedReport = false, currentId) {
  const sources = businessFields[kind].filter((field) => field.path && (field.name !== 'utilisateurId' || isAdmin)
    && !(kind === 'rapport' && fixedReport && field.name === 'stageParticipantId'));
  if (kind === 'participation') sources.push({ name: 'stagiaireId', path: '/api/stagiaires' });
  return Object.fromEntries(await Promise.all(sources.map(async (field) => {
    const rows = requireList(await apiRequest(field.path, kind === 'encadreur' && field.name === 'utilisateurId'
      ? { ...options, query: { encadreurId: currentId } } : options));
    if (kind === 'encadreur' && field.name === 'utilisateurId') return [field.name, rows.map(row => ({ ...row, id: row.utilisateurId }))];
    return [field.name, kind === 'rapport' && field.name === 'stageParticipantId' ? rows.filter(row => row.peutRedigerRapport === true) : rows];
  })));
}
