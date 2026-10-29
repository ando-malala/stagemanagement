import { isLocalDate } from './dateUtils.js';

// Dedicated service operations and the transitions present in Conception2.sql.
// The server remains authoritative, notably for chronological event constraints.
export const participationActions = [
  { code: 'demarrer', label: 'Démarrer', statuses: ['PLANIFIEE'] },
  { code: 'suspendre', label: 'Suspendre', statuses: ['EN_COURS'], dateEffet: true },
  { code: 'reprendre', label: 'Reprendre', statuses: ['SUSPENDUE'], dateEffet: true },
  { code: 'prolonger', label: 'Prolonger', statuses: ['PLANIFIEE', 'EN_COURS', 'SUSPENDUE', 'A_CLOTURER'] },
  { code: 'abandonner', label: 'Abandonner', statuses: ['EN_COURS', 'SUSPENDUE'] },
  { code: 'annuler', label: 'Annuler la participation', statuses: ['PLANIFIEE', 'EN_COURS', 'SUSPENDUE'] }
];

export function todayMadagascar(now = new Date()) {
  const parts = new Intl.DateTimeFormat('en', { timeZone: 'Indian/Antananarivo', year: 'numeric', month: '2-digit', day: '2-digit' }).formatToParts(now);
  return ['year', 'month', 'day'].map((type) => parts.find((part) => part.type === type).value).join('-');
}

export function availableParticipationActions(participation) {
  return participation.dateCloture ? [] : participationActions.filter((action) => action.statuses.includes(participation.statut));
}

export function participationEventPayload(participation, code, values, today = todayMadagascar()) {
  const action = availableParticipationActions(participation).find((item) => item.code === code);
  if (!action) throw new Error('Cette action n’est pas disponible pour le statut actuel. Actualisez le dossier.');
  const payload = { motif: values.motif?.trim() || null };
  if (code === 'demarrer') payload.statutCode = 'EN_COURS';
  if (code === 'prolonger') {
    if (!isLocalDate(values.nouvelleDateFin) || values.nouvelleDateFin <= participation.dateFinStage) throw new Error('La nouvelle date de fin doit être postérieure à la fin actuelle.');
    payload.nouvelleDateFin = values.nouvelleDateFin;
  }
  if (action.dateEffet) {
    if (!isLocalDate(values.dateEffet) || values.dateEffet < participation.dateDebutStage || values.dateEffet > participation.dateFinStage || values.dateEffet > today) throw new Error('La date d’effet doit être comprise dans la période du stage et ne pas être future.');
    payload.dateEffet = values.dateEffet;
  }
  return payload;
}
