import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { login } from '../src/api/authApi.js';
import { createStagiaire } from '../src/api/stagiaireApi.js';
import { createParticipation } from '../src/api/stageApi.js';
import { createRapport } from '../src/api/rapportApi.js';
import { createSoutenance } from '../src/api/soutenanceApi.js';
import { uploadFichierParticipation } from '../src/api/fichierApi.js';
import { marquerAlerteLue } from '../src/api/alerteApi.js';

test('services de domaines : méthodes, chemins et corps vérifiés contre les contrôleurs', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const requests = [];
  context.mock.method(globalThis, 'fetch', async (path, options) => {
    requests.push({ path, ...options });
    return Response.json({ id: 19 });
  });
  const credentials = { identifiant: 'test-contract', motDePasse: 'only-used-in-test' };
  await login(credentials);
  await createStagiaire({ nom: 'Test', prenoms: 'Contrat' });
  await createParticipation({ stageId: 1, stagiaireAcademiqueId: 2, cadreStageId: 3, encadreurId: 4, dateDebutStage: '2026-10-01', dateFinStage: '2026-11-01' });
  await createRapport({ stageParticipantId: 19, moisStageId: 1, debutSemaine: '2026-10-01', finSemaine: '2026-10-04' });
  await createSoutenance({ stageParticipantId: 19, dateSoutenance: '2026-11-01T09:00:00', lieu: 'Salle 1' });
  const form = new FormData();
  form.append('typePieceJointeCode', 'RAPPORT_FINAL');
  form.append('fichier', new Blob(['test']), 'test.txt');
  await uploadFichierParticipation(19, form);
  await marquerAlerteLue(19);

  assert.deepEqual(requests.map(({ method, path }) => [method, path]), [
    ['POST', '/api/auth/login'], ['POST', '/api/stagiaires'], ['POST', '/api/stage-participants'],
    ['POST', '/api/rapports-activite'], ['POST', '/api/soutenances'],
    ['POST', '/api/stage-participants/19/fichiers'], ['PATCH', '/api/alertes/19/lire']
  ]);
  assert.equal(requests[0].headers.has('Authorization'), false);
  assert.deepEqual(JSON.parse(requests[0].body), credentials);
  assert.equal(requests[5].body, form);
  for (const request of requests) {
    const endpoint = inventory.find((entry) => entry.method === request.method
      && new RegExp('^' + entry.path.replace(/\{[^}]+\}/g, '[^/]+') + '$').test(request.path));
    assert.ok(endpoint, `${request.method} ${request.path} absent du backend`);
    assert.notEqual(endpoint.access, 'denyAll');
    if (endpoint.body) {
      const root = new URL('../../backend/src/main/java/mg/mef/stagemanagement/dto/', import.meta.url);
      const domain = { LoginRequest: 'auth', StagiaireRequest: 'stagiaire', StageParticipantRequest: 'stage', RapportActiviteRequest: 'rapport', SoutenanceRequest: 'soutenance' }[endpoint.body];
      const source = readFileSync(new URL(`${domain}/${endpoint.body}.java`, root), 'utf8');
      for (const key of Object.keys(JSON.parse(request.body))) assert.ok(source.includes(` ${key};`), `${key} absent de ${endpoint.body}`);
    }
  }
});
