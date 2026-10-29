import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getStage } from '../../api/stageApi.js';

const fields = [["libelle", "Libellé"], ["structureAccueil", "Structure d’accueil"], ["departement", "Département"], ["dateCreation", "Création", "date"]];
const links = [];

export default function StageDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Stage" fetcher={getStage} fields={fields} links={links} backTo="/stages" navigate={navigate} />;
}
