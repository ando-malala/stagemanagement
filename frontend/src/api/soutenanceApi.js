import { apiRequest } from './apiClient.js';

export function listSoutenances() {
  return apiRequest('/api/soutenances');
}

export function createSoutenance(payload) {
  return apiRequest('/api/soutenances', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateSoutenance(id, payload) {
  return apiRequest(`/api/soutenances/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}
