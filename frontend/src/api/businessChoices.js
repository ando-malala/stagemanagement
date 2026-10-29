import { apiRequest } from './apiClient.js';
import { businessFields, requireList } from '../utils/businessForms.js';

export async function loadBusinessChoices(kind, isAdmin, options = {}) {
  const sources = businessFields[kind].filter((field) => field.path && (field.name !== 'utilisateurId' || isAdmin));
  if (kind === 'participation') sources.push({ name: 'stagiaireId', path: '/api/stagiaires' });
  return Object.fromEntries(await Promise.all(sources.map(async (field) => [field.name, requireList(await apiRequest(field.path, options))])));
}
