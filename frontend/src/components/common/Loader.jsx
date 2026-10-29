export default function Loader({ label = 'Chargement...' }) {
  return <p className="loader" role="status" aria-live="polite">{label}</p>;
}
