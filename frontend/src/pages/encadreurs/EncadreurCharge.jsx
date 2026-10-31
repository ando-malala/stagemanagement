import { useCallback } from 'react';
import { getChargeEncadreur } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

export default function EncadreurCharge({ id }) {
  const fetcher = useCallback(async (options) => {
    const result = await getChargeEncadreur(id, options);
    if (!Number.isInteger(result?.capaciteMax) || result.capaciteMax < 1 || !Number.isSafeInteger(result.participationsActives) || result.participationsActives < 0) throw new Error('Charge indisponible ou réponse invalide.');
    return result;
  }, [id]);
  const { data, error, loading, reload } = useFetch(fetcher);
  return <Card><div className="section-heading"><h3>Charge d’encadrement</h3><Button variant="secondary" disabled={loading} onClick={reload}>Actualiser</Button></div>
    {loading && <p role="status">Chargement…</p>}<ErrorMessage error={error} />
    {!loading && !error && data && <><p>{data.participationsActives} participation(s) active(s) pour une capacité de {data.capaciteMax}.</p>
      <p>{data.surcharge ? 'Capacité dépassée.' : 'Capacité respectée.'}</p><p>Les participations en cours et à clôturer sont comptées par le serveur.</p></>}
  </Card>;
}
