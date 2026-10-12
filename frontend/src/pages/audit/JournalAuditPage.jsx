/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState } from 'react';
import { listJournalAudit } from '../../api/auditApi.js';
import Card from '../../components/common/Card.jsx';
import Table from '../../components/common/Table.jsx';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'utilisateur', label: 'Utilisateur' },
  { key: 'action', label: 'Action' },
  { key: 'entite', label: 'Entité' },
  { key: 'entiteId', label: 'ID entité' },
  { key: 'dateAction', label: 'Date' }
];

export default function JournalAuditPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError('');

    listJournalAudit()
      .then((result) => {
        if (!ignore) setData(Array.isArray(result) ? result : []);
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

  return (
    <Card>
      <div className="section-heading">
        <div>
          <h2>Journal d’audit</h2>
          <p className="small">Historique des actions sensibles de l’application.</p>
        </div>
      </div>

      {error && <p className="alert alert-danger" role="alert">{error}</p>}
      <Table columns={columns} data={data} loading={loading} />
    </Card>
  );
}
