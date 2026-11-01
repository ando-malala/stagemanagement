import { listAlertesNonLues } from '../../api/alerteApi.js';
import { getDashboardResume } from '../../api/dashboardApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { requireList } from '../../utils/businessForms.js';
import { dashboardCards, requireDashboard } from '../../utils/pilotage.js';
import { formatNumber } from '../../utils/formatUtils.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import PilotagePanel from './PilotagePanel.jsx';

const fetchSummary = async (options) => requireDashboard(await getDashboardResume(options));
const fetchAlerts = async (options) => requireList(await listAlertesNonLues(options));
export default function DashboardPage({ navigate }) {
  const summary = useFetch(fetchSummary);
  const alerts = useFetch(fetchAlerts);
  return <div className="page-stack">
    <div className="section-heading"><h2>Tableau de bord</h2><Button variant="secondary" disabled={summary.loading || alerts.loading}
      onClick={() => { void summary.reload(); void alerts.reload(); }}>Actualiser</Button></div>
    <p className="small">Totaux fournis par le serveur pour l’ensemble des dossiers accessibles.</p>
    {summary.loading ? <Loader label="Chargement des compteurs…" /> : summary.error ? <ErrorMessage error={summary.error} /> : <DashboardSummary resume={summary.data} navigate={navigate} />}
    <Card><div className="section-heading"><h3>Alertes à traiter</h3><AppLink to="/alertes?statut=A_TRAITER" navigate={navigate}>Voir toutes les alertes à traiter</AppLink></div>
      {alerts.loading ? <Loader /> : alerts.error ? <ErrorMessage error={alerts.error} /> : alerts.data.length === 0 ? <EmptyState message="Aucune alerte à traiter." /> : <div className="alert-list">
        {alerts.data.slice(0, 6).map((alerte) => <article className="alert-card" key={alerte.id}>
          <strong>{alerte.typeAlerteLibelle ?? alerte.typeAlerteCode}</strong><p>{alerte.message}</p><p>{alerte.priorite} · {alerte.natureAlerte}</p>
          <AppLink to={`/alertes?alerte=${alerte.id}`} navigate={navigate}>Consulter l’alerte</AppLink>
        </article>)}
      </div>}
    </Card>
    <PilotagePanel navigate={navigate} />
  </div>;
}

export function DashboardSummary({ resume, navigate }) {
  return <section className="stat-grid">{dashboardCards.map(([key, label, path]) => <Card className="stat-card" key={key}>
    <span>{label}</span><strong>{formatNumber(resume[key])}</strong><AppLink to={path} navigate={navigate}>Consulter</AppLink>
  </Card>)}</section>;
}
