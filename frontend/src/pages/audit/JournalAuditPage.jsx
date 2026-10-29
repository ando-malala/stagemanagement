import { useCallback, useState } from 'react';
import { listJournalAudit, listAuditByUtilisateur, listAuditByEntite } from '../../api/auditApi.js';
import { listUtilisateurs } from '../../api/utilisateurApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { auditQuery, redactAuditValue } from '../../utils/pilotage.js';
import { requireList, toOptions } from '../../utils/businessForms.js';
import { formatDateTime } from '../../utils/dateUtils.js';
import Card from '../../components/common/Card.jsx';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import Table from '../../components/common/Table.jsx';
import Modal from '../../components/common/Modal.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import AppLink from '../../components/common/AppLink.jsx';

const columns = [{ key: 'utilisateur', label: 'Utilisateur' }, { key: 'action', label: 'Action' },
  { key: 'entite', label: 'Entité' }, { key: 'entiteId', label: 'Identifiant de l’entité' },
  { key: 'dateAction', label: 'Date', render: (row) => formatDateTime(row.dateAction) }];
const emptyQuery = { mode: 'all', utilisateurId: '', entite: '', entiteId: '' };

export default function JournalAuditPage({ navigate }) {
  const [draft, setDraft] = useState(emptyQuery);
  const [query, setQuery] = useState({ mode: 'all' });
  const [validationError, setValidationError] = useState(null);
  const [selected, setSelected] = useState(null);
  const fetcher = useCallback(async (options) => requireList(await (query.mode === 'user'
    ? listAuditByUtilisateur(query.utilisateurId, options) : query.mode === 'entity'
      ? listAuditByEntite(query.entite, query.entiteId, options) : listJournalAudit(options))), [query]);
  const { data, loading, error, reload } = useFetch(fetcher);
  const fetchUsers = useCallback(async (options) => draft.mode === 'user' ? requireList(await listUtilisateurs(options)) : [], [draft.mode]);
  const users = useFetch(fetchUsers);
  const change = (event) => setDraft((current) => ({ ...current, [event.target.name]: event.target.value }));
  return <Card>
    <div className="section-heading"><div><h2>Journal d’audit</h2><p className="small">Consultation des actions enregistrées par le serveur. Aucun ajout ou retrait depuis cette page.</p></div>
      <Button variant="secondary" disabled={loading} onClick={() => { setSelected(null); void reload(); }}>Actualiser</Button></div>
    <form className="entity-form" onSubmit={(event) => {
      event.preventDefault(); setValidationError(null);
      try { const next = auditQuery(draft); setSelected(null); setQuery(next); }
      catch (failure) { setValidationError(failure); }
    }}>
      <Input label="Périmètre de recherche" name="mode" type="select" required value={draft.mode} onChange={change}
        options={[{ value: 'all', label: 'Tout le journal' }, { value: 'user', label: 'Par utilisateur' }, { value: 'entity', label: 'Par entité' }]} />
      {draft.mode === 'user' && <Input label="Utilisateur" name="utilisateurId" type="select" required value={draft.utilisateurId} onChange={change}
        options={toOptions(users.data ?? [], ['identifiant'])} disabled={users.loading || Boolean(users.error)} />}
      {draft.mode === 'entity' && <>
        <Input label="Nom exact de l’entité" name="entite" required value={draft.entite} onChange={change} hint="Reprenez le nom affiché dans une ligne du journal." />
        <Input label="Identifiant de l’entité" name="entiteId" type="number" min={1} max={2147483647} step={1} required value={draft.entiteId} onChange={change} />
      </>}
      <Button type="submit" disabled={loading || (draft.mode === 'user' && (users.loading || Boolean(users.error)))}>Rechercher</Button>
      <Button variant="secondary" onClick={() => { setDraft(emptyQuery); setQuery({ mode: 'all' }); setSelected(null); setValidationError(null); }}>Tout afficher</Button>
    </form>
    {draft.mode === 'user' && users.error && <><ErrorMessage error={users.error} /><Button onClick={users.reload} variant="secondary">Recharger les utilisateurs</Button></>}
    <ErrorMessage error={validationError} /><ErrorMessage error={error} />
    <p className="small">Résultats : {query.mode === 'all' ? 'tout le journal' : query.mode === 'user' ? `utilisateur n° ${query.utilisateurId}` : `${query.entite} n° ${query.entiteId}`}.</p>
    <Table columns={columns} data={data ?? []} loading={loading} error={error} caption="Journal d’audit"
      renderActions={(row) => <Button variant="secondary" onClick={() => setSelected(row)}>Consulter les valeurs</Button>} />
    <Modal open={Boolean(selected)} title={`Événement d’audit n° ${selected?.id ?? ''}`} onClose={() => setSelected(null)}>
      {selected && <div className="page-stack">
        <p>{selected.action} — {selected.entite} n° {selected.entiteId}</p>
        <p>{formatDateTime(selected.dateAction)} · {selected.utilisateur || 'Auteur non renseigné'}</p>
        {selected.utilisateurId != null && <AppLink to={`/utilisateurs/${selected.utilisateurId}`} navigate={navigate}>Consulter l’utilisateur</AppLink>}
        <h3>Anciennes valeurs</h3><pre className="audit-values">{formatValues(selected.anciennesValeurs)}</pre>
        <h3>Nouvelles valeurs</h3><pre className="audit-values">{formatValues(selected.nouvellesValeurs)}</pre>
      </div>}
    </Modal>
  </Card>;
}

function formatValues(value) { return value == null ? 'Non renseignées' : JSON.stringify(redactAuditValue(value), null, 2); }
