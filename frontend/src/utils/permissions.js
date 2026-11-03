// Mirrors SecurityConfig.java. UI checks never replace server authorization.
import { hasAnyRole } from './roleUtils.js';

export const BUSINESS_ROLES = ['ADMIN', 'ENCADREUR'];
export const PERSONAL_ROLES = [...BUSINESS_ROLES, 'STAGIAIRE'];
export function isTraineeOnly(user) { return hasAnyRole(user, ['STAGIAIRE']) && !hasAnyRole(user, BUSINESS_ROLES); }
export const MANAGEMENT_ROLES = ['ADMIN'];
const permissions = {
  consulterMetier: BUSINESS_ROLES,
  gererStagiaires: MANAGEMENT_ROLES,
  gererStages: MANAGEMENT_ROLES,
  gererEncadreurs: MANAGEMENT_ROLES,
  gererSoutenances: MANAGEMENT_ROLES,
  televerserDocuments: MANAGEMENT_ROLES,
  gererRapports: ['STAGIAIRE'],
  validerRapports: ['ENCADREUR'],
  gererEvaluations: BUSINESS_ROLES,
  traiterAlertes: BUSINESS_ROLES,
  creerAlertes: MANAGEMENT_ROLES,
  gererUtilisateurs: ['ADMIN'],
  modifierReferentiels: ['ADMIN'],
  consulterAudit: ['ADMIN']
};

export function can(utilisateur, action) {
  return hasAnyRole(utilisateur, permissions[action] ?? []);
}

export function canActOnReport(utilisateur, rapport, target) {
  if (target === 'SOUMIS') return can(utilisateur, 'gererRapports') && rapport?.peutRediger === true
    && ['BROUILLON', 'A_CORRIGER'].includes(rapport.statut);
  return ['VALIDE', 'A_CORRIGER'].includes(target) && can(utilisateur, 'validerRapports')
    && rapport?.peutExaminer === true && rapport.statut === 'SOUMIS';
}

export function canTreatAlert(utilisateur, alerte) {
  return can(utilisateur, 'traiterAlertes') && alerte?.statut === 'A_TRAITER'
    && alerte?.natureAlerte === 'A_TRAITER';
}
