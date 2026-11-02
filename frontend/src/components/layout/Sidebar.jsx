import { useAuth } from '../../hooks/useAuth.js';
import { matchRoute, visibleNavigation } from '../../routes/routeConfig.js';
import AppLink from '../common/AppLink.jsx';
import NavigationIcon from './NavigationIcon.jsx';

const groups = [
  { label: 'Vue d’ensemble', icons: ['MD', 'TB'] },
  { label: 'Gestion des stages', icons: ['ST', 'EN', 'SP'] },
  { label: 'Suivi des dossiers', icons: ['RA', 'EV', 'SO', 'DO', 'AL'] },
  { label: 'Administration', icons: ['UT', 'RE', 'JA'] }
];

export default function Sidebar({ currentPath, navigate }) {
  const { utilisateur } = useAuth();
  const items = visibleNavigation(utilisateur);
  const route = matchRoute(currentPath);
  return <aside className="sidebar">
    <div className="brand"><span className="brand-mark">MEF</span><div><strong>Suivi des stages</strong><span>SIIGFP · Madagascar</span></div></div>
    <nav className="nav-list" aria-label="Navigation principale">
      {groups.map((group) => {
        const links = items.filter((item) => group.icons.includes(item.icon));
        if (!links.length) return null;
        return <div className="nav-group" key={group.label}>
          <h2 className="nav-group-title">{group.label}</h2>
          <div className="nav-group-links">{links.map((item) => {
            const active = route?.path === item.path || route?.parent === item.path;
            return <AppLink key={item.path} to={item.path} navigate={navigate} className={active ? 'nav-item active' : 'nav-item'} aria-current={active ? 'page' : undefined}>
              <span className="nav-icon"><NavigationIcon name={item.icon} /></span>
              <span className="nav-label">{item.title}</span>
            </AppLink>;
          })}</div>
        </div>;
      })}
      {!items.length && <p>Aucun module disponible pour vos rôles actuels.</p>}
    </nav>
  </aside>;
}
