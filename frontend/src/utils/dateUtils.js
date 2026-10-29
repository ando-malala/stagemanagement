// LocalDate and LocalDateTime contain no offset: preserve their wall date/time.
// Explicit instants are displayed in Madagascar, independently of the browser zone.
function parseDate(value) {
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}(?:$|T)/.test(value)) return null;
  const hasOffset = /(?:Z|[+-]\d{2}:?\d{2})$/i.test(value);
  const date = new Date(hasOffset ? value : `${value.length === 10 ? value + 'T00:00:00' : value}Z`);
  if (Number.isNaN(date.getTime())) return null;
  return { date, timeZone: hasOffset ? 'Indian/Antananarivo' : 'UTC' };
}
export function formatDate(value) {
  const parsed = parseDate(value);
  return parsed ? new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium', timeZone: parsed.timeZone }).format(parsed.date) : '—';
}
export function formatDateTime(value) {
  const parsed = parseDate(value);
  if (!parsed) return '—';
  return new Intl.DateTimeFormat('fr-FR', {
    dateStyle: 'medium', ...(value.length > 10 ? { timeStyle: 'short' } : {}), timeZone: parsed.timeZone
  }).format(parsed.date);
}
