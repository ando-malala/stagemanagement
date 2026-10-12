import { apiRequest, getStoredToken } from './apiClient.js';

export function listFichiersStagiaire(stagiaireId) {
  return apiRequest(`/api/stagiaires/${stagiaireId}/fichiers`);
}

export function uploadFichierStagiaire(stagiaireId, formData) {
  return apiRequest(`/api/stagiaires/${stagiaireId}/fichiers`, {
    method: 'POST',
    body: formData
  });
}

export function listFichiersParticipation(participationId) {
  return apiRequest(`/api/stage-participants/${participationId}/fichiers`);
}

export function uploadFichierParticipation(participationId, formData) {
  return apiRequest(`/api/stage-participants/${participationId}/fichiers`, {
    method: 'POST',
    body: formData
  });
}

export function getFichierDownloadUrl(id) {
  return `/api/fichiers/${id}/telecharger`;
}

export async function downloadFichier(id) {
  const token = getStoredToken();
  const response = await fetch(getFichierDownloadUrl(id), {
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });

  if (!response.ok) {
    throw new Error(`Téléchargement impossible (${response.status}).`);
  }

  const blob = await response.blob();
  const disposition = response.headers.get('content-disposition') ?? '';
  const match = disposition.match(/filename="(.+)"/);
  const filename = match?.[1] ?? `fichier-${id}`;
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
