import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createEvaluation, listEvaluations } from '../../api/evaluationApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';

const columns = [
  { key: 'id', label: 'ID' }, { key: 'stagiaire', label: 'Stagiaire' }, { key: 'encadreur', label: 'Encadreur' },
  { key: 'appreciationGlobale', label: 'Appréciation' }, { key: 'estFinalisee', label: 'Finalisée' }, { key: 'dateEvaluation', label: 'Date' }
];

export default function EvaluationsPage({ navigate }) {
  const { utilisateur } = useAuth();
  return <EntityPage title="Évaluations" description="Ouvrez une fiche pour renseigner les critères, consulter les notes et finaliser l’évaluation."
    columns={columns} fetcher={listEvaluations} creator={createEvaluation} canCreate={can(utilisateur, 'gererEvaluations')}
    renderCreate={(reload) => <BusinessEditor kind="evaluation" label="Créer l’évaluation" save={createEvaluation} onSaved={reload} />}
    renderActions={(row) => <AppLink to={`/evaluations/${row.id}`} navigate={navigate}>Consulter</AppLink>} />;
}
