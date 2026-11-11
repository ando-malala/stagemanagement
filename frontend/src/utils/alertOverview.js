import { filterAlertes } from './pilotage.js';

export const alertMetrics = [
  { key: 'pending', label: 'À traiter', hint: 'Statut · toutes natures', icon: 'AL', tone: 'neutral', filters: { statut: 'A_TRAITER' } },
  { key: 'urgent', label: 'Urgentes', hint: 'Priorité urgente', icon: 'warning', tone: 'danger', filters: { statut: 'A_TRAITER', priorite: 'URGENT' } },
  { key: 'warning', label: 'Avertissements', hint: 'Priorité avertissement', icon: 'warning', tone: 'warning', filters: { statut: 'A_TRAITER', priorite: 'AVERTISSEMENT' } },
  { key: 'information', label: 'Informatives', hint: 'Sans traitement manuel', icon: 'info', tone: 'info', filters: { statut: 'A_TRAITER', natureAlerte: 'INFORMATION' } }
];

const labels = {
  statut: { A_TRAITER: 'À traiter', TRAITEE: 'Traitée', IGNOREE: 'Ignorée', EXPIREE: 'Expirée' },
  priorite: { URGENT: 'Urgente', AVERTISSEMENT: 'Avertissement', INFORMATION: 'Information' },
  natureAlerte: { A_TRAITER: 'Action requise', INFORMATION: 'Informative' }
};
export function alertLabel(field, value) {
  const options = Object.hasOwn(labels, field) ? labels[field] : {};
  return Object.hasOwn(options, value) ? options[value] : value || 'Non renseigné';
}

export function sameAlertFilters(current, target) {
  const keys = new Set([...Object.keys(current), ...Object.keys(target)]);
  return [...keys].every(key => (current[key] || '') === (target[key] || ''));
}

export function summarizeAlerts(rows) {
  const counts = Object.fromEntries(alertMetrics.map(metric => [metric.key, filterAlertes(rows, metric.filters).length]));
  const types = new Map();
  for (const row of rows) {
    if (!row.typeAlerteCode) continue;
    if (!types.has(row.typeAlerteCode)) types.set(row.typeAlerteCode, {
      code: row.typeAlerteCode, label: row.typeAlerteLibelle || row.typeAlerteCode, count: 0
    });
    if (row.statut === 'A_TRAITER') types.get(row.typeAlerteCode).count += 1;
  }
  return { counts, types: [...types.values()].sort((a, b) => b.count - a.count || a.label.localeCompare(b.label, 'fr')) };
}

const priorities = { URGENT: 0, AVERTISSEMENT: 1, INFORMATION: 2 };
const priorityRank = value => Object.hasOwn(priorities, value) ? priorities[value] : 3;
function deadline(value) {
  const time = value ? Date.parse(value) : NaN;
  return Number.isFinite(time) ? time : Infinity;
}

export function sortAlerts(rows) {
  return [...rows].sort((a, b) => {
    const priority = priorityRank(a.priorite) - priorityRank(b.priorite);
    if (priority) return priority;
    const first = deadline(a.dateEcheance), second = deadline(b.dateEcheance);
    return first === second ? 0 : first < second ? -1 : 1;
  });
}

export function notificationCount(count) {
  return count > 9 ? '9+' : String(count);
}
