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
