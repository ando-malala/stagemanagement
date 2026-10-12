const API_BASE = '';
const TOKEN_KEY = 'token';
const LEGACY_TOKEN_KEY = 'stage_token';

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY) ?? localStorage.getItem(LEGACY_TOKEN_KEY);
}

export function setStoredToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.removeItem(LEGACY_TOKEN_KEY);
}

export function clearStoredToken() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(LEGACY_TOKEN_KEY);
}

export async function apiRequest(path, options = {}) {
  const token = getStoredToken();
  const headers = {
    ...(options.headers || {})
  };

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers
  });

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get('content-type') ?? '';
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = data?.message || data?.error || messageForStatus(response.status);
    const error = new Error(message);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

function messageForStatus(status) {
  if (status === 400) return 'Les informations envoyées ne sont pas valides.';
  if (status === 401) return 'Session expirée ou invalide. Veuillez vous reconnecter.';
  if (status === 403) return 'Vous ne disposez pas des droits nécessaires.';
  if (status === 404) return 'Ressource introuvable.';
  if (status === 409) return 'Cette action entre en conflit avec les données existantes.';
  return 'Erreur serveur.';
}
