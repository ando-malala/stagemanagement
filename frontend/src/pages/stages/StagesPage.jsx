import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import { createParticipation, createStage, listParticipations, listStages } from '../../api/stageApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const stageColumns = [
  { key: 'id', label: 'ID' },
  { key: 'libelle', label: 'Libellé' },
  { key: 'structureAccueil', label: 'Structure', filterable: true },
  { key: 'departement', label: 'Département', filterable: true },
  { key: 'dateCreation', label: 'Créé le' }
];

const participationColumns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaireNomComplet', label: 'Stagiaire' },
  { key: 'stage', label: 'Stage' },
  { key: 'themeStage', label: 'Thème' },
  { key: 'encadreurNomComplet', label: 'Encadreur', filterable: true },
  { key: 'dateDebutStage', label: 'Début' },
  { key: 'dateFinStage', label: 'Fin' },
  { key: 'statut', label: 'Statut', filterable: true }
];

export default function StagesPage({ navigate }) {
  const { utilisateur } = useAuth();
  const canWrite = hasAnyRole(utilisateur, ['ADMIN']);

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
        description="Une participation relie un stagiaire à un stage, un encadreur et une période. Créez d’abord son dossier et son profil académique."
        renderActions={(row) => <AppLink to={`/participations/${row.id}`} navigate={navigate}>Consulter</AppLink>}
        columns={participationColumns}
        renderCreate={(reload) => <BusinessEditor kind="participation" label="Affecter un stagiaire" save={createParticipation} onSaved={reload} />}
        fetcher={listParticipations}
        creator={createParticipation}
        canCreate={canWrite}
        createLabel="Créer la participation"
      />
    </div>
  );
}
