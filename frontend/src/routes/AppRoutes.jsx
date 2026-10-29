/* eslint-disable react-hooks/set-state-in-effect -- Synchronizes History API with session changes. */
import { useCallback, useEffect, useState } from 'react';
import AdminLayout from '../components/layout/AdminLayout.jsx';
import ProtectedRoute from '../components/layout/ProtectedRoute.jsx';
import Loader from '../components/common/Loader.jsx';
import Button from '../components/common/Button.jsx';
import { useAuth } from '../hooks/useAuth.js';
import { hasAnyRole } from '../utils/roleUtils.js';
import { matchRoute, normalizePath, pageInstanceKey, safeReturnTo, loginDestination } from './routeConfig.js';
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
import EncadreurDetailPage from '../pages/encadreurs/EncadreurDetailPage.jsx';
import EvaluationDetailPage from '../pages/evaluations/EvaluationDetailPage.jsx';
import RapportDetailPage from '../pages/rapports/RapportDetailPage.jsx';
import SoutenanceDetailPage from '../pages/soutenances/SoutenanceDetailPage.jsx';
import ParticipationDetailPage from '../pages/stages/ParticipationDetailPage.jsx';
import StageDetailPage from '../pages/stages/StageDetailPage.jsx';
import StagiaireDetailPage from '../pages/stagiaires/StagiaireDetailPage.jsx';
import UtilisateurDetailPage from '../pages/utilisateurs/UtilisateurDetailPage.jsx';

const pages = { DashboardPage, UtilisateursPage, ReferentielsPage, AcademiquesPage, OrganisationPage, StagiairesPage, EncadreursPage, StagesPage, RapportsPage, SoutenancesPage, EvaluationsPage, AlertesPage, JournalAuditPage, FichiersPage, EncadreurDetailPage, EvaluationDetailPage, RapportDetailPage, SoutenanceDetailPage, ParticipationDetailPage, StageDetailPage, StagiaireDetailPage, UtilisateurDetailPage };

export default function AppRoutes() {
  const { estConnecte, initialisation, utilisateur, status, erreur, retrySession, logout } = useAuth();
  const [location, setLocation] = useState(() => window.location.pathname + window.location.search + window.location.hash);
  const navigate = useCallback((to, options = {}) => {
    if (!to.startsWith('/') || to.startsWith('//') || to.includes('\\')) return;
    if (options.replace) window.history.replaceState({}, '', to);
    else window.history.pushState({}, '', to);
    setLocation(window.location.pathname + window.location.search + window.location.hash);
  }, []);
  useEffect(() => {
    const update = () => setLocation(window.location.pathname + window.location.search + window.location.hash);
    window.addEventListener('popstate', update);
    return () => window.removeEventListener('popstate', update);
  }, []);
  const url = new URL(location, window.location.origin);
  const path = normalizePath(url.pathname);
  const route = matchRoute(path);
  const title = path === '/login' ? 'Connexion' : route?.title ?? 'Page introuvable';
  const returnTo = safeReturnTo(url.searchParams.get('retour'));
  useEffect(() => {
    if (initialisation || status === 'unavailable') return;
    if (!estConnecte && path !== '/login') {
      navigate(loginDestination(location), { replace: true });
    } else if (estConnecte && (path === '/login' || path === '/')) {
      navigate(returnTo, { replace: true });
    }
  }, [estConnecte, initialisation, status, path, location, navigate, returnTo]);
  useEffect(() => {
    document.title = `${title} · MEF–SIIGFP`;
    if (estConnecte) document.getElementById('main-content')?.focus();
  }, [title, path, estConnecte]);

  if (initialisation) return <main className="center-screen"><Loader label="Vérification de la session…" /></main>;
  if (status === 'unavailable') return <main className="center-screen"><section className="auth-card">
    <h1>Session à vérifier</h1><p role="alert">{erreur}</p><p>Votre session est conservée. Réessayez lorsque le serveur est accessible.</p>
    <Button onClick={retrySession}>Réessayer</Button>
    <Button variant="secondary" onClick={() => { navigate('/login', { replace: true }); void logout(); }}>Fermer la session locale</Button>
  </section></main>;
  if (path === '/login') return estConnecte ? null : <LoginPage />;
  const Component = route ? pages[route.page] : NotFoundPage;
  const allowed = !route || hasAnyRole(utilisateur, route.roles);
  return <ProtectedRoute><AdminLayout currentPath={path} navigate={navigate} title={title} route={route}>
    {allowed ? <Component key={pageInstanceKey(location)} params={route?.params ?? {}} navigate={navigate} /> : <ForbiddenPage />}
  </AdminLayout></ProtectedRoute>;
}
