import AppLink from '../../components/common/AppLink.jsx';
import { formatDate, formatDateTime } from '../../utils/dateUtils.js';
import { alerteLinks } from '../../utils/pilotage.js';

export default function AlerteDetail({ alerte, navigate }) {
  return <div className="page-stack">
    <p>{alerte.message}</p>
    <dl className="detail-grid">
      <div><dt>Type</dt><dd>{alerte.typeAlerteLibelle} ({alerte.typeAlerteCode})</dd></div>
      <div><dt>Nature</dt><dd>{alerte.natureAlerte ?? 'Non renseignée'}</dd></div>
      <div><dt>Priorité</dt><dd>{alerte.priorite}</dd></div>
      <div><dt>Statut</dt><dd>{alerte.statut}</dd></div>
      <div><dt>Échéance</dt><dd>{formatDate(alerte.dateEcheance)}</dd></div>
      <div><dt>Détection</dt><dd>{formatDateTime(alerte.dateDetection)}</dd></div>
      <div><dt>Traitement</dt><dd>{formatDateTime(alerte.dateTraitement)}</dd></div>
      <div><dt>Traité par</dt><dd>{alerte.traitePar ?? 'Non renseigné'}</dd></div>
    </dl>
    <p><strong>Action recommandée : </strong>{alerte.actionRecommandee || 'Non renseignée'}</p>
    <nav className="row-actions" aria-label="Dossiers liés à l’alerte">{alerteLinks(alerte).map((link) => <AppLink key={link.path} to={link.path} navigate={navigate}>{link.label}</AppLink>)}</nav>
  </div>;
}
