import { useCallback, useState } from 'react';
import { useFetch } from '../../hooks/useFetch.js';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import { requireList } from '../../utils/businessForms.js';
import { associerCompteEncadreur, getCompteEncadreur, listComptesEligiblesEncadreur } from '../../api/encadreurApi.js';

export default function CompteEncadreur({ id }) {
  const fetcher = useCallback((options) => getCompteEncadreur(id, options), [id]);
  const loadEligible = useCallback(async (options) => requireList(await listComptesEligiblesEncadreur(id, options)), [id]);
  const account = useFetch(fetcher);
  const users = useFetch(loadEligible);
  const [selection, setSelection] = useState(null);
  const selected = selection ?? String(account.data?.utilisateurId ?? '');
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState('');
  const currentMissing = account.data?.utilisateurId && !users.data?.some((u) => u.utilisateurId === account.data.utilisateurId);
  const unchanged = selected === String(account.data?.utilisateurId ?? '');
  const blocked = account.loading || users.loading || Boolean(account.error || users.error);
  return <section className="page-stack"><h3>Compte de l’encadreur</h3>
    <ErrorMessage error={account.error || users.error} />
    <p>Compte associé : {account.loading ? 'Chargement…' : account.data?.identifiant || 'Aucun'}. Seul un compte encadreur actif et disponible peut être associé.</p>
    {message && <p role="status">{message}</p>}
    {!blocked && users.data?.length === 0 && <p className="notice">Aucun compte encadreur actif disponible. Créez ou rendez éligible un compte depuis la gestion des utilisateurs.</p>}
    {currentMissing && !blocked && <p className="notice">Le compte actuel n’est plus éligible à une nouvelle association. Son lien historique est conservé tant que vous ne le remplacez pas ou ne le dissociez pas.</p>}
    <Input label="Compte à associer (vide pour dissocier)" type="select" value={selected} disabled={blocked || pending} emptyOptionLabel="Aucun compte — dissocier"
      options={[...(users.data ?? []).map((u) => ({ value: u.utilisateurId, label: u.identifiant })),
        ...(currentMissing ? [{ value: account.data.utilisateurId, label: `${account.data.identifiant} — compte actuel non éligible`, disabled: true }] : [])]}
      onChange={(event) => setSelection(event.target.value)} />
    <div className="row-actions"><Button disabled={blocked || unchanged} onClick={() => setPending(true)}>Modifier l’association</Button>
      <Button variant="secondary" disabled={account.loading || users.loading || pending} onClick={() => { void account.reload(); void users.reload(); }}>Actualiser les comptes</Button></div>
    <ConfirmDialog open={pending} title="Association du compte" onClose={() => setPending(false)} onConfirm={async () => {
      await associerCompteEncadreur(id, selected ? Number(selected) : null);
      setPending(false); setMessage('Association enregistrée.'); setSelection(null); await Promise.all([account.reload(), users.reload()]);
    }}>L’accès personnel aux ressources encadreur sera accordé au compte sélectionné. L’ancien compte perdra cet accès.</ConfirmDialog>
  </section>;
}
