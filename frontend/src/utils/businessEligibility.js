const same = (a, b) => a != null && b != null && String(a) === String(b);
const terminal = new Set(['CLOTUREE', 'ANNULEE', 'ABANDONNEE']);

export function eligibleChoices(kind, name, rows, initial = {}, values = {}, choices = {}) {
  return rows.filter(row => {
    const current = same(row.id, initial[name]);
    if (row.actif === false && !current) return false;
    if ((name === 'departementId' || (kind === 'participation' && ['encadreurId', 'stagiaireId'].includes(name)))
        && row.statut && row.statut !== 'ACTIF' && !current) return false;
    if (kind === 'participation' && name === 'stageId' && row.eligibleNouvelleParticipation === false && !current) return false;
    if (['evaluation', 'soutenance', 'rapport'].includes(kind) && name === 'stageParticipantId' && (terminal.has(row.statut) || row.dateCloture)) return false;
    if (kind === 'evaluation' && name === 'encadreurId') {
      const participation = choices.stageParticipantId?.find(p => same(p.id, values.stageParticipantId));
      if (!participation || !same(row.id, participation.encadreurId)) return false;
    }
    if (kind === 'alerte') {
      const report = choices.rapportId?.find(r => same(r.id, values.rapportId));
      const defense = choices.soutenanceId?.find(s => same(s.id, values.soutenanceId));
      const ownerId = values.stageParticipantId || report?.stageParticipantId || defense?.stageParticipantId;
      if (ownerId && ['rapportId', 'soutenanceId'].includes(name) && !same(row.stageParticipantId, ownerId)) return false;
      if (ownerId && name === 'encadreurId') {
        const participation = choices.stageParticipantId?.find(p => same(p.id, ownerId));
        if (!participation || !same(row.id, participation.encadreurId)) return false;
      }
    }
    return true;
  });
}
