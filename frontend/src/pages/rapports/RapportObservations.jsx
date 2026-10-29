import { useCallback } from 'react';
import { listRapportObservations, addRapportObservation } from '../../api/rapportApi.js';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { formatDateTime } from '../../utils/dateUtils.js';

const columns = [{ key: 'auteur', label: 'Auteur' }, { key: 'typeObservation', label: 'Type' },
  { key: 'commentaire', label: 'Commentaire' }, { key: 'visibleStagiaire', label: 'Visible stagiaire' },
  { key: 'dateCreation', label: 'Date', render: (row) => formatDateTime(row.dateCreation) }];
export default function RapportObservations({ rapportId }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback((options) => listRapportObservations(rapportId, options), [rapportId]);
  return <EntityPage title="Observations" columns={columns} fetcher={fetcher} canCreate={can(utilisateur, 'gererRapports')}
    description="Une observation accompagne le rapport. Son type ne change pas le statut du rapport; utilisez les actions de validation pour cela."
    renderCreate={(reload) => <BusinessEditor kind="observation" initial={{ typeObservation: 'OBSERVATION', visibleStagiaire: 'true' }}
      label="Ajouter une observation" save={(payload) => addRapportObservation(rapportId, payload)} onSaved={reload} />} />;
}
