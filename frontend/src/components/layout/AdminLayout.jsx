import { useAuth } from '../../hooks/useAuth.js';
import Sidebar from './Sidebar.jsx';
import Topbar from './Topbar.jsx';
import MobileMenu from './MobileMenu.jsx';
import Breadcrumbs from './Breadcrumbs.jsx';

export default function AdminLayout({ children, currentPath, navigate, title, route }) {
  const { utilisateur } = useAuth();
  return <div className="app-shell">
    <a className="skip-link" href="#main-content">Aller au contenu</a>
    <div className="desktop-navigation"><Sidebar currentPath={currentPath} navigate={navigate} /></div>
    <div className="workspace">
      <Topbar title={title} navigate={navigate} menu={<MobileMenu key={currentPath} currentPath={currentPath} navigate={navigate} />} />
      <main className="content" id="main-content" tabIndex={-1}>
        <Breadcrumbs utilisateur={utilisateur} route={route} navigate={navigate} />
        {children}
      </main>
    </div>
  </div>;
}
