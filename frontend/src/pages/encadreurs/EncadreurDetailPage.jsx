import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getEncadreur } from '../../api/encadreurApi.js';

const fields = [["nom", "Nom"], ["prenoms", "Prénoms"], ["fonction", "Fonction"], ["departement", "Département"], ["localite", "Localité"], ["porte", "Porte"], ["contact", "Contact"], ["courriel", "Courriel"], ["capaciteMaxStagiaires", "Capacité maximale déclarée"], ["identifiantUtilisateur", "Compte associé"], ["statut", "Statut"]];
const links = [];

export default function EncadreurDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Fiche encadreur" fetcher={getEncadreur} fields={fields} links={links} backTo="/encadreurs" navigate={navigate} />;
}
