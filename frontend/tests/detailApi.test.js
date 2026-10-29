import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { getStagiaire } from '../src/api/stagiaireApi.js';
import { getEncadreur } from '../src/api/encadreurApi.js';
import { getStage, getParticipation } from '../src/api/stageApi.js';
import { getRapport } from '../src/api/rapportApi.js';
import { getEvaluation } from '../src/api/evaluationApi.js';
import { getSoutenance } from '../src/api/soutenanceApi.js';
import { getUtilisateur } from '../src/api/utilisateurApi.js';

const cases = [
  ['stagiaires/Stagiaire', 'stagiaire/StagiaireResponse', '/api/stagiaires', getStagiaire],
  ['encadreurs/Encadreur', 'encadreur/EncadreurResponse', '/api/encadreurs', getEncadreur],
  ['stages/Stage', 'stage/StageResponse', '/api/stages', getStage],
  ['stages/Participation', 'stage/StageParticipantResponse', '/api/stage-participants', getParticipation],
  ['rapports/Rapport', 'rapport/RapportActiviteResponse', '/api/rapports-activite', getRapport],
  ['evaluations/Evaluation', 'evaluation/EvaluationFinaleResponse', '/api/evaluations', getEvaluation],
  ['soutenances/Soutenance', 'soutenance/SoutenanceResponse', '/api/soutenances', getSoutenance],
  ['utilisateurs/Utilisateur', 'utilisateur/UtilisateurListResponse', '/api/utilisateurs', getUtilisateur]
];
for (const [page, dto, path, fetcher] of cases) {
  test(`détail ${page} : contrat GET, annulation et champs du DTO`, async (context) => {
    const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
    const endpoint = inventory.find((entry) => entry.method === 'GET' && entry.path === path + '/{id}');
    assert.ok(endpoint);
    assert.notEqual(endpoint.access, 'denyAll');
    assert.match(endpoint.response, new RegExp(dto.split('/')[1]));
    const controller = new AbortController();
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path + '/42');
      assert.equal(options.method ?? 'GET', 'GET');
      assert.equal(options.signal, controller.signal);
      return Response.json({ id: 42 });
    });
    assert.deepEqual(await fetcher('42', { signal: controller.signal }), { id: 42 });
    const source = readFileSync(new URL(`../src/pages/${page}DetailPage.jsx`, import.meta.url), 'utf8');
    const fields = JSON.parse(source.match(/const fields = (.+);/)[1]);
    const links = JSON.parse(source.match(/const links = (.+);/)[1]);
    const dtoSource = readFileSync(new URL(`../../backend/src/main/java/mg/mef/stagemanagement/dto/${dto}.java`, import.meta.url), 'utf8');
    for (const field of [...fields.map(([key]) => key), ...links.map((link) => link.key)]) {
      assert.ok(dtoSource.includes(` ${field};`), `${field} absent du DTO ${dto}`);
    }
    for (const [, label] of fields) assert.ok(!label.includes('?'), `Libellé corrompu : ${label}`);
  });
}
