import { apiRequest } from './apiClient.js';

export function listEncadreurs() {
  return apiRequest('/api/encadreurs');
}

export function createEncadreur(payload) {
  return apiRequest('/api/encadreurs', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateEncadreur(id, payload) {
  return apiRequest(`/api/encadreurs/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}
