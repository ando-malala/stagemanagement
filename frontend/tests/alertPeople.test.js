import test from 'node:test';
import assert from 'node:assert/strict';
import { nameParticipationMessage, nameAlerts } from '../src/utils/alertPeople.js';
import { loadAlertContext } from '../src/api/alertContextApi.js';
import { groupDashboardAlerts, summarizePriorityReports } from '../src/utils/dashboardAlerts.js';

test('alertes existantes : nom ajouté à la référence exacte, sans toucher aux autres dossiers ni au texte enregistré', () => {
  assert.equal(nameParticipationMessage('La participation n°2 arrive à sa fin.', 2, 'Rakoto Jean'),
    'La participation de Rakoto Jean (n° 2) arrive à sa fin.');
  assert.equal(nameParticipationMessage('Dossier incomplet pour la participation n° 2.', 2, 'Rakoto $&'),
    'Dossier incomplet pour la participation de Rakoto $& (n° 2).');
  assert.equal(nameParticipationMessage('Participation n°20', 2, 'Rakoto'), 'Participation n°20');
  const alerts = [{ id: 1, stageParticipantId: 2, message: 'La participation n°2' },
    { id: 2, stageParticipantId: 99, message: 'La participation n°99' }];
  const named = nameAlerts(alerts, [{ id: 2, stagiaireId: 7, stagiaireNomComplet: 'Rakoto Jean' }]);
  assert.equal(named[0].stagiaireNomComplet, 'Rakoto Jean');
  assert.equal(named[0].stagiaireId, 7);
  assert.equal(alerts[0].message, 'La participation n°2');
  assert.deepEqual(named[1], alerts[1]);
});

test('dashboard : toutes les alertes liées affichent le nom, y compris les situations prioritaires', () => {
  const alerts = [{ id: 1, stageParticipantId: 2, typeAlerteCode: 'FIN_STAGE_PROCHE',
    typeAlerteLibelle: 'Fin de stage proche', message: 'La participation n°2 se termine bientôt.' }];
  const cards = groupDashboardAlerts(alerts, [{ id: 2, stagiaireId: 7, stagiaireNomComplet: 'Rakoto Jean' }]);
  assert.equal(cards[0].title, 'Stagiaire Rakoto Jean — Fin de stage proche');
  assert.equal(cards[0].message, 'La participation de Rakoto Jean (n° 2) se termine bientôt.');
  const priorities = [{ participationId: 2, stagiaire: 'Rakoto Jean', situation: alerts[0].message, nombreAlertes: 1 }];
  assert.equal(summarizePriorityReports(priorities)[0].situation, cards[0].message);
});

test('contexte des alertes : deux lectures autorisées, noms des dossiers reçus et annulation propagée', async context => {
  const signal = new AbortController().signal;
  const calls = [];
  context.mock.method(globalThis, 'fetch', async (path, options) => {
    calls.push(path);
    assert.equal(options.signal, signal);
    return Response.json(path === '/api/stage-participants'
      ? [{ id: 2, stagiaireId: 7, stagiaireNomComplet: 'Rakoto Jean' }]
      : [{ id: 1, stageParticipantId: 2, message: 'La participation n°2' }]);
  });
  const result = await loadAlertContext({ signal });
  assert.deepEqual(calls.sort(), ['/api/alertes', '/api/stage-participants']);
  assert.equal(result.rows[0].message, 'La participation de Rakoto Jean (n° 2)');
  calls.length = 0;
  await loadAlertContext({ signal }, true);
  assert.deepEqual(calls.sort(), ['/api/alertes/non-lues', '/api/stage-participants']);
});
