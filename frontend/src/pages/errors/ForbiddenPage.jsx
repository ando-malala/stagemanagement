import Card from '../../components/common/Card.jsx';
import NavigationIcon from '../../components/layout/NavigationIcon.jsx';

export default function ForbiddenPage() {
  return (
    <Card className="message-card">
      <span className="icon-tile"><NavigationIcon name="UT" /></span><span className="message-code">403 · Habilitations</span>
      <h2>Accès refusé</h2>
      <p>Votre rôle ne permet pas d’accéder à cette page.</p>
    </Card>
  );
}
