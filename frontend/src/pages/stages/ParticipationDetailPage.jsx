import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getParticipation, updateParticipation } from '../../api/stageApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import ParticipationActions from './ParticipationActions.jsx';
import ParticipationRapports from './ParticipationRapports.jsx';
import DocumentsPanel from '../fichiers/DocumentsPanel.jsx';
import ParticipationSuivi from './ParticipationSuivi.jsx';

const fields = [["stagiaireNomComplet", "Stagiaire"], ["stage", "Stage"], ["themeStage", "Thème"], ["encadreurNomComplet", "Encadreur"], ["niveauEtude", "Niveau d’étude"], ["filiere", "Filière"], ["etablissement", "Établissement"], ["anneeEtudes", "Année d’études"], ["dateDebutStage", "Début", "date"], ["dateFinStage", "Fin", "date"], ["statut", "Statut"], ["dateCreation", "Création", "date"], ["dateModification", "Modification", "date"], ["dateCloture", "Clôture", "date"]];
const links = [{"key": "stagiaireId", "path": "/stagiaires", "label": "Dossier stagiaire"}, {"key": "stageId", "path": "/stages", "label": "Stage"}, {"key": "encadreurId", "path": "/encadreurs", "label": "Encadreur"}];

export default function ParticipationDetailPage({ params, navigate }) {
  return <ResourceDetail id={params.id} title="Participation" fetcher={getParticipation} fields={fields} links={links} backTo="/stages" navigate={navigate}>
    {(data, reload) => <div className="page-stack">
      <BusinessEditor kind="participation" initial={data} save={(payload) => updateParticipation(params.id, payload)} onSaved={reload} />
      <ParticipationActions key={`actions/${data.dateModification}`} participation={data} onSaved={reload} />
      <ParticipationSuivi key={`suivi/${data.dateModification}`} id={data.id} navigate={navigate} />
      <ParticipationRapports participationId={data.id} navigate={navigate} />
      <DocumentsPanel key={`participation/${data.id}`} scope="participation" ownerId={data.id} ownerLabel={`${data.stagiaireNomComplet} — ${data.stage}`} />
    </div>}
  </ResourceDetail>;
}
