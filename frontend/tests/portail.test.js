import test from 'node:test';
import assert from 'node:assert/strict';
import { can, canActOnReport, isTraineeOnly } from '../src/utils/permissions.js';
import { canEditBusiness } from '../src/utils/businessForms.js';
import { matchRoute, visibleNavigation } from '../src/routes/routeConfig.js';

const trainee = { roles: ['STAGIAIRE'] };
test('portail : le stagiaire peut rediger mais ne peut ni valider ni administrer', () => {
  assert.equal(can(trainee, 'gererRapports'), true);
  for (const action of ['validerRapports', 'gererUtilisateurs', 'gererStages', 'gererEvaluations', 'televerserDocuments', 'traiterAlertes'])
    assert.equal(can(trainee, action), false, action);
  for (const statut of ['BROUILLON', 'A_CORRIGER']) assert.equal(canEditBusiness(trainee, 'rapport', { id: 1, statut, peutRediger: true }), true);
  for (const statut of ['SOUMIS', 'VALIDE']) assert.equal(canEditBusiness(trainee, 'rapport', { id: 1, statut }), false);
  assert.equal(canEditBusiness(trainee, 'observation'), false);
});

test('rapports : rédaction et soumission au stagiaire, examen à son encadreur, refus par défaut', () => {
  const mentor = { roles: ['ENCADREUR'] };
  const admin = { roles: ['ADMIN'] };
  const draft = { id: 1, statut: 'BROUILLON', peutRediger: true, peutExaminer: false };
  assert.equal(canActOnReport(trainee, draft, 'SOUMIS'), true);
  for (const user of [mentor, admin]) {
    assert.equal(can(user, 'gererRapports'), false);
    assert.equal(canEditBusiness(user, 'rapport', draft), false);
    assert.equal(canActOnReport(user, draft, 'SOUMIS'), false);
  }
  const submitted = { ...draft, statut: 'SOUMIS', peutRediger: false, peutExaminer: true };
  for (const action of ['VALIDE', 'A_CORRIGER']) {
    assert.equal(canActOnReport(mentor, submitted, action), true);
    for (const user of [trainee, admin]) assert.equal(canActOnReport(user, submitted, action), false);
    assert.equal(canActOnReport(mentor, { ...submitted, peutExaminer: false }, action), false);
  }
  assert.equal(canEditBusiness(trainee, 'rapport', { ...draft, peutRediger: false }), false);
  assert.equal(canActOnReport(trainee, { id: 1, statut: 'BROUILLON' }, 'SOUMIS'), false);
  assert.equal(canActOnReport({ roles: ['ADMIN', 'STAGIAIRE'] }, { ...draft, peutRediger: false }, 'SOUMIS'), false);
});
test('portail : navigation personnelle et reutilisation des fiches protegees', () => {
  assert.deepEqual(visibleNavigation(trainee).map(r => r.path), ['/mon-dossier', '/rapports', '/fichiers']);
  for (const path of ['/mon-dossier', '/stagiaires/1', '/participations/1', '/rapports/1'])
    assert.ok(matchRoute(path).roles.includes('STAGIAIRE'));
  assert.equal(matchRoute('/utilisateurs').roles.includes('STAGIAIRE'), false);
  assert.equal(isTraineeOnly({ roles: ['ADMIN', 'STAGIAIRE'] }), false);
});
