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
import { listTypesDocuments, listDocumentsObligatoires } from '../../api/suiviApi.js';

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
  const types = useFetch(listTypesDocuments);
  const loadRequired = useCallback((options) => scope === 'participation' ? listDocumentsObligatoires(ownerId, options) : Promise.resolve([]), [scope, ownerId]);
  const required = useFetch(loadRequired);
  const choices = (types.data ?? []).filter((t) => scope === 'stagiaire' ? t.pourStagiaire : t.pourParticipation)
    .filter((t) => t.multipleAutorise || !(data ?? []).some((f) => f.typePieceJointeCode === t.code));
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
      if (!choices.some((t) => t.code === code)) throw new Error('Sélectionnez un type de document disponible.');
      const payload = documentUploadForm(file, code);
      pending.current = true;
      setSaving(true);
      await (scope === 'stagiaire' ? uploadFichierStagiaire(ownerId, payload) : uploadFichierParticipation(ownerId, payload));
      setFile(null);
      setCode('');
      setInputVersion((version) => version + 1);
      setMessage('Document envoyé.');
      await Promise.all([reload(), required.reload()]);
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
      <p>{ownerLabel} — n° {ownerId}</p></div><Button variant="secondary" onClick={() => { void reload(); void types.reload(); void required.reload(); }} disabled={loading || saving}>Actualiser</Button></div>
    <ErrorMessage error={types.error} /><ErrorMessage error={required.error} />
    {scope === 'participation' && <section><h4>Pièces obligatoires pour la clôture</h4>
      {required.loading && <p role="status">Vérification des pièces…</p>}
      {!required.loading && !required.error && (required.data?.length ? <ul>{required.data.map((d) => <li key={d.id}>{d.libelle} : {d.present ? 'présent' : 'manquant'}</li>)}</ul> : <p>Aucune pièce obligatoire configurée.</p>)}
    </section>}
    {can(utilisateur, 'televerserDocuments') && <form className="page-stack business-form" onSubmit={upload}>
      <Input label="Type de document" type="select" name="typePieceJointeCode" value={code} onChange={(event) => setCode(event.target.value)} required disabled={saving || types.loading || Boolean(types.error)}
        options={choices.map((t) => ({ value: t.code, label: `${t.libelle}${t.obligatoireCloture ? ' — obligatoire pour la clôture' : ''}` }))} />
      <Input key={inputVersion} label="Fichier" name="fichier" type="file" accept=".pdf,.png,.jpg,.jpeg" required disabled={saving} onChange={(event) => setFile(event.target.files?.[0] ?? null)}
        hint="PDF, JPEG ou PNG, maximum 10 Mio. Le contenu est controle par le serveur." />
      {file && <p>Fichier sélectionné : {file.name} ({formatFileSize(file.size)})</p>}
      <Button type="submit" disabled={saving || loading || types.loading || Boolean(types.error) || Boolean(loadError) || !choices.length}>{saving ? 'Envoi en cours…' : 'Envoyer le document'}</Button>
    </form>}
    {message && <p className="alert alert-success" role="status">{message}</p>}
    <ErrorMessage error={error} /><ErrorMessage error={loadError} />
    <Table caption="Documents enregistrés" columns={columns} data={data ?? []} loading={loading} error={loadError}
      renderActions={(row) => <Button variant="secondary" disabled={downloading != null} onClick={() => retrieve(row.id)}>{downloading === row.id ? 'Téléchargement…' : 'Télécharger'}</Button>} />
  </Card>;
}
