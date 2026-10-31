import test from 'node:test';
import assert from 'node:assert/strict';
import { availableParticipationActions as available, participationEventPayload as payload, todayMadagascar } from '../src/utils/participationEvents.js';

const participation = { id: 1, statut: 'EN_COURS', dateDebutStage: '2026-10-01', dateFinStage: '2026-10-31', dateCloture: null };
const today = '2026-10-20';

// Fixture responses for action availability; production has no fallback matrix.
const fixture = (p) => p.statut === 'PLANIFIEE' ? [{ code: 'statut', statutCible: 'EN_COURS' }, { code: 'prolonger' }, { code: 'annuler' }]
  : p.statut === 'EN_COURS' ? [{ code: 'suspendre', dateEffet: true }, { code: 'prolonger' }, { code: 'abandonner' }, { code: 'annuler' }]
  : p.statut === 'SUSPENDUE' ? [{ code: 'reprendre', dateEffet: true }] : [];
const availableParticipationActions = (p) => available(p, fixture(p));
const participationEventPayload = (p, code, values, today) => payload(p, code, values, today, fixture(p));

test('actions : suspension/reprise selon statut, aucune clôture ni action sur dossier clôturé', () => {
  const actions = (value) => availableParticipationActions(value).map((item) => item.code);
  assert.deepEqual(actions({ ...participation, statut: 'PLANIFIEE' }), ['statut', 'prolonger', 'annuler']);
  assert.ok(actions(participation).includes('suspendre'));
  assert.ok(!actions(participation).includes('reprendre'));
  assert.ok(actions({ ...participation, statut: 'SUSPENDUE' }).includes('reprendre'));
  for (const statut of ['CLOTUREE', 'TERMINEE', 'ANNULEE', 'ABANDONNEE', 'INCONNU']) assert.deepEqual(actions({ ...participation, statut }), []);
  assert.deepEqual(actions({ ...participation, dateCloture: '2026-10-20T12:00:00' }), []);
  assert.throws(() => participationEventPayload(participation, 'cloturer', {}, today), /pas disponible/);
});

test('date d’effet : obligatoire, réelle, dans la période et non future', () => {
  for (const dateEffet of [undefined, '', '2026-02-30', '2026-09-30', '2026-11-01', '2026-10-21']) {
    assert.throws(() => participationEventPayload(participation, 'suspendre', { dateEffet }, today), /date d’effet/);
  }
  assert.deepEqual(participationEventPayload(participation, 'suspendre', { dateEffet: today, motif: ' Motif ' }, today), { dateEffet: today, motif: 'Motif' });
  assert.throws(() => participationEventPayload(participation, 'reprendre', { dateEffet: today }, today), /pas disponible/);
  assert.deepEqual(participationEventPayload({ ...participation, statut: 'SUSPENDUE' }, 'reprendre', { dateEffet: today }, today), { dateEffet: today, motif: null });
});

test('prolongation strictement postérieure; démarrage limité à PLANIFIEE', () => {
  for (const nouvelleDateFin of ['', '2026-10-31', '2026-10-30']) assert.throws(() => participationEventPayload(participation, 'prolonger', { nouvelleDateFin }, today), /postérieure/);
  assert.deepEqual(participationEventPayload(participation, 'prolonger', { nouvelleDateFin: '2026-11-01' }, today), { motif: null, nouvelleDateFin: '2026-11-01' });
  assert.deepEqual(participationEventPayload({ ...participation, statut: 'PLANIFIEE' }, 'statut', {}, today), { motif: null, statutCode: 'EN_COURS' });
  assert.throws(() => participationEventPayload(participation, 'statut', {}, today), /pas disponible/);
});

test('abandon et annulation : ne pas envoyer de date d’effet ignorée par le service', () => {
  assert.deepEqual(participationEventPayload(participation, 'abandonner', { motif: 'Test', dateEffet: today }, today), { motif: 'Test' });
  assert.deepEqual(participationEventPayload(participation, 'annuler', {}, today), { motif: null });
});

test('la date du jour utilise Madagascar y compris au changement de jour UTC', () => {
  assert.equal(todayMadagascar(new Date('2026-10-20T20:59:59Z')), '2026-10-20');
  assert.equal(todayMadagascar(new Date('2026-10-20T21:00:00Z')), '2026-10-21');
});

test('server actions are required; closure is only available when returned', () => {
  assert.deepEqual(available(participation), []);
  assert.deepEqual(payload(participation, 'cloturer', {}, today, [{ code: 'cloturer' }]), { motif: null });
  assert.deepEqual(available(participation, [{ code: 'invented-route' }]), []);
});
