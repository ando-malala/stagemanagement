import { BUSINESS_ROLES } from '../utils/permissions.js';
import { hasAnyRole } from '../utils/roleUtils.js';

const module = (path, title, page, icon, roles = BUSINESS_ROLES) => ({ path, title, page, icon, roles });
export const navigation = [
  module('/dashboard', 'Tableau de bord', 'DashboardPage', 'TB'),
  module('/stagiaires', 'Stagiaires', 'StagiairesPage', 'ST'),
  module('/encadreurs', 'Encadreurs', 'EncadreursPage', 'EN'),
  module('/stages', 'Stages et participations', 'StagesPage', 'SP'),
  module('/rapports', 'Rapports', 'RapportsPage', 'RA'),
  module('/evaluations', 'Évaluations', 'EvaluationsPage', 'EV'),
  module('/soutenances', 'Soutenances', 'SoutenancesPage', 'SO'),
  module('/fichiers', 'Documents', 'FichiersPage', 'DO'),
  module('/alertes', 'Alertes', 'AlertesPage', 'AL'),
  module('/utilisateurs', 'Utilisateurs', 'UtilisateursPage', 'UT', ['ADMIN']),
  module('/referentiels', 'Référentiels', 'ReferentielsPage', 'RE', ['ADMIN']),
  module('/journal-audit', 'Journal d’audit', 'JournalAuditPage', 'JA', ['ADMIN'])
];
const detail = (path, title, page, parent) => ({ path, title, page, parent, roles: navigation.find((item) => item.path === parent).roles });
export const routes = [
  ...navigation,
  detail('/stagiaires/:id', 'Dossier stagiaire', 'StagiaireDetailPage', '/stagiaires'),
  detail('/encadreurs/:id', 'Fiche encadreur', 'EncadreurDetailPage', '/encadreurs'),
  detail('/stages/:id', 'Détail du stage', 'StageDetailPage', '/stages'),
  detail('/participations/:id', 'Dossier de participation', 'ParticipationDetailPage', '/stages'),
  detail('/rapports/:id', 'Détail du rapport', 'RapportDetailPage', '/rapports'),
  detail('/evaluations/:id', 'Détail de l’évaluation', 'EvaluationDetailPage', '/evaluations'),
  detail('/soutenances/:id', 'Détail de la soutenance', 'SoutenanceDetailPage', '/soutenances'),
  detail('/utilisateurs/:id', 'Détail utilisateur', 'UtilisateurDetailPage', '/utilisateurs'),
  detail('/referentiels/academiques', 'Référentiels académiques', 'AcademiquesPage', '/referentiels'),
  detail('/referentiels/organisation', 'Organisation', 'OrganisationPage', '/referentiels')
];

export function normalizePath(path) { return path === '/' ? '/' : path.replace(/\/+$/, ''); }
export function matchRoute(path) {
  const parts = normalizePath(path).split('/');
  for (const route of routes) {
    const pattern = route.path.split('/');
    if (pattern.length !== parts.length) continue;
    const params = {};
    if (pattern.every((part, i) => {
      if (part !== ':id') return part === parts[i];
      if (!/^[1-9]\d*$/.test(parts[i]) || Number(parts[i]) > 2147483647) return false;
      params.id = parts[i];
      return true;
    })) return { ...route, params };
  }
  return null;
}
export function visibleNavigation(user) { return navigation.filter((item) => hasAnyRole(user, item.roles)); }
export function safeReturnTo(value) {
  if (typeof value !== 'string' || !value.startsWith('/') || value.startsWith('//') || /[\\\s]/.test(value)) return '/dashboard';
  const url = new URL(value, 'https://local.invalid');
  if (url.origin !== 'https://local.invalid' || !matchRoute(url.pathname)) return '/dashboard';
  return normalizePath(url.pathname) + url.search + url.hash;
}
export function loginDestination(location) {
  return '/login?retour=' + encodeURIComponent(safeReturnTo(location));
}
export function breadcrumbItems(route) {
  const items = [{ path: '/dashboard', title: 'Accueil' }];
  if (route?.path === '/dashboard') return [{ title: 'Tableau de bord' }];
  if (route?.parent) items.push(navigation.find((item) => item.path === route.parent));
  items.push({ title: route ? route.title + (route.params.id ? ` n° ${route.params.id}` : '') : 'Page introuvable' });
  return items;
}
