import { useCallback, useState } from 'react';
import { changeRapportStatus } from '../../api/rapportApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { rapportTransitions, rapportStatusPayload } from '../../utils/followUpWorkflows.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import { listTransitions } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

export default function RapportActions({ rapport, onSaved }) {
  const { utilisateur } = useAuth();
  const [target, setTarget] = useState(null);
  const [motif, setMotif] = useState('');
  const [pending, setPending] = useState(null);
  const fetcher = useCallback((options) => listTransitions('RAPPORT', rapport.statut, options), [rapport.statut]);
  const transitions = useFetch(fetcher);
  const actions = rapportTransitions(rapport.statut, transitions.data ?? []);
  if (!can(utilisateur, 'gererRapports')) return null;
  return <section className="page-stack">
    <h3>Validation du rapport</h3>
    {transitions.loading && <p role="status">Chargement des transitions…</p>}
    <ErrorMessage error={transitions.error} />
    {transitions.error && <Button onClick={transitions.reload}>Réessayer</Button>}
    {rapport.statut === 'VALIDE' && <p className="notice">Ce rapport est validé et ne peut plus être modifié.</p>}
    <div className="row-actions">{actions.map((action) => <Button key={action.code} variant="secondary" onClick={() => { setTarget(action); setMotif(''); }}>{action.label}</Button>)}</div>
    {target && <form className="page-stack" onSubmit={(event) => {
      event.preventDefault();
      setPending(rapportStatusPayload(rapport.statut, target.code, motif, transitions.data ?? []));
    }}>
      <Input label="Motif du changement de statut" name="motif" type="textarea" value={motif} onChange={(event) => setMotif(event.target.value)} />
      <div className="row-actions"><Button type="submit">Confirmer : {target.label.toLowerCase()}</Button><Button variant="secondary" onClick={() => setTarget(null)}>Annuler</Button></div>
    </form>}
    <ConfirmDialog open={Boolean(pending)} title={target?.label ?? 'Changer le statut'} onClose={() => setPending(null)} onConfirm={async () => {
      await changeRapportStatus(rapport.id, pending);
      setPending(null);
      setTarget(null);
      await onSaved();
    }}>
      <p>Rapport n° {rapport.id} — {rapport.stagiaire}. Confirmer « {target?.label} » ?</p>
      {pending?.statutCode === 'VALIDE' && <p>Le contenu du rapport ne sera plus modifiable.</p>}
      <p>Motif : {pending?.motif || 'Non renseigné'}</p>
    </ConfirmDialog>
  </section>;
}
