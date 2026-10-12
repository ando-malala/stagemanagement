export function formatNumber(value) {
  return new Intl.NumberFormat('fr-FR').format(value ?? 0);
}
