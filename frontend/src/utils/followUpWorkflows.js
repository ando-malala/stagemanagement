// Confirmed transitions from Conception2.sql; the server validates installed rules.
export function rapportTransitions(statut) {
  if (statut === 'BROUILLON' || statut === 'A_CORRIGER') return [{ code: 'SOUMIS', label: 'Soumettre' }];
  if (statut === 'SOUMIS') return [{ code: 'A_CORRIGER', label: 'Demander une correction' }, { code: 'VALIDE', label: 'Valider' }];
  return [];
}

export function rapportStatusPayload(statut, target, motif) {
  if (!rapportTransitions(statut).some((item) => item.code === target)) throw new Error('Transition indisponible. Actualisez le rapport.');
  return { statutCode: target, motif: motif?.trim() || null };
}

export function evaluationNotePayload(critereId, values) {
  const note = Number(values.note);
  if (!Number.isInteger(critereId) || critereId < 1) throw new Error('Critère invalide.');
  if (values.note == null || String(values.note).trim() === '' || !Number.isFinite(note) || note < 0 || note > 20) throw new Error('La note doit être comprise entre 0 et 20.');
  return { critereId, note, commentaire: values.commentaire?.trim() || null };
}

export function missingRequiredCriteria(criteres, notes) {
  return criteres.filter((critere) => critere.obligatoire === true && !notes.some((note) => note.critereId === critere.id));
}
