import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';
import { currentReportParticipations, reportWritingAccess } from '../src/utils/reportWeeks.js';
import { loadReportWeeks } from '../src/api/reportWeeksApi.js';
import { loadBusinessChoices } from '../src/api/businessChoices.js';
import { businessPayload } from '../src/utils/businessForms.js';
import { createRapport } from '../src/api/rapportApi.js';

test('stage automatique : son stage en cours parmi les anciens, futurs et dossiers d’autrui', () => {
  const rows = [
    { id: 1, statut: 'CLOTUREE', peutRedigerRapport: true },
    { id: 2, statut: 'PLANIFIEE', peutRedigerRapport: true },
    { id: 3, statut: 'EN_COURS', peutRedigerRapport: false },
    { id: 4, statut: 'EN_COURS', peutRedigerRapport: true },
    { id: 5, statut: 'SUSPENDUE', peutRedigerRapport: true },
    { id: 6, statut: 'A_CLOTURER', peutRedigerRapport: true }
  ];
  assert.deepEqual(currentReportParticipations(rows), [rows[3]]);
  assert.deepEqual(currentReportParticipations(rows.filter(row => row.id !== 4)), []);
  // Do not silently choose the first record if the assignment data is ambiguous.
  assert.equal(currentReportParticipations([...rows, { ...rows[3], id: 7 }]).length, 2);
  assert.equal(rows.length, 6);
});

test('dossier de participation : les droits sont rechargés avec les semaines, sans dépendre du parent', async context => {
  const calls = [];
  const controller = new AbortController();
  const owner = { roles: ['STAGIAIRE'] };
  context.mock.method(globalThis, 'fetch', async (path, options) => {
    assert.equal(options.signal, controller.signal);
    calls.push(path);
    if (path === '/api/stage-participants/4') return Response.json({ id: 4, statut: 'PLANIFIEE', peutRedigerRapport: true });
    if (path === '/api/stage-participants/4/periodes-rapports') return Response.json([
      { debut: '2026-11-02', fin: '2026-11-08', rapportId: null },
      { debut: '2026-11-09', fin: '2026-11-15', rapportId: 55 }
    ]);
    assert.equal(path, '/api/rapports-activite/participation/4');
    return Response.json([{ id: 55, statut: 'BROUILLON', peutRediger: true }]);
  });
  const result = await loadReportWeeks(4, { signal: controller.signal });
  assert.equal(calls.length, 3);
  assert.equal(reportWritingAccess(owner, result.participation).allowed, true);
  assert.equal(result.periods[0].rapport, undefined);
  assert.equal(result.periods[1].rapport.id, 55);
  for (const statut of ['PLANIFIEE', 'EN_COURS', 'SUSPENDUE', 'A_CLOTURER']) {
    assert.equal(reportWritingAccess(owner, { ...result.participation, statut }).allowed, true, statut);
  }
  for (const statut of ['CLOTUREE', 'ANNULEE', 'ABANDONNEE']) {
    assert.equal(reportWritingAccess(owner, { ...result.participation, statut }).allowed, false, statut);
  }
  assert.equal(reportWritingAccess(owner, { ...result.participation, dateCloture: '2026-11-03' }).allowed, false);
});

test('rédaction : refus expliqué pour un autre compte, une API incomplète et les rôles non stagiaires', () => {
  const participation = { id: 4, statut: 'EN_COURS', peutRedigerRapport: true };
  for (const roles of [['ADMIN'], ['ENCADREUR'], []]) {
    assert.equal(reportWritingAccess({ roles }, participation).allowed, false);
  }
  const owner = { roles: ['STAGIAIRE'] };
  const denied = reportWritingAccess(owner, { ...participation, peutRedigerRapport: false });
  assert.equal(denied.allowed, false);
  assert.match(denied.reason, /compte stagiaire associé/);
  const missing = reportWritingAccess(owner, { id: 4, statut: 'EN_COURS' });
  assert.equal(missing.allowed, false);
  assert.match(missing.reason, /Impossible de vérifier/);
});

test('rapport prérempli : aucun choix de stage à charger, période et participation conservées dans le POST', async context => {
  const calls = [];
  context.mock.method(globalThis, 'fetch', async (path, options) => {
    calls.push(path);
    if (options.method === 'POST') {
      assert.equal(path, '/api/rapports-activite');
      assert.deepEqual(JSON.parse(options.body), { stageParticipantId: 4, debutSemaine: '2026-11-02', finSemaine: '2026-11-08',
        activite: 'Préparation du bilan', difficultes: null, solutions: null, moisStageId: 12 });
      return Response.json({ id: 55, statut: 'BROUILLON' });
    }
    return Response.json([{ id: 12, libelle: 'Premier mois' }]);
  });
  const choices = await loadBusinessChoices('rapport', false, {}, true);
  assert.deepEqual(calls, ['/api/referentiels/academiques/mois-stage']);
  const report = await createRapport(businessPayload('rapport', { stageParticipantId: 4,
    debutSemaine: '2026-11-02', finSemaine: '2026-11-08', activite: 'Préparation du bilan', moisStageId: choices.moisStageId[0].id }));
  assert.equal(report.id, 55);
});

test('grille hebdomadaire : semaines vides, brouillons, corrections et validation ; formulaire sans sélecteurs', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { RapportWeeksTable } = await server.ssrLoadModule('/src/pages/rapports/RapportWeeks.jsx');
    const { default: Form } = await server.ssrLoadModule('/src/components/common/BusinessForm.jsx');
    const { default: Page } = await server.ssrLoadModule('/src/pages/rapports/RapportsPage.jsx');
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const periods = [
      { debut: '2026-10-05', fin: '2026-10-11', manquante: true },
      { debut: '2026-10-12', fin: '2026-10-18', rapportId: 11, statutRapport: 'BROUILLON' },
      { debut: '2026-10-19', fin: '2026-10-25', rapportId: 12, statutRapport: 'A_CORRIGER' },
      { debut: '2026-10-26', fin: '2026-11-01', rapportId: 13, statutRapport: 'SOUMIS' },
      { debut: '2026-11-02', fin: '2026-11-08', rapportId: 14, statutRapport: 'VALIDE' }
    ];
    const render = (Component, props, roles = ['STAGIAIRE']) => renderToStaticMarkup(React.createElement(AuthContext.Provider,
      { value: { utilisateur: { roles } } }, React.createElement(Component, props)));
    const html = render(RapportWeeksTable, { periods, canWrite: true });
    assert.match(html, /Rédiger le rapport de la semaine 1/);
    assert.match(html, /href="\/rapports\/11">Reprendre/);
    assert.match(html, /href="\/rapports\/12">Corriger/);
    assert.match(html, /href="\/rapports\/13">Consulter/);
    assert.match(html, /href="\/rapports\/14">Consulter/);
    assert.equal((html.match(/>Rédiger<\/button>/g) ?? []).length, 1);
    for (const statut of ['PLANIFIEE', 'EN_COURS', 'SUSPENDUE']) {
      const rights = reportWritingAccess({ roles: ['STAGIAIRE'] }, { statut, peutRedigerRapport: true });
      const ownGrid = render(RapportWeeksTable, { periods: [periods[0]], canWrite: rights.allowed, onSelect() {} });
      assert.match(ownGrid, /Rédiger le rapport de la semaine 1/);
      assert.doesNotMatch(ownGrid, /aria-label="Rédiger le rapport de la semaine 1"[^>]*disabled/);
    }
    assert.doesNotMatch(render(RapportWeeksTable, { periods, canWrite: false }), />Rédiger<|>Reprendre<|>Corriger</);
    const form = render(Form, { kind: 'rapport', reportContext: { stageLabel: 'Stage finances' }, initial: {
      stageParticipantId: 4, debutSemaine: '2026-11-02', finSemaine: '2026-11-08' } });
    assert.match(form, /Stage finances/);
    assert.match(form, /Semaine du 2 nov\. 2026 au 8 nov\. 2026/);
    assert.doesNotMatch(form, /name="(?:stageParticipantId|periodeAttendue|debutSemaine|finSemaine)"/);
    assert.match(form, /name="activite"/);
    assert.match(render(Page, {}), /Mes rapports — stage en cours/);
    assert.doesNotMatch(render(Page, {}, ['ENCADREUR']), /Mes rapports — stage en cours/);
    for (const roles of [['ENCADREUR'], ['ADMIN']]) {
      const reviewPage = render(Page, {}, roles);
      assert.match(reviewPage, /Rapports par stagiaire/);
      assert.match(reviewPage, /Stagiaire<\/label>/);
      assert.doesNotMatch(reviewPage, />Soumettre<|>Rédiger</);
    }
    const withPermissions = periods.map(period => ({ ...period, rapport: {
      id: period.rapportId, statut: period.statutRapport, peutRediger: true, peutExaminer: false
    } }));
    const traineeGrid = render(RapportWeeksTable, { periods: withPermissions, canWrite: true, onAction() {}, onSelect() {} });
    assert.equal((traineeGrid.match(/>Soumettre<\/button>/g) ?? []).length, 2);
    assert.doesNotMatch(traineeGrid, />Valider<\/button>|>Demander une correction<\/button>/);
    const reviewerPeriods = withPermissions.map(period => ({ ...period, rapport: { ...period.rapport, peutRediger: false, peutExaminer: true } }));
    const reviewerGrid = render(RapportWeeksTable, { periods: reviewerPeriods, canWrite: false, onAction() {} }, ['ENCADREUR']);
    assert.equal((reviewerGrid.match(/>Valider<\/button>/g) ?? []).length, 1);
    assert.equal((reviewerGrid.match(/>Demander une correction<\/button>/g) ?? []).length, 1);
    assert.doesNotMatch(reviewerGrid, />Soumettre<\/button>|>Rédiger<\/button>/);
    const adminGrid = render(RapportWeeksTable, { periods: reviewerPeriods, canWrite: false, onAction() {} }, ['ADMIN']);
    assert.doesNotMatch(adminGrid, />Soumettre<\/button>|>Valider<\/button>|>Demander une correction<\/button>/);
    const otherMentor = render(RapportWeeksTable, { periods: withPermissions, canWrite: false, onAction() {} }, ['ENCADREUR']);
    assert.doesNotMatch(otherMentor, />Soumettre<\/button>|>Valider<\/button>/);
  } finally { await server.close(); }
});
