import { apiRequest } from './apiClient.js';

export function listUtilisateurs() {
  return apiRequest('/api/utilisateurs');
}

export function createUtilisateur(payload) {
  return apiRequest('/api/utilisateurs', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getUtilisateur(id) {
  return apiRequest(`/api/utilisateurs/${id}`);
}
