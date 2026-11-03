import { useCallback } from 'react';
import { listParticipations } from '../../api/stageApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { requireList } from '../../utils/businessForms.js';
import { currentReportParticipations } from '../../utils/reportWeeks.js';
import { formatDate } from '../../utils/dateUtils.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import RapportWeeks from './RapportWeeks.jsx';

export default function MesRapports({ navigate, onSaved }) {
  const fetcher = useCallback(async options => currentReportParticipations(requireList(await listParticipations(options))), []);
  const { data, loading, error, reload } = useFetch(fetcher);
  const current = data?.length === 1 ? data[0] : null;
  return <div className="page-stack">
    <Card>
      <div className="section-heading"><h2>Mes rapports — stage en cours</h2><Button variant="secondary" onClick={reload} disabled={loading}>Actualiser mon stage</Button></div>
      <ErrorMessage error={error} />
      {loading && <p role="status">Recherche de votre stage en cours…</p>}
      {!loading && !error && data?.length === 0 && <p className="notice">Aucun stage en cours n’est associé à votre compte. Vos rapports déjà enregistrés restent accessibles ci-dessous. Si votre stage a commencé, contactez l’administrateur pour vérifier votre affectation.</p>}
      {!loading && !error && data?.length > 1 && <p className="notice">Plusieurs stages sont indiqués en cours pour votre compte. Contactez l’administrateur pour identifier l’affectation actuelle.</p>}
      {current && <p>Votre stage est sélectionné automatiquement : <strong>{current.stage}</strong>, du {formatDate(current.dateDebutStage)} au {formatDate(current.dateFinStage)}.</p>}
    </Card>
    {current && <RapportWeeks key={current.id} participationId={current.id} stageLabel={current.stage} navigate={navigate} onSaved={onSaved} />}
  </div>;
}
