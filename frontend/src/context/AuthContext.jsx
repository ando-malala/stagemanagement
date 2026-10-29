/* eslint-disable react-refresh/only-export-components */
import { createContext, useEffect, useState, useSyncExternalStore } from 'react';
import { clearStoredToken, getStoredToken, setStoredToken, SESSION_EXPIRED_EVENT } from '../api/apiClient.js';
import * as api from '../api/authApi.js';
import { createSessionStore } from './sessionStore.js';

export const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [store] = useState(() => createSessionStore({ api, storage: { get: getStoredToken, set: setStoredToken, clear: clearStoredToken } }));
  const state = useSyncExternalStore(store.subscribe, store.getSnapshot);
  useEffect(() => {
    void store.restore();
    const synchronize = (event) => {
      if (['token', 'stage_token', null].includes(event.key) && getStoredToken() !== store.getSnapshot().token) void store.restore();
    };
    window.addEventListener(SESSION_EXPIRED_EVENT, store.expire);
    window.addEventListener('storage', synchronize);
    return () => {
      store.stop();
      window.removeEventListener(SESSION_EXPIRED_EVENT, store.expire);
      window.removeEventListener('storage', synchronize);
    };
  }, [store]);
  return <AuthContext.Provider value={{ ...state, initialisation: state.status === 'checking', estConnecte: state.status === 'authenticated', login: store.login, logout: store.logout, retrySession: store.restore }}>{children}</AuthContext.Provider>;
}
