import { useCallback } from 'react';
import { createAcademique, listAcademiques } from '../../api/stagiaireApi.js';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'niveauEtude', label: 'Niveau d’étude' }, { key: 'filiere', label: 'Filière' },
  { key: 'etablissement', label: 'Établissement' }, { key: 'anneeEtudes', label: 'Année d’études' }
];
export default function AcademiquesPanel({ stagiaireId }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback((options) => listAcademiques(stagiaireId, options), [stagiaireId]);
  return <EntityPage title="Profils académiques" description="Profils utilisables pour les affectations aux stages."
    columns={columns} fetcher={fetcher} canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE'])}
    renderCreate={(reload) => <BusinessEditor kind="academique" label="Ajouter un profil académique" save={(payload) => createAcademique(stagiaireId, payload)} onSaved={reload} />} />;
}
