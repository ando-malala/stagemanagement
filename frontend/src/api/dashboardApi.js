import { apiRequest } from './apiClient.js';
import { detecterToutesAlertes } from './alerteApi.js';
import { loadAlertContext } from './alertContextApi.js';
import { getPilotage } from './syntheseApi.js';
import { requireDashboard } from '../utils/pilotage.js';
import { groupDashboardAlerts } from '../utils/dashboardAlerts.js';

export function getDashboardResume(options = {}) {
  return apiRequest('/api/dashboard/resume', options);
}

export async function loadDashboard(options = {}) {
  const [resume, context, pilotage] = await Promise.all([
    getDashboardResume(options), loadAlertContext(options, true), getPilotage(options)
  ]);
  return {
    resume: requireDashboard(resume),
    rows: context.rows,
    cards: groupDashboardAlerts(context.rows, context.participations),
    pilotage
  };
}

export async function refreshDashboard(reload, canDetect) {
  // The global detection endpoint is restricted to administrators by Spring.
  // Wait for all seven checks before reading counters, alerts and follow-up data.
  if (canDetect) await detecterToutesAlertes();
  return reload();
}
