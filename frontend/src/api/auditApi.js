import { apiRequest } from './apiClient.js';

export function listJournalAudit() {
  return apiRequest('/api/journal-audit');
}
