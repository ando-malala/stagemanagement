import { loadAlertContext } from '../../api/alertContextApi.js';
import { groupDashboardAlerts } from '../../utils/dashboardAlerts.js';
import { getDashboardResume } from '../../api/dashboardApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { dashboardCards, requireDashboard } from '../../utils/pilotage.js';
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

const fetchSummary = async (options) => requireDashboard(await getDashboardResume(options));
const fetchAlerts = async (options) => {
  const { rows, participations } = await loadAlertContext(options, true);
  return { rows, cards: groupDashboardAlerts(rows, participations) };
};
export default function DashboardPage({ navigate }) {
  const { utilisateur } = useAuth();
  const summary = useFetch(fetchSummary);
  const alerts = useFetch(fetchAlerts);
  return <div className="page-stack">
    <div className="section-heading"><h2>Tableau de bord</h2><Button variant="secondary" disabled={summary.loading || alerts.loading}
      onClick={() => { void summary.reload(); void alerts.reload(); }}>Actualiser</Button></div>
    <DashboardShortcuts utilisateur={utilisateur} navigate={navigate} />
    <p className="small">Vue d’ensemble des dossiers auxquels vous avez accès.</p>
    {summary.loading ? <Loader label="Chargement des compteurs…" /> : summary.error ? <ErrorMessage error={summary.error} /> : <DashboardSummary resume={summary.data} navigate={navigate} />}
    <Card><div className="section-heading"><h3>Alertes à traiter</h3><AppLink to="/alertes?statut=A_TRAITER" navigate={navigate}>Voir toutes les alertes à traiter</AppLink></div>
      {alerts.loading ? <Loader /> : alerts.error ? <ErrorMessage error={alerts.error} /> : <DashboardAlerts cards={alerts.data.cards} navigate={navigate} />}
    </Card>
    <PilotagePanel navigate={navigate} alertRows={alerts.data?.rows} reloadAlerts={alerts.reload} />
  </div>;
}

export function DashboardAlerts({ cards, navigate }) {
  if (!cards.length) return <EmptyState message="Aucune alerte à traiter." />;
  return <div className="alert-list">{cards.slice(0, 6).map(card => <article className="alert-card" key={card.key}>
    <strong>{card.title}</strong><p>{card.message}</p><p>{card.priorite} · {card.natureAlerte}</p>
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
  return <nav className="quick-actions" aria-label="Actions courantes">{actions.map(([path, title, description]) =>
    <AppLink key={path} to={path} navigate={navigate} className="quick-action"><strong>{title}</strong><span>{description}</span></AppLink>
  )}</nav>;
}

export function DashboardSummary({ resume, navigate }) {
  return <section className="stat-grid">{dashboardCards.map(([key, label, path]) => <Card className="stat-card" key={key}>
    <span>{label}</span><strong>{formatNumber(resume[key])}</strong><AppLink to={path} navigate={navigate}>Consulter</AppLink>
  </Card>)}</section>;
}
