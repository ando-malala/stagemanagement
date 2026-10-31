// Server limit: 10 MiB per file, 11 MiB for the multipart envelope.
export const MAX_UPLOAD_BYTES = 10 * 1024 * 1024;

export function documentUploadForm(file, code) {
  if (!file || !Number.isFinite(file.size) || file.size <= 0) throw new Error('Sélectionnez un fichier non vide.');
  if (file.size > MAX_UPLOAD_BYTES) throw new Error('Le fichier doit faire moins de 10 Mio, enveloppe de transfert comprise.');
  if (!code?.trim()) throw new Error('Le code du type de pièce est obligatoire.');
  const formats = { pdf: 'application/pdf', png: 'image/png', jpg: 'image/jpeg', jpeg: 'image/jpeg' };
  const extension = file.name?.split('.').pop()?.toLowerCase();
  if (!formats[extension] || formats[extension] !== file.type) throw new Error('Choisissez un PDF, JPEG ou PNG avec un type de fichier valide.');
  const form = new FormData();
  form.append('typePieceJointeCode', code.trim());
  form.append('fichier', file);
  return form;
}

export function downloadFilename(disposition, id) {
  let name;
  const encoded = /filename\*\s*=\s*(?:"?UTF-8'[^']*')([^;"\r\n]+)/i.exec(disposition);
  if (encoded) {
    try { name = decodeURIComponent(encoded[1].trim()); } catch { /* Fall back to the ordinary filename. */ }
  }
  if (!name) {
    const match = /(?:^|;)\s*filename\s*=\s*(?:"((?:\\.|[^"\\])*)"|([^;\r\n]+))/i.exec(disposition);
    name = match?.[1]?.replace(/\\(.)/g, '$1') ?? match?.[2]?.trim();
  }
  // Strip control characters from server-provided filenames deliberately.
  // eslint-disable-next-line no-control-regex
  name = name?.split(/[\\/]/).pop()?.replace(/[\x00-\x1f\x7f]/g, '').trim();
  return !name || name === '.' || name === '..' ? `fichier-${id}` : name;
}

export function formatFileSize(bytes) {
  if (!Number.isFinite(bytes) || bytes < 0) return 'Non renseignée';
  if (bytes < 1024) return `${bytes} octets`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toLocaleString('fr-FR', { maximumFractionDigits: 1 })} Kio`;
  return `${(bytes / (1024 * 1024)).toLocaleString('fr-FR', { maximumFractionDigits: 2 })} Mio`;
}
