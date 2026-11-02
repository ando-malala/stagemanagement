import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { createServer } from 'node:http';
import { once } from 'node:events';
import { runApiChecks, validateApiBase } from '../scripts/apiReadiness.js';

const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
const counts = { totalStagiaires: 1, totalStages: 1, totalParticipations: 1, totalRapports: 1, totalAlertesATraiter: 1, totalAlertesTraitees: 0 };
const token = 'test-token-never-reported';

function endpointFor(path) {
  return inventory.find((row) => row.method === 'GET' && row.path === path)
    ?? inventory.find((row) => row.method === 'GET' && new RegExp(`^${row.path.replace(/\{[^}]+\}/g, '[^/]+')}$`).test(path));
}

function serverResponse(path, authorization, roles) {
  const endpoint = endpointFor(path);
  assert.ok(endpoint, `Unknown route ${path}`);
  assert.notEqual(endpoint.access, 'denyAll', path);
  if (!authorization) return [401, {}];
  assert.equal(authorization, `Bearer ${token}`);
  if (endpoint.roles.length && !endpoint.roles.some((role) => roles.includes(role))) return [403, {}];
  if (path === '/api/auth/me') return [200, { utilisateurId: 7, identifiant: 'private-identity', roles }];
  if (path === '/api/dashboard/resume') return [200, counts];
  return [200, endpoint.response.includes('List<') ? [{ id: 7, nom: 'private-name' }] : { id: 7, nom: 'private-name' }];
}

test('contrôle HTTP réel sur serveur local : GET seulement, droits et sous-dossiers conformes à l’inventaire', async (context) => {
  let roles = ['ADMIN'];
  const requests = [];
  const server = createServer((request, response) => {
    requests.push({ method: request.method, path: request.url, authorization: request.headers.authorization });
    const [status, body] = serverResponse(request.url, request.headers.authorization, roles);
    response.writeHead(status, { 'Content-Type': 'application/json' });
    response.end(JSON.stringify(body));
  });
  context.after(() => new Promise((resolve) => { server.close(resolve); server.closeAllConnections(); }));
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const baseUrl = `http://127.0.0.1:${server.address().port}/api`;
  for (const role of ['ADMIN', 'ENCADREUR', 'STAGIAIRE']) {
    roles = [role];
    const start = requests.length;
    const result = await runApiChecks({ baseUrl, token });
    assert.equal(result.scope, 'authenticated');
    assert.equal(result.ok, true, JSON.stringify(result));
    assert.ok(result.checks.some((check) => check.path === '/api/referentiels/academiques/etablissements' && check.status === 200));
    assert.ok(result.checks.some((check) => check.path === '/api/utilisateurs' && check.status === (role === 'ADMIN' ? 200 : 403)));
    assert.ok(result.checks.some((check) => check.path === '/api/dashboard/resume' && check.status === (role === 'STAGIAIRE' ? 403 : 200)));
    if (role !== 'STAGIAIRE') assert.ok(result.checks.some((check) => check.path === '/api/stage-participants/7/fichiers'));
    assert.ok(requests.slice(start).every((request) => request.method === 'GET'));
    assert.ok(requests.slice(start, start + 2).every((request) => !request.authorization));
    assert.doesNotMatch(JSON.stringify(result), /test-token-never-reported|private-identity|private-name/);
  }
});

test('sans jeton : seuls deux contrôles publics, sans prétendre valider les lectures métier', async () => {
  const calls = [];
  const result = await runApiChecks({ baseUrl: 'http://localhost:8080', fetchImpl: async (url, options) => {
    calls.push(url); assert.equal(options.headers.Authorization, undefined); return new Response(null, { status: 401 });
  } });
  assert.equal(result.ok, true);
  assert.equal(result.scope, 'public');
  assert.equal(calls.length, 2);
});

test('une session expirée arrête les lectures authentifiées', async () => {
  const result = await runApiChecks({ baseUrl: 'http://localhost:8080', token,
    fetchImpl: async () => new Response(null, { status: 401 }) });
  assert.equal(result.ok, false);
  assert.equal(result.scope, 'session-invalid');
  assert.equal(result.checks.length, 3);
});

test('réponses malformées : profil, dashboard et listes ne sont pas considérés valides', async () => {
  for (const [brokenPath, body] of [['/api/auth/me', { utilisateurId: 7, roles: 'ADMIN' }],
    ['/api/dashboard/resume', { totalStagiaires: -1 }], ['/api/stagiaires', { data: [] }]]) {
    const result = await runApiChecks({ baseUrl: 'http://localhost:8080', token, fetchImpl: async (url, options) => {
      const path = new URL(url).pathname;
      if (options.headers.Authorization && path === brokenPath) return Response.json(body);
      const [status, data] = serverResponse(path, options.headers.Authorization, ['ADMIN']);
      return Response.json(data, { status });
    } });
    assert.equal(result.ok, false, brokenPath);
    assert.ok(result.checks.some((check) => check.path === brokenPath && !check.ok));
  }
});

test('panne et délai : arrêt immédiat, messages serveur et secrets non recopiés', async () => {
  for (const error of [new Error('sensitive-connection-info'), new DOMException('sensitive-timeout-info', 'TimeoutError')]) {
    const result = await runApiChecks({ baseUrl: 'http://localhost:8080', token, fetchImpl: async () => { throw error; } });
    assert.equal(result.ok, false);
    assert.equal(result.checks.length, 1);
    assert.doesNotMatch(JSON.stringify(result), /sensitive|test-token/);
  }
});

test('la cible accepte une origine ou /api, refuse les identifiants et destinations ambiguës', () => {
  assert.equal(validateApiBase('http://localhost:8080/api/'), 'http://localhost:8080/api');
  for (const value of ['file:///tmp/api', 'http://user:secret@localhost', 'http://localhost?token=secret', 'http://localhost/#secret', 'http://localhost/other', '/api']) {
    assert.throws(() => validateApiBase(value));
  }
});
