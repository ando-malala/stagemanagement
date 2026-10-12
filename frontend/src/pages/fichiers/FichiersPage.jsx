import { useState } from 'react';
import { downloadFichier, listFichiersParticipation, listFichiersStagiaire, uploadFichierParticipation, uploadFichierStagiaire } from '../../api/fichierApi.js';
import Button from '../../components/common/Button.jsx';
import Card from '../../components/common/Card.jsx';
import Input from '../../components/common/Input.jsx';
import Table from '../../components/common/Table.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { hasAnyRole } from '../../utils/roleUtils.js';

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'nomOriginal', label: 'Nom' },
  { key: 'typePieceJointeLibelle', label: 'Type' },
  { key: 'typeMime', label: 'MIME' },
  { key: 'tailleOctets', label: 'Taille' },
  { key: 'dateAjout', label: 'Ajouté le' }
];

export default function FichiersPage() {
  const { utilisateur } = useAuth();
  const canUpload = hasAnyRole(utilisateur, ['ADMIN', 'RESPONSABLE_STAGE']);
  const [scope, setScope] = useState('stagiaire');
  const [ownerId, setOwnerId] = useState('');
  const [typePieceJointeCode, setTypePieceJointeCode] = useState('');
  const [file, setFile] = useState(null);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  async function load(event) {
    event?.preventDefault();
    if (!ownerId) return;

    setLoading(true);
    setError('');
    setMessage('');

    try {
      const result = scope === 'stagiaire'
        ? await listFichiersStagiaire(ownerId)
        : await listFichiersParticipation(ownerId);
      setData(Array.isArray(result) ? result : []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function upload(event) {
    event.preventDefault();
    if (!file || !ownerId || !typePieceJointeCode.trim()) return;

    setSaving(true);
    setError('');
    setMessage('');

    const formData = new FormData();
    formData.append('typePieceJointeCode', typePieceJointeCode.trim());
    formData.append('fichier', file);

    try {
      if (scope === 'stagiaire') {
        await uploadFichierStagiaire(ownerId, formData);
      } else {
        await uploadFichierParticipation(ownerId, formData);
      }
      setFile(null);
      setMessage('Fichier envoyé.');
      await load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <Card>
      <div className="section-heading">
        <div>
          <h2>Fichiers</h2>
          <p className="small">Documents liés à un stagiaire ou à une participation.</p>
        </div>
      </div>

      <form className="entity-form" onSubmit={load}>
        <Input
          label="Périmètre"
          name="scope"
          type="select"
          value={scope}
          onChange={(event) => setScope(event.target.value)}
          options={[
            { value: 'stagiaire', label: 'Stagiaire' },
            { value: 'participation', label: 'Participation' }
          ]}
        />
        <Input label={scope === 'stagiaire' ? 'ID stagiaire' : 'ID participation'} name="ownerId" type="number" value={ownerId} onChange={(event) => setOwnerId(event.target.value)} required />
        <Button type="submit" variant="secondary" disabled={loading}>{loading ? 'Chargement...' : 'Lister'}</Button>
      </form>

      {canUpload && (
        <form className="entity-form" onSubmit={upload}>
          <Input label="Code type pièce jointe" name="typePieceJointeCode" value={typePieceJointeCode} onChange={(event) => setTypePieceJointeCode(event.target.value)} required />
          <label className="field">
            <span>Fichier</span>
            <input type="file" onChange={(event) => setFile(event.target.files?.[0] ?? null)} required />
          </label>
          <Button type="submit" disabled={saving}>{saving ? 'Envoi...' : 'Envoyer'}</Button>
        </form>
      )}

      {message && <p className="alert alert-success" role="status">{message}</p>}
      {error && <p className="alert alert-danger" role="alert">{error}</p>}

      <Table
        columns={columns}
        data={data}
        loading={loading}
        renderActions={(row) => (
          <Button variant="secondary" onClick={() => downloadFichier(row.id)}>Télécharger</Button>
        )}
      />
    </Card>
  );
}
