import { apiRequest } from './apiClient.js';

export function listJournalAudit(options = {}) {
  return apiRequest('/api/journal-audit', options);
}
