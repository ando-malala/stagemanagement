import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { updateRapport, listRapportObservations, addRapportObservation, changeRapportStatus, listParticipationRapports } from '../src/api/rapportApi.js';
import { updateEvaluation, listNotesEvaluation, addNoteEvaluation, finaliserEvaluation } from '../src/api/evaluationApi.js';
import { updateSoutenance } from '../src/api/soutenanceApi.js';

test('suivi métier : endpoints autorisés, corps exacts, signaux et conflits', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const cases = [
    [updateRapport, '/api/rapports-activite/{id}', 'PATCH', { stageParticipantId: 2, moisStageId: 1, debutSemaine: '2026-10-01', finSemaine: '2026-10-07', activite: null, difficultes: null, solutions: null }],
    [addRapportObservation, '/api/rapports-activite/{id}/observations', 'POST', { typeObservation: 'OBSERVATION', commentaire: 'Test', visibleStagiaire: false }],
    [changeRapportStatus, '/api/rapports-activite/{id}/statut', 'PATCH', { statutCode: 'SOUMIS', motif: null }],
    [updateEvaluation, '/api/evaluations/{id}', 'PATCH', { stageParticipantId: 2, encadreurId: 3, appreciationGlobale: null, observationsFinales: null }],
    [addNoteEvaluation, '/api/evaluations/{id}/notes', 'POST', { critereId: 4, note: 0, commentaire: null }],
    [finaliserEvaluation, '/api/evaluations/{id}/finaliser', 'POST', undefined],
    [updateSoutenance, '/api/soutenances/{id}', 'PATCH', { stageParticipantId: 2, dateSoutenance: '2026-10-29T09:30', lieu: null, observation: null }]
  ];
  for (const [call, path, method, body] of cases) {
    const endpoint = inventory.find((item) => item.method === method && item.path === path);
    assert.ok(endpoint);
    assert.notEqual(endpoint.access, 'denyAll');
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path.replace('{id}', '9'));
      assert.equal(options.method, method);
      assert.deepEqual(options.body ? JSON.parse(options.body) : undefined, body);
      return Response.json({ id: 9 });
    });
    await call(9, body);
  }
  const controller = new AbortController();
  for (const [call, path] of [[listRapportObservations, '/api/rapports-activite/9/observations'], [listNotesEvaluation, '/api/evaluations/9/notes'], [listParticipationRapports, '/api/rapports-activite/participation/9']]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path);
      assert.equal(options.signal, controller.signal);
      return Response.json([]);
    });
    assert.deepEqual(await call(9, { signal: controller.signal }), []);
  }
  context.mock.method(globalThis, 'fetch', async () => Response.json({ detail: 'Évaluation déjà finalisée' }, { status: 409 }));
  await assert.rejects(addNoteEvaluation(9, { critereId: 4, note: 10 }), (error) => error.status === 409 && error.message.includes('déjà finalisée'));
});
