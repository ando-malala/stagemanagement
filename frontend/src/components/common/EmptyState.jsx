import NavigationIcon from '../layout/NavigationIcon.jsx';

export default function EmptyState({ message = 'Aucune donnée disponible.' }) {
  return <div className="empty-state" role="status"><span className="icon-tile"><NavigationIcon name="DO" /></span><p>{message}</p></div>;
}
