import Button from '../common/Button.jsx';
import AppLink from '../common/AppLink.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';

export default function Topbar({ title, navigate, menu }) {
  const { utilisateur, logout } = useAuth();
  function handleLogout() {
    navigate('/login', { replace: true });
    void logout();
  }
  return <header className="topbar">
    <div className="topbar-heading">{menu}<div><span className="eyebrow">Ministère de l’Économie et des Finances</span><h1>{title}</h1></div></div>
    <div className="account">
      {can(utilisateur, 'consulterMetier') && <AppLink className="btn btn-secondary" to="/alertes" navigate={navigate}>Alertes</AppLink>}
      <details className="user-menu"><summary><strong>{utilisateur?.identifiant}</strong><span>{utilisateur?.roles?.join(', ') || 'Aucun rôle actif'}</span></summary>
        <div className="user-menu-panel"><p>Session de {utilisateur?.identifiant}</p><Button variant="secondary" onClick={handleLogout}>Déconnexion</Button></div>
      </details>
    </div>
  </header>;
}
