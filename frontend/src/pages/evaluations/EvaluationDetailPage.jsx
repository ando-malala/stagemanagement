import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getEvaluation } from '../../api/evaluationApi.js';

const fields = [["stagiaire", "Stagiaire"], ["encadreur", "Encadreur"], ["appreciationGlobale", "Appréciation globale"], ["observationsFinales", "Observations finales"], ["estFinalisee", "Finalisée"], ["dateEvaluation", "Évaluation", "date"], ["dateFinalisation", "Finalisation", "date"]];
const links = [{"key": "stageParticipantId", "path": "/participations", "label": "Participation"}, {"key": "encadreurId", "path": "/encadreurs", "label": "Encadreur"}];

export default function EvaluationDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Évaluation" fetcher={getEvaluation} fields={fields} links={links} backTo="/evaluations" navigate={navigate} />;
}
