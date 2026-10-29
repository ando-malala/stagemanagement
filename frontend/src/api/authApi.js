import { apiRequest } from './apiClient.js';

export function login(payload, options = {}) {
  return apiRequest('/api/auth/login', {
    ...options,
    method: 'POST',
    auth: false,
    body: JSON.stringify(payload)
  });
}

export function me(options = {}) {
  return apiRequest('/api/auth/me', options);
}

export function logout(options = {}) {
  return apiRequest('/api/auth/logout', {
    ...options,
    method: 'POST'
  });
}
