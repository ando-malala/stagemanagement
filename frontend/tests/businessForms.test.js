import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { businessFields, businessPayload, toOptions } from '../src/utils/businessForms.js';
import { loadBusinessChoices } from '../src/api/businessChoices.js';

test('formulaires métier : tous les champs correspondent exactement aux DTO de requête', () => {
  const contracts = { stagiaire: 'stagiaire/StagiaireRequest', encadreur: 'encadreur/EncadreurRequest',
    stage: 'stage/StageRequest', academique: 'stagiaire/StagiaireAcademiqueRequest', participation: 'stage/StageParticipantRequest',
    rapport: 'rapport/RapportActiviteRequest', observation: 'rapport/RapportObservationRequest',
    evaluation: 'evaluation/EvaluationFinaleRequest', soutenance: 'soutenance/SoutenanceRequest', alerte: 'alerte/AlerteRequest',
    reference: 'referentiel/ReferentielRequest', departement: 'referentiel/DepartementRequest', structure: 'referentiel/StructureAccueilRequest', cadre: 'referentiel/CadreStageRequest' };
  for (const [kind, dto] of Object.entries(contracts)) {
    const source = readFileSync(new URL(`../../backend/src/main/java/mg/mef/stagemanagement/dto/${dto}.java`, import.meta.url), 'utf8');
    const names = [...source.matchAll(/private \w+ (\w+);/g)].map((match) => match[1]);
    assert.deepEqual(businessFields[kind].map((field) => field.name).sort(), names.sort());
  }
});

test('édition complète : effacement explicite des champs optionnels et exclusion des champs de réponse', () => {
  assert.deepEqual(businessPayload('stagiaire', { id: 44, nom: ' Nom ', prenoms: ' Prénom ', adresse: ' Adresse ', email: ' ', statut: 'ACTIF' }), {
    nom: 'Nom', prenoms: 'Prénom', adresse: 'Adresse', contact: null, email: null, departementId: null
  });
  const updated = businessPayload('encadreur', { nom: 'Nom', prenoms: 'Prénom', utilisateurId: 17, departementId: '2', capaciteMaxStagiaires: '' });
  assert.equal(updated.utilisateurId, 17); // Preserved even when the account selector is hidden.
  assert.equal(updated.departementId, 2);
  assert.equal(updated.capaciteMaxStagiaires, null);
});

test('validations métier : texte obligatoire, limites, entiers, année académique et dates', () => {
  assert.throws(() => businessPayload('stagiaire', { nom: ' ', prenoms: 'P' }), (e) => Boolean(e.fieldErrors.nom));
  assert.throws(() => businessPayload('stagiaire', { nom: 'N'.repeat(151), prenoms: 'P' }), (e) => Boolean(e.fieldErrors.nom));
  for (const id of ['0', '-1', '1.5', 'NaN', '2147483648']) {
    assert.throws(() => businessPayload('stage', { libelle: 'Stage', structureAccueilId: id }), (e) => Boolean(e.fieldErrors.structureAccueilId));
  }
  const profile = { niveauEtudeId: 1, filiereId: 2, etablissementId: 3, anneeEtudes: '2026-2027' };
  assert.deepEqual(businessPayload('academique', profile), profile);
  assert.throws(() => businessPayload('academique', { ...profile, anneeEtudes: '2026/2027' }), (e) => Boolean(e.fieldErrors.anneeEtudes));
  const participation = { stageId: 1, stagiaireAcademiqueId: 2, cadreStageId: 3, encadreurId: 4, dateDebutStage: '2026-10-01', dateFinStage: '2026-10-01', stagiaireId: 55 };
  assert.equal(businessPayload('participation', participation).stagiaireId, undefined);
  assert.throws(() => businessPayload('participation', { ...participation, dateFinStage: '2026-09-30' }), (e) => Boolean(e.fieldErrors.dateFinStage));
  assert.throws(() => businessPayload('participation', { ...participation, dateDebutStage: '2026-02-30' }), (e) => Boolean(e.fieldErrors.dateDebutStage));
});

test('choix : droits ADMIN pour les comptes, références réelles et annulation propagée', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const calls = [];
  const controller = new AbortController();
  context.mock.method(globalThis, 'fetch', async (path, options) => {
    calls.push(path);
    assert.equal(options.signal, controller.signal);
    const endpoint = inventory.find((item) => item.path === path && item.method === 'GET');
    assert.ok(endpoint, path);
    assert.notEqual(endpoint.access, 'denyAll');
    return Response.json([{ id: 7, libelle: 'Référence test' }]);
  });
  const options = { signal: controller.signal };
  await loadBusinessChoices('encadreur', false, options);
  assert.deepEqual(calls, ['/api/referentiels/organisation/departements']);
  calls.length = 0;
  await loadBusinessChoices('encadreur', true, options);
  assert.ok(calls.includes('/api/utilisateurs'));
  calls.length = 0;
  for (const kind of ['stagiaire', 'stage', 'academique', 'participation', 'rapport', 'evaluation', 'soutenance', 'observation']) await loadBusinessChoices(kind, false, options);
  assert.ok(calls.includes('/api/stagiaires'));
  assert.ok(calls.includes('/api/referentiels/academiques/niveaux-etude'));
  assert.ok(!calls.some((path) => path.includes('/academiques/') && !path.includes('/referentiels/'))); // No N+1 profile loading.
});

test('choix : réponse inattendue ou erreur réseau ne devient pas une liste vide', async (context) => {
  context.mock.method(globalThis, 'fetch', async () => Response.json({ data: [] }));
  await assert.rejects(loadBusinessChoices('stage', false), /liste attendue/);
  globalThis.fetch = async () => { throw new TypeError('offline'); };
  await assert.rejects(loadBusinessChoices('stage', false));
});

test('libellés des choix : homonymes distingués et inactivité explicite', () => {
  assert.deepEqual(toOptions([{ id: 8, nom: 'Nom', prenoms: 'Prénom', actif: false }], ['nom', 'prenoms']), [
    { value: 8, label: 'Nom · Prénom (n° 8) — inactif' }
  ]);
});
