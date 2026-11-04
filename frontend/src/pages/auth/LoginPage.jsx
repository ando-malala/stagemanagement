import { useState } from 'react';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

export default function LoginPage() {
  const { login, erreur } = useAuth();
  const [form, setForm] = useState({
    identifiant: '',
    motDePasse: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login({
        identifiant: form.identifiant.trim(),
        motDePasse: form.motDePasse
      });
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-screen">
      <section className="auth-introduction" aria-labelledby="auth-introduction-title">
        <div className="brand"><span className="brand-mark">MEF</span><div><strong>Suivi des stages</strong><span>SIIGFP · Madagascar</span></div></div>
        <div className="auth-presentation">
          <span className="eyebrow">Accompagner les talents</span>
          <h2 id="auth-introduction-title">Chaque parcours mérite<br />un suivi de qualité.</h2>
          <p>Un espace commun pour accompagner les stagiaires, faciliter l’encadrement et suivre chaque étape du stage.</p>
          <div className="auth-features">
            <div><span className="icon-tile"><NavigationIcon name="ST" /></span><span><strong>Des dossiers centralisés</strong><small>Parcours, affectations et documents</small></span></div>
            <div><span className="icon-tile"><NavigationIcon name="RA" /></span><span><strong>Un accompagnement continu</strong><small>Rapports, évaluations et échéances</small></span></div>
          </div>
        </div>
        <p className="auth-ministry">Ministère de l’Économie et des Finances</p>
      </section>
      <div className="auth-form-panel">
      <form className="auth-card" onSubmit={handleSubmit}>
        <span className="icon-tile auth-form-icon"><NavigationIcon name="UT" /></span>
        <span className="eyebrow">SIIGFP · Suivi des stages</span>
        <h1>Connexion</h1>
        <p>Accédez à votre espace de gestion selon vos habilitations.</p>

        <Input
          label="Identifiant"
          name="identifiant"
          value={form.identifiant}
          onChange={updateField}
          autoComplete="username"
          required
        />

        <Input
          label="Mot de passe"
          name="motDePasse"
          type="password"
          value={form.motDePasse}
          onChange={updateField}
          autoComplete="current-password"
          required
        />

        <ErrorMessage error={error || erreur} />

        <Button type="submit" disabled={loading}>
          {loading ? 'Connexion...' : 'Se connecter'}
        </Button>
        <p className="auth-help">Un problème d’accès ? Contactez votre administrateur.</p>
      </form>
      <p className="auth-footer">MEF · SIIGFP — Gestion et suivi des stagiaires</p>
      </div>
    </main>
  );
}
