import { useCallback, useRef, useState } from 'react';
import { loadBusinessChoices } from '../../api/businessChoices.js';
import { listAcademiques } from '../../api/stagiaireApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { useFetch } from '../../hooks/useFetch.js';
import { hasAnyRole } from '../../utils/roleUtils.js';
import { businessFields, businessPayload, requireList, toOptions } from '../../utils/businessForms.js';
import Input from './Input.jsx';
import Button from './Button.jsx';
import ErrorMessage from './ErrorMessage.jsx';

export default function BusinessForm({ kind, initial = {}, onSave, onSaved, onCancel }) {
  const { utilisateur } = useAuth();
  const isAdmin = hasAnyRole(utilisateur, ['ADMIN']);
  const fields = businessFields[kind];
  const [values, setValues] = useState(() => Object.fromEntries(fields.map(({ name }) => [name, initial[name] ?? ''])));
  const [stagiaireId, setStagiaireId] = useState(initial.stagiaireId ?? '');
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const pending = useRef(false);
  const loadChoices = useCallback((options) => loadBusinessChoices(kind, isAdmin, options), [kind, isAdmin]);
  const choices = useFetch(loadChoices);
  const loadProfiles = useCallback(async (options) => kind === 'participation' && stagiaireId
    ? requireList(await listAcademiques(stagiaireId, options)) : [], [kind, stagiaireId]);
  const profiles = useFetch(loadProfiles);
  const blocked = saving || choices.loading || Boolean(choices.error) || (kind === 'participation' && (profiles.loading || Boolean(profiles.error)));

  async function submit(event) {
    event.preventDefault();
    if (pending.current || blocked) return;
    setError(null);
    try {
      const payload = businessPayload(kind, values);
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
      {fields.filter((field) => field.name !== 'utilisateurId' || isAdmin).map((definition) => {
        const field = Object.fromEntries(Object.entries(definition).filter(([key]) => !['path', 'labelKeys', 'valueType'].includes(key)));
        const rows = field.name === 'stagiaireAcademiqueId' ? profiles.data : choices.data?.[field.name];
        const options = field.type === 'select' ? toOptions(rows ?? [], definition.labelKeys) : undefined;
        return <Input key={field.name} {...field} options={options} value={values[field.name]} disabled={blocked}
          min={field.name === 'dateFinStage' ? values.dateDebutStage : field.min}
          error={error?.fieldErrors?.[field.name]} onChange={(event) => setValues((current) => ({ ...current, [field.name]: event.target.value }))} />;
      })}
    </div>
    <ErrorMessage error={error} />
    <div className="row-actions"><Button type="submit" disabled={blocked}>{saving ? 'Enregistrement…' : 'Enregistrer'}</Button>
      {onCancel && <Button onClick={onCancel} disabled={saving} variant="secondary">Annuler</Button>}</div>
  </form>;
}
