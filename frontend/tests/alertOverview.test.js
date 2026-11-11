import test from 'node:test';
import assert from 'node:assert/strict';
import { alertLabel, alertMetrics, notificationCount, sameAlertFilters, sortAlerts, summarizeAlerts } from '../src/utils/alertOverview.js';
import { filterAlertes } from '../src/utils/pilotage.js';
import { canTreatAlert } from '../src/utils/permissions.js';

const row = (id, fields = {}) => Object.freeze({ id, statut: 'A_TRAITER', priorite: 'AVERTISSEMENT',
  natureAlerte: 'A_TRAITER', typeAlerteCode: 'RAPPORT_MANQUANT', typeAlerteLibelle: 'Rapport manquant', ...fields });

test('synthèse : totaux exacts, types du serveur, priorité et nature indépendantes', () => {
  const rows = Object.freeze([
    ...Array.from({ length: 12 }, (_, i) => row(i + 1)),
    row(13, { priorite: 'URGENT', natureAlerte: 'INFORMATION', typeAlerteCode: 'FIN_STAGE_PROCHE', typeAlerteLibelle: 'Fin de stage proche' }),
    row(14, { priorite: 'INFORMATION', natureAlerte: 'A_TRAITER' }),
    row(15, { statut: 'TRAITEE', priorite: 'URGENT' }),
    row(16, { statut: 'EXPIREE', typeAlerteCode: 'TYPE_PERSONNALISE', typeAlerteLibelle: 'Type personnalisé' })
  ]);
  const summary = summarizeAlerts(rows);
  assert.deepEqual(summary.counts, { pending: 14, urgent: 1, warning: 12, information: 1 });
  assert.deepEqual(summary.types.map(type => [type.code, type.count]), [
    ['RAPPORT_MANQUANT', 13], ['FIN_STAGE_PROCHE', 1], ['TYPE_PERSONNALISE', 0]
  ]);
  for (const metric of alertMetrics) assert.equal(filterAlertes(rows, metric.filters).length, summary.counts[metric.key]);
  for (const type of summary.types) assert.equal(filterAlertes(rows, { statut: 'A_TRAITER', typeAlerteCode: type.code }).length, type.count);
  assert.equal(canTreatAlert({ roles: ['ADMIN'] }, rows[12]), false);
  assert.equal(canTreatAlert({ roles: ['ADMIN'] }, rows[13]), true);
  assert.equal(notificationCount(summary.counts.pending), '9+');
  assert.equal(summary.counts.pending, 14);
  assert.equal(filterAlertes(rows, {}).length, 16);
});

test('tri : urgence avant échéance, retards en premier, dates absentes en dernier, données intactes', () => {
  const rows = Object.freeze([
    row(1, { dateEcheance: '2026-01-01' }),
    row(2, { priorite: 'URGENT', dateEcheance: null }),
    row(3, { priorite: 'URGENT', dateEcheance: '2026-11-20' }),
    row(4, { priorite: 'URGENT', dateEcheance: '2026-11-01' }),
    row(5, { priorite: 'INFORMATION', dateEcheance: '2026-01-01' }),
    row(6, { priorite: 'INCONNUE', dateEcheance: '2026-01-01' }),
    row(7, { priorite: 'URGENT', dateEcheance: 'invalide' })
  ]);
  assert.deepEqual(sortAlerts(rows).map(alert => alert.id), [4, 3, 2, 7, 1, 5, 6]);
  assert.deepEqual(rows.map(alert => alert.id), [1, 2, 3, 4, 5, 6, 7]);
});

test('sélections, libellés, badges et états vides', () => {
  assert.deepEqual(summarizeAlerts([]), { counts: { pending: 0, urgent: 0, warning: 0, information: 0 }, types: [] });
  assert.equal(sameAlertFilters({ statut: '' }, {}), true);
  assert.equal(sameAlertFilters({ statut: 'A_TRAITER', priorite: 'URGENT' }, { statut: 'A_TRAITER' }), false);
  assert.equal(alertLabel('natureAlerte', 'A_TRAITER'), 'Action requise');
  assert.equal(alertLabel('statut', 'A_TRAITER'), 'À traiter');
  assert.equal(alertLabel('priorite', 'INFORMATION'), 'Information');
  assert.equal(alertLabel('natureAlerte', 'INFORMATION'), 'Informative');
  assert.equal(alertLabel('priorite', '__proto__'), '__proto__');
  assert.deepEqual([0, 1, 9, 10, 140].map(notificationCount), ['0', '1', '9', '9+', '9+']);
});
