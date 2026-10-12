import EmptyState from './EmptyState.jsx';
import Loader from './Loader.jsx';

export default function Table({ columns, data, loading, renderActions }) {
  if (loading) return <Loader />;
  if (!data?.length) return <EmptyState />;

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.label}</th>
            ))}
            {renderActions && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {data.map((row) => (
            <tr key={row.id ?? JSON.stringify(row)}>
              {columns.map((column) => (
                <td key={column.key}>{column.render ? column.render(row) : (row[column.key] ?? '-')}</td>
              ))}
              {renderActions && <td>{renderActions(row)}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
