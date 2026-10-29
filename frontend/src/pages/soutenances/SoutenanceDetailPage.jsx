import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getSoutenance } from '../../api/soutenanceApi.js';

const fields = [["stagiaire", "Stagiaire"], ["encadreur", "Encadreur"], ["dateSoutenance", "Date et heure", "date"], ["lieu", "Lieu"], ["observation", "Observation"], ["dateCreation", "Création", "date"]];
const links = [{"key": "stageParticipantId", "path": "/participations", "label": "Participation"}];

export default function SoutenanceDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Soutenance" fetcher={getSoutenance} fields={fields} links={links} backTo="/soutenances" navigate={navigate} />;
}
