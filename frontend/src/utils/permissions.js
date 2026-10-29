// Mirrors SecurityConfig.java. UI checks never replace server authorization.
import { hasAnyRole } from './roleUtils.js';

export const BUSINESS_ROLES = ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR'];
export const MANAGEMENT_ROLES = ['ADMIN', 'RESPONSABLE_STAGE'];
const permissions = {
  consulterMetier: BUSINESS_ROLES,
  gererStagiaires: MANAGEMENT_ROLES,
  gererStages: MANAGEMENT_ROLES,
  gererEncadreurs: MANAGEMENT_ROLES,
  gererSoutenances: MANAGEMENT_ROLES,
  televerserDocuments: MANAGEMENT_ROLES,
  gererRapports: BUSINESS_ROLES,
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

export function canTreatAlert(utilisateur, alerte) {
  return can(utilisateur, 'traiterAlertes') && alerte?.statut === 'A_TRAITER'
    && alerte?.natureAlerte === 'A_TRAITER';
}
