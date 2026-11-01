import { apiRequest } from './apiClient.js';
import { downloadResource } from './fichierApi.js';

export async function getDossier(id, options = {}) {
  const data = await apiRequest(`/api/stagiaires/${id}/dossier`, options);
  if (!data?.stagiaire?.id || !['academiques', 'fichiers', 'participations'].every(key => Array.isArray(data[key]))) throw new Error('Dossier reçu dans un format inattendu.');
  return data;
}
export async function getPilotage(options = {}) {
  const data = await apiRequest('/api/dashboard/pilotage', options);
  if (!data?.dateCalcul || !['dossiers', 'priorites', 'charges', 'soutenancesProchaines'].every(key => Array.isArray(data[key]))) throw new Error('Pilotage reçu dans un format inattendu.');
  return data;
}
export const downloadBilan = (id) => downloadResource(`/api/stage-participants/${id}/bilan.pdf`, `bilan-${id}.pdf`);
const exports = new Set(['participations', 'statistiques', 'alertes']);
export function downloadExport(type) {
  if (!exports.has(type)) throw new Error('Export inconnu.');
  return downloadResource(`/api/exports/${type}.csv`, `${type}.csv`);
}
