import { apiRequest } from './apiClient.js';

export function listJournalAudit(options = {}) {
  return apiRequest('/api/journal-audit', options);
}

export function listAuditByUtilisateur(id, options = {}) {
  return apiRequest(`/api/journal-audit/utilisateur/${id}`, options);
}

export function listAuditByEntite(entite, id, options = {}) {
  return apiRequest(`/api/journal-audit/entite/${encodeURIComponent(entite)}/${id}`, options);
}
