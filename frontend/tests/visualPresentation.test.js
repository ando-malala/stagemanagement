import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('présentation : statuts exacts, valeurs inconnues et rendus personnalisés préservés', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { default: Table } = await server.ssrLoadModule('/src/components/common/Table.jsx');
    const { default: StatusBadge } = await server.ssrLoadModule('/src/components/common/StatusBadge.jsx');
    const render = (component, props) => renderToStaticMarkup(React.createElement(component, props));
    assert.match(render(StatusBadge, { value: 'A_CORRIGER' }), /badge-correction">A_CORRIGER/);
    assert.match(render(StatusBadge, { value: 'BLOQUE' }), /badge-danger">BLOQUE/);
    assert.match(render(StatusBadge, { value: '__proto__' }), /badge-neutral">__proto__/);
    assert.match(render(StatusBadge, { value: '<statut inconnu>' }), /&lt;statut inconnu&gt;/);
    assert.equal(render(StatusBadge, { value: null }), '—');
    const rows = Object.freeze([Object.freeze({ id: 1, statut: 'EN_COURS', priorite: 'URGENT' })]);
    const html = render(Table, { caption: 'Participations', data: rows,
      columns: [{ key: 'statut', label: 'Statut' }, { key: 'priorite', label: 'Priorité', render: row => `Priorité : ${row.priorite}` }] });
    assert.match(html, /badge-info">EN_COURS/);
    assert.match(html, /<td>Priorité : URGENT<\/td>/);
    assert.match(html, /role="region"[^>]*aria-label="Participations — tableau défilant"[^>]*tabindex="0"/i);
    assert.match(html, /<caption>Participations<\/caption>/);
    assert.match(html, /Faites défiler le tableau horizontalement/);
  } finally { await server.close(); }
});

test('connexion : champs natifs obligatoires et labels associés conservés après refonte', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: LoginPage } = await server.ssrLoadModule('/src/pages/auth/LoginPage.jsx');
    const html = renderToStaticMarkup(React.createElement(AuthContext.Provider, { value: { login: () => {}, erreur: 'Service indisponible' } }, React.createElement(LoginPage)));
    assert.match(html, /<input[^>]*autoComplete="username"[^>]*required=""/i);
    assert.match(html, /<input[^>]*autoComplete="current-password"[^>]*required=""[^>]*type="password"/i);
    assert.match(html, /role="alert"/);
    assert.match(html, /Service indisponible/);
    assert.equal((html.match(/<form/g) ?? []).length, 1);
    assert.equal((html.match(/<label for=/g) ?? []).length, 2);
  } finally { await server.close(); }
});
