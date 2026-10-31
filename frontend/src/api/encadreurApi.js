import { apiRequest } from './apiClient.js';

export function listEncadreurs(options = {}) {
  return apiRequest('/api/encadreurs', options);
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

export function getEncadreur(id, options = {}) {
  return apiRequest(`/api/encadreurs/${id}`, options);
}

export function changeEncadreurStatus(id, payload) {
  return apiRequest(`/api/encadreurs/${id}/statut`, { method: 'PATCH', body: JSON.stringify(payload) });
}
