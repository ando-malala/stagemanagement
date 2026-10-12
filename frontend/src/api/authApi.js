import { apiRequest } from './apiClient.js';

export function login(payload) {
  return apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function me() {
  return apiRequest('/api/auth/me');
}

export function logout() {
  return apiRequest('/api/auth/logout', {
    method: 'POST'
  });
}
