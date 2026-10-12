export function hasRole(utilisateur, role) {
  return Boolean(utilisateur?.roles?.includes(role));
}

export function hasAnyRole(utilisateur, roles = []) {
  return roles.some((role) => hasRole(utilisateur, role));
}
