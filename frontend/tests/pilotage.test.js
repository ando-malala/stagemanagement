import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { dashboardCards, requireDashboard, filterAlertes, alertFilterOptions, alerteLinks, auditQuery, redactAuditValue } from '../src/utils/pilotage.js';
import { businessPayload, canEditBusiness } from '../src/utils/businessForms.js';
import { referentiels } from '../src/utils/referentiels.js';

test('dashboard : six compteurs du DTO, zéro réel accepté, champ absent ou invalide refusé', () => {
  const source = readFileSync(new URL('../../backend/src/main/java/mg/mef/stagemanagement/dto/dashboard/DashboardResumeResponse.java', import.meta.url), 'utf8');
  const dto = [...source.matchAll(/private long (\w+);/g)].map((match) => match[1]);
  assert.deepEqual(dashboardCards.map(([key]) => key).sort(), dto.sort());
  const values = Object.fromEntries(dto.map((key) => [key, 0]));
  assert.deepEqual(requireDashboard(values), values);
  for (const invalid of [null, {}, { ...values, totalStages: null }, { ...values, totalStages: -1 }, { ...values, totalStages: '4' }]) assert.throws(() => requireDashboard(invalid), /compteurs attendus/);
});

test('alertes : filtres combinés sans confondre nature et priorité, liens selon DTO', () => {
  const rows = [{ id: 1, natureAlerte: 'INFORMATION', priorite: 'URGENT', statut: 'A_TRAITER', typeAlerteCode: 'TYPE_A', typeAlerteLibelle: 'Type A' },
    { id: 2, natureAlerte: 'A_TRAITER', priorite: 'URGENT', statut: 'TRAITEE', typeAlerteCode: 'TYPE_A' }];
  assert.deepEqual(filterAlertes(rows, { priorite: 'URGENT', natureAlerte: 'INFORMATION' }), [rows[0]]);
  assert.deepEqual(filterAlertes(rows, { statut: 'A_TRAITER', natureAlerte: 'A_TRAITER' }), []);
  assert.deepEqual(alertFilterOptions(rows, 'typeAlerteCode'), [{ value: 'TYPE_A', label: 'Type A' }]);
  assert.deepEqual(alerteLinks({ stageParticipantId: 5, rapportId: 7, encadreurId: null, soutenanceId: -1 }), [
    { label: 'Participation', path: '/participations/5' }, { label: 'Rapport', path: '/rapports/7' }
  ]);
});

test('référentiels : valeurs false préservées, contraintes et département sans changement de statut', () => {
  assert.equal(referentiels.length, 7);
  assert.equal(referentiels.filter((item) => item.group === 'academiques').length, 4);
  assert.deepEqual(businessPayload('reference', { libelle: ' Filière ', actif: false }), { libelle: 'Filière', actif: false });
  assert.deepEqual(businessPayload('departement', { libelle: 'Département', statut: 'INACTIF' }), { libelle: 'Département', statutCode: null });
  assert.throws(() => businessPayload('reference', { libelle: 'x'.repeat(151), actif: true }));
  assert.throws(() => businessPayload('structure', { libelle: 'Structure', actif: true }));
  assert.equal(businessPayload('structure', { libelle: 'Structure', departementId: '3', actif: 'false', porte: '' }).porte, null);
  for (const kind of ['reference', 'departement', 'structure', 'cadre']) {
    assert.equal(canEditBusiness({ roles: ['ADMIN'] }, kind), true);
    assert.equal(canEditBusiness({ roles: ['RESPONSABLE_STAGE'] }, kind), false);
  }
});

test('audit : filtres appliqués explicites et identifiants Java valides', () => {
  assert.deepEqual(auditQuery({ mode: 'user', utilisateurId: '3' }), { mode: 'user', utilisateurId: 3 });
  assert.deepEqual(auditQuery({ mode: 'entity', entite: ' RapportActivite ', entiteId: '4' }), { mode: 'entity', entite: 'RapportActivite', entiteId: 4 });
  for (const invalid of ['0', '-1', '1.5', '2147483648', '1e2', '']) assert.throws(() => auditQuery({ mode: 'user', utilisateurId: invalid }));
  assert.throws(() => auditQuery({ mode: 'entity', entite: ' ', entiteId: '4' }));
  assert.deepEqual(auditQuery({ mode: 'all', entite: 'ignored' }), { mode: 'all' });
});

test('audit : masquage récursif des secrets sans modifier les données source', () => {
  const value = { statut: 'ACTIF', mot_de_passe: 'fixture', enfants: [{ accessToken: 'fixture', apiKey: 'fixture', note: 0 }] };
  assert.deepEqual(redactAuditValue(value), { statut: 'ACTIF', mot_de_passe: '[masqué]', enfants: [{ accessToken: '[masqué]', apiKey: '[masqué]', note: 0 }] });
  assert.equal(value.mot_de_passe, 'fixture');
});
