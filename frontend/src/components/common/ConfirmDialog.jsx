import { useState } from 'react';
import Modal from './Modal.jsx';
import { useRef } from 'react';
import Button from './Button.jsx';

export default function ConfirmDialog({ open, title, children, onConfirm, onClose, confirmLabel = 'Confirmer' }) {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const pending = useRef(false);
  const close = () => { if (!pending.current) { setError(''); onClose(); } };
  async function confirm() {
    if (pending.current) return;
    pending.current = true;
    setSaving(true);
    setError('');
    try { await onConfirm(); onClose(); }
    catch (failure) { setError(failure.message); }
    finally { pending.current = false; setSaving(false); }
  }
  return (
    <Modal open={open} title={title} onClose={close}>
      {children}
      {error && <p className="alert alert-danger" role="alert">{error}</p>}
      <div className="row-actions">
        <Button variant="secondary" onClick={close} disabled={saving}>Annuler</Button>
        <Button variant="danger" onClick={confirm} disabled={saving}>{saving ? 'Enregistrement…' : confirmLabel}</Button>
      </div>
    </Modal>
  );
}

