import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getUtilisateur } from '../../api/utilisateurApi.js';
import AccountAdministration from './AccountAdministration.jsx';

const fields = [["identifiant", "Identifiant"], ["statut", "Statut"]];
const links = [];

export default function UtilisateurDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Utilisateur" fetcher={getUtilisateur} fields={fields} links={links} backTo="/utilisateurs" navigate={navigate}>
    {(data) => <AccountAdministration key={data.id} account={data} />}
  </ResourceDetail>;
}
