import { apiRequest } from './apiClient.js';

export function listSoutenances(options = {}) {
  return apiRequest('/api/soutenances', options);
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

export function getSoutenance(id, options = {}) {
  return apiRequest(`/api/soutenances/${id}`, options);
}
