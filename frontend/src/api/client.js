export async function apiRequest(token, path, options = {}) {
  const headers = {
    ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers
  };

  const response = await fetch(path, {
    ...options,
    headers
  });

  if (!response.ok) {
    const error = new Error(messageForStatus(response.status));
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get('content-type') ?? '';
  return contentType.includes('application/json') ? response.json() : response.text();
}

function messageForStatus(status) {
  if (status === 400) return 'Les informations envoyées ne sont pas valides.';
  if (status === 401) return 'Session expirée ou invalide. Veuillez vous reconnecter.';
  if (status === 403) return 'Vous ne disposez pas des droits nécessaires.';
  if (status === 404) return 'Élément introuvable.';
  if (status === 409) return 'Cette action entre en conflit avec les données existantes.';
  return `Erreur serveur (${status}).`;
}
