import { apiRequest } from './apiClient.js';

export function getDashboardResume() {
  return apiRequest('/api/dashboard/resume');
}
