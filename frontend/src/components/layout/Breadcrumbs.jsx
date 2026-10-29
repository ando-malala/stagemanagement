import AppLink from '../common/AppLink.jsx';
import { breadcrumbItems } from '../../routes/routeConfig.js';

export default function Breadcrumbs({ route, navigate }) {
  return <nav aria-label="Fil d’Ariane" className="breadcrumbs"><ol>
    {breadcrumbItems(route).map((item, index) => <li key={index}>
      {item.path ? <AppLink to={item.path} navigate={navigate}>{item.title}</AppLink> : <span aria-current="page">{item.title}</span>}
    </li>)}
  </ol></nav>;
}
