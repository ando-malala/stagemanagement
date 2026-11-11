import Badge from './Badge.jsx';

// Presentation only: preserve exact business labels, including unknown values.
const variants = {
  EN_COURS: 'info', SOUMIS: 'info', INFORMATION: 'info', PLANIFIEE: 'info',
  VALIDE: 'success', VALIDATION: 'success', CLOTUREE: 'success', TRAITEE: 'success', ACTIF: 'success', DISPONIBLE: 'success',
  A_TRAITER: 'warning', AVERTISSEMENT: 'warning', HAUTE: 'warning', A_CLOTURER: 'warning', SUSPENDUE: 'warning', INDISPONIBLE: 'warning',
  A_CORRIGER: 'correction', DEMANDE_CORRECTION: 'correction',
  URGENT: 'danger', URGENTE: 'danger', EXPIREE: 'danger', BLOQUE: 'danger', NORMALE: 'info',
  BROUILLON: 'neutral', ANNULEE: 'neutral', ABANDONNEE: 'neutral', IGNOREE: 'neutral', INACTIF: 'neutral', ARCHIVE: 'neutral',
  'Brouillon': 'neutral', 'Soumis à l’encadreur': 'info', 'À corriger': 'correction',
  'Validé': 'success', 'Rapport manquant': 'danger', 'À rédiger': 'warning'
};

export default function StatusBadge({ value, label }) {
  if (value == null || value === '') return '—';
  return <Badge variant={Object.hasOwn(variants, value) ? variants[value] : 'neutral'}>{label ?? String(value)}</Badge>;
}
