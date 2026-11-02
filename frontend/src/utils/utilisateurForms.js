import { requireList } from './businessForms.js';

export function activeRoleOptions(roles) {
  return requireList(roles).filter((role) => role.actif === true && ['ADMIN', 'ENCADREUR', 'STAGIAIRE'].includes(role.code)).map((role) => ({ value: role.code, label: `${role.libelle} (${role.code})` }));
}

export function accountPayload(values, roles) {
  const identifiant = values.identifiant?.trim();
  if (!identifiant || identifiant.length > 50) throw new Error('L’identifiant doit contenir entre 1 et 50 caractères.');
  // Passwords must never be trimmed or otherwise normalized.
  if (typeof values.motDePasse !== 'string' || values.motDePasse.length < 6) throw new Error('Le mot de passe initial doit contenir au moins 6 caractères.');
  if (values.motDePasse !== values.confirmation) throw new Error('Les deux mots de passe ne correspondent pas.');
  if (!activeRoleOptions(roles).some((role) => role.value === values.roleCode)) throw new Error('Sélectionnez un rôle actif.');
  return { identifiant, motDePasse: values.motDePasse, roleCode: values.roleCode };
}

export function resetPasswordPayload(password, confirmation) {
  if (typeof password !== 'string' || password.length < 12) throw new Error('Le nouveau mot de passe doit contenir au moins 12 caractères.');
  if (password !== confirmation) throw new Error('Les deux mots de passe ne correspondent pas.');
  return { nouveauMotDePasse: password };
}

export function requireRoleCodes(value) {
  const codes = requireList(value);
  if (codes.some((code) => typeof code !== 'string')) throw new Error('Le serveur n’a pas renvoyé les rôles attendus.');
  return codes;
}

export function canRemoveRole(roles, code) {
  return roles.includes(code) && roles.length > 1;
}
