import test from 'node:test';
import assert from 'node:assert/strict';
import { eligibleChoices } from '../src/utils/businessEligibility.js';
import { updateBusinessField, businessPayload, businessFields } from '../src/utils/businessForms.js';
import { referentiels } from '../src/utils/referentiels.js';
import { loadBusinessChoices } from '../src/api/businessChoices.js';
import { evaluationNotePayload } from '../src/utils/followUpWorkflows.js';

test('références : exclure les inactifs, conserver seulement l’association historique courante', () => {
  const rows = [{ id: 1, actif: true }, { id: 2, actif: false }, { id: 3, actif: false }];
  assert.deepEqual(eligibleChoices('stage', 'structureAccueilId', rows), [rows[0]]);
  assert.deepEqual(eligibleChoices('stage', 'structureAccueilId', rows, { structureAccueilId: 2 }), rows.slice(0, 2));
  const mentors = [{ id: 1, statut: 'ACTIF' }, { id: 2, statut: 'INACTIF' }];
  assert.deepEqual(eligibleChoices('participation', 'encadreurId', mentors), [mentors[0]]);
  assert.deepEqual(eligibleChoices('participation', 'encadreurId', mentors, { encadreurId: 2 }), mentors);
  assert.deepEqual(eligibleChoices('participation', 'stageId', [{ id: 1, eligibleNouvelleParticipation: false }]), []);
});
test('évaluation : encadreur affecté uniquement et dossiers terminés exclus', () => {
  const choices = { stageParticipantId: [{ id: 1, encadreurId: 3, statut: 'EN_COURS' }, { id: 2, statut: 'CLOTUREE' }] };
  assert.deepEqual(eligibleChoices('evaluation', 'encadreurId', [{ id: 3 }, { id: 4 }], {}, { stageParticipantId: '1' }, choices), [{ id: 3 }]);
  assert.deepEqual(eligibleChoices('evaluation', 'encadreurId', [{ id: 3 }]), []);
  assert.equal(eligibleChoices('evaluation', 'stageParticipantId', choices.stageParticipantId).length, 1);
  assert.equal(updateBusinessField('evaluation', { encadreurId: 3 }, 'stageParticipantId', '2').encadreurId, '');
});
test('alerte : dossiers compatibles et réinitialisation des sélections dépendantes', () => {
  const rows = [{ id: 1, stageParticipantId: 8 }, { id: 2, stageParticipantId: 9 }];
  assert.deepEqual(eligibleChoices('alerte', 'rapportId', rows, {}, { stageParticipantId: 8 }), [rows[0]]);
  assert.deepEqual(eligibleChoices('alerte', 'soutenanceId', rows, {}, { rapportId: 1 }, { rapportId: rows }), [rows[0]]);
  const values = updateBusinessField('alerte', { rapportId: 1, soutenanceId: 2, encadreurId: 3, message: 'Conserver' }, 'stageParticipantId', '8');
  assert.equal(values.rapportId, ''); assert.equal(values.soutenanceId, ''); assert.equal(values.encadreurId, ''); assert.equal(values.message, 'Conserver');
  assert.throws(() => businessPayload('alerte', { typeAlerteCode: 'DOSSIER_INCOMPLET', message: 'Test', priorite: 'URGENT' }));
});
test('comptes encadreurs : endpoint éligible contextualisé, identifiants réels et liste vide', async context => {
  const paths = [];
  context.mock.method(globalThis, 'fetch', async path => {
    paths.push(path);
    return Response.json(path.includes('comptes-eligibles') ? [{ utilisateurId: 42, identifiant: 'encadreur' }] : []);
  });
  const data = await loadBusinessChoices('encadreur', true, {}, false, 9);
  assert.ok(paths.includes('/api/encadreurs/comptes-eligibles?encadreurId=9'));
  assert.equal(data.utilisateurId[0].id, 42); assert.deepEqual(data.departementId, []);
});
test('notes : refuser l’arrondi silencieux et accepter deux décimales', () => {
  assert.throws(() => evaluationNotePayload(1, { note: '4.123' }));
  assert.equal(evaluationNotePayload(1, { note: '4.25' }).note, 4.25);
});

test('libellés : limites réelles des niveaux et mois, avant envoi à PostgreSQL', () => {
  for (const [id, expected] of [['niveaux', 100], ['mois', 50], ['filieres', 150], ['etablissements', 150]]) {
    const config = referentiels.find(ref => ref.id === id);
    assert.equal(config.maxLabelLength, expected);
    const fields = businessFields.reference.map(field => field.name === 'libelle' ? { ...field, maxLength: config.maxLabelLength } : field);
    assert.equal(businessPayload('reference', { libelle: 'a'.repeat(expected), actif: true }, fields).libelle.length, expected);
    assert.throws(() => businessPayload('reference', { libelle: 'a'.repeat(expected + 1), actif: true }, fields), e => Boolean(e.fieldErrors.libelle));
  }
});
