import { apiRequest } from './apiClient.js';

export function listStages(options = {}) {
  return apiRequest('/api/stages', options);
}

export function createStage(payload) {
  return apiRequest('/api/stages', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function listParticipations(options = {}) {
  return apiRequest('/api/stage-participants', options);
}

export function createParticipation(payload) {
  return apiRequest('/api/stage-participants', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getStage(id, options = {}) {
  return apiRequest(`/api/stages/${id}`, options);
}

export function getParticipation(id, options = {}) {
  return apiRequest(`/api/stage-participants/${id}`, options);
}

export function updateStage(id, payload) {
  return apiRequest(`/api/stages/${id}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export function updateParticipation(id, payload) {
  return apiRequest(`/api/stage-participants/${id}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export function listStageParticipants(id, options = {}) {
  return apiRequest(`/api/stages/${id}/participants`, options);
}

const eventActions = ['prolonger', 'suspendre', 'reprendre', 'abandonner', 'annuler'];
export function applyParticipationEvent(id, action, payload) {
  if (!eventActions.includes(action)) throw new Error('Action de participation non prise en charge.');
  return apiRequest(`/api/stage-participants/${id}/${action}`, { method: 'POST', body: JSON.stringify(payload) });
}

export function startParticipation(id, payload) {
  return apiRequest(`/api/stage-participants/${id}/statut`, { method: 'PATCH', body: JSON.stringify(payload) });
}
