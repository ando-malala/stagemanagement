import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getRapport } from '../../api/rapportApi.js';

const fields = [["stagiaire", "Stagiaire"], ["encadreur", "Encadreur"], ["debutSemaine", "Début de période", "date"], ["finSemaine", "Fin de période", "date"], ["moisStage", "Mois de stage"], ["activite", "Activités"], ["difficultes", "Difficultés"], ["solutions", "Solutions"], ["statut", "Statut"], ["dateSoumission", "Soumission", "date"], ["dateValidation", "Validation", "date"]];
const links = [{"key": "stageParticipantId", "path": "/participations", "label": "Participation"}];

export default function RapportDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Rapport d’activité" fetcher={getRapport} fields={fields} links={links} backTo="/rapports" navigate={navigate} />;
}
