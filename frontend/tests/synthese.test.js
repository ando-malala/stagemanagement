import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';
import { getDossier, getPilotage, downloadBilan, downloadExport } from '../src/api/syntheseApi.js';

test('synthèses : contrats réels, signal et erreurs de format', async context => {
  const signal = new AbortController().signal;
  for (const [call, path, data] of [
    [() => getDossier(42, { signal }), '/api/stagiaires/42/dossier', { stagiaire: { id: 42 }, academiques: [], fichiers: [], participations: [] }],
    [() => getPilotage({ signal }), '/api/dashboard/pilotage', { dateCalcul: '2026-11-01', dossiers: [], priorites: [], charges: [], soutenancesProchaines: [] }]
  ]) {
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, path); assert.equal(options.signal, signal); return Response.json(data);
    });
    assert.deepEqual(await call(), data);
    context.mock.method(globalThis, 'fetch', async () => Response.json({}));
    await assert.rejects(call, /format inattendu/);
  }
});

test('exports : refus serveur conservé et périmètres déclarés dans Spring', async context => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  for (const [call, path, template, roles] of [
    [() => downloadBilan(42), '/api/stage-participants/42/bilan.pdf', '/api/stage-participants/{id}/bilan.pdf', ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR', 'STAGIAIRE']],
    ...['participations', 'statistiques', 'alertes'].map(type => [() => downloadExport(type), `/api/exports/${type}.csv`, `/api/exports/${type}.csv`, ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR']])
  ]) {
    assert.deepEqual(inventory.find(e => e.method === 'GET' && e.path === template)?.roles, roles);
    context.mock.method(globalThis, 'fetch', async url => {
      assert.equal(url, path); return Response.json({ detail: 'Accès refusé' }, { status: 403 });
    });
    await assert.rejects(call, error => error.status === 403);
  }
  assert.throws(() => downloadExport('../secret'), /inconnu/);
});

test('synthèse SSR : données échappées, liens réels et aucune troncature du dossier imprimable', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { DossierContent } = await server.ssrLoadModule('/src/pages/stagiaires/DossierSynthese.jsx');
    const { PilotageTables } = await server.ssrLoadModule('/src/pages/dashboard/PilotagePanel.jsx');
    const participation = { participation: { id: 7, stage: '<script>stage</script>', statut: 'EN_COURS' }, fichiers: [], documentsObligatoires: [],
      rapports: [{ rapport: { id: 9, activite: 'travail réel' }, observations: [{ id: 4, commentaire: '<script>observation</script>' }] }],
      periodes: Array.from({ length: 60 }, (_, i) => ({ debut: `periode-${i}`, fin: `fin-${i}` })), historique: [], evenements: [], soutenances: [] };
    const html = renderToStaticMarkup(React.createElement(DossierContent, { dossier: { stagiaire: { id: 1, nom: 'Test' }, academiques: [], fichiers: [], participations: [participation] } }));
    assert.match(html, /periode-59/); assert.match(html, /href="\/rapports\/9"/); assert.match(html, /href="\/participations\/7"/);
    assert.match(html, /&lt;script&gt;observation/); assert.doesNotMatch(html, /<script>/);
    const dashboard = renderToStaticMarkup(React.createElement(PilotageTables, { data: { dateCalcul: '2026-11-01',
      dossiers: [], charges: [], soutenancesProchaines: [], priorites: [{ participationId: 7, stagiaire: 'Test', score: 50, priorite: 'URGENTE', actionRecommandee: '<script>action</script>' }] } }));
    assert.match(dashboard, /URGENTE/); assert.match(dashboard, /href="\/participations\/7"/); assert.doesNotMatch(dashboard, /<script>/);
  } finally { await server.close(); }
});
