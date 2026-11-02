import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createStagiaire, listStagiaires } from '../../api/stagiaireApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'nom', label: 'Nom' },
  { key: 'prenoms', label: 'Prénoms' },
  { key: 'contact', label: 'Contact' },
  { key: 'email', label: 'Email' },
  { key: 'statut', label: 'Statut' }
];

export default function StagiairesPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Stagiaires"
      description="Dossiers administratifs des stagiaires."
      renderActions={(row) => <AppLink to={`/stagiaires/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      renderCreate={(reload) => <BusinessEditor kind="stagiaire" label="Créer le stagiaire" save={createStagiaire} onSaved={reload} />}
      fetcher={listStagiaires}
      creator={createStagiaire}
      canCreate={hasAnyRole(utilisateur, ['ADMIN'])}
      createLabel="Créer le stagiaire"
    />
  );
}
