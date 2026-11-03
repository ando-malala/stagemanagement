import { useState } from 'react';
import AppLink from '../../components/common/AppLink.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import { listRapports } from '../../api/rapportApi.js';
import MesRapports from './MesRapports.jsx';
import RapportsParStagiaire from './RapportsParStagiaire.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaire', label: 'Stagiaire' },
  { key: 'encadreur', label: 'Encadreur' },
  { key: 'debutSemaine', label: 'Début' },
  { key: 'finSemaine', label: 'Fin' },
  { key: 'moisStage', label: 'Mois', filterable: true },
  { key: 'statut', label: 'Statut', filterable: true }
];

export default function RapportsPage({ navigate }) {
  const { utilisateur } = useAuth();
  const [revision, setRevision] = useState(0);
  const trainee = can(utilisateur, 'gererRapports');
  if (!trainee) return <RapportsParStagiaire navigate={navigate} />;

  return (
    <div className="page-stack">
    {trainee && <MesRapports navigate={navigate} onSaved={() => setRevision(value => value + 1)} />}
    {can(utilisateur, 'consulterMetier') && <RapportsParStagiaire navigate={navigate} />}
    <EntityPage key={revision}
      title={trainee ? 'Rapports enregistrés' : 'Rapports d’activité'}
      description="Le stagiaire rédige et soumet ses rapports. Son encadreur les examine, demande une correction ou les valide."
      renderActions={(row) => <AppLink to={`/rapports/${row.id}`} navigate={navigate}>Consulter</AppLink>}
      columns={columns}
      fetcher={listRapports}
    />
    </div>
  );
}
