import { isTraineeOnly } from '../../utils/permissions.js';
import AppLink from '../common/AppLink.jsx';
import { breadcrumbItems } from '../../routes/routeConfig.js';

export default function Breadcrumbs({ route, navigate, utilisateur }) {
  const items = isTraineeOnly(utilisateur) ? [{ path: '/mon-dossier', title: 'Mon dossier' }, { title: route?.title ?? 'Dossier' }] : breadcrumbItems(route);
  return <nav aria-label="Fil d’Ariane" className="breadcrumbs"><ol>
    {items.map((item, index) => <li key={index}>
      {item.path ? <AppLink to={item.path} navigate={navigate}>{item.title}</AppLink> : <span aria-current="page">{item.title}</span>}
    </li>)}
  </ol></nav>;
}
