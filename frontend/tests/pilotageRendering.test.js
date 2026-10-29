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
    const { DashboardSummary } = await server.ssrLoadModule('/src/pages/dashboard/DashboardPage.jsx');
    const { default: Audit } = await server.ssrLoadModule('/src/pages/audit/JournalAuditPage.jsx');
    const render = (Component, props = {}, roles = ['ADMIN']) => renderToStaticMarkup(React.createElement(AuthContext.Provider, { value: { utilisateur: { roles } } }, React.createElement(Component, props)));
    const academic = render(References, { group: 'academiques' });
    assert.match(academic, /Établissements/); assert.doesNotMatch(academic, /Structures d’accueil/);
    assert.match(academic, /aria-pressed="true"/);
    assert.doesNotMatch(render(References, { group: 'organisation' }), /Filières/);
    assert.doesNotMatch(render(References, {}, ['ENCADREUR']), />Créer<\/button>/);
    assert.match(render(Form, { kind: 'structure' }), /<select[^>]*name="departementId"/);
    assert.doesNotMatch(render(Form, { kind: 'departement' }), /name="statutCode"/);
    const detail = render(Detail, { alerte: { id: 1, stageParticipantId: 7, rapportId: 8, actionRecommandee: '<script>fixture</script>' } });
    assert.match(detail, /href="\/participations\/7"/); assert.match(detail, /href="\/rapports\/8"/);
    assert.doesNotMatch(detail, /<script>/); assert.match(detail, /&lt;script&gt;/);
    const resume = { totalStagiaires: 0, totalStages: 1, totalParticipations: 2, totalRapports: 3, totalAlertesATraiter: 4, totalAlertesTraitees: 5 };
    const dashboard = render(DashboardSummary, { resume });
    assert.match(dashboard, /<strong>0<\/strong>/); assert.equal((dashboard.match(/<strong>/g) ?? []).length, 6);
    assert.match(dashboard, /href="\/alertes\?statut=TRAITEE"/);
    const audit = render(Audit);
    assert.match(audit, /Périmètre de recherche/); assert.doesNotMatch(audit, /Supprimer|Enregistrer/);
  } finally { await server.close(); }
});
