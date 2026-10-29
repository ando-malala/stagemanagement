import { useState } from 'react';
import Button from '../../components/common/Button.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import { createAlerte, listAlertes, marquerAlerteLue } from '../../api/alerteApi.js';
import { canTreatAlert } from '../../utils/permissions.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'typeAlerteLibelle', label: 'Type' },
  { key: 'message', label: 'Message' },
  { key: 'priorite', label: 'Priorité' },
  { key: 'dateEcheance', label: 'Échéance' },
  { key: 'statut', label: 'Statut' }
];

const fields = [
  { name: 'typeAlerteCode', label: 'Code type alerte', required: true },
  { name: 'message', label: 'Message', type: 'textarea', required: true },
  { name: 'dateEcheance', label: 'Échéance', type: 'date' },
  {
    name: 'priorite',
    label: 'Priorité',
    type: 'select',
    defaultValue: 'AVERTISSEMENT',
    options: ['INFORMATION', 'AVERTISSEMENT', 'URGENT']
  },
  { name: 'stageParticipantId', label: 'ID participation', type: 'number' },
  { name: 'rapportId', label: 'ID rapport', type: 'number' },
  { name: 'encadreurId', label: 'ID encadreur', type: 'number' },
  { name: 'soutenanceId', label: 'ID soutenance', type: 'number' },
  { name: 'cleDeduplication', label: 'Clé déduplication' }
];

export default function AlertesPage() {
  const { utilisateur } = useAuth();
  const canCreate = hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE']);
  const [confirmation, setConfirmation] = useState(null);

  async function markRead(row, reload) {
    await marquerAlerteLue(row.id);
    await reload();
  }

  return (
    <>
    <EntityPage
      title="Alertes"
      description="Alertes à traiter, échéances et priorités."
      columns={columns}
      fields={fields}
      fetcher={listAlertes}
      creator={createAlerte}
      canCreate={canCreate}
      createLabel="Créer l’alerte"
      renderActions={(row, reload) => (
        <div className="row-actions">
          {canTreatAlert(utilisateur, row) && <Button variant="secondary" onClick={() => setConfirmation({ row, reload })}>Traiter</Button>}
        </div>
      )}
    />
    <ConfirmDialog open={Boolean(confirmation)} title="Traiter cette alerte" onClose={() => setConfirmation(null)} onConfirm={() => markRead(confirmation.row, confirmation.reload)}>
      <p>Confirmez que la situation signalée a été vérifiée avant de marquer l’alerte comme traitée.</p>
    </ConfirmDialog>
    </>
  );
}
