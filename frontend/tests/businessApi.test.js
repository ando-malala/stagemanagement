import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { updateStagiaire, listAcademiques, createAcademique } from '../src/api/stagiaireApi.js';
import { updateEncadreur } from '../src/api/encadreurApi.js';
import { updateStage, updateParticipation, listStageParticipants, applyParticipationEvent, startParticipation } from '../src/api/stageApi.js';

test('édition, profils, participants et événements : contrats HTTP et erreurs serveur', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const cases = [
    [updateStagiaire, '/api/stagiaires/{id}', 'PATCH', { nom: 'N', prenoms: 'P', email: null }],
    [updateEncadreur, '/api/encadreurs/{id}', 'PATCH', { nom: 'N', prenoms: 'P', departementId: 2 }],
    [updateStage, '/api/stages/{id}', 'PATCH', { libelle: 'Stage', structureAccueilId: 3 }],
    [updateParticipation, '/api/stage-participants/{id}', 'PATCH', { stageId: 1, stagiaireAcademiqueId: 2, cadreStageId: 3, encadreurId: 4, dateDebutStage: '2026-10-01', dateFinStage: '2026-11-01' }],
    [createAcademique, '/api/stagiaires/{id}/academiques', 'POST', { niveauEtudeId: 1, filiereId: 2, etablissementId: 3, anneeEtudes: '2026-2027' }],
    [startParticipation, '/api/stage-participants/{id}/statut', 'PATCH', { statutCode: 'EN_COURS', motif: null }],
    ...['suspendre', 'reprendre', 'abandonner', 'annuler', 'prolonger'].map((action) => [
      (id, body) => applyParticipationEvent(id, action, body), `/api/stage-participants/{id}/${action}`, 'POST',
      action === 'prolonger' ? { nouvelleDateFin: '2026-11-01', motif: 'Test' } : { motif: 'Test', ...(['suspendre', 'reprendre'].includes(action) ? { dateEffet: '2026-10-20' } : {}) }
    ])
  ];
  for (const [call, path, method, body] of cases) {
    const endpoint = inventory.find((item) => item.path === path && item.method === method);
    assert.ok(endpoint, path);
    assert.notEqual(endpoint.access, 'denyAll');
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path.replace('{id}', '42'));
      assert.equal(options.method, method);
      assert.deepEqual(JSON.parse(options.body), body);
      return Response.json({ id: 42 });
    });
    await call(42, body);
  }
  const controller = new AbortController();
  for (const [call, path] of [[listAcademiques, '/api/stagiaires/42/academiques'], [listStageParticipants, '/api/stages/42/participants']]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path);
      assert.equal(options.method ?? 'GET', 'GET');
      assert.equal(options.signal, controller.signal);
      return Response.json([]);
    });
    assert.deepEqual(await call(42, { signal: controller.signal }), []);
  }
  context.mock.method(globalThis, 'fetch', async () => Response.json({ detail: 'Chronologie refusée' }, { status: 409 }));
  await assert.rejects(applyParticipationEvent(42, 'suspendre', { dateEffet: '2026-10-20' }), (error) => error.status === 409 && error.message.includes('Chronologie refusée'));
  assert.throws(() => applyParticipationEvent(42, 'cloturer', {}), /non prise en charge/);
});
