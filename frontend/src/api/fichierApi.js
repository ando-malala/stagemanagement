import { apiRequest } from './apiClient.js';

import { downloadFilename } from '../utils/documentUtils.js';

export function listFichiersStagiaire(stagiaireId, options = {}) {
  return apiRequest(`/api/stagiaires/${stagiaireId}/fichiers`, options);
}

export function uploadFichierStagiaire(stagiaireId, formData) {
  return apiRequest(`/api/stagiaires/${stagiaireId}/fichiers`, {
    method: 'POST',
    body: formData
  });
}

export function listFichiersParticipation(participationId, options = {}) {
  return apiRequest(`/api/stage-participants/${participationId}/fichiers`, options);
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

export async function downloadFichier(id, options = {}) {
  return downloadResource(getFichierDownloadUrl(id), id, options);
}

export async function downloadResource(path, fallback, options = {}) {
  const { blob, headers } = await apiRequest(path, { ...options, responseType: 'blob' });
  const disposition = headers.get('content-disposition') ?? '';
  const filename = downloadFilename(disposition, fallback);
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  try {
    link.href = url;
    link.download = filename;
    document.body.append(link);
    link.click();
  } finally {
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  }
}
