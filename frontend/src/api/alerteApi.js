import { apiRequest } from './apiClient.js';

export function detecterToutesAlertes(options = {}) {
  return apiRequest('/api/alertes/detection/test-orchestration', {
    ...options,
    method: 'POST'
  });
}

export function listAlertesNonLues(options = {}) {
  return apiRequest('/api/alertes/non-lues', options);
}

export function listAlertes(options = {}) {
  return apiRequest('/api/alertes', options);
}

export function createAlerte(payload) {
  return apiRequest('/api/alertes', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function marquerAlerteLue(id) {
  return apiRequest(`/api/alertes/${id}/lire`, {
    method: 'PATCH'
  });
}

export function marquerToutesAlertesLues() {
  return apiRequest('/api/alertes/tout-lire', {
    method: 'PATCH'
  });
}
