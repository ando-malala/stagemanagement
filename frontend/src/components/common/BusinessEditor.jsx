import { useState } from 'react';
import { useAuth } from '../../hooks/useAuth.js';
import { canEditBusiness } from '../../utils/businessForms.js';
import BusinessForm from './BusinessForm.jsx';
import Button from './Button.jsx';

export default function BusinessEditor({ kind, initial, save, onSaved, label }) {
  const { utilisateur } = useAuth();
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  if (!canEditBusiness(utilisateur, kind, initial)) return null;
  return <section className="page-stack">
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {open ? <BusinessForm kind={kind} initial={initial} onSave={save} onCancel={() => setOpen(false)} onSaved={async () => {
      setOpen(false);
      setMessage('Enregistrement effectué.');
      await onSaved?.();
    }} /> : <Button variant="secondary" onClick={() => { setMessage(''); setOpen(true); }}>{label ?? (initial ? 'Modifier le dossier' : 'Ajouter')}</Button>}
  </section>;
}
