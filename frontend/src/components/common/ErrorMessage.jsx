import NavigationIcon from '../layout/NavigationIcon.jsx';

export default function ErrorMessage({ error }) {
  if (!error) return null;
  const message = typeof error === 'string' ? error : error.message;
  return <p className="alert alert-danger" role="alert"><span className="alert-icon"><NavigationIcon name="warning" /></span><span>{message}</span></p>;
}
