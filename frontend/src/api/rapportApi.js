import { apiRequest } from './apiClient.js';

export function listRapports(options = {}) {
  return apiRequest('/api/rapports-activite', options);
}

export function createRapport(payload) {
  return apiRequest('/api/rapports-activite', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getRapport(id, options = {}) {
  return apiRequest(`/api/rapports-activite/${id}`, options);
}
