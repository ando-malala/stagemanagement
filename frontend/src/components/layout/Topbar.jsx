import Button from '../common/Button.jsx';
import AppLink from '../common/AppLink.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import NavigationIcon from './NavigationIcon.jsx';

export default function Topbar({ title, navigate, menu }) {
  const { utilisateur, logout } = useAuth();
  function handleLogout() {
    navigate('/login', { replace: true });
    void logout();
  }
  return <header className="topbar">
    <div className="topbar-heading">{menu}<div><span className="eyebrow">Ministère de l’Économie et des Finances</span><h1>{title}</h1></div></div>
    <div className="account">
      {can(utilisateur, 'consulterMetier') && <AppLink className="btn btn-secondary alerts-shortcut" to="/alertes" navigate={navigate}><NavigationIcon name="AL" />Alertes</AppLink>}
      <details className="user-menu"><summary><span className="account-avatar" aria-hidden="true">{utilisateur?.identifiant?.slice(0, 2).toLocaleUpperCase('fr') || 'ME'}</span><span className="account-identity"><strong>{utilisateur?.identifiant}</strong><small>{utilisateur?.roles?.join(', ') || 'Aucun rôle actif'}</small></span></summary>
        <div className="user-menu-panel"><p>Session de {utilisateur?.identifiant}</p><Button variant="secondary" onClick={handleLogout}>Déconnexion</Button></div>
      </details>
    </div>
  </header>;
}
