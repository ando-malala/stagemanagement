export const dashboardCards = [
  ['totalStagiaires', 'Stagiaires', '/stagiaires'], ['totalStages', 'Stages', '/stages'],
  ['totalParticipations', 'Participations', '/stages'], ['totalRapports', 'Rapports', '/rapports'],
  ['totalAlertesATraiter', 'Alertes à traiter', '/alertes?statut=A_TRAITER'], ['totalAlertesTraitees', 'Alertes traitées', '/alertes?statut=TRAITEE']
];

export function requireDashboard(value) {
  if (!value || dashboardCards.some(([key]) => !Number.isSafeInteger(value[key]) || value[key] < 0)) throw new Error('Le serveur n’a pas renvoyé les compteurs attendus.');
  return value;
}

export function filterAlertes(rows, filters) {
  return rows.filter((row) => ['statut', 'priorite', 'natureAlerte', 'typeAlerteCode'].every((key) => !filters[key] || row[key] === filters[key]));
}

export function alertFilterOptions(rows, key) {
  return [...new Set(rows.map((row) => row[key]).filter(Boolean))].sort().map((value) => ({ value,
    label: key === 'typeAlerteCode' ? rows.find((row) => row[key] === value)?.typeAlerteLibelle ?? value : value }));
}

export function alerteLinks(row) {
  return [['stageParticipantId', 'Participation', '/participations'], ['rapportId', 'Rapport', '/rapports'],
    ['encadreurId', 'Encadreur', '/encadreurs'], ['soutenanceId', 'Soutenance', '/soutenances']]
    .filter(([key]) => Number.isInteger(row[key]) && row[key] > 0)
    .map(([key, label, path]) => ({ label, path: `${path}/${row[key]}` }));
}

export function redactAuditValue(value) {
  if (Array.isArray(value)) return value.map(redactAuditValue);
  if (value && typeof value === 'object') return Object.fromEntries(Object.entries(value).map(([key, item]) => [key,
    /password|motdepasse|token|jeton|secret|authorization|cookie|apikey|privatekey/i.test(key.replace(/[^a-z]/gi, '')) ? '[masqué]' : redactAuditValue(item)]));
  return value;
}

export function auditQuery(values) {
  if (values.mode === 'all') return { mode: 'all' };
  const raw = values.mode === 'user' ? values.utilisateurId : values.entiteId;
  if (!/^[1-9]\d*$/.test(String(raw)) || Number(raw) > 2147483647) throw new Error('Sélectionnez ou saisissez un identifiant entier positif valide.');
  if (values.mode === 'user') return { mode: 'user', utilisateurId: Number(raw) };
  if (values.mode !== 'entity' || !values.entite?.trim()) throw new Error('Le nom exact de l’entité est obligatoire.');
  return { mode: 'entity', entite: values.entite.trim(), entiteId: Number(raw) };
}
