import { useState } from 'react';
import Button from './Button.jsx';
import ErrorMessage from './ErrorMessage.jsx';

export default function DownloadButton({ download, children }) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  async function run() {
    setBusy(true); setError('');
    try { await download(); } catch (failure) { setError(failure.message || 'Téléchargement impossible.'); }
    finally { setBusy(false); }
  }
  return <span className="download-control"><Button variant="secondary" disabled={busy} onClick={run}>
    {busy ? 'Téléchargement…' : children}</Button>{error && <ErrorMessage error={error} />}</span>;
}
