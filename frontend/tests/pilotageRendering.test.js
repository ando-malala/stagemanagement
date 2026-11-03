import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('pilotage SSR : groupes distincts, champs métier, liens des alertes et compteurs', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: References } = await server.ssrLoadModule('/src/pages/referentiels/ReferentielsPage.jsx');
    const { default: Form } = await server.ssrLoadModule('/src/components/common/BusinessForm.jsx');
    const { default: Detail } = await server.ssrLoadModule('/src/pages/alertes/AlerteDetail.jsx');
    const { DashboardSummary, DashboardAlerts } = await server.ssrLoadModule('/src/pages/dashboard/DashboardPage.jsx');
    const { groupDashboardAlerts } = await server.ssrLoadModule('/src/utils/dashboardAlerts.js');
    const { default: Audit } = await server.ssrLoadModule('/src/pages/audit/JournalAuditPage.jsx');
    const render = (Component, props = {}, roles = ['ADMIN']) => renderToStaticMarkup(React.createElement(AuthContext.Provider, { value: { utilisateur: { roles } } }, React.createElement(Component, props)));
    const academic = render(References, { group: 'academiques' });
    assert.match(academic, /Établissements/); assert.doesNotMatch(academic, /Structures d’accueil/);
    assert.match(academic, /aria-pressed="true"/);
    assert.doesNotMatch(render(References, { group: 'organisation' }), /Filières/);
    assert.doesNotMatch(render(References, {}, ['ENCADREUR']), />Créer<\/button>/);
    assert.match(render(Form, { kind: 'structure' }), /<select[^>]*name="departementId"/);
    assert.match(render(Form, { kind: 'departement' }), /<select[^>]*name="statutCode"/);
    const detail = render(Detail, { alerte: { id: 1, stageParticipantId: 7, rapportId: 8, actionRecommandee: '<script>fixture</script>' } });
    assert.match(detail, /href="\/participations\/7"/); assert.match(detail, /href="\/rapports\/8"/);
    assert.doesNotMatch(detail, /<script>/); assert.match(detail, /&lt;script&gt;/);
    const namedDetail = render(Detail, { alerte: { id: 1, stagiaireId: 7, stagiaireNomComplet: 'Rakoto Jean', message: 'Fin de stage proche' } });
    assert.match(namedDetail, /href="\/stagiaires\/7">Rakoto Jean/);
    const resume = { totalStagiaires: 0, totalStages: 1, totalParticipations: 2, totalRapports: 3, totalAlertesATraiter: 4, totalAlertesTraitees: 5 };
    const dashboard = render(DashboardSummary, { resume });
    assert.match(dashboard, /<strong>0<\/strong>/); assert.equal((dashboard.match(/<strong>/g) ?? []).length, 6);
    assert.match(dashboard, /href="\/alertes\?statut=TRAITEE"/);
    const grouped = groupDashboardAlerts(Array.from({ length: 9 }, (_, i) => ({ id: i + 1,
      stageParticipantId: 2, typeAlerteCode: 'RAPPORT_MANQUANT', message: `Rapport manquant participation n° 2 semaine ${i}` })),
    [{ id: 2, stagiaireId: 7, stagiaireNomComplet: 'Rakoto Jean' }]);
    const groupedHtml = render(DashboardAlerts, { cards: grouped });
    assert.match(groupedHtml, /Stagiaire Rakoto Jean/);
    assert.match(groupedHtml, /9 rapports manquants/);
    assert.equal((groupedHtml.match(/class="alert-card"/g) ?? []).length, 1);
    assert.match(groupedHtml, /href="\/participations\/2"/);
    assert.doesNotMatch(groupedHtml, /participation n°/);
    const audit = render(Audit);
    assert.match(audit, /Périmètre de recherche/); assert.doesNotMatch(audit, /Supprimer|Enregistrer/);
  } finally { await server.close(); }
});
