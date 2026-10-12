import Sidebar from './Sidebar.jsx';
import Topbar from './Topbar.jsx';

export default function AdminLayout({ children, currentPath, navigate, title }) {
  return (
    <div className="app-shell">
      <Sidebar currentPath={currentPath} navigate={navigate} />
      <div className="workspace">
        <Topbar title={title} navigate={navigate} />
        <main className="content">
          {children}
        </main>
      </div>
    </div>
  );
}
