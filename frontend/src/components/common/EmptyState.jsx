export default function EmptyState({ message = 'Aucune donnée disponible.' }) {
  return <p className="empty-state">{message}</p>;
}
