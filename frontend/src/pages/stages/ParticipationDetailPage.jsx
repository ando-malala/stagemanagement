import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getParticipation } from '../../api/stageApi.js';

const fields = [["stagiaireNomComplet", "Stagiaire"], ["stage", "Stage"], ["themeStage", "Thème"], ["encadreurNomComplet", "Encadreur"], ["niveauEtude", "Niveau d’étude"], ["filiere", "Filière"], ["etablissement", "Établissement"], ["anneeEtudes", "Année d’études"], ["dateDebutStage", "Début", "date"], ["dateFinStage", "Fin", "date"], ["statut", "Statut"], ["dateCreation", "Création", "date"], ["dateModification", "Modification", "date"], ["dateCloture", "Clôture", "date"]];
const links = [{"key": "stagiaireId", "path": "/stagiaires", "label": "Dossier stagiaire"}, {"key": "stageId", "path": "/stages", "label": "Stage"}, {"key": "encadreurId", "path": "/encadreurs", "label": "Encadreur"}];

export default function ParticipationDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Participation" fetcher={getParticipation} fields={fields} links={links} backTo="/stages" navigate={navigate} />;
}
