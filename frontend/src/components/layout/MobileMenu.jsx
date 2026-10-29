import { useState } from 'react';
import Button from '../common/Button.jsx';
import Modal from '../common/Modal.jsx';
import Sidebar from './Sidebar.jsx';

export default function MobileMenu({ currentPath, navigate }) {
  const [open, setOpen] = useState(false);
  return <>
    <Button className="mobile-menu-button" variant="secondary" aria-haspopup="dialog" aria-expanded={open} onClick={() => setOpen(true)}>Menu</Button>
    <Modal open={open} title="Navigation" onClose={() => setOpen(false)}>
      <Sidebar currentPath={currentPath} navigate={(to) => { setOpen(false); navigate(to); }} />
    </Modal>
  </>;
}
