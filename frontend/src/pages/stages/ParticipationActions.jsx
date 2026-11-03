import { useCallback, useState } from 'react';
import { applyParticipationEvent, startParticipation } from '../../api/stageApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';
import { availableParticipationActions, participationEventPayload, todayMadagascar } from '../../utils/participationEvents.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import { formatDate } from '../../utils/dateUtils.js';
import { listActionsParticipation } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';

export default function ParticipationActions({ participation, onSaved }) {
  const { utilisateur } = useAuth();
  const [action, setAction] = useState(null);
  const [values, setValues] = useState({});
  const [pending, setPending] = useState(null);
  const [error, setError] = useState('');
  const allowed = hasAnyRole(utilisateur, ['ADMIN']);
  const fetcher = useCallback((options) => listActionsParticipation(participation.id, options), [participation.id]);
  const loaded = useFetch(fetcher);
  const actions = availableParticipationActions(participation, loaded.data ?? []);
  function prepare(event) {
    event.preventDefault();
    setError('');
    try { setPending(participationEventPayload(participation, action.code, values, todayMadagascar(), [action])); }
    catch (failure) { setError(failure.message); }
  }
  const update = (event) => setValues((current) => ({ ...current, [event.target.name]: event.target.value }));
  return <section className="page-stack">
    <h3>Suivi de la participation</h3>
    {loaded.loading && <p role="status">Chargement des actions autorisées…</p>}
    <ErrorMessage error={loaded.error} />
    {loaded.error && <Button onClick={loaded.reload}>Réessayer</Button>}
    {allowed && <>
      <div className="row-actions">{actions.map((item) => <Button variant="secondary" key={`${item.code}/${item.statutCible}`} onClick={() => { setAction(item); setValues({}); setError(''); }}>{item.label}</Button>)}</div>
      {action && <form onSubmit={prepare} className="page-stack">
        <h4>{action.label} — {participation.stagiaireNomComplet} (participation n° {participation.id})</h4>
        {action.dateEffet && <Input name="dateEffet" label="Date d’effet" type="date" required value={values.dateEffet ?? ''} min={participation.dateDebutStage}
          max={[participation.dateFinStage, todayMadagascar()].sort()[0]} onChange={update} hint="Elle doit suivre le dernier événement de suspension ou de reprise. Cette chronologie est vérifiée lors de l’enregistrement." />}
        {action.code === 'prolonger' && <><p>Fin actuelle : {formatDate(participation.dateFinStage)}</p><Input name="nouvelleDateFin" label="Nouvelle date de fin" type="date" required value={values.nouvelleDateFin ?? ''} min={participation.dateFinStage} onChange={update} /></>}
        <Input name="motif" label="Motif" type="textarea" value={values.motif ?? ''} onChange={update} />
        <ErrorMessage error={error} />
        <div className="row-actions"><Button type="submit">Vérifier et confirmer</Button><Button variant="secondary" onClick={() => setAction(null)}>Fermer</Button></div>
      </form>}
      <ConfirmDialog open={Boolean(pending)} title={action?.label ?? 'Confirmer'} onClose={() => setPending(null)} onConfirm={async () => {
        if (action.code === 'statut') await startParticipation(participation.id, pending);
        else await applyParticipationEvent(participation.id, action.code, pending);
        setPending(null);
        setAction(null);
        await onSaved();
      }}>
        <p>Confirmer « {action?.label} » pour {participation.stagiaireNomComplet}, participation n° {participation.id} ?</p>
        {(pending?.dateEffet || pending?.nouvelleDateFin) && <p>Date : {formatDate(pending.dateEffet ?? pending.nouvelleDateFin)}</p>}
        <p>Motif : {pending?.motif || 'Non renseigné'}</p>
        {action?.code === 'cloturer' && <p>La clôture sera refusée si une pièce obligatoire manque. Le serveur revérifie le dossier lors de l’enregistrement.</p>}
      </ConfirmDialog>
    </>}
  </section>;
}
