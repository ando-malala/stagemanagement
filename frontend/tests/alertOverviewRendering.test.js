import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('synthèse interactive : filtres, accessibilité, compteurs exacts et badge plafonné', async context => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { default: Overview } = await server.ssrLoadModule('/src/pages/alertes/AlertOverview.jsx');
    const { default: NotificationBadge } = await server.ssrLoadModule('/src/components/common/NotificationBadge.jsx');
    const { DashboardSummary } = await server.ssrLoadModule('/src/pages/dashboard/DashboardPage.jsx');
    const rows = Array.from({ length: 12 }, (_, id) => ({ id, statut: 'A_TRAITER', priorite: 'URGENT',
      natureAlerte: 'INFORMATION', typeAlerteCode: 'TYPE_REEL', typeAlerteLibelle: '<Type réel>' }));
    const onSelect = context.mock.fn();
    const props = { rows, filters: { statut: 'A_TRAITER', priorite: 'URGENT' }, onSelect };
    const html = renderToStaticMarkup(React.createElement(Overview, props));
    assert.match(html, /aria-labelledby="alert-overview-title"/);
    assert.match(html, /aria-pressed="true"[^>]*aria-controls="alert-results"/);
    assert.match(html, /<strong>12<\/strong>/);
    assert.doesNotMatch(html, /9\+/);
    assert.match(html, /&lt;Type réel&gt;/);
    assert.match(html, /Sans traitement manuel/);
    assert.match(html, /aria-hidden="true"/);
    // Invoke the actual rendered button handlers: metrics replace filters and categories use backend codes.
    const tree = Overview(props);
    const buttons = [];
    function walk(node) {
      if (!React.isValidElement(node)) return;
      if (node.props.onClick) buttons.push(node);
      React.Children.forEach(node.props.children, walk);
    }
    walk(tree);
    for (const button of buttons) button.props.onClick();
    assert.deepEqual(onSelect.mock.calls.map(call => call.arguments[0]), [
      {}, { statut: 'A_TRAITER' }, { statut: 'A_TRAITER', priorite: 'URGENT' },
      { statut: 'A_TRAITER', priorite: 'AVERTISSEMENT' }, { statut: 'A_TRAITER', natureAlerte: 'INFORMATION' },
      { statut: 'A_TRAITER', typeAlerteCode: 'TYPE_REEL' }
    ]);
    const badge = renderToStaticMarkup(React.createElement(NotificationBadge, { count: 12 }));
    assert.match(badge, />9\+<\/span>/);
    assert.match(badge, /aria-label="12 alertes à traiter"/);
    assert.equal(renderToStaticMarkup(React.createElement(NotificationBadge, { count: 0 })), '');
    const resume = { totalStagiaires: 1, totalStages: 1, totalParticipations: 1, totalRapports: 0, totalAlertesATraiter: 12, totalAlertesTraitees: 20 };
    const dashboard = renderToStaticMarkup(React.createElement(DashboardSummary, { resume }));
    assert.match(dashboard, /<strong>12<\/strong>/);
    assert.match(dashboard, /<strong>20<\/strong>/);
    assert.doesNotMatch(dashboard, /9\+/);
    assert.match(renderToStaticMarkup(React.createElement(Overview, { rows: [], filters: {}, onSelect })), /Aucun type d’alerte/);
  } finally { await server.close(); }
});
