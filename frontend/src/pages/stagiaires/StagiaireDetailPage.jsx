import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getStagiaire, updateStagiaire } from '../../api/stagiaireApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import AcademiquesPanel from './AcademiquesPanel.jsx';

const fields = [["nom", "Nom"], ["prenoms", "Prénoms"], ["adresse", "Adresse"], ["contact", "Contact"], ["email", "Courriel"], ["statut", "Statut"], ["dateCreation", "Création", "date"]];
const links = [];

export default function StagiaireDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Dossier stagiaire" fetcher={getStagiaire} fields={fields} links={links} backTo="/stagiaires" navigate={navigate}>
    {(data, reload) => <div className="page-stack">
      <BusinessEditor kind="stagiaire" initial={data} save={(payload) => updateStagiaire(params.id, payload)} onSaved={reload} />
      <AcademiquesPanel stagiaireId={params.id} />
    </div>}
  </ResourceDetail>;
}
