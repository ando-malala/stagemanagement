import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';

export default function NotFoundPage({ navigate }) {
  return (
    <Card className="message-card">
      <span className="icon-tile"><NavigationIcon name="DO" /></span><span className="message-code">404 · Navigation</span>
      <h2>Page introuvable</h2>
      <p>La page demandée n’existe pas dans l’application.</p>
      <Button onClick={() => navigate('/dashboard')}>Retour au tableau de bord</Button>
    </Card>
  );
}
