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

export function listRoles(options = {}) {
  return apiRequest('/api/roles', options);
}

export function listUtilisateurRoles(id, options = {}) {
  return apiRequest(`/api/utilisateurs/${id}/roles`, options);
}

export function addUtilisateurRole(id, payload) {
  return apiRequest(`/api/utilisateurs/${id}/roles`, { method: 'POST', body: JSON.stringify(payload) });
}

export function removeUtilisateurRole(id, code) {
  return apiRequest(`/api/utilisateurs/${id}/roles/${encodeURIComponent(code)}`, { method: 'DELETE' });
}

export function listUtilisateurDepartements(id, options = {}) {
  return apiRequest(`/api/utilisateurs/${id}/departements`, options);
}

export function addUtilisateurDepartement(id, payload) {
  return apiRequest(`/api/utilisateurs/${id}/departements`, { method: 'POST', body: JSON.stringify(payload) });
}

export function removeUtilisateurDepartement(id, departementId) {
  return apiRequest(`/api/utilisateurs/${id}/departements/${departementId}`, { method: 'DELETE' });
}

export function resetUtilisateurPassword(id, payload) {
  return apiRequest(`/api/utilisateurs/${id}/mot-de-passe`, { method: 'PATCH', body: JSON.stringify(payload) });
}
