import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import { createParticipation, createStage, listParticipations, listStages } from '../../api/stageApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const stageColumns = [
  { key: 'id', label: 'ID' },
  { key: 'libelle', label: 'Libellé' },
  { key: 'structureAccueil', label: 'Structure' },
  { key: 'departement', label: 'Département' },
  { key: 'dateCreation', label: 'Créé le' }
];

const stageFields = [
  { name: 'libelle', label: 'Libellé', required: true },
  { name: 'structureAccueilId', label: 'ID structure d’accueil', type: 'number', required: true }
];

const participationColumns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaireNomComplet', label: 'Stagiaire' },
  { key: 'stage', label: 'Stage' },
  { key: 'themeStage', label: 'Thème' },
  { key: 'encadreurNomComplet', label: 'Encadreur' },
  { key: 'dateDebutStage', label: 'Début' },
  { key: 'dateFinStage', label: 'Fin' },
  { key: 'statut', label: 'Statut' }
];

const participationFields = [
  { name: 'stageId', label: 'ID stage', type: 'number', required: true },
  { name: 'stagiaireAcademiqueId', label: 'ID dossier académique', type: 'number', required: true },
  { name: 'cadreStageId', label: 'ID cadre de stage', type: 'number', required: true },
  { name: 'encadreurId', label: 'ID encadreur', type: 'number', required: true },
  { name: 'dateDebutStage', label: 'Début stage', type: 'date', required: true },
  { name: 'dateFinStage', label: 'Fin stage', type: 'date', required: true }
];

export default function StagesPage({ navigate }) {
  const { utilisateur } = useAuth();
  const canWrite = hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE']);

  return (
    <div className="page-stack">
      <EntityPage
        title="Stages"
        description="Stages ouverts par structure d’accueil."
        renderActions={(row) => <AppLink to={`/stages/${row.id}`} navigate={navigate}>Consulter</AppLink>}
        columns={stageColumns}
        fields={stageFields}
        fetcher={listStages}
        creator={createStage}
        canCreate={canWrite}
        createLabel="Créer le stage"
      />

      <EntityPage
        title="Participations"
        description="Affectations des stagiaires aux stages."
        renderActions={(row) => <AppLink to={`/participations/${row.id}`} navigate={navigate}>Consulter</AppLink>}
        columns={participationColumns}
        fields={participationFields}
        fetcher={listParticipations}
        creator={createParticipation}
        canCreate={canWrite}
        createLabel="Créer la participation"
      />
    </div>
  );
}
