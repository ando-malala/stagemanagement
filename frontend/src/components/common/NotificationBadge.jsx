import { notificationCount } from '../../utils/alertOverview.js';

export default function NotificationBadge({ count }) {
  if (!Number.isSafeInteger(count) || count <= 0) return null;
  return <span className="notification-badge" role="img" aria-label={`${count} alerte${count > 1 ? 's' : ''} à traiter`}>
    {notificationCount(count)}
  </span>;
}
