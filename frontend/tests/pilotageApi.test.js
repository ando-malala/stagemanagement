import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { referentiels } from '../src/utils/referentiels.js';
import { listReferentiel, updateReferentiel } from '../src/api/referentielApi.js';
import { listAuditByUtilisateur, listAuditByEntite } from '../src/api/auditApi.js';
import { createAlerte } from '../src/api/alerteApi.js';
import { businessPayload } from '../src/utils/businessForms.js';

test('référentiels : sept PATCH autorisés, corps complets et GET annulables', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const controller = new AbortController();
  const inputs = { reference: { libelle: 'Test', actif: false }, departement: { libelle: 'Test' },
    structure: { libelle: 'Test', departementId: 3, actif: false }, cadre: { themeStage: 'Test', actif: true } };
  for (const item of referentiels) {
    const endpoint = inventory.find((row) => row.path === `${item.endpoint}/{id}` && row.method === 'PATCH');
    assert.ok(endpoint); assert.notEqual(endpoint.access, 'denyAll');
    const payload = businessPayload(item.kind, inputs[item.kind]);
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, `${item.endpoint}/9`); assert.equal(options.method, 'PATCH'); assert.deepEqual(JSON.parse(options.body), payload);
      return Response.json({ id: 9 });
    });
    await updateReferentiel(item.endpoint, 9, payload);
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, item.endpoint); assert.equal(options.signal, controller.signal); return Response.json([]);
    });
    await listReferentiel(item.endpoint, { signal: controller.signal });
  }
});

test('audit : recherche utilisateur/entité, encodage et annulation; alerte : contrat de création', async (context) => {
  const controller = new AbortController();
  for (const [call, path] of [[() => listAuditByUtilisateur(4, { signal: controller.signal }), '/api/journal-audit/utilisateur/4'],
    [() => listAuditByEntite('Entité A/B', 7, { signal: controller.signal }), '/api/journal-audit/entite/Entit%C3%A9%20A%2FB/7']]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path); assert.equal(options.signal, controller.signal); assert.equal(options.method ?? 'GET', 'GET'); return Response.json([]);
    });
    await call();
  }
  const payload = businessPayload('alerte', { typeAlerteCode: 'TYPE_TEST', message: 'Test', priorite: 'URGENT', stageParticipantId: '5' });
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(url, '/api/alertes'); assert.equal(options.method, 'POST'); assert.deepEqual(JSON.parse(options.body), payload); return Response.json({ id: 3 });
  });
  await createAlerte(payload);
});
