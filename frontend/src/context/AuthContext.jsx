/* eslint-disable react-refresh/only-export-components, react-hooks/set-state-in-effect */
import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import { clearStoredToken, getStoredToken, setStoredToken } from '../api/apiClient.js';
import { login as loginApi, logout as logoutApi, me } from '../api/authApi.js';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken());
  const [utilisateur, setUtilisateur] = useState(null);
  const [initialisation, setInitialisation] = useState(Boolean(getStoredToken()));
  const [erreur, setErreur] = useState('');

  const clearSession = useCallback(() => {
    clearStoredToken();
    setToken(null);
    setUtilisateur(null);
  }, []);

  useEffect(() => {
    if (!token) {
      setInitialisation(false);
      return;
    }

    let ignore = false;
    setInitialisation(true);

    me()
      .then((profil) => {
        if (ignore) return;
        setUtilisateur(profil);
        setErreur('');
      })
      .catch((error) => {
        if (ignore) return;
        setErreur(error.message);
        clearSession();
      })
      .finally(() => {
        if (!ignore) setInitialisation(false);
      });

    return () => {
      ignore = true;
    };
  }, [clearSession, token]);

  const login = useCallback(async ({ identifiant, motDePasse }) => {
    const resultat = await loginApi({ identifiant, motDePasse });
    setStoredToken(resultat.token);
    setToken(resultat.token);
    const profil = await me();
    setUtilisateur(profil);
    setErreur('');
    return profil;
  }, []);

  const logout = useCallback(async () => {
    try {
      if (getStoredToken()) {
        await logoutApi();
      }
    } finally {
      clearSession();
    }
  }, [clearSession]);

  const value = useMemo(() => ({
    token,
    utilisateur,
    initialisation,
    erreur,
    estConnecte: Boolean(utilisateur),
    login,
    logout,
    clearSession
  }), [clearSession, erreur, initialisation, login, logout, token, utilisateur]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
