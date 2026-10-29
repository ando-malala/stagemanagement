import test from 'node:test';
import assert from 'node:assert/strict';
import { ApiError, buildApiUrl, createApiClient } from '../src/api/apiClient.js';

test('URL configurable sans doublon /api et paramètres encodés', () => {
  assert.equal(buildApiUrl('/api/stagiaires', 'http://localhost:8080/api/'), 'http://localhost:8080/api/stagiaires');
  assert.equal(buildApiUrl('/api/stagiaires', '', { q: 'Ando & Cie', actif: false, page: 0, absent: null }), '/api/stagiaires?q=Ando+%26+Cie&actif=false&page=0');
  assert.throws(() => buildApiUrl('https://outside.example/api/users'));
});

test('JSON brut, Bearer opaque, GET sans Content-Type inutile', async () => {
  const request = createApiClient({ getToken: () => 'opaque-test-token', fetchImpl: async (url, options) => {
    assert.equal(url, '/api/stagiaires');
    assert.equal(options.headers.get('Authorization'), 'Bearer opaque-test-token');
    assert.equal(options.headers.has('Content-Type'), false);
    return Response.json([{ id: 12 }]);
  } });
  assert.deepEqual(await request('/api/stagiaires'), [{ id: 12 }]);
});

test('connexion publique : aucun token envoyé et aucun effacement sur 401', async () => {
  let expired = false;
  const request = createApiClient({ getToken: () => 'old', onUnauthorized: () => { expired = true; }, fetchImpl: async (_, options) => {
    assert.equal(options.headers.has('Authorization'), false);
    assert.equal(options.headers.get('Content-Type'), 'application/json');
    return new Response('', { status: 401 });
  } });
  await assert.rejects(request('/api/auth/login', { method: 'POST', body: '{}', auth: false }), { status: 401 });
  assert.equal(expired, false);
});

test('401 sur ressource protégée signale la session concernée', async () => {
  let expired;
  const request = createApiClient({ getToken: () => 'old', onUnauthorized: (token) => { expired = token; }, fetchImpl: async () => new Response('', { status: 401 }) });
  await assert.rejects(request('/api/stages'), { status: 401 });
  assert.equal(expired, 'old');
});

for (const status of [400, 403, 404, 409, 413, 422, 500]) {
  test(`erreur ${status} lisible, HTML non présenté comme message`, async () => {
    const request = createApiClient({ onUnauthorized: () => assert.fail('pas de déconnexion'), fetchImpl: async () => new Response('<html>proxy</html>', { status, headers: { 'content-type': 'text/html' } }) });
    await assert.rejects(request('/api/stages'), (error) => error instanceof ApiError && error.status === status && !error.message.includes('<html>'));
  });
}

test('ProblemDetail et erreurs de champs restent accessibles', async () => {
  const request = createApiClient({ fetchImpl: async () => Response.json({ detail: 'Date invalide', errors: [{ field: 'dateEffet', defaultMessage: 'Date hors période' }] }, { status: 400, headers: { 'content-type': 'application/problem+json' } }) });
  await assert.rejects(request('/api/stage-participants/1/suspendre'), (error) => error.message === 'Date invalide' && error.fieldErrors.dateEffet === 'Date hors période');
});

test('multipart conserve le fichier et laisse le navigateur définir la boundary', async () => {
  const data = new FormData();
  data.append('typePieceJointeCode', 'CV');
  data.append('fichier', new Blob(['document']), 'cv.txt');
  const request = createApiClient({ fetchImpl: async (_, options) => {
    assert.equal(options.body, data);
    assert.equal(options.headers.has('Content-Type'), false);
    return Response.json({ id: 5 });
  } });
  assert.deepEqual(await request('/api/stagiaires/1/fichiers', { method: 'POST', body: data, headers: { 'Content-Type': 'application/json' } }), { id: 5 });
});

test('téléchargement binaire authentifié et nom retourné par le serveur', async () => {
  const request = createApiClient({ getToken: () => 'opaque', fetchImpl: async (_, options) => {
    assert.equal(options.headers.get('Authorization'), 'Bearer opaque');
    return new Response('contenu', { headers: { 'content-disposition': 'attachment; filename="rapport.pdf"' } });
  } });
  const result = await request('/api/fichiers/1/telecharger', { responseType: 'blob' });
  assert.equal(await result.blob.text(), 'contenu');
  assert.match(result.headers.get('content-disposition'), /rapport.pdf/);
});

test('204 et réponse vide ne déclenchent pas de parsing JSON', async () => {
  const request = createApiClient({ fetchImpl: async () => new Response(null, { status: 204 }) });
  assert.equal(await request('/api/auth/logout', { method: 'POST' }), null);
});

test('annulation conservée et erreur réseau explicite', async () => {
  const controller = new AbortController();
  controller.abort();
  const cancelled = createApiClient({ fetchImpl: async (_, options) => { options.signal.throwIfAborted(); } });
  await assert.rejects(cancelled('/api/stages', { signal: controller.signal }), { name: 'AbortError' });
  const offline = createApiClient({ fetchImpl: async () => { throw new TypeError('fetch failed'); } });
  await assert.rejects(offline('/api/stages'), (error) => error.status === 0 && /Connexion au serveur impossible/.test(error.message));
});

test('JSON de succès malformé : erreur explicite, aucune fausse donnée', async () => {
  const request = createApiClient({ fetchImpl: async () => new Response('{broken', { headers: { 'content-type': 'application/json' } }) });
  await assert.rejects(request('/api/stages'), /JSON illisible/);
});
