import { useState } from 'react';
import { resetUtilisateurPassword } from '../../api/utilisateurApi.js';
import { resetPasswordPayload } from '../../utils/utilisateurForms.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';

export default function ResetPassword({ account }) {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  return <section className="page-stack">
    <h3>Mot de passe</h3>
    {message && <p role="status" className="alert alert-success">{message}</p>}
    {open ? <PasswordForm account={account} onCancel={() => setOpen(false)} onSaved={() => {
      setOpen(false); setMessage('Mot de passe réinitialisé.');
    }} /> : <Button variant="secondary" onClick={() => { setOpen(true); setMessage(''); }}>Réinitialiser le mot de passe</Button>}
  </section>;
}

function PasswordForm({ account, onCancel, onSaved }) {
  const [password, setPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');
  const [error, setError] = useState(null);
  const [confirming, setConfirming] = useState(false);
  return <>
    <form className="page-stack" onSubmit={(event) => {
      event.preventDefault(); setError(null);
      try { resetPasswordPayload(password, confirmation); setConfirming(true); }
      catch (failure) { setError(failure); }
    }}>
      <Input label="Nouveau mot de passe" name="nouveauMotDePasse" type="password" minLength={12} required autoComplete="new-password" value={password} onChange={(event) => setPassword(event.target.value)} />
      <Input label="Confirmer le mot de passe" name="confirmation" type="password" required autoComplete="new-password" value={confirmation} onChange={(event) => setConfirmation(event.target.value)} />
      <ErrorMessage error={error} />
      <div className="row-actions"><Button type="submit">Réinitialiser</Button><Button variant="secondary" onClick={onCancel}>Annuler</Button></div>
    </form>
    <ConfirmDialog open={confirming} title="Réinitialiser le mot de passe" onClose={() => setConfirming(false)} onConfirm={async () => {
      await resetUtilisateurPassword(account.id, resetPasswordPayload(password, confirmation));
      setPassword(''); setConfirmation(''); setConfirming(false); onSaved();
    }}><p>Remplacer le mot de passe de {account.identifiant} (n° {account.id}) ?</p></ConfirmDialog>
  </>;
}
