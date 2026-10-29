// Session lifecycle independent of React so races can be tested without a server.
export function createSessionStore({ api, storage }) {
  const initialToken = storage.get();
  let state = { token: initialToken, utilisateur: null, status: initialToken ? 'checking' : 'anonymous', erreur: '' };
  let generation = 0;
  let controller;
  const listeners = new Set();
  const update = (patch) => { state = { ...state, ...patch }; listeners.forEach((listener) => listener()); };
  const cancel = () => { generation += 1; controller?.abort(); };
  const begin = () => { cancel(); controller = new AbortController(); return { version: generation, signal: controller.signal }; };
  const validProfile = (profile) => profile && Number.isInteger(profile.utilisateurId) && profile.utilisateurId > 0
    && typeof profile.identifiant === 'string' && Array.isArray(profile.roles)
    && profile.roles.every((role) => typeof role === 'string');

  async function verify(token, operation) {
    try {
      const profile = await api.me({ token, signal: operation.signal });
      if (operation.version !== generation) return;
      if (!validProfile(profile)) throw new Error('Le profil reçu du serveur est incomplet.');
      update({ utilisateur: profile, status: 'authenticated', erreur: '' });
      return profile;
    } catch (error) {
      if (operation.version !== generation || error.name === 'AbortError') return;
      if (error.status === 401 || error.status === 403) {
        storage.clear();
        update({ token: null, utilisateur: null, status: 'anonymous', erreur: error.message });
      } else {
        update({ utilisateur: null, status: 'unavailable', erreur: error.message });
      }
    }
  }

  return {
    getSnapshot: () => state,
    subscribe: (listener) => { listeners.add(listener); return () => listeners.delete(listener); },
    stop: cancel,
    async restore() {
      const operation = begin();
      const token = storage.get();
      update({ token, utilisateur: null, status: token ? 'checking' : 'anonymous' });
      if (token) return verify(token, operation);
    },
    async login(credentials) {
      const operation = begin();
      update({ status: 'checking', utilisateur: null, erreur: '' });
      try {
        const result = await api.login(credentials, { signal: operation.signal });
        if (operation.version !== generation) return;
        if (!result?.token || typeof result.token !== 'string') throw new Error('La réponse de connexion est incomplète.');
        storage.set(result.token);
        update({ token: result.token });
        return await verify(result.token, operation);
      } catch (error) {
        if (operation.version !== generation || error.name === 'AbortError') return;
        storage.clear();
        update({ token: null, utilisateur: null, status: 'anonymous', erreur: error.message });
        throw error;
      }
    },
    expire() {
      cancel();
      storage.clear();
      update({ token: null, utilisateur: null, status: 'anonymous', erreur: 'Session expirée ou invalide. Veuillez vous reconnecter.' });
    },
    async logout() {
      const token = state.token;
      cancel();
      const version = generation;
      storage.clear();
      update({ token: null, utilisateur: null, status: 'anonymous', erreur: '' });
      if (!token) return;
      try { await api.logout({ token }); }
      catch (error) {
        if (version === generation && error.status !== 401) update({ erreur: 'Session locale fermée. La révocation sur le serveur n’a pas pu être confirmée.' });
      }
    }
  };
}
