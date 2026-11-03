export function nameParticipationMessage(message, participationId, name) {
  if (!message || !name || !Number.isSafeInteger(participationId) || participationId <= 0) return message;
  const reference = new RegExp(`\\bparticipation\\s+n[°º]\\s*${participationId}(?!\\d)`, 'gi');
  return message.replace(reference, match => `${match.startsWith('P') ? 'Participation' : 'participation'} de ${name} (n° ${participationId})`);
}

export function nameAlerts(alerts, participations) {
  const byId = new Map(participations.map(row => [row.id, row]));
  return alerts.map(alert => {
    const participation = byId.get(alert.stageParticipantId);
    if (!participation?.stagiaireNomComplet) return alert;
    return { ...alert, stagiaireId: participation.stagiaireId, stagiaireNomComplet: participation.stagiaireNomComplet,
      message: nameParticipationMessage(alert.message, participation.id, participation.stagiaireNomComplet) };
  });
}
