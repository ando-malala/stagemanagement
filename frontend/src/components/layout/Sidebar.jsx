import { useAuth } from '../../hooks/useAuth.js';
import { matchRoute, visibleNavigation } from '../../routes/routeConfig.js';
import AppLink from '../common/AppLink.jsx';

export default function Sidebar({ currentPath, navigate }) {
  const { utilisateur } = useAuth();
  const items = visibleNavigation(utilisateur);
  const route = matchRoute(currentPath);
  return <aside className="sidebar">
    <div className="brand"><span className="brand-mark">MEF</span><div><strong>Suivi des stages</strong><span>SIIGFP · Madagascar</span></div></div>
    <nav className="nav-list" aria-label="Navigation principale">
      {items.map((item) => {
        const active = route?.path === item.path || route?.parent === item.path;
        return <AppLink key={item.path} to={item.path} navigate={navigate} className={active ? 'nav-item active' : 'nav-item'} aria-current={active ? 'page' : undefined}>
          <span aria-hidden="true">{item.icon}</span>{item.title}
        </AppLink>;
      })}
      {!items.length && <p>Aucun module disponible pour vos rôles actuels.</p>}
    </nav>
  </aside>;
}
