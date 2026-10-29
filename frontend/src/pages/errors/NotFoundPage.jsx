import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';

export default function NotFoundPage({ navigate }) {
  return (
    <Card className="message-card">
      <h2>Page introuvable</h2>
      <p>La page demandée n’existe pas dans l’application.</p>
      <Button onClick={() => navigate('/dashboard')}>Retour au tableau de bord</Button>
    </Card>
  );
}
