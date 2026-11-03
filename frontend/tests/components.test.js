import test from 'node:test';
import assert from 'node:assert/strict';
import { createServer } from 'vite';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';

test('rendu React des composants : champs accessibles, listes vides, chargement, pagination', async () => {
  // SSR verifies rendering with the installed Vite/React stack. No browser or backend is simulated here.
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { default: Input } = await server.ssrLoadModule('/src/components/common/Input.jsx');
    const { default: Table } = await server.ssrLoadModule('/src/components/common/Table.jsx');
    const render = (component, props) => renderToStaticMarkup(React.createElement(component, props));
    const field = render(Input, { label: 'Date d’effet', name: 'dateEffet', type: 'date', error: 'Date invalide', required: true });
    assert.match(field, /aria-invalid="true"/);
    assert.match(field, /aria-describedby=/);
    assert.match(field, /for=/);
    assert.match(field, /Date invalide/);
    const columns = [{ key: 'nom', label: 'Nom' }];
    assert.match(render(Table, { columns, data: [] }), /Aucune donnée/);
    assert.match(render(Table, { columns, loading: true }), /role="status"/);
    assert.equal(render(Table, { columns, error: 'Erreur serveur' }), '');
    const rows = Array.from({ length: 13 }, (_, index) => ({ id: index + 1, nom: `Stagiaire ${index + 1}` }));
    const table = render(Table, { columns, data: rows, caption: 'Stagiaires' });
    assert.equal((table.match(/<tr/g) ?? []).length, 11); // Header + first ten records.
    assert.match(table, /Page 1 sur 2/);
    assert.match(table, /<caption>Stagiaires<\/caption>/);
    assert.match(table, /scope="col"/);
    assert.match(table, /aria-sort="none"/);
    assert.match(table, /Trier par Nom, ordre croissant/);
    const filteredTable = render(Table, { columns: [{ key: 'nom', label: 'Nom', filterable: true }], data: rows });
    assert.match(filteredTable, /Filtrer : Nom/);
    assert.match(filteredTable, />Tous<\/option>/);
  } finally {
    await server.close();
  }
});
