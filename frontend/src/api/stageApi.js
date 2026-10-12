import { apiRequest } from './apiClient.js';

export function listStages() {
  return apiRequest('/api/stages');
}

export function createStage(payload) {
  return apiRequest('/api/stages', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function listParticipations() {
  return apiRequest('/api/stage-participants');
}

export function createParticipation(payload) {
  return apiRequest('/api/stage-participants', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
