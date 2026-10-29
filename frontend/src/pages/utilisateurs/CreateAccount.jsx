import { useRef, useState } from 'react';
import { createUtilisateur, listRoles } from '../../api/utilisateurApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { requireList } from '../../utils/businessForms.js';
import { accountPayload, activeRoleOptions } from '../../utils/utilisateurForms.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

const fetchRoles = async (options) => requireList(await listRoles(options));
export default function CreateAccount({ onSaved }) {
  const { utilisateur } = useAuth();
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  if (!can(utilisateur, 'gererUtilisateurs')) return null;
  return <section className="page-stack">
    {message && <p role="status" className="alert alert-success">{message}</p>}
    {open ? <AccountForm onCancel={() => setOpen(false)} onSaved={async () => {
      setOpen(false); setMessage('Compte créé.'); await onSaved?.();
    }} /> : <Button onClick={() => { setMessage(''); setOpen(true); }}>Créer le compte</Button>}
  </section>;
}

function AccountForm({ onSaved, onCancel }) {
  const { data, loading, error: loadError, reload } = useFetch(fetchRoles);
  const [values, setValues] = useState({ identifiant: '', motDePasse: '', confirmation: '', roleCode: '' });
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const pending = useRef(false);
  const options = activeRoleOptions(data ?? []);
  const change = (event) => setValues((current) => ({ ...current, [event.target.name]: event.target.value }));
  async function save(event) {
    event.preventDefault();
    if (pending.current || loading || loadError) return;
    setError(null);
    try {
      const payload = accountPayload(values, data);
      pending.current = true; setSaving(true);
      await createUtilisateur(payload);
      setValues({ identifiant: '', motDePasse: '', confirmation: '', roleCode: '' });
      await onSaved();
    } catch (failure) { setError(failure); }
    finally { pending.current = false; setSaving(false); }
  }
  return <form className="page-stack business-form" onSubmit={save}>
    <ErrorMessage error={loadError} />
    {loadError && <Button onClick={reload} variant="secondary">Recharger les rôles</Button>}
    {loading && <p role="status">Chargement des rôles…</p>}
    {!loading && !loadError && !options.length && <p className="notice">Aucun rôle actif disponible.</p>}
    <Input name="identifiant" label="Identifiant" required maxLength={50} autoComplete="off" value={values.identifiant} onChange={change} disabled={saving} />
    <Input name="motDePasse" label="Mot de passe initial" type="password" required minLength={6} autoComplete="new-password" value={values.motDePasse} onChange={change} disabled={saving} hint="Au moins 6 caractères, conformément à la règle de création du serveur." />
    <Input name="confirmation" label="Confirmer le mot de passe" type="password" required autoComplete="new-password" value={values.confirmation} onChange={change} disabled={saving} />
    <Input name="roleCode" label="Rôle initial" type="select" required options={options} value={values.roleCode} onChange={change} disabled={saving || loading || Boolean(loadError)} />
    <ErrorMessage error={error} />
    <div className="row-actions"><Button type="submit" disabled={saving || loading || Boolean(loadError) || !options.length}>{saving ? 'Création…' : 'Créer le compte'}</Button>
      <Button onClick={onCancel} variant="secondary" disabled={saving}>Annuler</Button></div>
  </form>;
}
