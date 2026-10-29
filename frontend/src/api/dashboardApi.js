import { apiRequest } from './apiClient.js';

export function getDashboardResume(options = {}) {
  return apiRequest('/api/dashboard/resume', options);
}
