import { apiRequest } from './apiClient.js';

export function listAlertesNonLues() {
  return apiRequest('/api/alertes/non-lues');
}

export function listAlertes() {
  return apiRequest('/api/alertes');
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
