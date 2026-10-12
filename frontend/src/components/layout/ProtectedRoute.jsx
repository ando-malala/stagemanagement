import { useEffect } from 'react';
import Loader from '../common/Loader.jsx';
import { useAuth } from '../../hooks/useAuth.js';

export default function ProtectedRoute({ children, navigate }) {
  const { estConnecte, initialisation } = useAuth();

  useEffect(() => {
    if (!initialisation && !estConnecte) {
      navigate('/login', { replace: true });
    }
  }, [estConnecte, initialisation, navigate]);

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
