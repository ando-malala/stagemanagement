import { useCallback, useState } from 'react';
import { listParticipations } from '../../api/stageApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { requireList, toOptions } from '../../utils/businessForms.js';
import Card from '../../components/common/Card.jsx';
import Input from '../../components/common/Input.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import RapportWeeks from './RapportWeeks.jsx';

export default function RapportsParStagiaire({ navigate }) {
  const fetcher = useCallback(async options => requireList(await listParticipations(options)), []);
  const { data, loading, error, reload } = useFetch(fetcher);
  const [stagiaireId, setStagiaireId] = useState('');
  const [participationId, setParticipationId] = useState('');
  const rows = data ?? [];
  const trainees = [...new Map(rows.map(row => [row.stagiaireId, { id: row.stagiaireId, nom: row.stagiaireNomComplet }])).values()]
    .sort((a, b) => (a.nom ?? '').localeCompare(b.nom ?? '', 'fr'));
  const participations = rows.filter(row => String(row.stagiaireId) === stagiaireId);
  const current = participations.filter(row => row.statut === 'EN_COURS');
  const selected = participationId ? participations.find(row => String(row.id) === participationId)
    : participations.length === 1 ? participations[0] : current.length === 1 ? current[0] : null;

  return <div className="page-stack">
    <Card>
      <div className="section-heading"><h2>Rapports par stagiaire</h2><Button variant="secondary" disabled={loading} onClick={reload}>Actualiser</Button></div>
      <p>Sélectionnez un stagiaire pour consulter ses semaines de rapport. Son encadreur peut valider les rapports soumis ou demander une correction.</p>
      <ErrorMessage error={error} />
      {loading && <p role="status">Chargement des stagiaires…</p>}
      <Input type="select" label="Stagiaire" value={stagiaireId} disabled={loading || Boolean(error)}
        options={toOptions(trainees, ['nom'])} onChange={event => { setStagiaireId(event.target.value); setParticipationId(''); }} />
      {participations.length > 1 && <Input type="select" label="Stage du stagiaire" value={selected?.id ?? ''}
        options={toOptions(participations, ['stage', 'dateDebutStage', 'dateFinStage', 'statut'])}
        onChange={event => setParticipationId(event.target.value)} />}
      {!loading && !error && rows.length === 0 && <p className="notice">Aucune participation accessible.</p>}
      {!loading && !error && rows.length > 0 && !selected && <p className="notice">{stagiaireId ? 'Choisissez le stage à consulter.' : 'Choisissez un stagiaire pour afficher sa grille.'}</p>}
    </Card>
    {selected && <RapportWeeks key={selected.id} participationId={selected.id} stageLabel={`${selected.stagiaireNomComplet} — ${selected.stage}`} navigate={navigate} />}
  </div>;
}
