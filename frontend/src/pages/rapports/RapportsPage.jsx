import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createRapport, listRapports } from '../../api/rapportApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaire', label: 'Stagiaire' },
  { key: 'encadreur', label: 'Encadreur' },
  { key: 'debutSemaine', label: 'Début' },
  { key: 'finSemaine', label: 'Fin' },
  { key: 'moisStage', label: 'Mois' },
  { key: 'statut', label: 'Statut' }
];

export default function RapportsPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Rapports d’activité"
      description="Rapports hebdomadaires et suivi de validation."
      renderActions={(row) => <AppLink to={`/rapports/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      renderCreate={(reload) => <BusinessEditor kind="rapport" label="Créer un brouillon" save={createRapport} onSaved={reload} />}
      fetcher={listRapports}
      creator={createRapport}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR'])}
      createLabel="Créer le rapport"
    />
  );
}
