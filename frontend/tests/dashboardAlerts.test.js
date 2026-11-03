import test from 'node:test';
import assert from 'node:assert/strict';
import { groupDashboardAlerts, summarizePriorityReports } from '../src/utils/dashboardAlerts.js';

const missing = (id, stageParticipantId = 2) => ({ id, stageParticipantId, typeAlerteCode: 'RAPPORT_MANQUANT',
  message: `Rapport manquant participation n° ${stageParticipantId}, semaine ${id}`, priorite: 'AVERTISSEMENT', natureAlerte: 'A_TRAITER' });
const person = (id, stagiaireId = 7) => ({ id, stagiaireId, stagiaireNomComplet: 'Rakoto Jean' });

test('dashboard : neuf semaines donnent une carte, homonymes séparés et autres alertes conservées', () => {
  const alerts = Array.from({ length: 9 }, (_, i) => missing(i + 1));
  alerts[8].priorite = 'URGENT';
  const other = { id: 30, stageParticipantId: 2, typeAlerteCode: 'PIECE_MANQUANTE', message: 'Convention manquante' };
  const cards = groupDashboardAlerts([...alerts, missing(20, 3), other], [person(2), person(3, 8)]);
  assert.equal(cards.length, 3);
  assert.equal(cards[0].title, 'Stagiaire Rakoto Jean');
  assert.equal(cards[0].message, '9 rapports manquants');
  assert.equal(cards[0].priorite, 'URGENT');
  assert.equal(cards[0].path, '/participations/2');
  assert.equal(cards[1].message, '1 rapport manquant');
  assert.notEqual(cards[0].key, cards[1].key);
  assert.equal(cards[2].message, 'Convention manquante');
  assert.equal(alerts[0].message, 'Rapport manquant participation n° 2, semaine 1');
});

test('dashboard : plusieurs participations regroupées par personne, absence de dossier sans perte d’alerte', () => {
  const cards = groupDashboardAlerts([missing(1), missing(2, 3), missing(3, 99)], [person(2), person(3)]);
  assert.equal(cards.length, 2);
  assert.equal(cards[0].message, '2 rapports manquants');
  assert.equal(cards[0].path, '/stagiaires/7');
  assert.equal(cards[1].path, '/alertes?alerte=3');
  assert.equal(groupDashboardAlerts([missing(1), missing(1)], [person(2)])[0].message, '1 rapport manquant');
});

test('priorités : résumé des semaines sans supprimer les autres motifs ni changer le score', () => {
  const alerts = [missing(1), missing(2), { id: 3, stageParticipantId: 2, typeAlerteCode: 'PIECE_MANQUANTE', message: 'Convention manquante' }];
  const rows = [{ participationId: 2, nombreAlertes: 3, score: 80, situation: 'Ancien résumé' }];
  const result = summarizePriorityReports(rows, alerts);
  assert.equal(result[0].situation, '2 rapports manquants · Convention manquante');
  assert.equal(result[0].score, 80);
  assert.equal(rows[0].situation, 'Ancien résumé');
  assert.deepEqual(summarizePriorityReports(rows, alerts.slice(0, 1)), rows);
});
