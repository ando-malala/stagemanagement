import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getSoutenance, updateSoutenance } from '../../api/soutenanceApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';

const fields = [["stagiaire", "Stagiaire"], ["encadreur", "Encadreur"], ["dateSoutenance", "Date et heure", "date"], ["lieu", "Lieu"], ["observation", "Observation"], ["dateCreation", "Création", "date"]];
const links = [{"key": "stageParticipantId", "path": "/participations", "label": "Participation"}];

export default function SoutenanceDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Soutenance" fetcher={getSoutenance} fields={fields} links={links} backTo="/soutenances" navigate={navigate}>
    {(data, reload) => <BusinessEditor kind="soutenance" initial={data} save={(payload) => updateSoutenance(data.id, payload)} onSaved={reload} />}
  </ResourceDetail>;
}
