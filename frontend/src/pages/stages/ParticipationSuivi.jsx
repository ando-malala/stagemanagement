import { useCallback } from 'react';
import { listHistoriqueStatuts, listEvenements, listPeriodesRapports } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Table from '../../components/common/Table.jsx';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import { formatDate, formatDateTime } from '../../utils/dateUtils.js';

export function SuiviTable({ title, fetcher, columns, renderActions }) {
  const { data, error, loading, reload } = useFetch(fetcher);
  return <Card><div className="section-heading"><h3>{title}</h3><Button variant="secondary" disabled={loading} onClick={reload}>Actualiser</Button></div>
    <Table caption={title} data={data ?? []} columns={columns} loading={loading} error={error} renderActions={renderActions} /></Card>;
}
const historyColumns = [{ key: 'dateModification', label: 'Date', render: (r) => formatDateTime(r.dateModification) },
  { key: 'ancienStatut', label: 'Ancien statut' }, { key: 'statut', label: 'Nouveau statut' }, { key: 'motif', label: 'Motif' }, { key: 'responsableId', label: 'Responsable (n°)' }];
const eventColumns = [{ key: 'dateEvenement', label: 'Enregistré le', render: (r) => formatDateTime(r.dateEvenement) },
  { key: 'libelle', label: 'Événement' }, { key: 'dateEffet', label: 'Date d’effet', render: (r) => formatDate(r.dateEffet) },
  { key: 'ancienneDateFin', label: 'Ancienne fin' }, { key: 'nouvelleDateFin', label: 'Nouvelle fin' }, { key: 'motif', label: 'Motif' }, { key: 'responsableId', label: 'Responsable (n°)' }];
const periodColumns = [{ key: 'debut', label: 'Début' }, { key: 'fin', label: 'Fin' }, { key: 'dateLimite', label: 'Échéance' },
  { key: 'statutRapport', label: 'Rapport' }, { key: 'soumise', label: 'Remise', render: (r) => r.soumise ? 'Oui' : 'Non' },
  { key: 'validee', label: 'Validée', render: (r) => r.validee ? 'Oui' : 'Non' },
  { key: 'manquante', label: 'Suivi', render: (r) => !r.exigible ? 'Non exigible' : r.manquante ? 'Rapport manquant' : r.soumise ? 'Remis' : 'Échéance à venir' }];
export default function ParticipationSuivi({ id, navigate }) {
  const history = useCallback((o) => listHistoriqueStatuts(id, o), [id]);
  const events = useCallback((o) => listEvenements(id, o), [id]);
  const periods = useCallback(async (o) => (await listPeriodesRapports(id, o)).map((r) => ({ ...r, id: r.debut })), [id]);
  return <>
    <SuiviTable title="Historique des statuts" fetcher={history} columns={historyColumns} />
    <SuiviTable title="Événements de la participation" fetcher={events} columns={eventColumns} />
    <SuiviTable title="Périodes de rapports attendues" fetcher={periods} columns={periodColumns}
      renderActions={(r) => r.rapportId && <AppLink to={`/rapports/${r.rapportId}`} navigate={navigate}>Consulter le rapport</AppLink>} />
  </>;
}
