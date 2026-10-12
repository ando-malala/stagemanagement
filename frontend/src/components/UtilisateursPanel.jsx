/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useState } from 'react';
import {
    creerUtilisateur,
    listerUtilisateurs
} from '../api/utilisateur/utilisateurApi.js';

const ROLES = [
    'ADMIN',
    'RESPONSABLE_STAGE',
    'ENCADREUR',
    'STAGIAIRE'
];

export default function UtilisateursPanel({token, onSessionExpiree}) {
    const [utilisateurs, setUtilisateurs] = useState([]);
    const [chargement, setChargement] = useState(true);

    const [identifiant, setIdentifiant] = useState('');
    const [motDePasse, setMotDePasse] = useState('');
    const [roleCode, setRoleCode] = useState('STAGIAIRE');

    const [soumission, setSoumission] = useState(false);
    const [message, setMessage] = useState('');
    const [erreur, setErreur] = useState('');

    const charger = useCallback(async () => {
        try {
            const donnees = await listerUtilisateurs(token);

            setUtilisateurs(donnees);
            setErreur('');
        } catch (e) {
            if (e.status === 401) {
                onSessionExpiree();
            } else {
                setErreur(e.message);
            }
        } finally {
            setChargement(false);
        }
    }, [token, onSessionExpiree]);

    useEffect(() => {
        void charger();
    }, [charger]);

    async function soumettre(event) {
        event.preventDefault();

        if (!identifiant.trim() || motDePasse.length < 12) {
            setErreur(
                'Indiquez un identifiant et un mot de passe d’au moins 12 caractères.'
            );
            return;
        }

        setSoumission(true);
        setErreur('');
        setMessage('');

        try {
            await creerUtilisateur(token, {
                identifiant: identifiant.trim(),
                motDePasse,
                roleCode
            });

            setIdentifiant('');
            setMotDePasse('');
            setMessage('Compte créé.');

            await charger();
        } catch (e) {
            if (e.status === 401) {
                onSessionExpiree();
            } else {
                setErreur(e.message);
            }
        } finally {
            setSoumission(false);
        }
    }

    return (
        <section className="panel">
            <div className="section-heading">
                <h2>Utilisateurs</h2>

                <span className="small">
                    Administration des comptes
                </span>
            </div>

            <form className="user-form" onSubmit={soumettre}>
                <label>
                    Identifiant
                    <input
                        value={identifiant}
                        maxLength={50}
                        autoComplete="off"
                        onChange={(e) => setIdentifiant(e.target.value)}
                        required
                    />
                </label>

                <label>
                    Mot de passe initial
                    <input
                        type="password"
                        value={motDePasse}
                        minLength={12}
                        autoComplete="new-password"
                        onChange={(e) => setMotDePasse(e.target.value)}
                        required
                    />
                </label>

                <label>
                    Rôle
                    <select
                        value={roleCode}
                        onChange={(e) => setRoleCode(e.target.value)}
                    >
                        {ROLES.map((role) => (
                            <option key={role} value={role}>
                                {role}
                            </option>
                        ))}
                    </select>
                </label>

                <button type="submit" disabled={soumission}>
                    {soumission ? 'Création...' : 'Créer le compte'}
                </button>
            </form>

            {message && (
                <p className="success" role="status">
                {message}
                </p>
            )}

            {erreur && (
                <p className="error" role="alert">
                {erreur}
                </p>
            )}

            {chargement ? (
                <p>Chargement des utilisateurs...</p>
            ) : (
                <div className="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Identifiant</th>
                                <th>Statut</th>
                            </tr>
                        </thead>

                        <tbody>
                            {utilisateurs.map((utilisateur) => (
                                <tr key={utilisateur.id}>
                                    <td>{utilisateur.id}</td>
                                    <td>{utilisateur.identifiant}</td>
                                    <td>{utilisateur.statut}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}
