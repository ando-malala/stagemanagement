import EntityPage from '../../components/common/EntityPage.jsx';
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

const fields = [
  { name: 'nom', label: 'Nom', required: true },
  { name: 'prenoms', label: 'Prénoms', required: true },
  { name: 'adresse', label: 'Adresse' },
  { name: 'contact', label: 'Contact' },
  { name: 'email', label: 'Email', type: 'email' }
];

export default function StagiairesPage() {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Stagiaires"
      description="Dossiers administratifs des stagiaires."
      columns={columns}
      fields={fields}
      fetcher={listStagiaires}
      creator={createStagiaire}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE'])}
      createLabel="Créer le stagiaire"
    />
  );
}
