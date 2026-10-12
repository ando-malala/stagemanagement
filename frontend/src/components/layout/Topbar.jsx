import Button from '../common/Button.jsx';
import { useAuth } from '../../hooks/useAuth.js';

export default function Topbar({ title, navigate }) {
  const { utilisateur, logout } = useAuth();

  async function handleLogout() {
    await logout();
    navigate('/login', { replace: true });
  }

  return (
    <header className="topbar">
      <div>
        <span className="eyebrow">Cellule SIIGFP</span>
        <h1>{title}</h1>
      </div>

      <div className="account">
        <div>
          <strong>{utilisateur?.identifiant}</strong>
          <span>{utilisateur?.roles?.join(', ')}</span>
        </div>
        <Button variant="secondary" onClick={handleLogout}>Déconnexion</Button>
      </div>
    </header>
  );
}
