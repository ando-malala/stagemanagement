import { useState } from 'react';
import { createAlerte, listAlertes, marquerAlerteLue } from '../../api/alerteApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { canTreatAlert } from '../../utils/permissions.js';
import { requireList } from '../../utils/businessForms.js';
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

const fetcher = async (options) => requireList(await listAlertes(options));
const columns = [{ key: 'typeAlerteLibelle', label: 'Type' }, { key: 'message', label: 'Message' },
  { key: 'natureAlerte', label: 'Nature' }, { key: 'priorite', label: 'Priorité' },
  { key: 'dateEcheance', label: 'Échéance', render: (row) => formatDate(row.dateEcheance) }, { key: 'statut', label: 'Statut' }];

export default function AlertesPage({ navigate }) {
  const { utilisateur } = useAuth();
  const { data, loading, error, reload } = useFetch(fetcher);
  const [filters, setFilters] = useState(() => ({ statut: new URLSearchParams(typeof window === 'undefined' ? '' : window.location.search).get('statut') ?? '' }));
  const [selectedId, setSelectedId] = useState(() => new URLSearchParams(typeof window === 'undefined' ? '' : window.location.search).get('alerte'));
  const [confirmation, setConfirmation] = useState(null);
  const [message, setMessage] = useState('');
  const rows = data ?? [];
  const selected = rows.find((row) => String(row.id) === String(selectedId));
  async function refresh() { setSelectedId(null); await reload(); }
  return <Card>
    <div className="section-heading"><div><h2>Alertes</h2><p className="small">Filtres appliqués aux alertes reçues du serveur. Les alertes informatives ne se traitent pas manuellement.</p></div>
      <Button variant="secondary" disabled={loading} onClick={refresh}>Actualiser</Button></div>
    <BusinessEditor kind="alerte" label="Créer une alerte" initial={{ priorite: 'INFORMATION' }} save={createAlerte} onSaved={refresh} />
    <p className="notice">Les types d’alertes proposés à la création sont chargés depuis le serveur.</p>
    <div className="entity-form">{[['statut', 'Statut'], ['priorite', 'Priorité'], ['natureAlerte', 'Nature'], ['typeAlerteCode', 'Type']].map(([key, label]) => <Input key={key} name={key} label={label} type="select"
      value={filters[key] ?? ''} options={filterOptions(rows, key, filters[key])} onChange={(event) => setFilters((current) => ({ ...current, [key]: event.target.value }))} />)}
      <Button variant="secondary" onClick={() => setFilters({})}>Réinitialiser les filtres</Button></div>
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {!loading && !error && selectedId && !selected && <p className="notice">L’alerte demandée n’est pas présente dans la liste reçue.</p>}
    <ErrorMessage error={error} />
    <Table caption="Alertes" columns={columns} data={filterAlertes(rows, filters)} loading={loading} error={error}
      renderActions={(row) => <div className="row-actions"><Button variant="secondary" onClick={() => setSelectedId(row.id)}>Consulter</Button>
        {canTreatAlert(utilisateur, row) && <Button onClick={() => setConfirmation(row)}>Traiter</Button>}</div>} />
    <Modal open={Boolean(selected)} title={`Alerte n° ${selected?.id ?? ''}`} onClose={() => setSelectedId(null)}>{selected && <AlerteDetail alerte={selected} navigate={navigate} />}</Modal>
    <ConfirmDialog open={Boolean(confirmation)} title="Traiter cette alerte" onClose={() => setConfirmation(null)} onConfirm={async () => {
      await marquerAlerteLue(confirmation.id); setConfirmation(null); setMessage('Alerte traitée.'); await refresh();
    }}><p>Confirmer le traitement de l’alerte n° {confirmation?.id} après vérification de la situation ?</p><p>{confirmation?.message}</p></ConfirmDialog>
  </Card>;
}

function filterOptions(rows, key, selected) {
  const options = alertFilterOptions(rows, key);
  if (selected && !options.some((option) => option.value === selected)) options.push({ value: selected, label: `${selected} (aucune alerte reçue)` });
  return options;
}
