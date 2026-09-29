import { useCallback, useState } from 'react';
import { login, logout, me } from './api/auth/authApi.js';
import UtilisateursPanel from './components/UtilisateursPanel.jsx';
import './App.css';

export default function App() {
    const [token, setToken] = useState(null);
    const [utilisateur, setUtilisateur] = useState(null);

    const [identifiant, setIdentifiant] = useState('');
    const [motDePasse, setMotDePasse] = useState('');

    const [enCours, setEnCours] = useState(false);
    const [erreur, setErreur] = useState('');

    const viderSession = useCallback(() => {
        setToken(null);
        setUtilisateur(null);
        setMotDePasse('');
    }, []);

    async function connexion(event) {
        event.preventDefault();

        setEnCours(true);
        setErreur('');

        try {
        const resultat = await login(
            identifiant.trim(),
            motDePasse
        );

        const profil = await me(resultat.token);

        setToken(resultat.token);
        setUtilisateur(profil);
        setMotDePasse('');
        } catch (e) {
        viderSession();
        setErreur(e.message);
        } finally {
        setEnCours(false);
        }
    }

    async function deconnexion() {
        setEnCours(true);
        setErreur('');

        try {
        if (token) {
            await logout(token);
        }
        } catch (e) {
        setErreur('La déconnexion serveur n’a pas pu être confirmée.');
        } finally {
        viderSession();
        setEnCours(false);
        }
    }

    if (!utilisateur) {
        return (
        <main className="auth-screen">
            <form className="auth-card" onSubmit={connexion}>
            <span className="eyebrow">
                SIIGFP · Suivi des stages
            </span>

            <h1>Connexion</h1>

            <p>
                Accédez à votre espace de suivi des stagiaires.
            </p>

            <label>
                Identifiant
                <input
                value={identifiant}
                onChange={(e) => setIdentifiant(e.target.value)}
                autoComplete="username"
                required
                autoFocus
                />
            </label>

            <label>
                Mot de passe
                <input
                type="password"
                value={motDePasse}
                onChange={(e) => setMotDePasse(e.target.value)}
                autoComplete="current-password"
                required
                />
            </label>

            {erreur && (
                <p role="alert" className="error">
                {erreur}
                </p>
            )}

            <button type="submit" disabled={enCours}>
                {enCours ? 'Connexion...' : 'Se connecter'}
            </button>
            </form>
        </main>
        );
    }

    const estAdmin = utilisateur.roles.includes('ADMIN');

    return (
        <div className="app-shell">
        <header className="topbar">
            <div>
            <span className="eyebrow">
                Cellule SIIGFP
            </span>

            <h1>Suivi des stages</h1>
            </div>

            <div className="account">
            <div>
                <strong>{utilisateur.identifiant}</strong>
                <span>{utilisateur.roles.join(', ')}</span>
            </div>

            <button
                type="button"
                className="secondary"
                disabled={enCours}
                onClick={deconnexion}
            >
                Déconnexion
            </button>
            </div>
        </header>

        <main className="content">
            {erreur && (
            <p role="alert" className="error">
                {erreur}
            </p>
            )}

            {estAdmin ? (
            <UtilisateursPanel
                token={token}
                onSessionExpiree={viderSession}
            />
            ) : (
            <section className="panel">
                <h2>Bienvenue</h2>

                <p>
                Vous êtes connecté. Les fonctionnalités de votre rôle seront ajoutées au prochain module.
                </p>
            </section>
            )}
        </main>
        </div>
    );
}