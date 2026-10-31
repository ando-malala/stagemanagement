import test from 'node:test';
import assert from 'node:assert/strict';
import { can, isTraineeOnly } from '../src/utils/permissions.js';
import { canEditBusiness } from '../src/utils/businessForms.js';
import { matchRoute, visibleNavigation } from '../src/routes/routeConfig.js';

const trainee = { roles: ['STAGIAIRE'] };
test('portail : le stagiaire peut rediger mais ne peut ni valider ni administrer', () => {
  assert.equal(can(trainee, 'gererRapports'), true);
  for (const action of ['validerRapports', 'gererUtilisateurs', 'gererStages', 'gererEvaluations', 'televerserDocuments', 'traiterAlertes'])
    assert.equal(can(trainee, action), false, action);
  for (const statut of ['BROUILLON', 'A_CORRIGER']) assert.equal(canEditBusiness(trainee, 'rapport', { id: 1, statut }), true);
  for (const statut of ['SOUMIS', 'VALIDE']) assert.equal(canEditBusiness(trainee, 'rapport', { id: 1, statut }), false);
  assert.equal(canEditBusiness(trainee, 'observation'), false);
});
test('portail : navigation personnelle et reutilisation des fiches protegees', () => {
  assert.deepEqual(visibleNavigation(trainee).map(r => r.path), ['/mon-dossier', '/rapports', '/fichiers']);
  for (const path of ['/mon-dossier', '/stagiaires/1', '/participations/1', '/rapports/1'])
    assert.ok(matchRoute(path).roles.includes('STAGIAIRE'));
  assert.equal(matchRoute('/utilisateurs').roles.includes('STAGIAIRE'), false);
  assert.equal(isTraineeOnly({ roles: ['ADMIN', 'STAGIAIRE'] }), false);
});
