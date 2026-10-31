import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('rendu des routes et du layout : accès, session indisponible, fiches et liens', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  const originalWindow = Object.getOwnPropertyDescriptor(globalThis, 'window');
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: AppRoutes } = await server.ssrLoadModule('/src/routes/AppRoutes.jsx');
    const { default: Button } = await server.ssrLoadModule('/src/components/common/Button.jsx');
    const { default: Breadcrumbs } = await server.ssrLoadModule('/src/components/layout/Breadcrumbs.jsx');
    const { matchRoute } = await server.ssrLoadModule('/src/routes/routeConfig.js');
    const render = (path, roles, overrides = {}) => {
      const url = new URL(path, 'http://localhost');
      Object.defineProperty(globalThis, 'window', { configurable: true, value: { location: url } });
      const value = { utilisateur: { utilisateurId: 1, identifiant: 'Test de rendu', roles }, estConnecte: true, initialisation: false, status: 'authenticated', logout: () => {}, ...overrides };
      return renderToStaticMarkup(React.createElement(AuthContext.Provider, { value }, React.createElement(AppRoutes)));
    };
    const detail = render('/participations/42', ['ENCADREUR']);
    assert.match(detail, /Participation n° 42/);
    assert.match(detail, /Dossier de participation n° 42/);
    assert.match(detail, /href="\/stages"/);
    assert.doesNotMatch(detail, /Créer la participation/);
    assert.doesNotMatch(detail, /href="\/utilisateurs"/);
    assert.match(detail, /aria-haspopup="dialog"/);
    assert.match(detail, /mobile-menu-button/);
    assert.match(detail, /aria-current="page"/);
    const forbidden = render('/utilisateurs/42', ['ENCADREUR']);
    assert.match(forbidden, /Accès refusé/);
    assert.doesNotMatch(forbidden, /Utilisateur n° 42/);
    const noRole = render('/dashboard', ['STAGIAIRE']);
    assert.match(noRole, /Mon dossier/);
    assert.doesNotMatch(noRole, /href="\/utilisateurs"/);
    assert.doesNotMatch(noRole, /href="\/alertes"/);
    assert.match(render('/absent', ['ADMIN']), /Page introuvable/);
    const offline = render('/stages/42', [], { estConnecte: false, status: 'unavailable', erreur: 'Réseau indisponible' });
    assert.match(offline, /Session à vérifier/);
    assert.match(offline, /Réessayer/);
    assert.doesNotMatch(offline, /Navigation principale/);
    assert.match(render('/login', [], { estConnecte: false, status: 'anonymous' }), /autocomplete="current-password"/i);
    const button = renderToStaticMarkup(React.createElement(Button, { className: 'menu', 'aria-expanded': false }, 'Menu'));
    assert.match(button, /aria-expanded="false"/);
    assert.match(button, /class="btn btn-primary menu"/);
    const crumbs = renderToStaticMarkup(React.createElement(Breadcrumbs, { route: matchRoute('/stages/9'), navigate: () => {} }));
    assert.match(crumbs, /aria-label="Fil d’Ariane"/);
  } finally {
    if (originalWindow) Object.defineProperty(globalThis, 'window', originalWindow);
    else delete globalThis.window;
    await server.close();
  }
});
