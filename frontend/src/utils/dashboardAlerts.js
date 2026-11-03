import { nameParticipationMessage } from './alertPeople.js';

const missingReport = alert => alert.typeAlerteCode === 'RAPPORT_MANQUANT';
const countLabel = count => `${count} rapport${count > 1 ? 's' : ''} manquant${count > 1 ? 's' : ''}`;
const priorityRank = { INFORMATION: 0, AVERTISSEMENT: 1, URGENT: 2 };

export function groupDashboardAlerts(alerts, participations) {
  const byId = new Map(participations.map(row => [row.id, row]));
  const groups = new Map();
  const cards = [];
  for (const alert of alerts) {
    const participation = byId.get(alert.stageParticipantId);
    if (!missingReport(alert) || !participation?.stagiaireId || !participation.stagiaireNomComplet) {
      const name = participation?.stagiaireNomComplet;
      const type = alert.typeAlerteLibelle ?? alert.typeAlerteCode;
      cards.push({ key: `alerte/${alert.id}`, title: name ? `Stagiaire ${name} — ${type}` : type,
        message: nameParticipationMessage(alert.message, participation?.id, name), priorite: alert.priorite, natureAlerte: alert.natureAlerte,
        path: `/alertes?alerte=${alert.id}`, linkLabel: 'Consulter l’alerte' });
      continue;
    }
    const id = participation.stagiaireId;
    if (!groups.has(id)) {
      const card = { key: `rapports/${id}`, title: `Stagiaire ${participation.stagiaireNomComplet}`,
        priorite: alert.priorite, natureAlerte: alert.natureAlerte,
        path: `/participations/${participation.id}`, linkLabel: 'Voir les semaines de rapport' };
      groups.set(id, { card, alerts: new Set(), participations: new Set() });
      cards.push(card);
    }
    const group = groups.get(id);
    group.alerts.add(alert.id);
    group.participations.add(participation.id);
    group.card.message = countLabel(group.alerts.size);
    if ((priorityRank[alert.priorite] ?? 0) > (priorityRank[group.card.priorite] ?? 0)) group.card.priorite = alert.priorite;
    if (group.participations.size > 1) {
      group.card.path = `/stagiaires/${id}`;
      group.card.linkLabel = 'Voir les participations du stagiaire';
    }
  }
  return cards;
}

export function summarizePriorityReports(priorities, alerts) {
  const byParticipation = new Map();
  for (const alert of alerts ?? []) {
    if (!byParticipation.has(alert.stageParticipantId)) byParticipation.set(alert.stageParticipantId, []);
    byParticipation.get(alert.stageParticipantId).push(alert);
  }
  return priorities.map(priority => {
    const rows = byParticipation.get(priority.participationId) ?? [];
    const missing = rows.filter(missingReport);
    // Preserve the server summary if some linked alerts are not in this snapshot.
    if (!missing.length || rows.length !== priority.nombreAlertes) return { ...priority,
      situation: nameParticipationMessage(priority.situation, priority.participationId, priority.stagiaire) };
    const others = [...new Set(rows.filter(row => !missingReport(row)).map(row => row.message ?? row.typeAlerteLibelle))].filter(Boolean).sort();
    return { ...priority, situation: nameParticipationMessage([countLabel(missing.length), ...others].join(' · '), priority.participationId, priority.stagiaire) };
  });
}
