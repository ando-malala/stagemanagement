import { apiRequest } from './apiClient.js';

export function listStagiaires() {
  return apiRequest('/api/stagiaires');
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
