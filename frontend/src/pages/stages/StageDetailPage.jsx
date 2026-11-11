import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getStage, updateStage } from '../../api/stageApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import StageParticipantsPanel from './StageParticipantsPanel.jsx';

const fields = [["libelle", "Libellé"], ["structureAccueil", "Structure d’accueil"], ["departement", "Département"]];
const links = [];

export default function StageDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Stage" fetcher={getStage} fields={fields} links={links} backTo="/stages" navigate={navigate}>
    {(data, reload) => <div className="page-stack">
      <BusinessEditor kind="stage" initial={data} save={(payload) => updateStage(params.id, payload)} onSaved={reload} />
      <StageParticipantsPanel stageId={params.id} navigate={navigate} />
    </div>}
  </ResourceDetail>;
}
