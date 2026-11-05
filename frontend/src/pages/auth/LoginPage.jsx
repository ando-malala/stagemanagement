import { useState } from 'react';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import { useAuth } from '../../hooks/useAuth.js';
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
      <form className="auth-card login-card" onSubmit={handleSubmit} aria-labelledby="login-title">
        <div className="login-brand">
          <span className="brand-mark">MEF</span>
          <div><strong>SIIGFP · Suivi des stages</strong><p>Ministère de l’Économie et des Finances</p></div>
        </div>
        <header className="login-heading">
          <h1 id="login-title">Connexion</h1>
          <p>Connectez-vous à votre espace de suivi.</p>
        </header>

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
    </main>
  );
}
