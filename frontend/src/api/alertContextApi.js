import { listAlertes, listAlertesNonLues } from './alerteApi.js';
import { listParticipations } from './stageApi.js';
import { requireList } from '../utils/businessForms.js';
import { nameAlerts } from '../utils/alertPeople.js';

export async function loadAlertContext(options = {}, pendingOnly = false) {
  const [alerts, participationResponse] = await Promise.all([
    pendingOnly ? listAlertesNonLues(options) : listAlertes(options), listParticipations(options)
  ]);
  const participations = requireList(participationResponse);
  return { rows: nameAlerts(requireList(alerts), participations), participations };
}
