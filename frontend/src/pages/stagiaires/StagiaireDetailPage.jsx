import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getStagiaire } from '../../api/stagiaireApi.js';

const fields = [["nom", "Nom"], ["prenoms", "Prénoms"], ["adresse", "Adresse"], ["contact", "Contact"], ["email", "Courriel"], ["statut", "Statut"], ["dateCreation", "Création", "date"]];
const links = [];

export default function StagiaireDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Dossier stagiaire" fetcher={getStagiaire} fields={fields} links={links} backTo="/stagiaires" navigate={navigate} />;
}
