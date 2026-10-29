import { apiRequest } from './apiClient.js';

export function listStagiaires(options = {}) {
  return apiRequest('/api/stagiaires', options);
}

export function createStagiaire(payload) {
  return apiRequest('/api/stagiaires', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateStagiaire(id, payload) {
  return apiRequest(`/api/stagiaires/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}

export function getStagiaire(id, options = {}) {
  return apiRequest(`/api/stagiaires/${id}`, options);
}
