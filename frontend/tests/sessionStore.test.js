import test from 'node:test';
import assert from 'node:assert/strict';
import { createSessionStore } from '../src/context/sessionStore.js';

const profile = { utilisateurId: 7, identifiant: 'compte-test', roles: ['ENCADREUR'] };
function setup(overrides = {}, initialToken = 'opaque-test') {
  let token = initialToken;
  const storage = { get: () => token, set: (value) => { token = value; }, clear: () => { token = null; } };
  const api = { me: async () => profile, login: async () => ({ token: 'new-token' }), logout: async () => {}, ...overrides };
  return { store: createSessionStore({ api, storage }), storage };
}
function deferred() { let resolve; let reject; const promise = new Promise((yes, no) => { resolve = yes; reject = no; }); return { promise, resolve, reject }; }

test('session restaurée par /me, jeton opaque conservé', async () => {
  const { store, storage } = setup();
  assert.equal(store.getSnapshot().status, 'checking');
  await store.restore();
  assert.equal(store.getSnapshot().status, 'authenticated');
  assert.deepEqual(store.getSnapshot().utilisateur, profile);
  assert.equal(storage.get(), 'opaque-test');
});
test('absence de session : aucun appel profil', async () => {
  const { store } = setup({ me: () => assert.fail('appel interdit') }, null);
  await store.restore();
  assert.equal(store.getSnapshot().status, 'anonymous');
});
test('panne réseau : session conservée et reprise possible', async () => {
  let offline = true;
  const { store, storage } = setup({ me: async () => { if (offline) throw new Error('Serveur indisponible'); return profile; } });
  await store.restore();
  assert.equal(store.getSnapshot().status, 'unavailable');
  assert.equal(storage.get(), 'opaque-test');
  offline = false;
  await store.restore();
  assert.equal(store.getSnapshot().status, 'authenticated');
});
for (const status of [401, 403]) test(`profil refusé ${status} : session fermée`, async () => {
  const { store, storage } = setup({ me: async () => { throw Object.assign(new Error('Accès refusé'), { status }); } });
  await store.restore();
  assert.equal(store.getSnapshot().status, 'anonymous');
  assert.equal(storage.get(), null);
});
test('connexion : un seul /me, identité confirmée par le serveur', async () => {
  let calls = 0;
  const { store, storage } = setup({ me: async ({ token }) => { calls++; assert.equal(token, 'new-token'); return profile; } }, null);
  await store.login({ identifiant: 'compte-test', motDePasse: 'test-seulement' });
  assert.equal(calls, 1);
  assert.equal(storage.get(), 'new-token');
  assert.equal(store.getSnapshot().status, 'authenticated');
});
test('échec de connexion ne laisse pas de session authentifiée', async () => {
  const { store, storage } = setup({ login: async () => { throw Object.assign(new Error('Identifiants incorrects'), { status: 401 }); } }, null);
  await assert.rejects(store.login({}), /incorrects/);
  assert.equal(storage.get(), null);
  assert.equal(store.getSnapshot().status, 'anonymous');
});
test('profil malformé bloque les écrans métier', async () => {
  const { store } = setup({ me: async () => ({ roles: ['ADMIN'] }) });
  await store.restore();
  assert.equal(store.getSnapshot().status, 'unavailable');
  assert.equal(store.getSnapshot().utilisateur, null);
});
test('déconnexion immédiate, même si la révocation échoue', async () => {
  const pending = deferred();
  const { store, storage } = setup({ logout: () => pending.promise });
  await store.restore();
  const logout = store.logout();
  assert.equal(store.getSnapshot().status, 'anonymous');
  assert.equal(storage.get(), null);
  pending.reject(new Error('Réseau indisponible'));
  await logout;
  assert.match(store.getSnapshot().erreur, /révocation/);
});
test('réponse tardive de profil après déconnexion ignorée', async () => {
  const pending = deferred();
  const { store } = setup({ me: () => pending.promise });
  const restore = store.restore();
  await store.logout();
  pending.resolve(profile);
  await restore;
  assert.equal(store.getSnapshot().status, 'anonymous');
  assert.equal(store.getSnapshot().utilisateur, null);
});
test('ancien 401 après changement de compte n’efface pas la nouvelle session', async () => {
  const old = deferred();
  let calls = 0;
  const { store, storage } = setup({ me: () => ++calls === 1 ? old.promise : Promise.resolve(profile) });
  const restore = store.restore();
  storage.set('other-session');
  await store.restore();
  old.reject(Object.assign(new Error('Ancienne session expirée'), { status: 401 }));
  await restore;
  assert.equal(store.getSnapshot().status, 'authenticated');
  assert.equal(storage.get(), 'other-session');
});
test('expiration invalide une connexion encore en attente', async () => {
  const pending = deferred();
  const { store, storage } = setup({ login: () => pending.promise }, null);
  const login = store.login({});
  store.expire();
  pending.resolve({ token: 'obsolete' });
  await login;
  assert.equal(store.getSnapshot().status, 'anonymous');
  assert.equal(storage.get(), null);
});
test('nettoyage/remontage StrictMode : ancienne requête annulée, restauration possible', async () => {
  const pending = deferred();
  let signal;
  let calls = 0;
  const { store } = setup({ me: (options) => { if (++calls === 1) { signal = options.signal; return pending.promise; } return Promise.resolve(profile); } });
  const first = store.restore();
  store.stop();
  assert.equal(signal.aborted, true);
  await store.restore();
  pending.resolve({ ...profile, identifiant: 'obsolète' });
  await first;
  assert.equal(store.getSnapshot().utilisateur.identifiant, 'compte-test');
});
