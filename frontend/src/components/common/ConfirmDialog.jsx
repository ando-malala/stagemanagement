import { useState } from 'react';
import Modal from './Modal.jsx';
import Button from './Button.jsx';

export default function ConfirmDialog({ open, title, children, onConfirm, onClose, confirmLabel = 'Confirmer' }) {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const close = () => { if (!saving) { setError(''); onClose(); } };
  async function confirm() {
    setSaving(true);
    setError('');
    try { await onConfirm(); onClose(); }
    catch (failure) { setError(failure.message); }
    finally { setSaving(false); }
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

