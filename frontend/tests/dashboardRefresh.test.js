import test from 'node:test';
import assert from 'node:assert/strict';
import { loadDashboard, refreshDashboard } from '../src/api/dashboardApi.js';

const detectionPath = '/api/alertes/detection/test-orchestration';
const readPaths = ['/api/dashboard/resume', '/api/alertes/non-lues', '/api/stage-participants', '/api/dashboard/pilotage'];
const fixtures = count => ({
  '/api/dashboard/resume': { totalStagiaires: 1, totalStages: 1, totalParticipations: 1,
    totalRapports: 0, totalAlertesATraiter: count, totalAlertesTraitees: 0 },
  '/api/alertes/non-lues': Array.from({ length: count }, (_, index) => ({ id: index + 1,
    stageParticipantId: 2, typeAlerteCode: 'RAPPORT_MANQUANT', priorite: 'AVERTISSEMENT',
    statut: 'A_TRAITER', message: 'Rapport manquant' })),
  '/api/stage-participants': [{ id: 2, stagiaireId: 3, stagiaireNomComplet: 'Rakoto Jean' }],
  '/api/dashboard/pilotage': { dateCalcul: '2026-11-11', dossiers: [], charges: [], soutenancesProchaines: [],
    priorites: count ? [{ participationId: 2, nombreAlertes: count }] : [] }
});

test('actualiser attend la détection puis recharge tous les visuels, y compris les alertes disparues', async context => {
  let releaseDetection;
  let count = 2;
  let data = fixtures(0);
  const calls = [];
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    calls.push(url);
    if (url === detectionPath) {
      assert.equal(options.method, 'POST');
      await new Promise(resolve => { releaseDetection = resolve; });
      data = fixtures(count);
      return Response.json({ message: 'Contrôles terminés' });
    }
    assert.ok(readPaths.includes(url));
    return Response.json(data[url]);
  });
  const initial = await loadDashboard();
  assert.equal(initial.cards.length, 0);
  calls.length = 0;
  const pending = refreshDashboard(loadDashboard, true);
  assert.deepEqual(calls, [detectionPath]);
  releaseDetection();
  const refreshed = await pending;
  assert.deepEqual(calls, [detectionPath, ...readPaths]);
  assert.equal(refreshed.resume.totalAlertesATraiter, 2);
  assert.equal(refreshed.rows.length, 2);
  assert.equal(refreshed.cards[0].message, '2 rapports manquants');
  assert.equal(refreshed.pilotage.priorites[0].nombreAlertes, 2);

  count = 0;
  const resolved = refreshDashboard(loadDashboard, true);
  releaseDetection();
  const cleared = await resolved;
  assert.equal(cleared.resume.totalAlertesATraiter, 0);
  assert.deepEqual(cleared.cards, []);
  assert.deepEqual(cleared.pilotage.priorites, []);
});

test('un encadreur recharge les données sans appeler la détection administrateur', async context => {
  const calls = [];
  const data = fixtures(1);
  context.mock.method(globalThis, 'fetch', async url => {
    calls.push(url);
    assert.ok(readPaths.includes(url));
    return Response.json(data[url]);
  });
  assert.equal((await refreshDashboard(loadDashboard, false)).rows.length, 1);
  assert.deepEqual(calls, readPaths);
});

test('une erreur de détection est remontée sans afficher une actualisation réussie', async context => {
  context.mock.method(globalThis, 'fetch', async url => {
    assert.equal(url, detectionPath);
    return Response.json({ message: 'Contrôle indisponible' }, { status: 500 });
  });
  const reload = context.mock.fn();
  await assert.rejects(refreshDashboard(reload, true), /Contrôle indisponible/);
  assert.equal(reload.mock.callCount(), 0);
});

test('toutes les lectures du tableau de bord partagent le signal d’annulation', async context => {
  const signal = new AbortController().signal;
  const data = fixtures(0);
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(options.signal, signal);
    return Response.json(data[url]);
  });
  await loadDashboard({ signal });
});
