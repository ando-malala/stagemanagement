import { apiRequest } from './apiClient.js';

export function listEvaluations(options = {}) {
  return apiRequest('/api/evaluations', options);
}

export function createEvaluation(payload) {
  return apiRequest('/api/evaluations', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateEvaluation(id, payload) {
  return apiRequest(`/api/evaluations/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}

export function listCriteresEvaluation(options = {}) {
  return apiRequest('/api/evaluations/criteres', options);
}

export function listNotesEvaluation(id, options = {}) {
  return apiRequest(`/api/evaluations/${id}/notes`, options);
}

export function addNoteEvaluation(id, payload) {
  return apiRequest(`/api/evaluations/${id}/notes`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function finaliserEvaluation(id) {
  return apiRequest(`/api/evaluations/${id}/finaliser`, {
    method: 'POST'
  });
}

export function getEvaluation(id, options = {}) {
  return apiRequest(`/api/evaluations/${id}`, options);
}
