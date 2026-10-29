// The raw controller is denied by SecurityConfig, including ADMIN.
export function getTypeAlerte() {
  return Promise.reject(new Error('Le référentiel des types d’alerte n’est pas accessible via l’API actuelle.'));
}
