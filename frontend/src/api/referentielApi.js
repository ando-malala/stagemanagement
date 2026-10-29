import { apiRequest } from './apiClient.js';

export function listReferentiel(path, options = {}) {
  return apiRequest(path, options);
}

export function createReferentiel(path, payload) {
  return apiRequest(path, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateReferentiel(path, id, payload) {
  return apiRequest(`${path}/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}
