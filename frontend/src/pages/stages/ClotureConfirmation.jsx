import { useCallback } from 'react';
import { listDocumentsObligatoires } from '../../api/suiviApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import Button from '../../components/common/Button.jsx';

export function ClotureDialog({ documents, loading, error, onRetry, onConfirm, onClose, children }) {
  const missing = (documents ?? []).filter(document => !document.present);
  return <ConfirmDialog open title="Clôturer la participation" onClose={onClose}
    confirmDisabled={loading || Boolean(error) || !documents}
    confirmLabel="Clôturer quand même"
    onConfirm={onConfirm}>
    {children}
    {loading ? <p role="status">Vérification des documents…</p> : error ? <>
      <ErrorMessage error={error} /><Button variant="secondary" onClick={onRetry}>Réessayer</Button>
    </> : missing.length > 0 ? <p className="notice">Dossiers manquants : {missing.map(document => document.libelle).join(', ')}.</p>
      : documents && <p>Tous les documents obligatoires sont présents.</p>}
    <p>Cette décision administrative clôture le stage même si des documents ou des rapports sont manquants, non soumis ou non validés. Elle sera enregistrée dans l’historique.</p>
  </ConfirmDialog>;
}

export default function ClotureConfirmation({ participationId, ...props }) {
  const fetcher = useCallback(async options => {
    const documents = await listDocumentsObligatoires(participationId, options);
    if (documents.some(document => !document || typeof document.present !== 'boolean' || typeof document.libelle !== 'string' || !document.libelle.trim())) {
      throw new Error('Impossible de vérifier les documents du dossier. Réessayez.');
    }
    return documents;
  }, [participationId]);
  const state = useFetch(fetcher);
  return <ClotureDialog {...props} documents={state.data} loading={state.loading} error={state.error} onRetry={state.reload} />;
}
