export function formatDateTime(value) {
  if (!value) return '-';

  return new Intl.DateTimeFormat('fr-FR', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}
