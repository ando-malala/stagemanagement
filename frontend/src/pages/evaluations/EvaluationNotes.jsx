import { useCallback, useRef, useState } from 'react';
import { addNoteEvaluation, finaliserEvaluation, listCriteresEvaluation, listNotesEvaluation } from '../../api/evaluationApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { requireList } from '../../utils/businessForms.js';
import { evaluationNotePayload, missingRequiredCriteria } from '../../utils/followUpWorkflows.js';
import Table from '../../components/common/Table.jsx';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';

const critereColumns = [{ key: 'libelle', label: 'Critère' }, { key: 'description', label: 'Description' },
  { key: 'coefficient', label: 'Coefficient' }, { key: 'obligatoire', label: 'Obligatoire' }, { key: 'note', label: 'Note / 5' }];
const noteColumns = [{ key: 'critereLibelle', label: 'Critère' }, { key: 'coefficient', label: 'Coefficient' },
  { key: 'note', label: 'Note / 5' }, { key: 'commentaire', label: 'Commentaire' }];

export default function EvaluationNotes({ evaluation, onSaved }) {
  const { utilisateur } = useAuth();
  const [editing, setEditing] = useState(null);
  const [confirming, setConfirming] = useState(false);
  const [message, setMessage] = useState('');
  const fetcher = useCallback(async (options) => {
    const [criteres, notes] = await Promise.all([listCriteresEvaluation(options), listNotesEvaluation(evaluation.id, options)]);
    return { criteres: requireList(criteres), notes: requireList(notes) };
  }, [evaluation.id]);
  const { data, error, loading, reload } = useFetch(fetcher);
  const canWrite = can(utilisateur, 'gererEvaluations') && !evaluation.estFinalisee;
  const missing = data ? missingRequiredCriteria(data.criteres, data.notes) : [];
  return <section className="page-stack">
    <div className="section-heading"><h3>Critères et notes</h3><Button variant="secondary" disabled={loading || Boolean(editing)} onClick={reload}>Actualiser les notes</Button></div>
    {evaluation.estFinalisee && <p className="notice">Cette évaluation est finalisée. Les appréciations et les notes ne sont plus modifiables.</p>}
    {message && <p role="status" className="alert alert-success">{message}</p>}
    {loading ? <Loader /> : error ? <ErrorMessage error={error} /> : data && <>
      <Table caption="Notes enregistrées" columns={noteColumns} data={data.notes} />
      <Table caption="Critères actifs" columns={critereColumns}
        data={data.criteres.map((critere) => ({ ...critere, note: data.notes.find((note) => note.critereId === critere.id)?.note ?? 'Non noté' }))}
        renderActions={canWrite && !editing ? (critere) => <Button variant="secondary" onClick={() => {
          setMessage('');
          setEditing({ critere, note: data.notes.find((note) => note.critereId === critere.id) });
        }}>{data.notes.some((note) => note.critereId === critere.id) ? 'Modifier la note' : 'Noter'}</Button> : undefined} />
      {canWrite && editing && <NoteForm key={editing.critere.id} evaluationId={evaluation.id} critere={editing.critere} initial={editing.note}
        onCancel={() => setEditing(null)} onSaved={async () => { setEditing(null); setMessage('Note enregistrée.'); await reload(); }} />}
      {canWrite && <>
        {missing.length > 0 && <p className="notice">Critères obligatoires restant à noter : {missing.map((critere) => critere.libelle).join(', ')}.</p>}
        <Button disabled={missing.length > 0 || Boolean(editing)} onClick={() => setConfirming(true)}>Finaliser l’évaluation</Button>
      </>}
    </>}
    <ConfirmDialog open={confirming} title="Finaliser l’évaluation" onClose={() => setConfirming(false)} onConfirm={async () => {
      await finaliserEvaluation(evaluation.id);
      setConfirming(false);
      await onSaved();
    }}>
      <p>Finaliser l’évaluation n° {evaluation.id} de {evaluation.stagiaire} ? Les appréciations et les notes ne pourront plus être modifiées.</p>
    </ConfirmDialog>
  </section>;
}

function NoteForm({ evaluationId, critere, initial, onCancel, onSaved }) {
  const [values, setValues] = useState({ note: initial?.note ?? '', commentaire: initial?.commentaire ?? '' });
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const pending = useRef(false);
  const change = (event) => setValues((current) => ({ ...current, [event.target.name]: event.target.value }));
  async function save(event) {
    event.preventDefault();
    if (pending.current) return;
    setError(null);
    try {
      const payload = evaluationNotePayload(critere.id, values);
      pending.current = true;
      setSaving(true);
      await addNoteEvaluation(evaluationId, payload);
      await onSaved();
    } catch (failure) { setError(failure); }
    finally { pending.current = false; setSaving(false); }
  }
  return <form onSubmit={save} className="page-stack">
    <h4>Noter : {critere.libelle}</h4>
    <Input name="note" label="Note sur 5" type="number" min="0" max="5" step="0.01" required value={values.note} onChange={change} disabled={saving} />
    <Input name="commentaire" label="Commentaire" type="textarea" value={values.commentaire} onChange={change} disabled={saving} />
    <ErrorMessage error={error} />
    <div className="row-actions"><Button type="submit" disabled={saving}>{saving ? 'Enregistrement…' : 'Enregistrer la note'}</Button>
      <Button variant="secondary" onClick={onCancel} disabled={saving}>Annuler</Button></div>
  </form>;
}
