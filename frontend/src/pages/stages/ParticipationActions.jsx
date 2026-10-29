import { useState } from 'react';
import { applyParticipationEvent, startParticipation } from '../../api/stageApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';
import { availableParticipationActions, participationEventPayload, todayMadagascar } from '../../utils/participationEvents.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import { formatDate } from '../../utils/dateUtils.js';

export default function ParticipationActions({ participation, onSaved }) {
  const { utilisateur } = useAuth();
  const [action, setAction] = useState(null);
  const [values, setValues] = useState({});
  const [pending, setPending] = useState(null);
  const [error, setError] = useState('');
  const allowed = hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE']);
  const actions = availableParticipationActions(participation);
  function prepare(event) {
    event.preventDefault();
    setError('');
    try { setPending(participationEventPayload(participation, action.code, values)); }
    catch (failure) { setError(failure.message); }
  }
  const update = (event) => setValues((current) => ({ ...current, [event.target.name]: event.target.value }));
  return <section className="page-stack">
    <h3>Suivi de la participation</h3>
    <p className="notice">L’historique des événements n’est pas disponible à la consultation. La clôture n’est pas encore disponible.</p>
    {allowed && <>
      <div className="row-actions">{actions.map((item) => <Button variant="secondary" key={item.code} onClick={() => { setAction(item); setValues({}); setError(''); }}>{item.label}</Button>)}</div>
      {action && <form onSubmit={prepare} className="page-stack">
        <h4>{action.label} — participation n° {participation.id}</h4>
        {action.dateEffet && <Input name="dateEffet" label="Date d’effet" type="date" required value={values.dateEffet ?? ''} min={participation.dateDebutStage}
          max={[participation.dateFinStage, todayMadagascar()].sort()[0]} onChange={update} hint="Elle doit suivre le dernier événement de suspension ou de reprise. Cette chronologie est vérifiée lors de l’enregistrement." />}
        {action.code === 'prolonger' && <><p>Fin actuelle : {formatDate(participation.dateFinStage)}</p><Input name="nouvelleDateFin" label="Nouvelle date de fin" type="date" required value={values.nouvelleDateFin ?? ''} min={participation.dateFinStage} onChange={update} /></>}
        <Input name="motif" label="Motif" type="textarea" value={values.motif ?? ''} onChange={update} />
        <ErrorMessage error={error} />
        <div className="row-actions"><Button type="submit">Vérifier et confirmer</Button><Button variant="secondary" onClick={() => setAction(null)}>Fermer</Button></div>
      </form>}
      <ConfirmDialog open={Boolean(pending)} title={action?.label ?? 'Confirmer'} onClose={() => setPending(null)} onConfirm={async () => {
        if (action.code === 'demarrer') await startParticipation(participation.id, pending);
        else await applyParticipationEvent(participation.id, action.code, pending);
        setPending(null);
        setAction(null);
        await onSaved();
      }}>
        <p>Confirmer « {action?.label} » pour {participation.stagiaireNomComplet}, participation n° {participation.id} ?</p>
        {(pending?.dateEffet || pending?.nouvelleDateFin) && <p>Date : {formatDate(pending.dateEffet ?? pending.nouvelleDateFin)}</p>}
        <p>Motif : {pending?.motif || 'Non renseigné'}</p>
      </ConfirmDialog>
    </>}
  </section>;
}
