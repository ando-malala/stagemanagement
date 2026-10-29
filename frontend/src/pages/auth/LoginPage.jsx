import { useState } from 'react';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import { useAuth } from '../../hooks/useAuth.js';

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
      <form className="auth-card" onSubmit={handleSubmit}>
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

        {(error || erreur) && <p className="alert alert-danger" role="alert">{error || erreur}</p>}

        <Button type="submit" disabled={loading}>
          {loading ? 'Connexion...' : 'Se connecter'}
        </Button>
      </form>
    </main>
  );
}
