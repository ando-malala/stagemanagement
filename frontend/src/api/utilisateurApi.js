import { apiRequest } from './apiClient.js';

export function listUtilisateurs(options = {}) {
  return apiRequest('/api/utilisateurs', options);
}

export function createUtilisateur(payload) {
  return apiRequest('/api/utilisateurs', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getUtilisateur(id, options = {}) {
  return apiRequest(`/api/utilisateurs/${id}`, options);
}
