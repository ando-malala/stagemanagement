import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { accountPayload, resetPasswordPayload, activeRoleOptions, requireRoleCodes, canRemoveRole } from '../src/utils/utilisateurForms.js';
import { createUtilisateur, listRoles, listUtilisateurRoles, addUtilisateurRole, removeUtilisateurRole,
  listUtilisateurDepartements, addUtilisateurDepartement, removeUtilisateurDepartement, resetUtilisateurPassword } from '../src/api/utilisateurApi.js';

const roles = [{ code: 'ROLE_TEST', libelle: 'Rôle de test', actif: true }, { code: 'INACTIF', libelle: 'Inactif', actif: false }];
test('comptes : identifiant normalisé, mot de passe intact et rôle actif issu du serveur', () => {
  const values = { identifiant: ' compte-test ', motDePasse: ' secret-test ', confirmation: ' secret-test ', roleCode: 'ROLE_TEST' };
  assert.deepEqual(accountPayload(values, roles), { identifiant: 'compte-test', motDePasse: ' secret-test ', roleCode: 'ROLE_TEST' });
  assert.deepEqual(activeRoleOptions(roles), [{ value: 'ROLE_TEST', label: 'Rôle de test (ROLE_TEST)' }]);
  for (const invalid of [{ identifiant: ' ' }, { identifiant: 'a'.repeat(51) }, { motDePasse: 'court' }, { confirmation: 'different' }, { roleCode: 'INACTIF' }, { roleCode: 'INCONNU' }]) {
    assert.throws(() => accountPayload({ ...values, ...invalid }, roles));
  }
});

test('réinitialisation : minimum 12, confirmation et absence de normalisation du secret', () => {
  const password = ' test-password ';
  assert.deepEqual(resetPasswordPayload(password, password), { nouveauMotDePasse: password });
  assert.throws(() => resetPasswordPayload('a'.repeat(11), 'a'.repeat(11)), /12 caractères/);
  assert.throws(() => resetPasswordPayload(password, 'different'), /correspondent pas/);
});

test('attributions : réponse de rôles validée et dernier rôle conservé', () => {
  assert.deepEqual(requireRoleCodes(['ROLE_TEST']), ['ROLE_TEST']);
  assert.throws(() => requireRoleCodes([{ code: 'ROLE_TEST' }]));
  assert.equal(canRemoveRole(['ADMIN'], 'ADMIN'), false);
  assert.equal(canRemoveRole(['ADMIN', 'ENCADREUR'], 'ADMIN'), true);
  assert.equal(canRemoveRole(['ADMIN', 'ENCADREUR'], 'ABSENT'), false);
});

test('administration : contrats des mutations et erreur 403 conservée', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const cases = [
    [() => createUtilisateur({ identifiant: 'test', motDePasse: 'fixture-password', roleCode: 'ROLE_TEST' }), '/api/utilisateurs', '/api/utilisateurs', 'POST', { identifiant: 'test', motDePasse: 'fixture-password', roleCode: 'ROLE_TEST' }, 'CreerUtilisateurRequest'],
    [() => addUtilisateurRole(42, { roleCode: 'ROLE_TEST' }), '/api/utilisateurs/{id}/roles', '/api/utilisateurs/42/roles', 'POST', { roleCode: 'ROLE_TEST' }, 'AjouterRoleUtilisateurRequest'],
    [() => removeUtilisateurRole(42, 'ROLE TEST/A'), '/api/utilisateurs/{id}/roles/{roleCode}', '/api/utilisateurs/42/roles/ROLE%20TEST%2FA', 'DELETE', undefined],
    [() => addUtilisateurDepartement(42, { departementId: 7 }), '/api/utilisateurs/{id}/departements', '/api/utilisateurs/42/departements', 'POST', { departementId: 7 }, 'AjouterDepartementUtilisateurRequest'],
    [() => removeUtilisateurDepartement(42, 7), '/api/utilisateurs/{id}/departements/{departementId}', '/api/utilisateurs/42/departements/7', 'DELETE', undefined],
    [() => resetUtilisateurPassword(42, { nouveauMotDePasse: 'fixture-password' }), '/api/utilisateurs/{id}/mot-de-passe', '/api/utilisateurs/42/mot-de-passe', 'PATCH', { nouveauMotDePasse: 'fixture-password' }, 'ReinitialiserMotDePasseRequest']
  ];
  for (const [call, path, expectedUrl, method, payload, dto] of cases) {
    const endpoint = inventory.find((item) => item.path === path && item.method === method);
    assert.ok(endpoint); assert.notEqual(endpoint.access, 'denyAll');
    if (dto) {
      const source = readFileSync(new URL(`../../backend/src/main/java/mg/mef/stagemanagement/dto/utilisateur/${dto}.java`, import.meta.url), 'utf8');
      assert.deepEqual([...source.matchAll(/private \w+ (\w+);/g)].map((match) => match[1]).sort(), Object.keys(payload).sort());
    }
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, expectedUrl); assert.equal(options.method, method);
      assert.deepEqual(options.body ? JSON.parse(options.body) : undefined, payload);
      return method === 'PATCH' ? new Response(null, { status: 204 }) : Response.json([]);
    });
    await call();
  }
  context.mock.method(globalThis, 'fetch', async () => new Response('', { status: 403 }));
  await assert.rejects(addUtilisateurRole(42, { roleCode: 'ROLE_TEST' }), (error) => error.status === 403);
});

test('administration : lectures annulables sans envoi de champs sensibles', async (context) => {
  const controller = new AbortController();
  for (const [call, path] of [[(options) => listRoles(options), '/api/roles'], [(options) => listUtilisateurRoles(42, options), '/api/utilisateurs/42/roles'], [(options) => listUtilisateurDepartements(42, options), '/api/utilisateurs/42/departements']]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path); assert.equal(options.signal, controller.signal); assert.equal(options.body, undefined);
      return Response.json([]);
    });
    assert.deepEqual(await call({ signal: controller.signal }), []);
  }
});
