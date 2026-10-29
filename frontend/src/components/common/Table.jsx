import { useMemo, useState } from 'react';
import EmptyState from './EmptyState.jsx';
import Loader from './Loader.jsx';
import Button from './Button.jsx';
import Input from './Input.jsx';

function displayValue(value) {
  if (value === null || value === undefined || value === '') return '—';
  if (typeof value === 'boolean') return value ? 'Oui' : 'Non';
  if (typeof value === 'object') return value.libelle ?? value.code ?? '—';
  return String(value);
}

export default function Table({ columns, data = [], loading, error, renderActions, caption = 'Résultats', searchable = true }) {
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const filtered = useMemo(() => {
    const term = query.trim().toLocaleLowerCase('fr');
    return (Array.isArray(data) ? data : []).filter((row) => !term || columns.some((column) =>
      displayValue(row[column.key]).toLocaleLowerCase('fr').includes(term)));
  }, [columns, data, query]);
  const pages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const currentPage = Math.min(page, pages);
  const rows = filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  if (loading) return <Loader />;
  if (error) return null; // The parent renders the error, never a misleading empty result.
  if (!Array.isArray(data)) return <p role="alert">Format de liste inattendu.</p>;
  return (
    <div>
      {searchable && <div className="table-tools">
        <Input label="Rechercher dans cette liste" type="search" value={query} onChange={(event) => { setQuery(event.target.value); setPage(1); }} />
        <span role="status">{filtered.length} résultat(s)</span>
      </div>}
      {!rows.length ? <EmptyState /> : <div className="table-wrap">
        <table>
          <caption>{caption}</caption>
          <thead><tr>
            {columns.map((column) => <th scope="col" key={column.key}>{column.label}</th>)}
            {renderActions && <th scope="col">Actions</th>}
          </tr></thead>
          <tbody>{rows.map((row) => <tr key={row.id ?? JSON.stringify(row)}>
            {columns.map((column) => <td key={column.key}>{column.render ? column.render(row) : displayValue(row[column.key])}</td>)}
            {renderActions && <td>{renderActions(row)}</td>}
          </tr>)}</tbody>
        </table>
      </div>}
      {filtered.length > 0 && <nav className="pagination" aria-label={`Pagination — ${caption}`}>
        <Input label="Lignes par page" type="select" value={pageSize} options={[10, 25, 50]} onChange={(event) => { setPageSize(Number(event.target.value) || 10); setPage(1); }} />
        <Button variant="secondary" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Précédent</Button>
        <span>Page {currentPage} sur {pages}</span>
        <Button variant="secondary" disabled={currentPage === pages} onClick={() => setPage(currentPage + 1)}>Suivant</Button>
      </nav>}
    </div>
  );
}
