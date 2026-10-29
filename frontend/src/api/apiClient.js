const TOKEN_KEY = 'token';
const LEGACY_TOKEN_KEY = 'stage_token';
export const SESSION_EXPIRED_EVENT = 'mef:session-expired';

export function getStoredToken() {
  return globalThis.localStorage?.getItem(TOKEN_KEY) ?? globalThis.localStorage?.getItem(LEGACY_TOKEN_KEY) ?? null;
}
export function setStoredToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.removeItem(LEGACY_TOKEN_KEY);
}
export function clearStoredToken() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(LEGACY_TOKEN_KEY);
}

export class ApiError extends Error {
  constructor(message, { status = 0, data = null, fieldErrors = {}, cause } = {}) {
    super(message, { cause });
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
    this.fieldErrors = fieldErrors;
  }
}

export function messageForStatus(status) {
  const messages = {
    400: 'Les informations envoyées ne sont pas valides.',
    401: 'Session expirée ou invalide. Veuillez vous reconnecter.',
    403: 'Vous ne disposez pas des droits nécessaires.',
    404: 'Ressource introuvable.',
    409: 'Cette action entre en conflit avec les données existantes.',
    413: 'Le fichier ou la requête dépasse la taille autorisée par le serveur.',
    422: 'Les informations envoyées ne respectent pas les règles de validation.',
    500: 'Une erreur est survenue sur le serveur.'
  };
  return messages[status] ?? `La requête a échoué (${status}).`;
}

export function buildApiUrl(path, baseUrl = '', query) {
  // Only verified relative API paths: never send a token to an arbitrary URL.
  if (!path.startsWith('/api/')) throw new Error('Une route API relative commençant par /api/ est requise.');
  const base = baseUrl.trim().replace(/\/+$/, '');
  const url = `${base.endsWith('/api') ? base.slice(0, -4) : base}${path}`;
  const params = new URLSearchParams();
  for (const [key, value] of Object.entries(query ?? {})) {
    if (value === undefined || value === null || value === '') continue;
    for (const item of Array.isArray(value) ? value : [value]) params.append(key, String(item));
  }
  return params.size ? `${url}${url.includes('?') ? '&' : '?'}${params}` : url;
}

function validationErrors(data) {
  if (Array.isArray(data?.errors)) {
    return Object.fromEntries(data.errors.filter((item) => item.field && item.defaultMessage)
      .map((item) => [item.field, item.defaultMessage]));
  }
  return data?.fieldErrors ?? {};
}

export function createApiClient({ baseUrl = '', fetchImpl = (...args) => fetch(...args), getToken = getStoredToken, onUnauthorized = () => {} } = {}) {
  return async function request(path, options = {}) {
    const { auth = true, token: explicitToken, query, responseType = 'auto', ...fetchOptions } = options;
    const token = auth ? (explicitToken ?? getToken()) : null;
    const headers = new Headers(fetchOptions.headers);
    headers.delete('Authorization');
    if (token) headers.set('Authorization', `Bearer ${token}`);
    if (fetchOptions.body instanceof FormData) {
      headers.delete('Content-Type');
    } else if (fetchOptions.body != null && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
    const url = buildApiUrl(path, baseUrl, query);
    let response;
    try {
      response = await fetchImpl(url, { ...fetchOptions, headers });
    } catch (cause) {
      if (cause.name === 'AbortError') throw cause;
      throw new ApiError('Connexion au serveur impossible. Vérifiez votre connexion et réessayez.', { cause });
    }
    if (!response.ok) {
      if (response.status === 401 && auth && token) onUnauthorized(token);
      const contentType = response.headers.get('content-type') ?? '';
      let data = null;
      try {
        data = contentType.includes('json') ? await response.json() : await response.text();
      } catch { /* A proxy may return an empty or malformed error body. */ }
      const fieldErrors = validationErrors(data);
      const message = [data?.detail, data?.message, ...Object.values(fieldErrors)]
        .find((value) => typeof value === 'string' && value.trim());
      throw new ApiError(message || messageForStatus(response.status), { status: response.status, data, fieldErrors });
    }
    if (response.status === 204) return null;
    if (responseType === 'blob') return { blob: await response.blob(), headers: response.headers };
    const body = await response.text();
    if (!body) return null;
    if ((response.headers.get('content-type') ?? '').includes('json')) {
      try { return JSON.parse(body); }
      catch (cause) { throw new ApiError('Le serveur a renvoyé une réponse JSON illisible.', { status: response.status, cause }); }
    }
    return body;
  };
}

export const apiRequest = createApiClient({
  baseUrl: import.meta.env?.VITE_API_URL ?? '',
  onUnauthorized: (failedToken) => {
    // A late response for an old session must not close a newer login.
    if (getStoredToken() !== failedToken) return;
    clearStoredToken();
    globalThis.window?.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
  }
});
