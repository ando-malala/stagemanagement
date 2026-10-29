import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import { createEncadreur, listEncadreurs } from '../../api/encadreurApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'nom', label: 'Nom' },
  { key: 'prenoms', label: 'Prénoms' },
  { key: 'fonction', label: 'Fonction' },
  { key: 'departement', label: 'Département' },
  { key: 'capaciteMaxStagiaires', label: 'Capacité' },
  { key: 'statut', label: 'Statut' }
];

const fields = [
  { name: 'utilisateurId', label: 'ID utilisateur', type: 'number' },
  { name: 'nom', label: 'Nom', required: true },
  { name: 'prenoms', label: 'Prénoms', required: true },
  { name: 'fonction', label: 'Fonction' },
  { name: 'localite', label: 'Localité' },
  { name: 'porte', label: 'Porte' },
  { name: 'contact', label: 'Contact' },
  { name: 'courriel', label: 'Courriel', type: 'email' },
  { name: 'departementId', label: 'ID département', type: 'number' },
  { name: 'capaciteMaxStagiaires', label: 'Capacité max', type: 'number' }
];

export default function EncadreursPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Encadreurs"
      description="Encadreurs, rattachements et capacité d’encadrement."
      renderActions={(row) => <AppLink to={`/encadreurs/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      fields={fields}
      fetcher={listEncadreurs}
      creator={createEncadreur}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE'])}
      createLabel="Créer l’encadreur"
    />
  );
}
