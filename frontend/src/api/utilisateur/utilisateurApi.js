import { apiRequest } from '../apiClient.js';

export function listerUtilisateurs(token) {
  return apiRequest('/api/utilisateurs', { token });
}
export function creerUtilisateur(token, { identifiant, motDePasse, roleCode }) {
  return apiRequest('/api/utilisateurs', { token, method: 'POST', body: JSON.stringify({ identifiant, motDePasse, roleCode }) });
}
