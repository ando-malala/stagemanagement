import { useCallback } from 'react';
import { listParticipationRapports, createRapport } from '../../api/rapportApi.js';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';

const columns = [{ key: 'debutSemaine', label: 'Début' }, { key: 'finSemaine', label: 'Fin' },
  { key: 'moisStage', label: 'Mois' }, { key: 'statut', label: 'Statut' }];
export default function ParticipationRapports({ participationId, navigate }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback((options) => listParticipationRapports(participationId, options), [participationId]);
  return <EntityPage title="Rapports de la participation" columns={columns} fetcher={fetcher} canCreate={can(utilisateur, 'gererRapports')}
    renderCreate={(reload) => <BusinessEditor kind="rapport" label="Créer un brouillon" initial={{ stageParticipantId: participationId }} save={createRapport} onSaved={reload} />}
    renderActions={(row) => <AppLink to={`/rapports/${row.id}`} navigate={navigate}>Consulter</AppLink>} />;
}
