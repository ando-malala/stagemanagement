import Button from '../../components/common/Button.jsx';
import Badge from '../../components/common/Badge.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';
import { alertMetrics, sameAlertFilters, summarizeAlerts } from '../../utils/alertOverview.js';
import { formatNumber } from '../../utils/formatUtils.js';

export default function AlertOverview({ rows, filters, onSelect }) {
  const { counts, types } = summarizeAlerts(rows);
  return <section className="alert-overview" aria-labelledby="alert-overview-title">
    <div className="section-heading">
      <div><h3 id="alert-overview-title">Vue d’ensemble</h3><p className="small">Compteurs au statut « À traiter », sur les alertes auxquelles vous avez accès.</p></div>
      <Button variant="secondary" aria-pressed={sameAlertFilters(filters, {})} aria-controls="alert-results" onClick={() => onSelect({})}>Toutes</Button>
    </div>
    <div className="alert-metrics">
      {alertMetrics.map(metric => <Button key={metric.key} variant="secondary" className={`alert-metric alert-metric-${metric.tone}`}
        aria-pressed={sameAlertFilters(filters, metric.filters)} aria-controls="alert-results" onClick={() => onSelect({ ...metric.filters })}>
        <span className="alert-metric-heading"><span>{metric.label}</span><NavigationIcon name={metric.icon} /></span>
        <strong>{formatNumber(counts[metric.key])}</strong><span className="alert-metric-hint">{metric.hint}</span>
      </Button>)}
    </div>
    <p className="small">La nature « Informative » n’autorise pas de traitement manuel, quelle que soit la priorité. Une alerte peut être à la fois urgente et informative.</p>
    <div className="alert-type-heading"><h4>Répartition par type</h4><span className="small">Nombre à traiter · un clic pour filtrer</span></div>
    {types.length ? <div className="alert-types" role="group" aria-label="Filtrer par type d’alerte">
      {types.map(type => <Button key={type.code} variant="secondary" className="alert-type"
        aria-pressed={sameAlertFilters(filters, { statut: 'A_TRAITER', typeAlerteCode: type.code })} aria-controls="alert-results"
        onClick={() => onSelect({ statut: 'A_TRAITER', typeAlerteCode: type.code })}>
        <span>{type.label}</span><Badge>{formatNumber(type.count)}</Badge>
      </Button>)}
    </div> : <EmptyState message="Aucun type d’alerte dans les données reçues." />}
  </section>;
}
