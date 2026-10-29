import { apiRequest } from '../apiClient.js';

export function login(identifiant, motDePasse) {
  return apiRequest('/api/auth/login', { method: 'POST', auth: false, body: JSON.stringify({ identifiant, motDePasse }) });
}
export function me(token) {
  return apiRequest('/api/auth/me', { token });
}
export function logout(token) {
  return apiRequest('/api/auth/logout', { method: 'POST', token });
}
