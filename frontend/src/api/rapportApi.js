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

export function updateRapport(id, payload) {
  return apiRequest(`/api/rapports-activite/${id}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export function listRapportObservations(id, options = {}) {
  return apiRequest(`/api/rapports-activite/${id}/observations`, options);
}

export function addRapportObservation(id, payload) {
  return apiRequest(`/api/rapports-activite/${id}/observations`, { method: 'POST', body: JSON.stringify(payload) });
}

export function changeRapportStatus(id, payload) {
  return apiRequest(`/api/rapports-activite/${id}/statut`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export function listParticipationRapports(id, options = {}) {
  return apiRequest(`/api/rapports-activite/participation/${id}`, options);
}
