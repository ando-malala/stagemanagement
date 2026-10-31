import { useCallback } from 'react';
import { apiRequest } from '../../api/apiClient.js';
import EntityPage from '../../components/common/EntityPage.jsx';
import AppLink from '../../components/common/AppLink.jsx';
const columns = [{ key: 'stage', label: 'Stage' }, { key: 'encadreurNomComplet', label: 'Encadreur' },
  { key: 'dateDebutStage', label: 'Début' }, { key: 'dateFinStage', label: 'Fin' }, { key: 'statut', label: 'Statut' }];
export default function StagiaireParticipations({ id, navigate }) {
  const fetcher = useCallback(options => apiRequest(`/api/stagiaires/${id}/participations`, options), [id]);
  return <EntityPage title="Participations et suivi" columns={columns} fetcher={fetcher}
    renderActions={row => <AppLink to={`/participations/${row.id}`} navigate={navigate}>Rapports, documents et historique</AppLink>} />;
}
