import { useCallback, useEffect, useRef, useState } from 'react';
import { loadReportWeeks } from '../../api/reportWeeksApi.js';
import { createRapport, updateRapport } from '../../api/rapportApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { useFetch } from '../../hooks/useFetch.js';
import { canActOnReport } from '../../utils/permissions.js';
import { formatDate } from '../../utils/dateUtils.js';
import { reportWeekAction, reportWeekState, reportWritingAccess } from '../../utils/reportWeeks.js';
import Card from '../../components/common/Card.jsx';
import Table from '../../components/common/Table.jsx';
import Button from '../../components/common/Button.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import BusinessForm from '../../components/common/BusinessForm.jsx';
import RapportActions from './RapportActions.jsx';
import RapportObservations from './RapportObservations.jsx';

const columns = [
  { key: 'semaine', label: 'Semaine' },
  { key: 'debut', label: 'Du', render: row => formatDate(row.debut) },
  { key: 'fin', label: 'Au', render: row => formatDate(row.fin) },
  { key: 'dateLimite', label: 'À remettre le', render: row => formatDate(row.dateLimite) },
  { key: 'etat', label: 'État', filterable: true },
  { key: 'manquante', label: 'Suivi', render: row => !row.exigible ? 'Non exigible' : row.manquante ? 'Rapport manquant' : row.soumise ? 'Remis' : 'Échéance à venir' }
];

export function RapportWeeksTable({ periods, canWrite, loading, error, onSelect, onAction, busy = false, navigate }) {
  const { utilisateur } = useAuth();
  const rows = periods.map((period, index) => ({ ...period, id: period.debut, semaine: index + 1, etat: reportWeekState(period) }));
  return <Table caption="Semaines de rapport" columns={columns} data={rows} loading={loading} error={error}
    renderActions={row => {
      const label = reportWeekAction(row, canWrite);
      if (row.rapportId) return <div className="row-actions">
        {onAction ? <Button disabled={busy} variant="secondary" onClick={() => onAction(row, canWrite && ['BROUILLON', 'A_CORRIGER'].includes(row.statutRapport) ? 'EDIT' : null)}>{label}</Button>
          : <AppLink to={`/rapports/${row.rapportId}`} navigate={navigate}>{label}</AppLink>}
        {onAction && canWrite && canActOnReport(utilisateur, row.rapport, 'SOUMIS') && <Button disabled={busy} onClick={() => onAction(row, 'SOUMIS')}>Soumettre</Button>}
        {onAction && canActOnReport(utilisateur, row.rapport, 'VALIDE') && <>
          <Button disabled={busy} onClick={() => onAction(row, 'VALIDE')}>Valider</Button>
          <Button disabled={busy} variant="secondary" onClick={() => onAction(row, 'A_CORRIGER')}>Demander une correction</Button>
        </>}
      </div>;
      return label ? <Button disabled={busy || !onSelect} onClick={() => onSelect(row)} aria-label={`Rédiger le rapport de la semaine ${row.semaine}`}>{label}</Button>
        : <span className="small">Aucun rapport déposé</span>;
    }} />;
}

export default function RapportWeeks({ participationId, stageLabel, navigate, onSaved }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback(options => loadReportWeeks(participationId, options), [participationId]);
  const { data, error, loading, reload } = useFetch(fetcher);
  const periods = data?.periods ?? [];
  const access = reportWritingAccess(utilisateur, data?.participation);
  const canWrite = access.allowed;
  const [selected, setSelected] = useState(null);
  const [message, setMessage] = useState('');
  const report = selected?.rapportId ? periods.find(period => period.rapportId === selected.rapportId)?.rapport : null;
  const editing = selected?.action === 'EDIT' && canWrite && canActOnReport(utilisateur, report, 'SOUMIS');
  const editor = useRef(null);
  useEffect(() => { if (selected) editor.current?.focus(); }, [selected]);

  async function refresh() {
    await reload();
    await onSaved?.();
  }

  return <Card>
    <div className="section-heading"><h3>Semaines de rapport</h3><Button variant="secondary" disabled={loading || Boolean(selected)} onClick={reload}>Actualiser</Button></div>
    {stageLabel && <p><strong>Stage : {stageLabel}</strong></p>}
    {!loading && !error && !canWrite && <p className="notice">{access.reason}</p>}
    <p className="small">{canWrite ? 'Choisissez une semaine pour rédiger votre rapport. Enregistrez votre brouillon, puis utilisez Soumettre dans cette grille pour le transmettre à votre encadreur.' : 'Consultez les rapports par semaine. L’encadreur affecté peut valider un rapport soumis ou demander une correction.'}</p>
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {selected && <section className="page-stack" ref={editor} tabIndex={-1} aria-label="Rapport de la semaine sélectionnée">
      <h4>Rapport de la semaine {selected.semaine}</h4>
      {canWrite && (!selected.rapportId || editing) && <BusinessForm key={`${selected.debut}/${selected.rapportId ?? 'new'}`} kind="rapport" reportContext={{ stageLabel }}
        initial={report ?? { stageParticipantId: participationId, debutSemaine: selected.debut, finSemaine: selected.fin }}
        onSave={selected.rapportId ? payload => updateRapport(selected.rapportId, payload) : createRapport}
        onCancel={() => setSelected(null)} onSaved={async saved => {
          setSelected({ ...selected, rapportId: saved.id, action: null });
          setMessage('Brouillon enregistré. Vous pouvez le relire et le soumettre ici.');
          await refresh();
        }} />
      }
      {report && !editing && <>
        <p>Du {formatDate(report.debutSemaine)} au {formatDate(report.finSemaine)}</p>
        <dl>{[['activite', 'Activités'], ['difficultes', 'Difficultés'], ['solutions', 'Solutions']].map(([field, label]) =>
          <div key={field}><dt><strong>{label}</strong></dt><dd style={{ whiteSpace: 'pre-wrap' }}>{report[field] || 'Non renseigné'}</dd></div>)}</dl>
        <RapportActions key={`${report.id}/${report.statut}/${selected.action}`} rapport={report} initialAction={selected.action} onSaved={async () => {
          setSelected(null);
          setMessage('Statut du rapport mis à jour.');
          await refresh();
        }} />
        <RapportObservations rapportId={report.id} peutExaminer={report.peutExaminer} />
        <Button variant="secondary" onClick={() => setSelected(null)}>Fermer le rapport</Button>
      </>}
      {selected.rapportId && loading && <p role="status">Chargement du rapport…</p>}
      {selected.rapportId && !loading && !report && <><p className="notice">Le rapport n’a pas pu être chargé. Actualisez la grille pour réessayer.</p><Button variant="secondary" onClick={() => setSelected(null)}>Fermer</Button></>}
    </section>}
    <ErrorMessage error={error} />
    {!loading && !error && periods.length === 0 ? <p className="notice">Aucune semaine de rapport disponible pour ce stage.</p>
      : <RapportWeeksTable periods={periods} loading={loading} error={error} canWrite={canWrite} navigate={navigate} busy={Boolean(selected)}
        onSelect={row => { setMessage(''); setSelected(row); }}
        onAction={(row, action) => { setMessage(''); setSelected({ ...row, action }); }} />}
  </Card>;
}
