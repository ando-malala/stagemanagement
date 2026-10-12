/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState } from 'react';
import { listAlertesNonLues } from '../../api/alerteApi.js';
import { getDashboardResume } from '../../api/dashboardApi.js';
import Card from '../../components/common/Card.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import Loader from '../../components/common/Loader.jsx';
import { formatNumber } from '../../utils/formatUtils.js';

const STAT_CARDS = [
  ['totalStagiaires', 'Stagiaires'],
  ['totalStages', 'Stages'],
  ['totalParticipations', 'Participations'],
  ['totalRapports', 'Rapports'],
  ['totalAlertesATraiter', 'Alertes à traiter'],
  ['totalAlertesTraitees', 'Alertes traitées']
];

export default function DashboardPage() {
  const [resume, setResume] = useState(null);
  const [alertes, setAlertes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError('');

    Promise.all([
      getDashboardResume(),
      listAlertesNonLues()
    ])
      .then(([resumeData, alertesData]) => {
        if (ignore) return;
        setResume(resumeData);
        setAlertes(Array.isArray(alertesData) ? alertesData : []);
      })
      .catch((e) => {
        if (!ignore) setError(e.message);
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });

    return () => {
      ignore = true;
    };
  }, []);

  if (loading) return <Loader label="Chargement du tableau de bord..." />;
  if (error) return <p className="alert alert-danger" role="alert">{error}</p>;

  return (
    <div className="page-stack">
      <section className="stat-grid">
        {STAT_CARDS.map(([key, label]) => (
          <Card className="stat-card" key={key}>
            <span>{label}</span>
            <strong>{formatNumber(resume?.[key])}</strong>
          </Card>
        ))}
      </section>

      <Card>
        <div className="section-heading">
          <div>
            <h2>Alertes non lues</h2>
            <p className="small">Alertes encore visibles pour votre rôle.</p>
          </div>
        </div>

        {alertes.length === 0 ? (
          <EmptyState message="Aucune alerte non lue." />
        ) : (
          <div className="alert-list">
            {alertes.slice(0, 6).map((alerte) => (
              <article className="alert-card" key={alerte.id}>
                <strong>{alerte.typeAlerteLibelle ?? alerte.typeAlerteCode ?? 'Alerte'}</strong>
                <p>{alerte.message}</p>
                <span>{alerte.priorite ?? 'Priorité non définie'} · {alerte.statut ?? '-'}</span>
              </article>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
}
