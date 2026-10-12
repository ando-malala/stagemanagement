import { hasRole } from '../../utils/roleUtils.js';
import { useAuth } from '../../hooks/useAuth.js';

const NAV_ITEMS = [
  { path: '/dashboard', label: 'Dashboard', icon: 'D' },
  { path: '/utilisateurs', label: 'Utilisateurs', icon: 'U', adminOnly: true },
  { path: '/referentiels', label: 'Référentiels', icon: 'R', adminOnly: true },
  { path: '/stagiaires', label: 'Stagiaires', icon: 'S' },
  { path: '/encadreurs', label: 'Encadreurs', icon: 'E' },
  { path: '/stages', label: 'Stages', icon: 'G' },
  { path: '/rapports', label: 'Rapports', icon: 'P' },
  { path: '/fichiers', label: 'Fichiers', icon: 'F' },
  { path: '/soutenances', label: 'Soutenances', icon: 'O' },
  { path: '/evaluations', label: 'Évaluations', icon: 'V' },
  { path: '/alertes', label: 'Alertes', icon: 'A' },
  { path: '/journal-audit', label: 'Journal d’audit', icon: 'J', adminOnly: true }
];

export default function Sidebar({ currentPath, navigate }) {
  const { utilisateur } = useAuth();
  const items = NAV_ITEMS.filter((item) => !item.adminOnly || hasRole(utilisateur, 'ADMIN'));

  return (
    <aside className="sidebar">
      <div className="brand">
        <span className="brand-mark">SM</span>
        <div>
          <strong>Stage Management</strong>
          <span>SIIGFP</span>
        </div>
      </div>

      <nav className="nav-list" aria-label="Navigation principale">
        {items.map((item) => (
          <button
            key={item.path}
            type="button"
            className={isActive(currentPath, item.path) ? 'nav-item active' : 'nav-item'}
            onClick={() => navigate(item.path)}
            title={item.label}
          >
            <span>{item.icon}</span>
            {item.label}
          </button>
        ))}
      </nav>
    </aside>
  );
}

function isActive(currentPath, itemPath) {
  return currentPath === itemPath || currentPath.startsWith(`${itemPath}/`);
}
