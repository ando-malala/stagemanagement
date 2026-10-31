import CompteStagiaire from './CompteStagiaire.jsx';
import StagiaireParticipations from './StagiaireParticipations.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getStagiaire, updateStagiaire } from '../../api/stagiaireApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import AcademiquesPanel from './AcademiquesPanel.jsx';
import DocumentsPanel from '../fichiers/DocumentsPanel.jsx';

const fields = [["nom", "Nom"], ["prenoms", "Prénoms"], ["adresse", "Adresse"], ["contact", "Contact"], ["email", "Courriel"], ["statut", "Statut"], ["dateCreation", "Création", "date"]];
const links = [];

export default function StagiaireDetailPage({ params, navigate }) {
  const { utilisateur } = useAuth();
  return <ResourceDetail id={params.id} title="Dossier stagiaire" fetcher={getStagiaire} fields={fields} links={links} backTo="/stagiaires" navigate={navigate}>
    {(data, reload) => <div className="page-stack">
      <BusinessEditor kind="stagiaire" initial={data} save={(payload) => updateStagiaire(params.id, payload)} onSaved={reload} />
      <AcademiquesPanel stagiaireId={params.id} />
      {can(utilisateur, 'gererUtilisateurs') && <CompteStagiaire id={params.id} />}
      <StagiaireParticipations id={params.id} navigate={navigate} />
      <DocumentsPanel key={`stagiaire/${data.id}`} scope="stagiaire" ownerId={data.id} ownerLabel={`${data.nom} ${data.prenoms}`} />
    </div>}
  </ResourceDetail>;
}
