import test from 'node:test';
import assert from 'node:assert/strict';
import { breadcrumbItems, loginDestination, matchRoute, safeReturnTo, visibleNavigation } from '../src/routes/routeConfig.js';

test('routes de détail : ID Java Integer positif, pas de correspondance ambiguë', () => {
  assert.equal(matchRoute('/participations/12/').page, 'ParticipationDetailPage');
  assert.deepEqual(matchRoute('/participations/12/').params, { id: '12' });
  for (const path of ['/stages/abc', '/stages/0', '/stages/-1', '/stages/2147483648', '/stages/1/extra', '/stages/%2F', '/absent']) assert.equal(matchRoute(path), null);
});
test('destination après connexion : chemins locaux connus seulement', () => {
  for (const value of [null, '//example.com', 'https://example.com', '/\\example.com', '/login', '/api/auth/me', '/%2f%2fexample.com']) assert.equal(safeReturnTo(value), '/dashboard');
  const target = '/participations/42?onglet=suivi#documents';
  const url = new URL(loginDestination(target), 'http://localhost');
  assert.equal(safeReturnTo(url.searchParams.get('retour')), target);
});
test('navigation conforme aux rôles et aucune entrée métier pour STAGIAIRE', () => {
  assert.equal(visibleNavigation({ roles: ['STAGIAIRE'] }).length, 0);
  assert.equal(visibleNavigation(null).length, 0);
  const encadreur = visibleNavigation({ roles: ['ENCADREUR'] });
  assert.ok(encadreur.some((r) => r.path === '/rapports'));
  assert.ok(!encadreur.some((r) => r.path === '/utilisateurs'));
  assert.ok(visibleNavigation({ roles: ['ADMIN'] }).some((r) => r.path === '/journal-audit'));
});
test('fil d’Ariane des participations rattaché aux stages', () => {
  const items = breadcrumbItems(matchRoute('/participations/42'));
  assert.equal(items[1].path, '/stages');
  assert.equal(items[2].title, 'Dossier de participation n° 42');
});
