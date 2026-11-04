import { apiRequest } from '../../api/apiClient.js';
import { useFetch } from '../../hooks/useFetch.js';
import StagiaireDetailPage from './StagiaireDetailPage.jsx';
import Loader from '../../components/common/Loader.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import EmptyState from '../../components/common/EmptyState.jsx';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';
const fetchProfile = (options) => apiRequest('/api/portail/me', options);
export default function MonDossierPage({ navigate }) {
  const { data, error, loading } = useFetch(fetchProfile);
  if (loading) return <Loader />;
  if (error) return <ErrorMessage error={error} />;
  if (!data?.stagiaireId) return <EmptyState message="Votre compte n’est pas encore associé à un dossier. Contactez l’administrateur." />;
  return <div className="page-stack"><section className="portal-intro"><span className="icon-tile"><NavigationIcon name="MD" /></span><div><span className="eyebrow">Espace stagiaire</span><h2>Votre parcours, étape par étape</h2><p>Retrouvez votre dossier, vos participations et les documents de votre stage.</p></div></section><StagiaireDetailPage params={{ id: data.stagiaireId }} navigate={navigate} /></div>;
}
