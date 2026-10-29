import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { businessFields, businessPayload, canEditBusiness } from '../src/utils/businessForms.js';
import { evaluationNotePayload, missingRequiredCriteria, rapportStatusPayload, rapportTransitions } from '../src/utils/followUpWorkflows.js';
import { isLocalDateTime } from '../src/utils/dateUtils.js';

test('rapport : mois obligatoire, contenu facultatif et période ordonnée', () => {
  const input = { stageParticipantId: '12', moisStageId: '2', debutSemaine: '2026-10-01', finSemaine: '2026-10-07' };
  assert.deepEqual(businessPayload('rapport', input), { ...input, stageParticipantId: 12, moisStageId: 2, activite: null, difficultes: null, solutions: null });
  assert.throws(() => businessPayload('rapport', { ...input, moisStageId: '' }), (error) => Boolean(error.fieldErrors.moisStageId));
  assert.throws(() => businessPayload('rapport', { ...input, finSemaine: '2026-09-30' }), (error) => Boolean(error.fieldErrors.finSemaine));
});

test('rapport : transitions confirmées par le SQL, pas de sortie de VALIDE', () => {
  const sql = readFileSync(new URL('../../conception/Conception2.sql', import.meta.url), 'utf8');
  const actual = [];
  for (const status of ['BROUILLON', 'SOUMIS', 'A_CORRIGER', 'VALIDE', 'INCONNU']) {
    for (const transition of rapportTransitions(status)) {
      assert.ok(sql.includes(`('RAPPORT','${status}','${transition.code}')`));
      actual.push([status, transition.code]);
    }
  }
  assert.equal(actual.length, 4);
  assert.deepEqual(rapportStatusPayload('SOUMIS', 'A_CORRIGER', ' Explication '), { statutCode: 'A_CORRIGER', motif: 'Explication' });
  assert.deepEqual(rapportStatusPayload('A_CORRIGER', 'SOUMIS', ''), { statutCode: 'SOUMIS', motif: null });
  assert.throws(() => rapportStatusPayload('BROUILLON', 'VALIDE', ''), /Transition indisponible/);
  assert.throws(() => rapportStatusPayload('VALIDE', 'A_CORRIGER', ''), /Transition indisponible/);
});

test('observation : enum Java, commentaire requis et visibilité false conservée', () => {
  const source = readFileSync(new URL('../../backend/src/main/java/mg/mef/stagemanagement/model/enums/TypeObservationRapport.java', import.meta.url), 'utf8');
  for (const { value } of businessFields.observation[0].options) assert.match(source, new RegExp(`\\b${value}\\b`));
  const values = { typeObservation: 'DEMANDE_CORRECTION', commentaire: ' Préciser les activités ', visibleStagiaire: 'false' };
  assert.deepEqual(businessPayload('observation', values), { typeObservation: 'DEMANDE_CORRECTION', commentaire: 'Préciser les activités', visibleStagiaire: false });
  for (const invalid of [{ commentaire: ' ' }, { typeObservation: 'INVENTE' }, { visibleStagiaire: 'invalide' }]) assert.throws(() => businessPayload('observation', { ...values, ...invalid }));
});

test('permissions d’édition : encadreur habilité au suivi, rapports validés et évaluations finales verrouillés', () => {
  const encadreur = { roles: ['ENCADREUR'] };
  for (const kind of ['rapport', 'evaluation', 'observation']) assert.equal(canEditBusiness(encadreur, kind), true);
  for (const kind of ['soutenance', 'participation', 'stage', 'stagiaire', 'inconnu']) assert.equal(canEditBusiness(encadreur, kind), false);
  for (const role of ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR']) {
    assert.equal(canEditBusiness({ roles: [role] }, 'rapport', { statut: 'VALIDE' }), false);
    assert.equal(canEditBusiness({ roles: [role] }, 'evaluation', { estFinalisee: true }), false);
  }
  assert.equal(canEditBusiness({ roles: ['STAGIAIRE'] }, 'rapport'), false);
  assert.equal(canEditBusiness({ roles: ['RESPONSABLE_STAGE'] }, 'soutenance'), true);
});

test('notes : zéro et décimales acceptés, vide et hors bornes refusés', () => {
  for (const note of [0, '0', '12.5', '20']) assert.equal(evaluationNotePayload(2, { note }).note, Number(note));
  for (const note of ['', ' ', null, undefined, '-0.1', '20.01', 'NaN', Infinity]) assert.throws(() => evaluationNotePayload(2, { note }), /entre 0 et 20/);
  assert.deepEqual(evaluationNotePayload(2, { note: '15', commentaire: ' Bien ' }), { critereId: 2, note: 15, commentaire: 'Bien' });
});

test('finalisation : tous les critères actifs obligatoires, y compris une note zéro', () => {
  const criteres = [{ id: 1, obligatoire: true }, { id: 2, obligatoire: false }, { id: 3, obligatoire: true }];
  assert.deepEqual(missingRequiredCriteria(criteres, [{ critereId: 1, note: 0 }, { critereId: 8, note: 18 }]), [criteres[2]]);
  assert.deepEqual(missingRequiredCriteria(criteres, [{ critereId: 1, note: 0 }, { critereId: 3, note: 12 }]), []);
  assert.deepEqual(missingRequiredCriteria([], []), []);
});

test('soutenance : LocalDateTime sans conversion, lieu facultatif et limité à 250 caractères', () => {
  const value = '2026-10-29T09:30:25.123456';
  const payload = businessPayload('soutenance', { stageParticipantId: '7', dateSoutenance: value });
  assert.deepEqual(payload, { stageParticipantId: 7, dateSoutenance: value, lieu: null, observation: null });
  assert.equal(isLocalDateTime('2026-10-29T09:30'), true);
  for (const invalid of ['2026-02-30T09:00', '2026-10-29T24:00', '2026-10-29T09:60', '2026-10-29T09:00:60', '2026-10-29T09:00Z']) assert.equal(isLocalDateTime(invalid), false);
  assert.throws(() => businessPayload('soutenance', { ...payload, lieu: 'a'.repeat(251) }), (error) => Boolean(error.fieldErrors.lieu));
});
