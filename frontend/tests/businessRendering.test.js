import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('rendu métier : permissions, champs alimentés par API et actions selon statut', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: BusinessEditor } = await server.ssrLoadModule('/src/components/common/BusinessEditor.jsx');
    const { default: BusinessForm } = await server.ssrLoadModule('/src/components/common/BusinessForm.jsx');
    const { default: Actions } = await server.ssrLoadModule('/src/pages/stages/ParticipationActions.jsx');
    const render = (Component, props, roles) => renderToStaticMarkup(React.createElement(AuthContext.Provider, {
      value: { utilisateur: { utilisateurId: 1, roles } }
    }, React.createElement(Component, props)));
    assert.equal(render(BusinessEditor, { kind: 'stage' }, ['ENCADREUR']), '');
    assert.match(render(BusinessEditor, { kind: 'stage', initial: { id: 2 } }, ['ADMIN']), /Modifier le dossier/);
    const responsible = render(BusinessForm, { kind: 'encadreur', initial: { utilisateurId: 8 } }, ['ADMIN']);
    assert.match(responsible, /name="utilisateurId"/);
    assert.doesNotMatch(render(BusinessForm, { kind: 'stagiaire' }, ['ADMIN']), /name="departementId"/);
    assert.match(responsible, /<select[^>]*name="departementId"/);
    assert.match(render(BusinessForm, { kind: 'encadreur' }, ['ADMIN']), /<select[^>]*name="utilisateurId"/);
    const participantForm = render(BusinessForm, { kind: 'participation' }, ['ADMIN']);
    for (const name of ['stagiaireId', 'stageId', 'stagiaireAcademiqueId', 'cadreStageId', 'encadreurId']) assert.match(participantForm, new RegExp(`<select[^>]*name="${name}"`));
    const reportForm = render(BusinessForm, { kind: 'rapport' }, ['STAGIAIRE']);
    assert.ok(reportForm.indexOf('name="stageParticipantId"') < reportForm.indexOf('name="periodeAttendue"'));
    assert.match(reportForm, /Enregistrer le brouillon/);
    assert.match(reportForm, /Choisissez d’abord le stage/);
    const participation = { id: 3, statut: 'EN_COURS' };
    const readOnly = render(Actions, { participation }, ['ENCADREUR']);
    assert.doesNotMatch(readOnly, /<button/);
    assert.match(readOnly, /Chargement des actions autorisées/);
    const manager = render(Actions, { participation }, ['ADMIN']);
    assert.match(manager, /Chargement des actions autorisées/);
    assert.doesNotMatch(manager, />Reprendre<\/button>/);
    assert.doesNotMatch(manager, />Clôturer<\/button>/);
    assert.doesNotMatch(render(Actions, { participation: { ...participation, statut: 'SUSPENDUE' } }, ['ADMIN']), />Reprendre<\/button>/);
  } finally { await server.close(); }
});
