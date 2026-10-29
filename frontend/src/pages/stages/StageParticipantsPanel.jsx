import { useCallback } from 'react';
import { listStageParticipants } from '../../api/stageApi.js';
import EntityPage from '../../components/common/EntityPage.jsx';
import AppLink from '../../components/common/AppLink.jsx';

const columns = [{ key: 'stagiaireNomComplet', label: 'Stagiaire' }, { key: 'encadreurNomComplet', label: 'Encadreur' },
  { key: 'dateDebutStage', label: 'Début' }, { key: 'dateFinStage', label: 'Fin' }, { key: 'statut', label: 'Statut' }];
export default function StageParticipantsPanel({ stageId, navigate }) {
  const fetcher = useCallback((options) => listStageParticipants(stageId, options), [stageId]);
  return <EntityPage title="Participants du stage" columns={columns} fetcher={fetcher}
    renderActions={(row) => <AppLink to={`/participations/${row.id}`} navigate={navigate}>Consulter</AppLink>} />;
}
