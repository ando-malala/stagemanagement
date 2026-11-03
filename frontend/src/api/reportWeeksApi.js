import { getParticipation } from './stageApi.js';
import { listPeriodesRapports } from './suiviApi.js';
import { listParticipationRapports } from './rapportApi.js';
import { requireList } from '../utils/businessForms.js';

export async function loadReportWeeks(participationId, options = {}) {
  const [participation, periods, reports] = await Promise.all([
    getParticipation(participationId, options),
    listPeriodesRapports(participationId, options),
    listParticipationRapports(participationId, options)
  ]);
  if (!participation || participation.id !== Number(participationId)) throw new Error('La participation attendue n’a pas pu être chargée.');
  const byId = new Map(requireList(reports).map(report => [report.id, report]));
  return { participation, periods: periods.map(period => ({ ...period, rapport: byId.get(period.rapportId) })) };
}
