/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState } from 'react';
import AppLink from '../../components/common/AppLink.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import { addNoteEvaluation, createEvaluation, finaliserEvaluation, listCriteresEvaluation, listEvaluations } from '../../api/evaluationApi.js';
import Button from '../../components/common/Button.jsx';
import Card from '../../components/common/Card.jsx';
import EntityPage from '../../components/common/EntityPage.jsx';
import Input from '../../components/common/Input.jsx';
import Table from '../../components/common/Table.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'stagiaire', label: 'Stagiaire' },
  { key: 'encadreur', label: 'Encadreur' },
  { key: 'appreciationGlobale', label: 'Appréciation' },
  { key: 'estFinalisee', label: 'Finalisée', render: (row) => row.estFinalisee ? 'Oui' : 'Non' },
  { key: 'dateEvaluation', label: 'Date' }
];

const fields = [
  { name: 'stageParticipantId', label: 'ID participation', type: 'number', required: true },
  { name: 'encadreurId', label: 'ID encadreur', type: 'number', required: true },
  { name: 'appreciationGlobale', label: 'Appréciation globale', type: 'textarea', required: true },
  { name: 'observationsFinales', label: 'Observations finales', type: 'textarea' }
];

const noteInitiale = {
  evaluationId: '',
  critereId: '',
  note: '',
  commentaire: ''
};

export default function EvaluationsPage({ navigate }) {
  const { utilisateur } = useAuth();
  const [confirmation, setConfirmation] = useState(null);
  const canWrite = hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE', 'ENCADREUR']);

  return (
    <div className="page-stack">
      <EntityPage
        title="Évaluations"
        description="Évaluations finales et appréciations globales."
        columns={columns}
        fields={fields}
        fetcher={listEvaluations}
        creator={createEvaluation}
        canCreate={canWrite}
        createLabel="Créer l’évaluation"
        renderActions={(row, reload) => (
          <div className="row-actions"><AppLink to={`/evaluations/${row.id}`} navigate={navigate}>Consulter</AppLink>
            {canWrite && !row.estFinalisee && <Button variant="secondary" onClick={() => setConfirmation({ row, reload })}>Finaliser</Button>}
          </div>
        )}
      />

      <NotesEvaluationPanel canWrite={canWrite} />
      <ConfirmDialog open={Boolean(confirmation)} title="Finaliser l’évaluation" onClose={() => setConfirmation(null)} onConfirm={async () => { await finaliserEvaluation(confirmation.row.id); await confirmation.reload(); }}>
        <p>Une évaluation finalisée ne peut plus être modifiée.</p>
      </ConfirmDialog>
    </div>
  );
}

function NotesEvaluationPanel({ canWrite }) {
  const [criteres, setCriteres] = useState([]);
  const [form, setForm] = useState(noteInitiale);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    let ignore = false;
    setLoading(true);

    listCriteresEvaluation()
      .then((result) => {
        if (!ignore) setCriteres(Array.isArray(result) ? result : []);
      })
      .catch((e) => {
        if (!ignore) setError(e.message);
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });

    return () => {
      ignore = true;
    };
  }, []);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function submitNote(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    try {
      await addNoteEvaluation(Number(form.evaluationId), {
        critereId: Number(form.critereId),
        note: Number(form.note),
        commentaire: form.commentaire
      });
      setForm(noteInitiale);
      setMessage('Note enregistrée.');
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  }

  const critereColumns = [
    { key: 'id', label: 'ID' },
    { key: 'code', label: 'Code' },
    { key: 'libelle', label: 'Critère' },
    { key: 'coefficient', label: 'Coefficient' },
    { key: 'obligatoire', label: 'Obligatoire', render: (row) => row.obligatoire ? 'Oui' : 'Non' }
  ];

  return (
    <Card>
      <div className="section-heading">
        <div>
          <h2>Critères et notes</h2>
          <p className="small">Consultez les critères et ajoutez une note à une évaluation existante.</p>
        </div>
      </div>

      {canWrite && (
        <form className="entity-form" onSubmit={submitNote}>
          <Input label="ID évaluation" name="evaluationId" type="number" value={form.evaluationId} onChange={updateField} required />
          <Input label="ID critère" name="critereId" type="number" value={form.critereId} onChange={updateField} required />
          <Input label="Note" name="note" type="number" value={form.note} onChange={updateField} required />
          <Input label="Commentaire" name="commentaire" value={form.commentaire} onChange={updateField} />
          <Button type="submit" disabled={saving}>{saving ? 'Enregistrement...' : 'Ajouter la note'}</Button>
        </form>
      )}

      {message && <p className="alert alert-success" role="status">{message}</p>}
      {error && <p className="alert alert-danger" role="alert">{error}</p>}
      <Table columns={critereColumns} data={criteres} loading={loading} />
    </Card>
  );
}
