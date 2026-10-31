import { apiRequest } from '../../api/apiClient.js';
import { useFetch } from '../../hooks/useFetch.js';
import StagiaireDetailPage from './StagiaireDetailPage.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
const fetchProfile = (options) => apiRequest('/api/portail/me', options);
export default function MonDossierPage({ navigate }) {
  const { data, error, loading } = useFetch(fetchProfile);
  if (loading) return <Loader />;
  if (error) return <ErrorMessage error={error} />;
  if (!data?.stagiaireId) return <p role="status">Votre compte n’est pas encore associé à un dossier. Contactez l’administrateur.</p>;
  return <StagiaireDetailPage params={{ id: data.stagiaireId }} navigate={navigate} />;
}
