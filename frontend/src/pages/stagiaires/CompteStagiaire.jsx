import { useCallback, useState } from 'react';
import { apiRequest } from '../../api/apiClient.js';
import { listUtilisateurs } from '../../api/utilisateurApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';

export default function CompteStagiaire({ id }) {
  const fetcher = useCallback((options) => apiRequest(`/api/stagiaires/${id}/compte`, options), [id]);
  const account = useFetch(fetcher);
  const users = useFetch(listUtilisateurs);
  const [selected, setSelected] = useState('');
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState('');
  return <section className="page-stack"><h3>Compte du stagiaire</h3>
    <ErrorMessage error={account.error || users.error} />
    <p>Compte associé : {account.data?.identifiant || 'Aucun'}. Seul un compte stagiaire actif peut être associé.</p>
    {message && <p role="status">{message}</p>}
    <Input label="Compte à associer (vide pour dissocier)" type="select" value={selected}
      options={(users.data ?? []).filter(u => u.statut === 'ACTIF').map(u => ({ value: u.id, label: u.identifiant }))}
      onChange={event => setSelected(event.target.value)} />
    <Button disabled={account.loading || users.loading || Boolean(account.error || users.error)} onClick={() => setPending(true)}>Modifier l’association</Button>
    <ConfirmDialog open={pending} title="Association du compte" onClose={() => setPending(false)} onConfirm={async () => {
      await apiRequest(`/api/stagiaires/${id}/compte`, { method: 'PATCH', body: JSON.stringify({ utilisateurId: selected ? Number(selected) : null }) });
      setPending(false); setMessage('Association enregistrée.'); await account.reload();
    }}>L’accès personnel au dossier sera accordé au compte sélectionné. L’ancien compte perdra cet accès.</ConfirmDialog>
  </section>;
}
