import { getPilotage, downloadExport } from '../../api/syntheseApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import Table from '../../components/common/Table.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import DownloadButton from '../../components/common/DownloadButton.jsx';

const priorities = [['stagiaire', 'Stagiaire'], ['priorite', 'Priorité'], ['score', 'Score'], ['nombreAlertes', 'Alertes'], ['situation', 'Situation'], ['actionRecommandee', 'Action recommandée']];
const dossiers = [['stagiaire', 'Stagiaire'], ['stage', 'Stage'], ['statut', 'Statut'], ['dateFin', 'Fin'], ['joursRestants', 'Jours restants'], ['documentsManquants', 'Pièces manquantes'], ['rapportsAttendus', 'Rapports attendus'], ['rapportsSoumis', 'Soumis'], ['rapportsValides', 'Validés'], ['rapportsManquants', 'Manquants']];
const charges = [['encadreur', 'Encadreur'], ['capaciteMax', 'Capacité'], ['participationsActives', 'Participations actives accessibles'], ['surcharge', 'Surcharge dans ce périmètre']];
const defenses = [['stagiaire', 'Stagiaire'], ['dateSoutenance', 'Date'], ['lieu', 'Lieu']];
const columns = definitions => definitions.map(([key, label]) => ({ key, label }));
export default function PilotagePanel({ navigate }) {
  const state = useFetch(getPilotage);
  return <section className="page-stack">
    <div className="section-heading"><h3>Suivi des dossiers</h3><Button variant="secondary" disabled={state.loading} onClick={() => void state.reload()}>Actualiser le suivi</Button></div>
    <div className="actions">{['participations', 'statistiques', 'alertes'].map(type => <DownloadButton key={type} download={() => downloadExport(type)}>Exporter {type} (CSV)</DownloadButton>)}</div>
    {state.loading ? <Loader /> : state.error ? <ErrorMessage error={state.error} /> : <PilotageTables data={state.data} navigate={navigate} />}
  </section>;
}
export function PilotageTables({ data, navigate }) {
  return <>
    <p className="small">Calcul au {data.dateCalcul}. Les rapports attendus couvrent les périodes achevées ; les manquants ont dépassé leur date limite. Les charges portent sur les participations accessibles.</p>
    <Card><h3>Dossiers prioritaires</h3><Table caption="Classement par priorité puis score décroissant" columns={columns(priorities)} data={data.priorites}
      renderActions={p => <AppLink to={`/participations/${p.participationId}`} navigate={navigate}>Ouvrir le dossier</AppLink>} /></Card>
    <Card><h3>Échéances et complétude</h3><Table caption="Suivi des participations" columns={columns(dossiers)} data={data.dossiers}
      renderActions={p => <AppLink to={`/participations/${p.participationId}`} navigate={navigate}>Consulter</AppLink>} /></Card>
    <Card><h3>Charge des encadreurs</h3><Table caption="Capacité et charge accessible" columns={columns(charges)} data={data.charges}
      renderActions={e => <AppLink to={`/encadreurs/${e.encadreurId}`} navigate={navigate}>Encadreur</AppLink>} /></Card>
    <Card><h3>Soutenances dans les 30 prochains jours</h3><Table caption="Prochaines soutenances" columns={columns(defenses)} data={data.soutenancesProchaines}
      renderActions={s => <AppLink to={`/soutenances/${s.id}`} navigate={navigate}>Consulter</AppLink>} /></Card>
  </>;
}
