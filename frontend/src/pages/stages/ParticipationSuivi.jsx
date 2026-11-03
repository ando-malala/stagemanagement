import { useCallback } from 'react';
import { listHistoriqueStatuts, listEvenements } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Table from '../../components/common/Table.jsx';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import { formatDate, formatDateTime } from '../../utils/dateUtils.js';

export function SuiviTable({ title, fetcher, columns, renderActions }) {
  const { data, error, loading, reload } = useFetch(fetcher);
  return <Card><div className="section-heading"><h3>{title}</h3><Button variant="secondary" disabled={loading} onClick={reload}>Actualiser</Button></div>
    <ErrorMessage error={error} />
    <Table caption={title} data={data ?? []} columns={columns} loading={loading} error={error} renderActions={renderActions} /></Card>;
}
const historyColumns = [{ key: 'dateModification', label: 'Date', render: (r) => formatDateTime(r.dateModification) },
  { key: 'ancienStatut', label: 'Ancien statut' }, { key: 'statut', label: 'Nouveau statut' }, { key: 'motif', label: 'Motif' }, { key: 'responsableId', label: 'Responsable (n°)' }];
const eventColumns = [{ key: 'dateEvenement', label: 'Enregistré le', render: (r) => formatDateTime(r.dateEvenement) },
  { key: 'libelle', label: 'Événement' }, { key: 'dateEffet', label: 'Date d’effet', render: (r) => formatDate(r.dateEffet) },
  { key: 'ancienneDateFin', label: 'Ancienne fin' }, { key: 'nouvelleDateFin', label: 'Nouvelle fin' }, { key: 'motif', label: 'Motif' }, { key: 'responsableId', label: 'Responsable (n°)' }];
export default function ParticipationSuivi({ id }) {
  const history = useCallback((o) => listHistoriqueStatuts(id, o), [id]);
  const events = useCallback((o) => listEvenements(id, o), [id]);
  return <>
    <SuiviTable title="Historique des statuts" fetcher={history} columns={historyColumns} />
    <SuiviTable title="Événements de la participation" fetcher={events} columns={eventColumns} />
  </>;
}
