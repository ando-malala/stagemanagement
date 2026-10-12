/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useMemo, useState } from 'react';
import AdminLayout from '../components/layout/AdminLayout.jsx';
import ProtectedRoute from '../components/layout/ProtectedRoute.jsx';
import Loader from '../components/common/Loader.jsx';
import { useAuth } from '../hooks/useAuth.js';
import { hasAnyRole } from '../utils/roleUtils.js';
import LoginPage from '../pages/auth/LoginPage.jsx';
import DashboardPage from '../pages/dashboard/DashboardPage.jsx';
import UtilisateursPage from '../pages/utilisateurs/UtilisateursPage.jsx';
import ReferentielsPage from '../pages/referentiels/ReferentielsPage.jsx';
import AcademiquesPage from '../pages/referentiels/AcademiquesPage.jsx';
import OrganisationPage from '../pages/referentiels/OrganisationPage.jsx';
import StagiairesPage from '../pages/stagiaires/StagiairesPage.jsx';
import EncadreursPage from '../pages/encadreurs/EncadreursPage.jsx';
import StagesPage from '../pages/stages/StagesPage.jsx';
import RapportsPage from '../pages/rapports/RapportsPage.jsx';
import SoutenancesPage from '../pages/soutenances/SoutenancesPage.jsx';
import EvaluationsPage from '../pages/evaluations/EvaluationsPage.jsx';
import AlertesPage from '../pages/alertes/AlertesPage.jsx';
import JournalAuditPage from '../pages/audit/JournalAuditPage.jsx';
import FichiersPage from '../pages/fichiers/FichiersPage.jsx';
import ForbiddenPage from '../pages/errors/ForbiddenPage.jsx';
import NotFoundPage from '../pages/errors/NotFoundPage.jsx';

const BUSINESS_ROLES = ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR'];

const ROUTES = [
  { path: '/dashboard', title: 'Dashboard', component: DashboardPage, roles: BUSINESS_ROLES },
  { path: '/utilisateurs', title: 'Utilisateurs', component: UtilisateursPage, roles: ['ADMIN'] },
  { path: '/utilisateurs/:id', title: 'Détail utilisateur', component: UtilisateursPage, roles: ['ADMIN'] },
  { path: '/referentiels', title: 'Référentiels', component: ReferentielsPage, roles: ['ADMIN'] },
  { path: '/referentiels/academiques', title: 'Référentiels académiques', component: AcademiquesPage, roles: ['ADMIN'] },
  { path: '/referentiels/organisation', title: 'Référentiels organisation', component: OrganisationPage, roles: ['ADMIN'] },
  { path: '/stagiaires', title: 'Stagiaires', component: StagiairesPage, roles: BUSINESS_ROLES },
  { path: '/stagiaires/:id', title: 'Détail stagiaire', component: StagiairesPage, roles: BUSINESS_ROLES },
  { path: '/encadreurs', title: 'Encadreurs', component: EncadreursPage, roles: BUSINESS_ROLES },
  { path: '/encadreurs/:id', title: 'Détail encadreur', component: EncadreursPage, roles: BUSINESS_ROLES },
  { path: '/stages', title: 'Stages', component: StagesPage, roles: BUSINESS_ROLES },
  { path: '/stages/:id', title: 'Détail stage', component: StagesPage, roles: BUSINESS_ROLES },
  { path: '/participations/:id', title: 'Détail participation', component: StagesPage, roles: BUSINESS_ROLES },
  { path: '/rapports', title: 'Rapports', component: RapportsPage, roles: BUSINESS_ROLES },
  { path: '/rapports/:id', title: 'Détail rapport', component: RapportsPage, roles: BUSINESS_ROLES },
  { path: '/fichiers', title: 'Fichiers', component: FichiersPage, roles: BUSINESS_ROLES },
  { path: '/soutenances', title: 'Soutenances', component: SoutenancesPage, roles: BUSINESS_ROLES },
  { path: '/evaluations', title: 'Évaluations', component: EvaluationsPage, roles: BUSINESS_ROLES },
  { path: '/evaluations/:id', title: 'Détail évaluation', component: EvaluationsPage, roles: BUSINESS_ROLES },
  { path: '/alertes', title: 'Alertes', component: AlertesPage, roles: BUSINESS_ROLES },
  { path: '/journal-audit', title: 'Journal d’audit', component: JournalAuditPage, roles: ['ADMIN'] }
];

export default function AppRoutes() {
  const { estConnecte, initialisation, utilisateur } = useAuth();
  const [path, setPath] = useState(() => normalizePath(window.location.pathname));

  const navigate = useCallback((to, options = {}) => {
    if (options.replace) {
      window.history.replaceState({}, '', to);
    } else {
      window.history.pushState({}, '', to);
    }
    setPath(normalizePath(window.location.pathname));
  }, []);

  useEffect(() => {
    function handlePopState() {
      setPath(normalizePath(window.location.pathname));
    }

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, []);

  useEffect(() => {
    if (initialisation) return;

    if (path === '/') {
      navigate(estConnecte ? '/dashboard' : '/login', { replace: true });
      return;
    }

    if (path === '/login' && estConnecte) {
      navigate('/dashboard', { replace: true });
    }
  }, [estConnecte, initialisation, navigate, path]);

  const match = useMemo(() => findRoute(path), [path]);

  if (initialisation) {
    return (
      <main className="center-screen">
        <Loader label="Préparation de l’application..." />
      </main>
    );
  }

  if (path === '/login') {
    return <LoginPage navigate={navigate} />;
  }

  const Component = match?.component ?? NotFoundPage;
  const title = match?.title ?? 'Page introuvable';
  const allowed = !match || hasAnyRole(utilisateur, match.roles);

  return (
    <ProtectedRoute navigate={navigate}>
      <AdminLayout currentPath={path} navigate={navigate} title={title}>
        {allowed ? <Component navigate={navigate} /> : <ForbiddenPage />}
      </AdminLayout>
    </ProtectedRoute>
  );
}

function normalizePath(path) {
  if (!path || path === '/') return '/';
  return path.endsWith('/') ? path.slice(0, -1) : path;
}

function findRoute(path) {
  return ROUTES.find((route) => route.path === path || matchesDynamic(route.path, path));
}

function matchesDynamic(pattern, path) {
  if (!pattern.includes(':')) return false;

  const patternParts = pattern.split('/');
  const pathParts = path.split('/');

  if (patternParts.length !== pathParts.length) return false;

  return patternParts.every((part, index) => part.startsWith(':') || part === pathParts[index]);
}
