import { useCallback, useState } from 'react';
import { listStatuts } from '../../api/suiviApi.js';
import { changeEncadreurStatus } from '../../api/encadreurApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

export default function EncadreurStatut({ encadreur, onSaved }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback((options) => listStatuts('ENCADREUR', options), []);
  const { data, loading, error, reload } = useFetch(fetcher);
  const [code, setCode] = useState('');
  const [motif, setMotif] = useState('');
  const [confirm, setConfirm] = useState(false);
  if (!can(utilisateur, 'gererEncadreurs')) return null;
  const selected = data?.find((r) => r.code === code && r.code !== encadreur.statut);
  return <section className="page-stack"><h3>Statut de l’encadreur</h3><ErrorMessage error={error} />
    {error && <Button onClick={reload}>Recharger les statuts</Button>}
    <Input label="Nouveau statut" name="statutCode" type="select" value={code} disabled={loading || Boolean(error)}
      options={(data ?? []).filter((s) => s.code !== encadreur.statut).map((s) => ({ value: s.code, label: s.libelle }))} onChange={(event) => setCode(event.target.value)} />
    <Input name="motif" label="Motif" value={motif} onChange={(event) => setMotif(event.target.value)} />
    <Button disabled={!selected} onClick={() => setConfirm(true)}>Changer le statut</Button>
    <ConfirmDialog open={confirm} title="Changer le statut" onClose={() => setConfirm(false)} onConfirm={async () => {
      await changeEncadreurStatus(encadreur.id, { statutCode: selected.code, motif: motif.trim() || null });
      setConfirm(false); setCode(''); setMotif(''); await onSaved();
    }}><p>Confirmer le statut « {selected?.libelle} » pour {encadreur.nom} {encadreur.prenoms} ?</p></ConfirmDialog>
  </section>;
}
