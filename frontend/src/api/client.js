import { apiRequest as request } from './apiClient.js';

// Compatibility with older callers; one shared transport.
export function apiRequest(token, path, options = {}) {
  return request(path, { ...options, token, auth: Boolean(token) });
}
