import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { documentUploadForm, downloadFilename, formatFileSize, MAX_UPLOAD_BYTES } from '../src/utils/documentUtils.js';
import { listFichiersStagiaire, listFichiersParticipation, uploadFichierStagiaire, uploadFichierParticipation, downloadFichier } from '../src/api/fichierApi.js';

test('documents : limite du serveur, fichier vide et code obligatoire', () => {
  const config = readFileSync(new URL('../../backend/src/main/resources/application.properties', import.meta.url), 'utf8');
  assert.match(config, /max-file-size=10MB/);
  assert.match(config, /max-request-size=10MB/);
  for (const file of [null, new Blob([]), { size: MAX_UPLOAD_BYTES }, { size: MAX_UPLOAD_BYTES + 1 }]) assert.throws(() => documentUploadForm(file, 'CODE_TEST'));
  assert.throws(() => documentUploadForm(new Blob(['test']), ' '), /code/);
  const form = documentUploadForm(new File(['test'], 'preuve.txt'), ' CODE_TEST ');
  assert.deepEqual([...form.keys()], ['typePieceJointeCode', 'fichier']);
  assert.equal(form.get('typePieceJointeCode'), 'CODE_TEST');
  assert.equal(form.get('fichier').name, 'preuve.txt');
  assert.equal(formatFileSize(1024), '1 Kio');
});

test('téléchargement : noms UTF-8, repli et suppression de chemins/caractères de contrôle', () => {
  assert.equal(downloadFilename("attachment; filename=backup.pdf; filename*=UTF-8''%C3%A9valuation.pdf", 1), 'évaluation.pdf');
  assert.equal(downloadFilename("attachment; filename=backup.pdf; filename*=UTF-8''%ZZ", 1), 'backup.pdf');
  assert.equal(downloadFilename('attachment; filename="../rapport.pdf"', 1), 'rapport.pdf');
  assert.equal(downloadFilename('attachment; filename="rapport\u0000.pdf"', 1), 'rapport.pdf');
  assert.equal(downloadFilename('', 12), 'fichier-12');
  assert.equal(downloadFilename('attachment; filename=".."', 12), 'fichier-12');
});

test('documents : contrats des deux périmètres, multipart et annulation des lectures', async (context) => {
  const inventory = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const controller = new AbortController();
  for (const [list, upload, prefix] of [[listFichiersStagiaire, uploadFichierStagiaire, '/api/stagiaires'], [listFichiersParticipation, uploadFichierParticipation, '/api/stage-participants']]) {
    for (const method of ['GET', 'POST']) {
      const endpoint = inventory.find((item) => item.path === `${prefix}/{id}/fichiers` && item.method === method);
      assert.ok(endpoint); assert.notEqual(endpoint.access, 'denyAll');
    }
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, `${prefix}/42/fichiers`);
      assert.equal(options.signal, controller.signal);
      return Response.json([]);
    });
    assert.deepEqual(await list(42, { signal: controller.signal }), []);
    const form = documentUploadForm(new Blob(['fixture']), 'CODE_TEST');
    context.mock.method(globalThis, 'fetch', async (url, options) => {
      assert.equal(url, `${prefix}/42/fichiers`);
      assert.equal(options.method, 'POST');
      assert.equal(options.body, form);
      assert.equal(options.headers.has('Content-Type'), false);
      return Response.json({ id: 1 });
    });
    await upload(42, form);
  }
  context.mock.method(globalThis, 'fetch', async () => new Response('', { status: 413 }));
  await assert.rejects(uploadFichierStagiaire(42, new FormData()), (error) => error.status === 413);
});

test('téléchargement binaire authentifié : lien temporaire et nettoyage même après erreur DOM', async (context) => {
  const originalDocument = Object.getOwnPropertyDescriptor(globalThis, 'document');
  const clicks = [];
  const removed = [];
  const revoked = [];
  let failure = false;
  Object.defineProperty(globalThis, 'document', { configurable: true, value: {
    createElement: () => ({ click() { clicks.push({ href: this.href, filename: this.download }); if (failure) throw new Error('click failed'); }, remove() { removed.push(true); } }),
    body: { append() {} }
  } });
  context.mock.method(URL, 'createObjectURL', () => 'blob:fixture');
  context.mock.method(URL, 'revokeObjectURL', (url) => revoked.push(url));
  context.mock.method(globalThis, 'setTimeout', (fn) => { fn(); return 1; });
  context.mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(url, '/api/fichiers/12/telecharger');
    assert.equal(options.headers.get('Authorization'), 'Bearer fixture-token');
    return new Response('fixture', { headers: { 'content-disposition': "attachment; filename*=UTF-8''pi%C3%A8ce.pdf" } });
  });
  try {
    await downloadFichier(12, { token: 'fixture-token' });
    assert.deepEqual(clicks[0], { href: 'blob:fixture', filename: 'pièce.pdf' });
    failure = true;
    await assert.rejects(downloadFichier(12, { token: 'fixture-token' }), /click failed/);
    assert.equal(removed.length, 2);
    assert.deepEqual(revoked, ['blob:fixture', 'blob:fixture']);
  } finally {
    if (originalDocument) Object.defineProperty(globalThis, 'document', originalDocument);
    else delete globalThis.document;
  }
});
