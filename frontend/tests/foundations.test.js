import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { formatDate, formatDateTime } from '../src/utils/dateUtils.js';
import { normalizePayload } from '../src/utils/formUtils.js';
import { can, canTreatAlert } from '../src/utils/permissions.js';

test('LocalDate et LocalDateTime sans décalage; instant converti à Madagascar', () => {
  assert.equal(formatDate('2026-10-29'), '29 oct. 2026');
  assert.match(formatDateTime('2026-10-29T00:30:00'), /29 oct. 2026.*00:30/);
  assert.match(formatDateTime('2026-10-28T22:30:00Z'), /29 oct. 2026.*01:30/);
  assert.equal(formatDate('incorrect'), '—');
  assert.equal(formatDateTime(null), '—');
});

test('payload : nombres, booléens, champs absents, mot de passe intact', () => {
  assert.deepEqual(normalizePayload({ id: '5', actif: 'false', commentaire: '', motDePasse: ' secret ' }, [
    { name: 'id', valueType: 'number' }, { name: 'actif', valueType: 'boolean' }, { name: 'commentaire' }, { name: 'motDePasse' }
  ]), { id: 5, actif: false, motDePasse: ' secret ' });
  assert.throws(() => normalizePayload({ id: 'abc' }, [{ name: 'id', type: 'number', label: 'Identifiant' }]), /nombre valide/);
});

test('permissions métier, refus par défaut et rôle STAGIAIRE sans accès métier', () => {
  assert.equal(can({ roles: ['ENCADREUR'] }, 'gererRapports'), true);
  assert.equal(can({ roles: ['ENCADREUR'] }, 'gererStages'), false);
  assert.equal(can({ roles: ['ADMIN'] }, 'gererUtilisateurs'), true);
  assert.equal(can({ roles: ['STAGIAIRE'] }, 'consulterMetier'), false);
  assert.equal(can({ roles: ['ADMIN'] }, 'actionInconnue'), false);
  assert.equal(can(null, 'consulterMetier'), false);
});

test('traitement des alertes : nature du DTO et état réel, sans confondre priorité', () => {
  const user = { roles: ['ENCADREUR'] };
  assert.equal(canTreatAlert(user, { natureAlerte: 'A_TRAITER', priorite: 'INFORMATION', statut: 'A_TRAITER' }), true);
  assert.equal(canTreatAlert(user, { natureAlerte: 'INFORMATION', priorite: 'URGENT', statut: 'A_TRAITER' }), false);
  assert.equal(canTreatAlert(user, { natureAlerte: 'A_TRAITER', statut: 'TRAITEE' }), false);
  const source = readFileSync(new URL('../../backend/src/main/java/mg/mef/stagemanagement/model/enums/NatureAlerte.java', import.meta.url), 'utf8');
  assert.match(source, /A_TRAITER/);
});

test('inventaire : endpoints métiers admis et CRUD sensibles fermés', () => {
  const endpoints = JSON.parse(readFileSync(new URL('../../docs/api-inventory.json', import.meta.url), 'utf8'));
  const find = (method, path) => endpoints.find((e) => e.method === method && e.path === path);
  assert.deepEqual(find('POST', '/api/stage-participants/{id}/suspendre').roles, ['ADMIN']);
  assert.equal(find('POST', '/api/stage-participants/{id}/suspendre').body, 'EvenementParticipationRequest');
  assert.equal(find('GET', '/api/types-piece-jointe').access, 'denyAll');
  assert.equal(find('GET', '/api/sessions-utilisateur').access, 'denyAll');
  assert.equal(find('GET', '/api/referentiels/academiques/mois-stage').access, 'authenticated');
  assert.equal(find('PATCH', '/api/rapports-activite/{id}/statut').body, 'ChangerStatutRapportRequest');
});
