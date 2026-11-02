import { buildApiUrl } from '../src/api/apiClient.js';
import { requireDashboard } from '../src/utils/pilotage.js';

export function validateApiBase(value) {
  const url = new URL(value);
  if (!['http:', 'https:'].includes(url.protocol) || url.username || url.password || url.search || url.hash || !['', '/', '/api', '/api/'].includes(url.pathname)) {
    throw new Error('MEF_API_URL doit être une origine HTTP(S), éventuellement suivie de /api, sans identifiants ni paramètres.');
  }
  return url.href.replace(/\/$/, '');
}

const businessRoles = ['ADMIN', 'ENCADREUR'];
const businessLists = ['/api/stagiaires', '/api/encadreurs', '/api/stages', '/api/stage-participants',
  '/api/rapports-activite', '/api/evaluations', '/api/soutenances', '/api/alertes', '/api/alertes/non-lues', '/api/evaluations/criteres'];
const referenceLists = ['/api/referentiels/academiques/etablissements', '/api/referentiels/academiques/filieres',
  '/api/referentiels/academiques/niveaux-etude', '/api/referentiels/academiques/mois-stage',
  '/api/referentiels/organisation/departements', '/api/referentiels/organisation/structures-accueil', '/api/referentiels/organisation/cadres-stage'];
const adminLists = ['/api/utilisateurs', '/api/roles', '/api/journal-audit'];
const record = (value) => value && typeof value === 'object' && !Array.isArray(value) && Number.isInteger(value.id) && value.id > 0;

// Only GETs. No login/session creation, writes, downloads or scheduler invocation.
// Reports contain paths/statuses/counts, never tokens, personal records or error bodies.
export async function runApiChecks({ baseUrl, token, fetchImpl = fetch, timeoutMs = 5000 }) {
  const base = validateApiBase(baseUrl);
  const checks = [];
  async function check(path, expected, authorization, validator) {
    const result = { path, expected, status: null, ok: false };
    checks.push(result);
    try {
      const response = await fetchImpl(buildApiUrl(path, base), {
        method: 'GET', redirect: 'error', signal: AbortSignal.timeout(timeoutMs),
        headers: { Accept: 'application/json', ...(authorization ? { Authorization: `Bearer ${authorization}` } : {}) }
      });
      result.status = response.status;
      if (response.status !== expected) { result.reason = 'Statut HTTP inattendu'; return; }
      if (expected !== 200) { result.ok = true; return; }
      if (!(response.headers.get('content-type') ?? '').includes('json')) { result.reason = 'Réponse JSON attendue'; return; }
      const data = await response.json();
      if (validator && !validator(data)) { result.reason = 'Contrat de réponse inattendu'; return; }
      result.ok = true;
      if (Array.isArray(data)) result.items = data.length;
      return data;
    } catch (error) {
      result.reason = error.name === 'TimeoutError' || error.name === 'AbortError' ? 'Délai dépassé' : 'Connexion ou réponse invalide';
    }
  }
  const report = (scope) => ({ scope, ok: checks.every((item) => item.ok), checks });
  await check('/api/auth/me', 401);
  // Stop when there is no reachable server, rather than sending the full suite.
  if (checks[0].status == null) return report('public');
  await check('/api/dashboard/resume', 401);
  if (!token) return report('public');
  const profile = await check('/api/auth/me', 200, token, (value) => value && Number.isInteger(value.utilisateurId)
    && value.utilisateurId > 0 && typeof value.identifiant === 'string' && Array.isArray(value.roles) && value.roles.every((role) => typeof role === 'string'));
  if (!profile) return report('session-invalid');
  const business = profile.roles.some((role) => businessRoles.includes(role));
  const personalLists = ['/api/stagiaires', '/api/encadreurs', '/api/stages', '/api/stage-participants', '/api/rapports-activite'];
  const personal = profile.roles.includes('STAGIAIRE');
  const admin = profile.roles.includes('ADMIN');
  await check('/api/dashboard/resume', business ? 200 : 403, token, (data) => { requireDashboard(data); return true; });
  const samples = new Map();
  for (const path of [...businessLists, ...referenceLists, ...adminLists]) {
    const expected = adminLists.includes(path) ? (admin ? 200 : 403) : businessLists.includes(path) ? (business || (personal && personalLists.includes(path)) ? 200 : 403) : 200;
    const rows = await check(path, expected, token, Array.isArray);
    if (rows?.length && record(rows[0])) samples.set(path, rows[0].id);
  }
  for (const path of [...businessLists.slice(0, 7), '/api/utilisateurs']) {
    const id = samples.get(path);
    if (id) await check(`${path}/${id}`, 200, token, (data) => record(data) && data.id === id);
  }
  const related = [
    ['/api/stagiaires', (id) => [`/api/stagiaires/${id}/academiques`, `/api/stagiaires/${id}/fichiers`]],
    ['/api/stages', (id) => [`/api/stages/${id}/participants`]],
    ['/api/stage-participants', (id) => [`/api/stage-participants/${id}/fichiers`, `/api/rapports-activite/participation/${id}`]],
    ['/api/rapports-activite', (id) => [`/api/rapports-activite/${id}/observations`]],
    ['/api/evaluations', (id) => [`/api/evaluations/${id}/notes`]],
    ['/api/utilisateurs', (id) => [`/api/utilisateurs/${id}/roles`, `/api/utilisateurs/${id}/departements`]]
  ];
  for (const [source, paths] of related) {
    const id = samples.get(source);
    if (id) for (const path of paths(id)) await check(path, 200, token, Array.isArray);
  }
  return report('authenticated');
}
