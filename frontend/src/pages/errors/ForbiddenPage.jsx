import Card from '../../components/common/Card.jsx';

export default function ForbiddenPage() {
  return (
    <Card className="message-card">
      <h2>Accès refusé</h2>
      <p>Votre rôle ne permet pas d’accéder à cette page.</p>
    </Card>
  );
}
