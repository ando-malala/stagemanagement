import { apiRequest } from './apiClient.js';

export function listRapports() {
  return apiRequest('/api/rapports-activite');
}

export function createRapport(payload) {
  return apiRequest('/api/rapports-activite', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
