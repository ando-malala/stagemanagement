import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('rendu documents et administration : périmètres, droits et absence de secret affiché', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: Documents } = await server.ssrLoadModule('/src/pages/fichiers/DocumentsPanel.jsx');
    const { default: Administration } = await server.ssrLoadModule('/src/pages/utilisateurs/AccountAdministration.jsx');
    const { default: CreateAccount } = await server.ssrLoadModule('/src/pages/utilisateurs/CreateAccount.jsx');
    const render = (Component, props, roles) => renderToStaticMarkup(React.createElement(AuthContext.Provider, {
      value: { utilisateur: { utilisateurId: 1, roles } }
    }, React.createElement(Component, props)));
    const owner = { scope: 'stagiaire', ownerId: 42, ownerLabel: 'Dossier test' };
    const manager = render(Documents, owner, ['RESPONSABLE_STAGE']);
    assert.match(manager, /Documents du stagiaire/);
    assert.match(manager, /name="typePieceJointeCode"/);
    assert.match(manager, /type="file"/);
    assert.match(manager, /complétude du dossier ne peut pas être vérifiée/);
    const reader = render(Documents, { ...owner, scope: 'participation' }, ['ENCADREUR']);
    assert.match(reader, /dossier de participation/);
    assert.doesNotMatch(reader, /type="file"/);
    assert.doesNotMatch(reader, /Supprimer/);
    assert.equal(render(Administration, { account: { id: 42 } }, ['RESPONSABLE_STAGE']), '');
    assert.equal(render(CreateAccount, {}, ['ENCADREUR']), '');
    const admin = render(Administration, { account: { id: 42, identifiant: 'compte-test' } }, ['ADMIN']);
    assert.match(admin, /Rôles du compte/);
    assert.match(admin, /Départements rattachés/);
    assert.match(admin, /Réinitialiser le mot de passe/);
    assert.doesNotMatch(admin, /type="password"/); // Closed forms hold no secret input.
  } finally { await server.close(); }
});
