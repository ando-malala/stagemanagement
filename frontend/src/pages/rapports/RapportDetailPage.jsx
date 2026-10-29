import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getRapport, updateRapport } from '../../api/rapportApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import RapportActions from './RapportActions.jsx';
import RapportObservations from './RapportObservations.jsx';

const fields = [["stagiaire", "Stagiaire"], ["encadreur", "Encadreur"], ["debutSemaine", "Début de période", "date"], ["finSemaine", "Fin de période", "date"], ["moisStage", "Mois de stage"], ["activite", "Activités"], ["difficultes", "Difficultés"], ["solutions", "Solutions"], ["statut", "Statut"], ["dateSoumission", "Soumission", "date"], ["dateValidation", "Validation", "date"]];
const links = [{"key": "stageParticipantId", "path": "/participations", "label": "Participation"}];

export default function RapportDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Rapport d’activité" fetcher={getRapport} fields={fields} links={links} backTo="/rapports" navigate={navigate}>
    {(data, reload) => <div className="page-stack">
      <BusinessEditor kind="rapport" initial={data} save={(payload) => updateRapport(data.id, payload)} onSaved={reload} />
      <RapportActions rapport={data} onSaved={reload} />
      <RapportObservations rapportId={data.id} />
    </div>}
  </ResourceDetail>;
}
