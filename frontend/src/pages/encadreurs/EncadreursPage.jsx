import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createEncadreur, listEncadreurs } from '../../api/encadreurApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'nom', label: 'Nom' },
  { key: 'prenoms', label: 'Prénoms' },
  { key: 'identifiantUtilisateur', label: 'Compte associé' },
  { key: 'fonction', label: 'Fonction' },
  { key: 'departement', label: 'Département' },
  { key: 'capaciteMaxStagiaires', label: 'Capacité' },
  { key: 'statut', label: 'Statut' }
];

export default function EncadreursPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Encadreurs"
      description="Encadreurs, rattachements et capacité d’encadrement."
      renderActions={(row) => <AppLink to={`/encadreurs/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      renderCreate={(reload) => <BusinessEditor kind="encadreur" label="Créer l’encadreur" save={createEncadreur} onSaved={reload} />}
      fetcher={listEncadreurs}
      creator={createEncadreur}
      canCreate={hasAnyRole(utilisateur, ['ADMIN'])}
      createLabel="Créer l’encadreur"
    />
  );
}
