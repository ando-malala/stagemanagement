import { useCallback, useEffect, useRef, useState } from 'react';
import { downloadFichier, listFichiersParticipation, listFichiersStagiaire, uploadFichierParticipation, uploadFichierStagiaire } from '../../api/fichierApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';
import { requireList } from '../../utils/businessForms.js';
import { documentUploadForm, formatFileSize } from '../../utils/documentUtils.js';
import { formatDateTime } from '../../utils/dateUtils.js';
import Card from '../../components/common/Card.jsx';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import Table from '../../components/common/Table.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';

const columns = [{ key: 'nomOriginal', label: 'Nom' }, { key: 'typePieceJointeLibelle', label: 'Type' },
  { key: 'typePieceJointeCode', label: 'Code du type' }, { key: 'typeMime', label: 'Format' },
  { key: 'tailleOctets', label: 'Taille', render: (row) => formatFileSize(row.tailleOctets) },
  { key: 'dateAjout', label: 'Ajout', render: (row) => formatDateTime(row.dateAjout) }];

// Parents key the panel by scope/id: drafts and downloads never follow a new owner.
export default function DocumentsPanel({ scope, ownerId, ownerLabel }) {
  const { utilisateur } = useAuth();
  const fetcher = useCallback(async (options) => requireList(await (scope === 'stagiaire'
    ? listFichiersStagiaire(ownerId, options) : listFichiersParticipation(ownerId, options))), [scope, ownerId]);
  const { data, loading, error: loadError, reload } = useFetch(fetcher);
  const [file, setFile] = useState(null);
  const [code, setCode] = useState('');
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);
  const [downloading, setDownloading] = useState(null);
  const [inputVersion, setInputVersion] = useState(0);
  const pending = useRef(false);
  const download = useRef(null);
  useEffect(() => () => download.current?.abort(), []);

  async function upload(event) {
    event.preventDefault();
    if (pending.current) return;
    setError(null);
    setMessage('');
    try {
      const payload = documentUploadForm(file, code);
      pending.current = true;
      setSaving(true);
      await (scope === 'stagiaire' ? uploadFichierStagiaire(ownerId, payload) : uploadFichierParticipation(ownerId, payload));
      setFile(null);
      setCode('');
      setInputVersion((version) => version + 1);
      setMessage('Document envoyé.');
      await reload();
    } catch (failure) { setError(failure); }
    finally { pending.current = false; setSaving(false); }
  }
  async function retrieve(id) {
    if (download.current) return;
    const controller = new AbortController();
    download.current = controller;
    setDownloading(id);
    setError(null);
    try { await downloadFichier(id, { signal: controller.signal }); }
    catch (failure) { if (failure.name !== 'AbortError') setError(failure); }
    finally { download.current = null; setDownloading(null); }
  }
  return <Card>
    <div className="section-heading"><div><h3>Documents du {scope === 'stagiaire' ? 'stagiaire' : 'dossier de participation'}</h3>
      <p>{ownerLabel} — n° {ownerId}</p></div><Button variant="secondary" onClick={reload} disabled={loading || saving}>Actualiser</Button></div>
    <p className="notice">Le catalogue des types de pièces et la liste des documents obligatoires ne sont pas disponibles. La complétude du dossier ne peut pas être vérifiée ici.</p>
    {can(utilisateur, 'televerserDocuments') && <form className="page-stack business-form" onSubmit={upload}>
      <Input label="Code du type de pièce" name="typePieceJointeCode" value={code} onChange={(event) => setCode(event.target.value)} required disabled={saving}
        hint="Saisissez un code connu de votre service. Le serveur vérifie le type, son périmètre et les doublons." />
      <Input key={inputVersion} label="Fichier" name="fichier" type="file" required disabled={saving} onChange={(event) => setFile(event.target.files?.[0] ?? null)}
        hint="Moins de 10 Mio, enveloppe de transfert comprise. Un transfert proche de cette limite peut être refusé." />
      {file && <p>Fichier sélectionné : {file.name} ({formatFileSize(file.size)})</p>}
      <Button type="submit" disabled={saving || loading || Boolean(loadError)}>{saving ? 'Envoi en cours…' : 'Envoyer le document'}</Button>
    </form>}
    {message && <p className="alert alert-success" role="status">{message}</p>}
    <ErrorMessage error={error} /><ErrorMessage error={loadError} />
    <Table caption="Documents enregistrés" columns={columns} data={data ?? []} loading={loading} error={loadError}
      renderActions={(row) => <Button variant="secondary" disabled={downloading != null} onClick={() => retrieve(row.id)}>{downloading === row.id ? 'Téléchargement…' : 'Télécharger'}</Button>} />
  </Card>;
}
