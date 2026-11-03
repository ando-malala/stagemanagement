import { can } from './permissions.js';

// Use the server lifecycle and ownership flag, never another trainee's stage.
export function currentReportParticipations(participations) {
  return participations.filter(row => row.peutRedigerRapport === true && row.statut === 'EN_COURS');
}

export function reportWritingAccess(user, participation) {
  if (!can(user, 'gererRapports')) return { allowed: false, reason: 'Seul le stagiaire concerné peut rédiger et soumettre ses rapports.' };
  if (typeof participation?.peutRedigerRapport !== 'boolean') return { allowed: false, reason: 'Impossible de vérifier vos droits de rédaction. Actualisez la grille ; si le problème persiste, contactez l’administrateur.' };
  if (!participation.peutRedigerRapport) return { allowed: false, reason: 'La rédaction est réservée au compte stagiaire associé à cette participation. Contactez l’administrateur si ce dossier est le vôtre.' };
  // Match ParticipationRulesService.mutable: planning or suspension alone does not lock reports.
  if (participation.dateCloture || ['CLOTUREE', 'ANNULEE', 'ABANDONNEE'].includes(participation.statut)) {
    return { allowed: false, reason: 'Cette participation est terminée. Ses rapports sont consultables, mais ne peuvent plus être rédigés ou soumis.' };
  }
  return { allowed: true, reason: '' };
}

export function reportWeekState(period) {
  if (period.rapportId) return ({ BROUILLON: 'Brouillon', SOUMIS: 'Soumis à l’encadreur',
    A_CORRIGER: 'À corriger', VALIDE: 'Validé' })[period.statutRapport] ?? period.statutRapport ?? 'Rapport enregistré';
  return period.manquante ? 'Rapport manquant' : 'À rédiger';
}

export function reportWeekAction(period, canWrite) {
  if (!period.rapportId) return canWrite ? 'Rédiger' : null;
  if (canWrite && period.statutRapport === 'BROUILLON') return 'Reprendre';
  if (canWrite && period.statutRapport === 'A_CORRIGER') return 'Corriger';
  return 'Consulter';
}
