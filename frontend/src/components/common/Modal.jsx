import { useEffect, useId, useRef } from 'react';
import Button from './Button.jsx';

export default function Modal({ open, title, children, onClose }) {
  const dialog = useRef(null);
  const titleId = useId();
  useEffect(() => {
    const element = dialog.current;
    if (!open) return;
    const previousFocus = document.activeElement;
    element.showModal();
    return () => {
      element.close();
      previousFocus?.focus();
    };
  }, [open]);
  return (
    <dialog ref={dialog} className="modal" aria-labelledby={titleId} onCancel={(event) => { event.preventDefault(); onClose(); }}>
      <header className="modal-header">
        <h2 id={titleId}>{title}</h2>
        <Button variant="ghost" onClick={onClose}>Fermer</Button>
      </header>
      {open && children}
    </dialog>
  );
}
