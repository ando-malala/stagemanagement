import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import { createUtilisateur, listUtilisateurs } from '../../api/utilisateurApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'identifiant', label: 'Identifiant' },
  { key: 'statut', label: 'Statut' }
];

const fields = [
  { name: 'identifiant', label: 'Identifiant', required: true },
  { name: 'motDePasse', label: 'Mot de passe initial', type: 'password', required: true, minLength: 12 },
  {
    name: 'roleCode',
    label: 'Rôle',
    type: 'select',
    required: true,
    defaultValue: 'RESPONSABLE_STAGE',
    options: ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR', 'STAGIAIRE']
  }
];

export default function UtilisateursPage({ navigate }) {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Utilisateurs"
      description="Création et consultation des comptes applicatifs."
      renderActions={(row) => <AppLink to={`/utilisateurs/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      fields={fields}
      fetcher={listUtilisateurs}
      creator={createUtilisateur}
      canCreate={hasRole(utilisateur, 'ADMIN')}
      createLabel="Créer le compte"
    />
  );
}
