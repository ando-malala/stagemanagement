import { apiRequest } from './apiClient.js';

export function listEvaluations() {
  return apiRequest('/api/evaluations');
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

export function listCriteresEvaluation() {
  return apiRequest('/api/evaluations/criteres');
}

export function listNotesEvaluation(id) {
  return apiRequest(`/api/evaluations/${id}/notes`);
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
