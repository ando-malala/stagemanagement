import { useAuth } from '../../hooks/useAuth.js';
import { isTraineeOnly } from '../../utils/permissions.js';
import { useCallback, useState } from 'react';
import { useFetch } from '../../hooks/useFetch.js';
import { formatDateTime } from '../../utils/dateUtils.js';
import Card from './Card.jsx';
import Button from './Button.jsx';
import AppLink from './AppLink.jsx';
import ErrorMessage from './ErrorMessage.jsx';
import Loader from './Loader.jsx';
import StatusBadge from './StatusBadge.jsx';
import NavigationIcon from '../layout/NavigationIcon.jsx';

const detailIcons = { '/stagiaires': 'ST', '/stages': 'SP', '/encadreurs': 'EN', '/rapports': 'RA', '/evaluations': 'EV', '/soutenances': 'SO', '/utilisateurs': 'UT' };

export default function ResourceDetail({ id, title, fetcher, fields, links = [], navigate, backTo, children }) {
  const { utilisateur } = useAuth();
  const personalBack = isTraineeOnly(utilisateur) && !['/rapports', '/fichiers'].includes(backTo) ? '/mon-dossier' : backTo;
  const load = useCallback((options) => fetcher(id, options), [fetcher, id]);
  const { data, loading, error, reload } = useFetch(load);
  const [message, setMessage] = useState('');
  const saved = async () => {
    setMessage('Enregistrement effectué.');
    await reload();
  };
  return <Card>
    <div className="section-heading dossier-heading"><div className="dossier-identity"><span className="icon-tile"><NavigationIcon name={detailIcons[backTo] ?? 'DO'} /></span><div><span className="eyebrow">Fiche de suivi</span><h2>{title} n° {id}</h2></div></div><Button variant="secondary" onClick={reload} disabled={loading}>Actualiser</Button></div>
    {message && <p className="alert alert-success" role="status">{message}</p>}
    {loading ? <Loader label="Chargement du dossier…" /> : error ? <ErrorMessage error={error} /> : !data || Array.isArray(data) || typeof data !== 'object' ? <ErrorMessage error="Le serveur n’a pas renvoyé le dossier attendu." /> : <>
      <dl className="detail-grid">{fields.map(([key, label, type]) => <div key={key}><dt>{label}</dt><dd>{key === 'statut' && data[key] ? <StatusBadge value={data[key]} /> : format(data[key], type)}</dd></div>)}</dl>
      <nav className="row-actions" aria-label="Dossiers liés">{links.filter((link) => data[link.key] != null).map((link) => <AppLink className="btn btn-secondary" key={link.key} to={`${link.path}/${data[link.key]}`} navigate={navigate}>{link.label}</AppLink>)}</nav>
      {children?.(data, saved)}
    </>}
    <AppLink className="back-link" to={personalBack} navigate={navigate}>Retour à la liste</AppLink>
  </Card>;
}

function format(value, type) {
  if (value == null || value === '') return 'Non renseigné';
  if (type === 'date') return formatDateTime(value);
  if (typeof value === 'boolean') return value ? 'Oui' : 'Non';
  return String(value);
}
