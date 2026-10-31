import { isLocalDate } from './dateUtils.js';

// Operations are fixed API routes; their availability comes from the server.
const operations = ['statut', 'suspendre', 'reprendre', 'prolonger', 'abandonner', 'annuler', 'cloturer'];

export function todayMadagascar(now = new Date()) {
  const parts = new Intl.DateTimeFormat('en', { timeZone: 'Indian/Antananarivo', year: 'numeric', month: '2-digit', day: '2-digit' }).formatToParts(now);
  return ['year', 'month', 'day'].map((type) => parts.find((part) => part.type === type).value).join('-');
}

export function availableParticipationActions(participation, serverActions = []) {
  return participation.dateCloture ? [] : serverActions.filter((action) => operations.includes(action.code));
}

export function participationEventPayload(participation, code, values, today = todayMadagascar(), serverActions = []) {
  const action = availableParticipationActions(participation, serverActions).find((item) => item.code === code);
  if (!action) throw new Error('Cette action n’est pas disponible pour le statut actuel. Actualisez le dossier.');
  const payload = { motif: values.motif?.trim() || null };
  if (code === 'statut') payload.statutCode = action.statutCible;
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
