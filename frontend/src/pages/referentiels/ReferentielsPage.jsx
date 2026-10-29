import { useCallback, useState } from 'react';
import { createReferentiel, listReferentiel, updateReferentiel } from '../../api/referentielApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { requireList } from '../../utils/businessForms.js';
import { referentiels } from '../../utils/referentiels.js';
import BusinessForm from '../../components/common/BusinessForm.jsx';
import Button from '../../components/common/Button.jsx';
import Card from '../../components/common/Card.jsx';
import Table from '../../components/common/Table.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

export default function ReferentielsPage({ group }) {
  const choices = referentiels.filter((item) => !group || item.group === group);
  const [activeId, setActiveId] = useState(choices[0].id);
  const active = choices.find((item) => item.id === activeId) ?? choices[0];
  return <div className="page-stack">
    <div className="segmented" role="group" aria-label="Choisir le référentiel">
      {choices.map((item) => <button type="button" key={item.id} aria-pressed={item.id === active.id} className={item.id === active.id ? 'active' : ''} onClick={() => setActiveId(item.id)}>{item.title}</button>)}
    </div>
    <ReferencePanel key={active.id} config={active} />
  </div>;
}

function ReferencePanel({ config }) {
  const { utilisateur } = useAuth();
  const writable = can(utilisateur, 'modifierReferentiels');
  const fetcher = useCallback(async (options) => requireList(await listReferentiel(config.endpoint, options)), [config.endpoint]);
  const { data, error, loading, reload } = useFetch(fetcher);
  const [editing, setEditing] = useState(null);
  const [message, setMessage] = useState('');
  return <Card>
    <div className="section-heading"><h2>{config.title}</h2><Button variant="secondary" disabled={loading || Boolean(editing)} onClick={reload}>Actualiser</Button></div>
    {config.kind === 'departement' && <p className="notice">Le statut existant est conservé lors d’une modification. Les nouveaux départements utilisent le statut par défaut du serveur.</p>}
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {writable && !editing && <Button onClick={() => { setMessage(''); setEditing({ actif: 'true' }); }}>Créer</Button>}
    {writable && editing && <section className="page-stack"><h3>{editing.id ? `Modifier : ${editing.libelle ?? editing.themeStage}` : 'Nouvelle référence'}</h3>
      <BusinessForm key={editing.id ?? 'new'} kind={config.kind} initial={editing} onCancel={() => setEditing(null)}
        onSave={(payload) => editing.id ? updateReferentiel(config.endpoint, editing.id, payload) : createReferentiel(config.endpoint, payload)}
        onSaved={async () => { setEditing(null); setMessage('Référence enregistrée.'); await reload(); }} />
    </section>}
    <ErrorMessage error={error} />
    <Table caption={config.title} columns={config.columns} data={data ?? []} error={error} loading={loading}
      renderActions={writable && !editing ? (row) => <Button variant="secondary" onClick={() => { setMessage(''); setEditing(row); }}>Modifier</Button> : undefined} />
  </Card>;
}
