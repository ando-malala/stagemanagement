export function displayValue(value) {
  if (value === null || value === undefined || value === '') return '—';
  if (typeof value === 'boolean') return value ? 'Oui' : 'Non';
  if (typeof value === 'object') return value.libelle ?? value.code ?? '—';
  return String(value);
}

export function normalizeSearch(value) {
  return displayValue(value).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('fr');
}

export function selectTableRows(data, columns, query = '', filters = {}, sort = null) {
  const terms = String(query ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('fr').trim().split(/\s+/).filter(Boolean);
  const rows = (Array.isArray(data) ? data : []).filter(row => {
    const text = columns.map(column => normalizeSearch(column.searchValue ? column.searchValue(row) : row[column.key])).join(' ');
    return terms.every(term => text.includes(term)) && columns.every(column =>
      !filters[column.key] || displayValue(row[column.key]) === filters[column.key]);
  });
  const column = columns.find(item => item.key === sort?.key);
  if (!column || column.sortable === false) return rows;
  const direction = sort.direction === 'desc' ? -1 : 1;
  return rows.sort((a, b) => {
    const left = column.sortValue ? column.sortValue(a) : a[column.key];
    const right = column.sortValue ? column.sortValue(b) : b[column.key];
    // Missing values stay at the end in either direction.
    if (left == null || left === '') return right == null || right === '' ? 0 : 1;
    if (right == null || right === '') return -1;
    if (typeof left === 'number' && typeof right === 'number') return (left - right) * direction;
    return displayValue(left).localeCompare(displayValue(right), 'fr', { numeric: true, sensitivity: 'base' }) * direction;
  });
}
