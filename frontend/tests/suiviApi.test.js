import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import * as suivi from '../src/api/suiviApi.js';
import { applyParticipationEvent } from '../src/api/stageApi.js';
import { changeEncadreurStatus } from '../src/api/encadreurApi.js';

test('API de suivi : routes réelles, permissions métier et annulation des lectures', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const signal = new AbortController().signal;
  const calls = [
    [() => suivi.listTypesDocuments({ signal }), '/api/referentiels/suivi/types-documents'],
    [() => suivi.listStatuts('ENCADREUR', { signal }), '/api/referentiels/suivi/statuts/ENCADREUR', '/api/referentiels/suivi/statuts/{type}'],
    [() => suivi.listTransitions('RAPPORT', 'SOUMIS', { signal }), '/api/referentiels/suivi/transitions/RAPPORT/SOUMIS', '/api/referentiels/suivi/transitions/{type}/{source}'],
    ...[['listDocumentsObligatoires', 'documents-obligatoires'], ['listHistoriqueStatuts', 'historique-statuts'],
      ['listEvenements', 'evenements'], ['listPeriodesRapports', 'periodes-rapports'], ['listActionsParticipation', 'actions']]
      .map(([method, path]) => [() => suivi[method](7, { signal }), `/api/stage-participants/7/${path}`, `/api/stage-participants/{id}/${path}`]),
    [() => suivi.getChargeEncadreur(7, { signal }), '/api/encadreurs/7/charge', '/api/encadreurs/{id}/charge']
  ];
  for (const [call, path, template = path] of calls) {
    const endpoint = inventory.find((r) => r.method === 'GET' && r.path === template);
    assert.ok(endpoint, template);
    assert.deepEqual(endpoint.roles, ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR', 'STAGIAIRE']);
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path); assert.equal(options.signal, signal); assert.equal(options.method ?? 'GET', 'GET');
      return Response.json(path.endsWith('/charge') ? { capaciteMax: 5, participationsActives: 2, surcharge: false } : []);
    });
    await call();
  }
});

test('clôture et statut encadreur : mutations existantes et refus serveur conservé', async (context) => {
  for (const [call, path, method] of [
    [() => applyParticipationEvent(7, 'cloturer', { motif: 'Dossier complet' }), '/api/stage-participants/7/cloturer', 'POST'],
    [() => changeEncadreurStatus(7, { statutCode: 'INACTIF', motif: null }), '/api/encadreurs/7/statut', 'PATCH']
  ]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path); assert.equal(options.method, method);
      return Response.json({ detail: 'Opération refusée' }, { status: 409 });
    });
    await assert.rejects(call, (error) => error.status === 409 && error.message === 'Opération refusée');
  }
});

test('un catalogue malformé ne devient pas une liste vide', async (context) => {
  context.mock.method(globalThis, 'fetch', async () => Response.json({ data: [] }));
  await assert.rejects(() => suivi.listTypesDocuments());
});
