import { useCallback, useState } from 'react';
import { listStagiaires } from '../../api/stagiaireApi.js';
import { listParticipations } from '../../api/stageApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { requireList, toOptions } from '../../utils/businessForms.js';
import Card from '../../components/common/Card.jsx';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import DocumentsPanel from './DocumentsPanel.jsx';

export default function FichiersPage() {
  const [scope, setScope] = useState('stagiaire');
  return <div className="page-stack">
    <Card><h2>Documents</h2><Input label="Dossier concerné" name="scope" type="select" value={scope} required
      options={[{ value: 'stagiaire', label: 'Stagiaire' }, { value: 'participation', label: 'Participation' }]}
      onChange={(event) => setScope(event.target.value || 'stagiaire')} /></Card>
    <OwnerDocuments key={scope} scope={scope} />
  </div>;
}

function OwnerDocuments({ scope }) {
  const [ownerId, setOwnerId] = useState('');
  const fetcher = useCallback(async (options) => requireList(await (scope === 'stagiaire' ? listStagiaires(options) : listParticipations(options))), [scope]);
  const { data, loading, error, reload } = useFetch(fetcher);
  const choices = toOptions(data ?? [], scope === 'stagiaire' ? ['nom', 'prenoms'] : ['stagiaireNomComplet', 'stage', 'dateDebutStage', 'dateFinStage']);
  const selected = choices.find((option) => String(option.value) === ownerId);
  return <>
    <Card>
      {loading && <p role="status">Chargement des dossiers…</p>}
      <ErrorMessage error={error} />
      {error && <Button onClick={reload} variant="secondary">Réessayer</Button>}
      <Input label={scope === 'stagiaire' ? 'Stagiaire' : 'Participation'} name="ownerId" type="select" value={ownerId} options={choices}
        onChange={(event) => setOwnerId(event.target.value)} disabled={loading || Boolean(error)} />
      {!loading && !error && choices.length === 0 && <p>Aucun dossier disponible.</p>}
    </Card>
    {selected && <DocumentsPanel key={`${scope}/${ownerId}`} scope={scope} ownerId={ownerId} ownerLabel={selected.label} />}
  </>;
}
