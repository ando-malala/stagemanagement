import { useRef, useState } from 'react';
import { loadDashboard, refreshDashboard } from '../../api/dashboardApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { dashboardCards } from '../../utils/pilotage.js';
import { formatNumber } from '../../utils/formatUtils.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import PilotagePanel from './PilotagePanel.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';
import StatusBadge from '../../components/common/StatusBadge.jsx';
import NotificationBadge from '../../components/common/NotificationBadge.jsx';

const indicatorStyles = {
  totalStagiaires: ['ST', 'blue'], totalStages: ['SP', 'teal'],
  totalParticipations: ['EN', 'violet'], totalRapports: ['RA', 'blue'],
  totalAlertesATraiter: ['AL', 'amber'], totalAlertesTraitees: ['EV', 'teal']
};

export default function DashboardPage({ navigate }) {
  const { utilisateur } = useAuth();
  const dashboard = useFetch(loadDashboard);
  const [refreshing, setRefreshing] = useState(false);
  const [refreshError, setRefreshError] = useState('');
  const refreshInProgress = useRef(false);
  const loading = dashboard.loading || refreshing;
  async function refresh() {
    if (refreshInProgress.current) return;
    refreshInProgress.current = true;
    setRefreshing(true);
    setRefreshError('');
    try {
      await refreshDashboard(dashboard.reload, hasAnyRole(utilisateur, ['ADMIN']));
    } catch (error) {
      setRefreshError(`La vérification des alertes a échoué : ${error.message}`);
    } finally {
      refreshInProgress.current = false;
      setRefreshing(false);
    }
  }
  return <div className="page-stack">
    <section className="dashboard-intro"><div><span className="eyebrow">Votre espace de pilotage</span><h2>Votre activité en un regard</h2>
      <p>Vue d’ensemble des dossiers auxquels vous avez accès.</p></div><Button variant="secondary" disabled={loading}
      onClick={refresh}><NavigationIcon name="refresh" />{refreshing ? 'Actualisation…' : 'Actualiser'}</Button></section>
    <ErrorMessage error={refreshError} />
    {loading ? <Loader label={refreshing ? 'Vérification des alertes et actualisation du tableau de bord…' : 'Chargement des compteurs…'} /> : dashboard.error ? <ErrorMessage error={dashboard.error} /> : <DashboardSummary resume={dashboard.data.resume} navigate={navigate} />}
    <section className="page-stack dashboard-shortcuts"><div className="section-heading"><h3>Accès rapides</h3><span className="small">Les actions du quotidien</span></div><DashboardShortcuts utilisateur={utilisateur} navigate={navigate} /></section>
    <Card><div className="section-heading"><h3>Alertes à traiter</h3><AppLink className="alerts-notification-link" to="/alertes?statut=A_TRAITER" navigate={navigate}>
      <NavigationIcon name="AL" />Voir toutes les alertes à traiter{!loading && !dashboard.error && <NotificationBadge count={dashboard.data.resume.totalAlertesATraiter} />}
    </AppLink></div>
      {loading ? <Loader /> : dashboard.error ? <ErrorMessage error={dashboard.error} /> : <DashboardAlerts cards={dashboard.data.cards} navigate={navigate} />}
    </Card>
    <PilotagePanel navigate={navigate} alertRows={dashboard.data?.rows} data={dashboard.data?.pilotage}
      loading={loading} error={dashboard.error} onRefresh={refresh} />
  </div>;
}

export function DashboardAlerts({ cards, navigate }) {
  if (!cards.length) return <EmptyState message="Aucune alerte à traiter." />;
  return <div className="alert-list">{cards.slice(0, 6).map(card => <article className="alert-card" key={card.key}>
    <strong>{card.title}</strong><p>{card.message}</p><div className="row-actions"><StatusBadge value={card.priorite} /><StatusBadge value={card.natureAlerte} /></div>
    <AppLink to={card.path} navigate={navigate}>{card.linkLabel}</AppLink>
  </article>)}</div>;
}

export function DashboardShortcuts({ utilisateur, navigate }) {
  const admin = hasAnyRole(utilisateur, ['ADMIN']);
  const actions = admin ? [
    ['/stagiaires', 'Enregistrer un stagiaire', 'Créer son dossier, puis renseigner ses études.'],
    ['/stages', 'Affecter un stagiaire', 'Choisir son stage, son encadreur et ses dates.'],
    ['/alertes?statut=A_TRAITER', 'Traiter les alertes', 'Retrouver les échéances et les dossiers à compléter.']
  ] : [
    ['/rapports', 'Examiner les rapports', 'Consulter les activités, demander une correction ou valider.'],
    ['/stages', 'Suivre mes stagiaires', 'Accéder aux participations qui vous sont affectées.'],
    ['/evaluations', 'Évaluer un stage', 'Renseigner les critères et l’appréciation finale.']
  ];
  return <nav className="quick-actions" aria-label="Actions courantes">{actions.map(([path, title, description], index) =>
    <AppLink key={path} to={path} navigate={navigate} className="quick-action"><span className="icon-tile"><NavigationIcon name={(admin ? ['ST', 'SP', 'AL'] : ['RA', 'EN', 'EV'])[index]} /></span><div><strong>{title}</strong><span>{description}</span></div><span className="quick-action-arrow"><NavigationIcon name="arrow" /></span></AppLink>
  )}</nav>;
}

export function DashboardSummary({ resume, navigate }) {
  return <section className="stat-grid" aria-label="Indicateurs d’activité">{dashboardCards.map(([key, label, path]) => <Card className={`stat-card stat-${indicatorStyles[key][1]}`} key={key}>
    <div className="stat-heading"><span>{label}</span><span className="icon-tile"><NavigationIcon name={indicatorStyles[key][0]} /></span></div><strong>{formatNumber(resume[key])}</strong><AppLink to={path} navigate={navigate} aria-label={`Consulter : ${label}`}>Consulter</AppLink>
  </Card>)}</section>;
}
