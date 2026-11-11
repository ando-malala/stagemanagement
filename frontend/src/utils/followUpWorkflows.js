export function rapportTransitions(statut, transitions = []) {
  return transitions.filter((t) => t.source === statut).map((t) => ({ code: t.cible, label: t.libelle }));
}

export function rapportStatusPayload(statut, target, motif, transitions = []) {
  if (!rapportTransitions(statut, transitions).some((item) => item.code === target)) throw new Error('Transition indisponible. Actualisez le rapport.');
  return { statutCode: target, motif: motif?.trim() || null };
}

export function evaluationNotePayload(critereId, values) {
  const note = Number(values.note);
  if (!Number.isInteger(critereId) || critereId < 1) throw new Error('Critère invalide.');
  if (values.note == null || String(values.note).trim() === '' || !Number.isFinite(note) || note < 0 || note > 5) throw new Error('La note doit être comprise entre 0 et 5.');
  if (Math.abs(note * 100 - Math.round(note * 100)) > 1e-8) throw new Error('La note accepte au maximum deux décimales.');
  return { critereId, note, commentaire: values.commentaire?.trim() || null };
}

export function missingRequiredCriteria(criteres, notes) {
  return criteres.filter((critere) => critere.obligatoire === true && !notes.some((note) => note.critereId === critere.id));
}
