import { Fragment, useCallback, useRef, useState } from 'react';
import { loadBusinessChoices } from '../../api/businessChoices.js';
import { listAcademiques } from '../../api/stagiaireApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { useFetch } from '../../hooks/useFetch.js';
import { hasAnyRole } from '../../utils/roleUtils.js';
import { businessFields, businessPayload, requireList, toOptions, updateBusinessField } from '../../utils/businessForms.js';
import { formatDate } from '../../utils/dateUtils.js';
import Input from './Input.jsx';
import Button from './Button.jsx';
import ErrorMessage from './ErrorMessage.jsx';
import { listPeriodesRapports } from '../../api/suiviApi.js';
import EncadreurCharge from '../../pages/encadreurs/EncadreurCharge.jsx';

export default function BusinessForm({ kind, initial = {}, reportContext, onSave, onSaved, onCancel }) {
  const { utilisateur } = useAuth();
  const isAdmin = hasAnyRole(utilisateur, ['ADMIN']);
  const fixedReport = kind === 'rapport' && Boolean(reportContext);
  const fields = businessFields[kind];
  const [values, setValues] = useState(() => Object.fromEntries(fields.map(({ name }) => [name, initial[name] ?? ''])));
  const [stagiaireId, setStagiaireId] = useState(initial.stagiaireId ?? '');
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const pending = useRef(false);
  const loadChoices = useCallback((options) => loadBusinessChoices(kind, isAdmin, options, fixedReport), [kind, isAdmin, fixedReport]);
  const choices = useFetch(loadChoices);
  const loadProfiles = useCallback(async (options) => kind === 'participation' && stagiaireId
    ? requireList(await listAcademiques(stagiaireId, options)) : [], [kind, stagiaireId]);
  const profiles = useFetch(loadProfiles);
  const loadPeriods = useCallback((options) => kind === 'rapport' && values.stageParticipantId
    ? listPeriodesRapports(values.stageParticipantId, options) : Promise.resolve([]), [kind, values.stageParticipantId]);
  const periods = useFetch(loadPeriods);
  const availablePeriods = (periods.data ?? []).filter(p => !p.rapportId || p.rapportId === initial.id);
  const blocked = (kind === 'rapport' && (periods.loading || Boolean(periods.error))) || saving || choices.loading || Boolean(choices.error) || (kind === 'participation' && (profiles.loading || Boolean(profiles.error)));

  async function submit(event) {
    event.preventDefault();
    if (pending.current || blocked) return;
    setError(null);
    try {
      const payload = businessPayload(kind, values);
      if (kind === 'rapport' && !availablePeriods.some(p => p.debut === payload.debutSemaine && p.fin === payload.finSemaine)) throw new Error('Choisissez une période disponible pour ce stage.');
      // A dependent profile must belong to the currently selected trainee.
      if (kind === 'participation' && !profiles.data?.some((row) => row.id === payload.stagiaireAcademiqueId)) throw new Error('Sélectionnez un profil académique de ce stagiaire.');
      pending.current = true;
      setSaving(true);
      const result = await onSave(payload);
      await onSaved?.(result);
    } catch (failure) { setError(failure); }
    finally { pending.current = false; setSaving(false); }
  }

  return <form className="page-stack business-form" onSubmit={submit} aria-label={initial.id ? 'Modifier le dossier' : 'Créer un dossier'}>
    <p className="form-introduction">Les champs marqués d’un * sont obligatoires.</p>
    {kind === 'rapport' && <p className="notice">{fixedReport ? 'Le stage et la semaine sont déjà renseignés. Complétez votre rapport, puis enregistrez le brouillon avant de le soumettre.' : 'Choisissez le stage, puis la semaine concernée. Enregistrez votre brouillon ; vous pourrez le relire et le soumettre depuis sa fiche.'}</p>}
    {fixedReport && <p>{reportContext.stageLabel && <><strong>{reportContext.stageLabel}</strong> — </>}Semaine du {formatDate(values.debutSemaine)} au {formatDate(values.finSemaine)}</p>}
    {kind === 'rapport' && !choices.loading && !choices.error && choices.data?.stageParticipantId?.length === 0 && <p className="notice">Aucun stage n’est associé à votre compte pour la rédaction d’un rapport. Contactez l’administrateur pour vérifier votre affectation.</p>}
    {choices.loading && <p role="status">Chargement des choix…</p>}
    {choices.error && <><ErrorMessage error={choices.error} /><Button onClick={choices.reload} variant="secondary">Recharger les choix</Button></>}
    {kind === 'encadreur' && !isAdmin && <p className="notice">L’association à un compte est réservée ici à l’administrateur. Le compte déjà associé est conservé.</p>}
    {kind === 'participation' && <>
      <Input type="select" name="stagiaireId" label="Stagiaire" required value={stagiaireId} disabled={saving || choices.loading || Boolean(choices.error)}
        options={toOptions(choices.data?.stagiaireId ?? [], ['nom', 'prenoms'])} onChange={(event) => {
          setStagiaireId(event.target.value);
          setValues((current) => ({ ...current, stagiaireAcademiqueId: '' }));
        }} />
      {profiles.loading && stagiaireId && <p role="status">Chargement des profils académiques…</p>}
      {profiles.error && <><ErrorMessage error={profiles.error} /><Button onClick={profiles.reload} variant="secondary">Recharger les profils</Button></>}
      {stagiaireId && !profiles.loading && !profiles.error && profiles.data?.length === 0 && <p className="notice">Ce stagiaire n’a aucun profil académique. Ajoutez-en un depuis sa fiche avant de créer une participation.</p>}
    </>}
    <div className="entity-form">
      {fields.filter((field) => field.type !== 'hidden' && !(fixedReport && field.name === 'stageParticipantId') && !(kind === 'rapport' && ['debutSemaine', 'finSemaine'].includes(field.name)) && (field.name !== 'utilisateurId' || isAdmin)).map((definition) => {
        const field = Object.fromEntries(Object.entries(definition).filter(([key]) => !['path', 'labelKeys', 'valueType', 'valueKey'].includes(key)));
        const rows = field.name === 'stagiaireAcademiqueId' ? profiles.data : choices.data?.[field.name];
        const options = definition.options ?? (definition.valueKey ? (rows ?? []).map((r) => ({ value: r[definition.valueKey], label: r.libelle })) : field.type === 'select' ? toOptions(rows ?? [], definition.labelKeys) : undefined);
        return <Fragment key={field.name}><Input {...field} options={options} value={values[field.name]}
          disabled={saving || choices.loading || Boolean(choices.error) || (kind === 'rapport' && Boolean(initial.id) && field.name === 'stageParticipantId') || (field.name === 'stagiaireAcademiqueId' && (!stagiaireId || profiles.loading || Boolean(profiles.error)))}
          min={field.name === 'dateFinStage' ? values.dateDebutStage : field.name === 'finSemaine' ? values.debutSemaine : field.min}
          error={error?.fieldErrors?.[field.name]} onChange={(event) => setValues((current) => updateBusinessField(kind, current, field.name, event.target.value))} />
          {kind === 'rapport' && field.name === 'stageParticipantId' && <div className="page-stack">
            <Input label="Semaine du rapport" type="select" name="periodeAttendue" required value={values.debutSemaine}
              disabled={saving || !values.stageParticipantId || periods.loading || Boolean(periods.error)}
              options={availablePeriods.map(p => ({ value: p.debut, label: `${formatDate(p.debut)} au ${formatDate(p.fin)} — à remettre le ${formatDate(p.dateLimite)}` }))}
              error={error?.fieldErrors?.debutSemaine || error?.fieldErrors?.finSemaine}
              onChange={event => {
                const period = availablePeriods.find(p => p.debut === event.target.value);
                setValues(current => ({ ...current, debutSemaine: period?.debut ?? '', finSemaine: period?.fin ?? '' }));
              }} hint={values.stageParticipantId ? 'Seules les semaines sans rapport et celle du rapport en cours de modification sont proposées.' : 'Choisissez d’abord le stage ci-dessus.'} />
            {values.stageParticipantId && periods.loading && <p role="status">Chargement des semaines disponibles…</p>}
            {values.stageParticipantId && !periods.loading && !periods.error && availablePeriods.length === 0 && <p className="notice">Aucune semaine disponible. Consultez les rapports existants pour reprendre un brouillon ou corriger un rapport.</p>}
            <ErrorMessage error={periods.error} />
            {periods.error && <Button variant="secondary" onClick={periods.reload}>Recharger les semaines</Button>}
          </div>}
        </Fragment>;
      })}
    </div>
    {fixedReport && <><ErrorMessage error={periods.error} />{periods.error && <Button variant="secondary" onClick={periods.reload}>Recharger les semaines</Button>}
      {!periods.loading && !periods.error && !availablePeriods.some(period => period.debut === values.debutSemaine && period.fin === values.finSemaine) && <p className="notice">Cette semaine n’est plus disponible. Fermez ce formulaire et actualisez la grille pour consulter le rapport existant.</p>}</>}
    <ErrorMessage error={error} />
    {kind === 'participation' && values.encadreurId && <EncadreurCharge key={values.encadreurId} id={values.encadreurId} />}
    <div className="row-actions"><Button type="submit" disabled={blocked || (kind === 'rapport' && !availablePeriods.length) || (fixedReport && !availablePeriods.some(period => period.debut === values.debutSemaine && period.fin === values.finSemaine))}>{saving ? 'Enregistrement…' : kind === 'rapport' ? 'Enregistrer le brouillon' : 'Enregistrer'}</Button>
      {onCancel && <Button onClick={onCancel} disabled={saving} variant="secondary">Annuler</Button>}</div>
  </form>;
}
