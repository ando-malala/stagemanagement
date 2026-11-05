import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';
import { applyParticipationEvent } from '../src/api/stageApi.js';

test('clôture : décision administrative disponible même quand toutes les pièces sont présentes', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { ClotureDialog } = await server.ssrLoadModule('/src/pages/stages/ClotureConfirmation.jsx');
    const render = props => renderToStaticMarkup(React.createElement(ClotureDialog, { onClose() {}, onConfirm() {}, ...props }));
    const documents = [{ libelle: 'Convention', present: false }, { libelle: 'Rapport final', present: false }, { libelle: 'Pièce déjà déposée', present: true }];
    const incomplete = render({ documents });
    assert.match(incomplete, /Dossiers manquants : Convention, Rapport final/);
    assert.doesNotMatch(incomplete, /Pièce déjà déposée/);
    assert.match(incomplete, />Clôturer quand même<\/button>/);
    assert.doesNotMatch(incomplete, /disabled=""/);
    const complete = render({ documents: documents.map(document => ({ ...document, present: true })) });
    assert.match(complete, /Tous les documents obligatoires sont présents/);
    assert.match(complete, />Clôturer quand même<\/button>/);
    assert.match(complete, /rapports sont manquants, non soumis ou non validés/);
    for (const state of [{ loading: true }, { error: 'Réseau indisponible' }, { documents: null }]) {
      assert.match(render(state), /<button[^>]*disabled=""[^>]*>Clôturer quand même<\/button>/);
    }
    assert.match(render({ error: 'Réseau indisponible' }), /Réessayer/);
  } finally { await server.close(); }
});

test('clôture dérogatoire : envoi explicite de l’option au backend, motif conservé', async context => {
  const body = { motif: 'Décision administrative', forcerCloture: true };
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(url, '/api/stage-participants/7/cloturer');
    assert.equal(options.method, 'POST');
    assert.deepEqual(JSON.parse(options.body), body);
    return Response.json({ id: 7, statut: 'CLOTUREE' });
  });
  assert.equal((await applyParticipationEvent(7, 'cloturer', body)).statut, 'CLOTUREE');
});
