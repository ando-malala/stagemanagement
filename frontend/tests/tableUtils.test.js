import test from 'node:test';
import assert from 'node:assert/strict';
import { selectTableRows } from '../src/utils/tableUtils.js';
import { updateBusinessField } from '../src/utils/businessForms.js';

const columns = [{ key: 'nom' }, { key: 'stage' }, { key: 'statut' }, { key: 'score' }];
const rows = [
  { id: 1, nom: 'Élodie', stage: 'Budget', statut: 'EN_COURS', score: 10 },
  { id: 2, nom: 'Ando', stage: 'Budget', statut: 'CLOTUREE', score: 2 },
  { id: 3, nom: 'Élodie', stage: 'Archives', statut: 'EN_COURS', score: null }
];

test('une recherche vide affiche toutes les lignes, sans modifier leur ordre', () => {
  for (const query of ['', '   ', null]) assert.deepEqual(selectTableRows(rows, columns, query), rows);
  assert.deepEqual(selectTableRows([], columns), []);
});

test('la recherche ignore les accents et combine mots et filtres', () => {
  assert.deepEqual(selectTableRows(rows, columns, 'ELODIE budget', { statut: 'EN_COURS' }).map(r => r.id), [1]);
  assert.deepEqual(selectTableRows(rows, columns, 'budget', { statut: 'CLOTUREE' }).map(r => r.id), [2]);
  assert.deepEqual(selectTableRows(rows, columns, 'absent'), []);
});

test('le tri numérique conserve les valeurs absentes en fin et ne modifie pas les données source', () => {
  assert.deepEqual(selectTableRows(rows, columns, '', {}, { key: 'score', direction: 'asc' }).map(r => r.id), [2, 1, 3]);
  assert.deepEqual(selectTableRows(rows, columns, '', {}, { key: 'score', direction: 'desc' }).map(r => r.id), [1, 2, 3]);
  assert.deepEqual(rows.map(r => r.id), [1, 2, 3]);
});

test('changer de participation efface les dates du rapport mais conserve le texte saisi', () => {
  const values = { stageParticipantId: 1, debutSemaine: '2026-11-02', finSemaine: '2026-11-08', activite: 'Travail saisi' };
  assert.deepEqual(updateBusinessField('rapport', values, 'stageParticipantId', '2'), {
    ...values, stageParticipantId: '2', debutSemaine: '', finSemaine: ''
  });
  assert.equal(updateBusinessField('rapport', values, 'stageParticipantId', '1').debutSemaine, values.debutSemaine);
  assert.equal(updateBusinessField('rapport', values, 'activite', 'Correction').debutSemaine, values.debutSemaine);
  assert.equal(values.stageParticipantId, 1);
});
