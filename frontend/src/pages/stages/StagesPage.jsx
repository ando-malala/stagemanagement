import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
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
        renderCreate={(reload) => <BusinessEditor kind="stage" label="Créer le stage" save={createStage} onSaved={reload} />}
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
        renderCreate={(reload) => <BusinessEditor kind="participation" label="Créer la participation" save={createParticipation} onSaved={reload} />}
        fetcher={listParticipations}
        creator={createParticipation}
        canCreate={canWrite}
        createLabel="Créer la participation"
      />
    </div>
  );
}
