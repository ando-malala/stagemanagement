import EntityPage from '../../components/common/EntityPage.jsx';
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

const fields = [
  { name: 'stageParticipantId', label: 'ID participation', type: 'number', required: true },
  { name: 'dateSoutenance', label: 'Date de soutenance', type: 'datetime-local', required: true },
  { name: 'lieu', label: 'Lieu', required: true },
  { name: 'observation', label: 'Observation', type: 'textarea' }
];

export default function SoutenancesPage() {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Soutenances"
      description="Planification facultative des soutenances."
      columns={columns}
      fields={fields}
      fetcher={listSoutenances}
      creator={createSoutenance}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE'])}
      createLabel="Planifier la soutenance"
    />
  );
}
