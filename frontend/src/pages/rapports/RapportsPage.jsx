import EntityPage from '../../components/common/EntityPage.jsx';
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

const fields = [
  { name: 'stageParticipantId', label: 'ID participation', type: 'number', required: true },
  { name: 'debutSemaine', label: 'Début semaine', type: 'date', required: true },
  { name: 'finSemaine', label: 'Fin semaine', type: 'date', required: true },
  { name: 'activite', label: 'Activités', type: 'textarea', required: true },
  { name: 'difficultes', label: 'Difficultés', type: 'textarea' },
  { name: 'solutions', label: 'Solutions', type: 'textarea' },
  { name: 'moisStageId', label: 'ID mois stage', type: 'number' }
];

export default function RapportsPage() {
  const { utilisateur } = useAuth();

  return (
    <EntityPage
      title="Rapports d’activité"
      description="Rapports hebdomadaires et suivi de validation."
      columns={columns}
      fields={fields}
      fetcher={listRapports}
      creator={createRapport}
      canCreate={hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR'])}
      createLabel="Créer le rapport"
    />
  );
}
