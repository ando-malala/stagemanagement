import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import CreateAccount from './CreateAccount.jsx';
import { createUtilisateur, listUtilisateurs } from '../../api/utilisateurApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'identifiant', label: 'Identifiant' },
  { key: 'statut', label: 'Statut' }
];

export default function UtilisateursPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Utilisateurs"
      description="Création et consultation des comptes applicatifs."
      renderActions={(row) => <AppLink to={`/utilisateurs/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      renderCreate={(reload) => <CreateAccount onSaved={reload} />}
      fetcher={listUtilisateurs}
      creator={createUtilisateur}
      canCreate={hasRole(utilisateur, 'ADMIN')}
      createLabel="Créer le compte"
    />
  );
}
