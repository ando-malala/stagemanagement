import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createSoutenance, listSoutenances } from '../../api/soutenanceApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaire', label: 'Stagiaire' },
  { key: 'encadreur', label: 'Encadreur' },
  { key: 'dateSoutenance', label: 'Date' },
  { key: 'lieu', label: 'Lieu' },
  { key: 'observation', label: 'Observation' }
];

export default function SoutenancesPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Soutenances"
      description="Planification facultative des soutenances."
      renderActions={(row) => <AppLink to={`/soutenances/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      renderCreate={(reload) => <BusinessEditor kind="soutenance" label="Planifier la soutenance" save={createSoutenance} onSaved={reload} />}
      fetcher={listSoutenances}
      creator={createSoutenance}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE'])}
      createLabel="Planifier la soutenance"
    />
  );
}
