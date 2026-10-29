import test from 'node:test';
import assert from 'node:assert/strict';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { createServer } from 'vite';

test('rendu du suivi : droits, verrouillage et sélecteurs métier', async () => {
  const server = await createServer({ server: { middlewareMode: true, hmr: false, ws: false }, appType: 'custom' });
  try {
    const { AuthContext } = await server.ssrLoadModule('/src/context/AuthContext.jsx');
    const { default: Editor } = await server.ssrLoadModule('/src/components/common/BusinessEditor.jsx');
    const { default: Form } = await server.ssrLoadModule('/src/components/common/BusinessForm.jsx');
    const { default: Actions } = await server.ssrLoadModule('/src/pages/rapports/RapportActions.jsx');
    const { default: Notes } = await server.ssrLoadModule('/src/pages/evaluations/EvaluationNotes.jsx');
    const render = (Component, props, roles = ['ENCADREUR']) => renderToStaticMarkup(React.createElement(AuthContext.Provider, {
      value: { utilisateur: { roles } }
    }, React.createElement(Component, props)));
    assert.match(render(Editor, { kind: 'rapport' }), /Ajouter/);
    assert.equal(render(Editor, { kind: 'rapport', initial: { statut: 'VALIDE' } }), '');
    assert.equal(render(Editor, { kind: 'evaluation', initial: { estFinalisee: true } }), '');
    assert.equal(render(Editor, { kind: 'soutenance' }), '');
    assert.match(render(Editor, { kind: 'soutenance' }, ['RESPONSABLE_STAGE']), /Ajouter/);
    const report = render(Form, { kind: 'rapport' });
    assert.match(report, /<select[^>]*name="stageParticipantId"/);
    assert.match(report, /<select[^>]*required=""[^>]*name="moisStageId"/);
    assert.match(render(Form, { kind: 'evaluation' }), /<select[^>]*name="encadreurId"/);
    assert.match(render(Form, { kind: 'soutenance' }, ['ADMIN']), /type="datetime-local"/);
    assert.match(render(Actions, { rapport: { id: 1, statut: 'BROUILLON' } }), />Soumettre<\/button>/);
    assert.doesNotMatch(render(Actions, { rapport: { id: 1, statut: 'VALIDE' } }), />(Soumettre|Valider|Demander une correction)<\/button>/);
    assert.match(render(Actions, { rapport: { id: 1, statut: 'SOUMIS' } }), />Demander une correction<\/button>/);
    const notes = render(Notes, { evaluation: { id: 1, estFinalisee: true } });
    assert.match(notes, /notes ne sont plus modifiables/);
    assert.doesNotMatch(notes, />Finaliser l’évaluation<\/button>/);
  } finally { await server.close(); }
});
