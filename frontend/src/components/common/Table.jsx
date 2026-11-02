import { useMemo, useState } from 'react';
import EmptyState from './EmptyState.jsx';
import Loader from './Loader.jsx';
import Button from './Button.jsx';
import Input from './Input.jsx';
import { displayValue, selectTableRows } from '../../utils/tableUtils.js';

export default function Table({ columns, data = [], loading, error, renderActions, caption = 'Résultats', searchable = true }) {
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [filters, setFilters] = useState({});
  const [sort, setSort] = useState(null);
  const filtered = useMemo(() => selectTableRows(data, columns, query, filters, sort), [columns, data, query, filters, sort]);
  const filterColumns = columns.filter(column => column.filterable);
  const hasCriteria = query !== '' || Object.values(filters).some(Boolean) || sort;
  const pages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const currentPage = Math.min(page, pages);
  const rows = filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  if (loading) return <Loader />;
  if (error) return null; // The parent renders the error, never a misleading empty result.
  if (!Array.isArray(data)) return <p role="alert">Format de liste inattendu.</p>;
  return (
    <div>
      {searchable && <div className="table-tools">
        <Input label="Rechercher dans cette liste" type="search" placeholder="Nom, stage, mot-clé…" value={query} onChange={(event) => { setQuery(event.target.value); setPage(1); }} />
        {filterColumns.map(column => <Input key={column.key} label={`Filtrer : ${column.label}`} type="select"
          emptyOptionLabel="Tous" value={filters[column.key] ?? ''}
          options={[...new Set(data.map(row => displayValue(row[column.key])))].sort((a, b) => a.localeCompare(b, 'fr'))}
          onChange={event => { setFilters(current => ({ ...current, [column.key]: event.target.value })); setPage(1); }} />)}
        {hasCriteria && <Button variant="secondary" onClick={() => { setQuery(''); setFilters({}); setSort(null); setPage(1); }}>Réinitialiser</Button>}
        <span role="status">{filtered.length} résultat(s)</span>
      </div>}
      {!rows.length ? <EmptyState message={data.length ? 'Aucun résultat pour ces critères. Modifiez la recherche ou réinitialisez les filtres.' : undefined} /> : <div className="table-wrap">
        <table>
          <caption>{caption}</caption>
          <thead><tr>
            {columns.map((column) => <th scope="col" key={column.key}
              aria-sort={column.sortable === false ? undefined : sort?.key === column.key ? (sort.direction === 'asc' ? 'ascending' : 'descending') : 'none'}>
              {column.sortable === false ? column.label : <button type="button" className="table-sort"
                aria-label={`Trier par ${column.label}, ordre ${sort?.key === column.key && sort.direction === 'asc' ? 'décroissant' : 'croissant'}`}
                onClick={() => { setSort({ key: column.key, direction: sort?.key === column.key && sort.direction === 'asc' ? 'desc' : 'asc' }); setPage(1); }}>
                {column.label}<span aria-hidden="true">{sort?.key === column.key ? (sort.direction === 'asc' ? ' ↑' : ' ↓') : ' ↕'}</span>
              </button>}
            </th>)}
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
