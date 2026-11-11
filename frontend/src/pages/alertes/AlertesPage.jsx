import { useState } from 'react';
import { createAlerte, marquerAlerteLue } from '../../api/alerteApi.js';
import { loadAlertContext } from '../../api/alertContextApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { canTreatAlert } from '../../utils/permissions.js';
import { filterAlertes, alertFilterOptions } from '../../utils/pilotage.js';
import { formatDate } from '../../utils/dateUtils.js';
import Button from '../../components/common/Button.jsx';
import Card from '../../components/common/Card.jsx';
import Input from '../../components/common/Input.jsx';
import Table from '../../components/common/Table.jsx';
import Modal from '../../components/common/Modal.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import AlerteDetail from './AlerteDetail.jsx';
import AlertOverview from './AlertOverview.jsx';
import StatusBadge from '../../components/common/StatusBadge.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';
import { alertLabel, sortAlerts } from '../../utils/alertOverview.js';

const fetcher = async (options) => (await loadAlertContext(options)).rows;
const alertColumn = (key, label) => ({ key, label, render: row => <StatusBadge value={row[key]} label={alertLabel(key, row[key])} /> });
const columns = [alertColumn('priorite', 'Priorité'),
  { key: 'typeAlerteLibelle', label: 'Type', render: row => row.typeAlerteLibelle || row.typeAlerteCode || 'Non renseigné' },
  { key: 'stagiaireNomComplet', label: 'Stagiaire' }, { key: 'message', label: 'Message' },
  { key: 'dateEcheance', label: 'Échéance', render: (row) => formatDate(row.dateEcheance) },
  alertColumn('natureAlerte', 'Nature'), alertColumn('statut', 'Statut')];

export default function AlertesPage({ navigate }) {
  const { utilisateur } = useAuth();
  const { data, loading, error, reload } = useFetch(fetcher);
  const [filters, setFilters] = useState(() => ({ statut: new URLSearchParams(typeof window === 'undefined' ? '' : window.location.search).get('statut') ?? '' }));
  const [selectedId, setSelectedId] = useState(() => new URLSearchParams(typeof window === 'undefined' ? '' : window.location.search).get('alerte'));
  const [confirmation, setConfirmation] = useState(null);
  const [message, setMessage] = useState('');
  const [filterRevision, setFilterRevision] = useState(0);
  const rows = data ?? [];
  const visibleRows = sortAlerts(filterAlertes(rows, filters));
  const selected = rows.find((row) => String(row.id) === String(selectedId));
  function selectFilters(next) { setFilters(next); setFilterRevision(value => value + 1); }
  async function refresh() { setSelectedId(null); await reload(); }
  return <Card>
    <div className="section-heading"><div><h2>Alertes</h2><p className="small">Repérez les priorités et retrouvez les dossiers à suivre.</p></div>
      <Button variant="secondary" disabled={loading} onClick={refresh}><NavigationIcon name="refresh" />Actualiser</Button></div>
    {!loading && !error && <AlertOverview rows={rows} filters={filters} onSelect={selectFilters} />}
    <section id="alert-results" aria-label="Liste des alertes">
    <div className="section-heading"><div><h3>Liste des alertes</h3><p className="small">Par défaut : priorité la plus urgente, puis échéance la plus proche.</p></div></div>
    <div className="alert-create"><BusinessEditor kind="alerte" label="Créer une alerte" initial={{ priorite: 'INFORMATION' }} save={createAlerte} onSaved={refresh} /></div>
    <div className="entity-form">{[['statut', 'Statut'], ['priorite', 'Priorité'], ['natureAlerte', 'Nature'], ['typeAlerteCode', 'Type']].map(([key, label]) => <Input key={key} name={key} label={label} type="select"
      emptyOptionLabel="Toutes" value={filters[key] ?? ''} options={filterOptions(rows, key, filters[key])} onChange={(event) => selectFilters({ ...filters, [key]: event.target.value })} />)}
      <Button variant="secondary" onClick={() => selectFilters({})}>Réinitialiser les filtres</Button></div>
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {!loading && !error && selectedId && !selected && <p className="notice">L’alerte demandée n’est pas présente dans la liste reçue.</p>}
    <ErrorMessage error={error} />
    {!loading && !error && !visibleRows.length ? <EmptyState message={rows.length ? 'Aucune alerte pour ces filtres. Sélectionnez « Toutes » pour retrouver la liste complète.' : 'Aucune alerte à afficher.'} /> :
    <Table key={filterRevision} caption="Alertes" columns={columns} data={visibleRows} loading={loading} error={error}
      renderActions={(row) => <div className="row-actions"><Button variant="secondary" onClick={() => setSelectedId(row.id)}>Consulter</Button>
        {canTreatAlert(utilisateur, row) && <Button onClick={() => setConfirmation(row)}>Traiter</Button>}</div>} />}
    </section>
    <Modal open={Boolean(selected)} title={`Alerte n° ${selected?.id ?? ''}`} onClose={() => setSelectedId(null)}>{selected && <AlerteDetail alerte={selected} navigate={navigate} />}</Modal>
    <ConfirmDialog open={Boolean(confirmation)} title="Traiter cette alerte" onClose={() => setConfirmation(null)} onConfirm={async () => {
      await marquerAlerteLue(confirmation.id); setConfirmation(null); setMessage('Alerte traitée.'); await refresh();
    }}><p>Confirmer le traitement de l’alerte n° {confirmation?.id} après vérification de la situation ?</p><p>{confirmation?.message}</p></ConfirmDialog>
  </Card>;
}

function filterOptions(rows, key, selected) {
  const options = alertFilterOptions(rows, key).map(option => ({ ...option, label: key === 'typeAlerteCode' ? option.label : alertLabel(key, option.value) }));
  if (selected && !options.some((option) => option.value === selected)) options.push({ value: selected, label: `${selected} (aucune alerte reçue)` });
  return options;
}
