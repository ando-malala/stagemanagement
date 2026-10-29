import Loader from '../common/Loader.jsx';
import { useAuth } from '../../hooks/useAuth.js';

export default function ProtectedRoute({ children }) {
  const { estConnecte, initialisation } = useAuth();

  if (initialisation) {
    return (
      <main className="center-screen">
        <Loader label="Vérification de la session..." />
      </main>
    );
  }

  if (!estConnecte) return null;

  return children;
}
