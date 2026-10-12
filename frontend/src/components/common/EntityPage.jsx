/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useMemo, useState } from 'react';
import Button from './Button.jsx';
import Card from './Card.jsx';
import Input from './Input.jsx';
import Table from './Table.jsx';

export default function EntityPage({
  title,
  description,
  columns,
  fields = [],
  fetcher,
  creator,
  canCreate = false,
  createLabel = 'Enregistrer',
  renderActions,
  afterCreate,
  emptyForm
}) {
  const initialForm = useMemo(() => buildInitialForm(fields, emptyForm), [emptyForm, fields]);
  const [data, setData] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const result = await fetcher();
      setData(Array.isArray(result) ? result : []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [fetcher]);

  useEffect(() => {
    setForm(initialForm);
  }, [initialForm]);

  useEffect(() => {
    void load();
  }, [load]);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    try {
      const payload = normalizePayload(form, fields);
      await creator(payload);
      setForm(initialForm);
      setMessage('Enregistrement effectué.');
      await load();
      afterCreate?.();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page-stack">
      <Card>
        <div className="section-heading">
          <div>
            <h2>{title}</h2>
            {description && <p className="small">{description}</p>}
          </div>
          <Button variant="secondary" onClick={load} disabled={loading}>Actualiser</Button>
        </div>

        {canCreate && creator && (
          <form className="entity-form" onSubmit={handleSubmit}>
            {fields.map((field) => (
              <Input
                key={field.name}
                label={field.label}
                name={field.name}
                type={field.type}
                value={form[field.name] ?? ''}
                onChange={updateField}
                placeholder={field.placeholder}
                required={field.required}
                minLength={field.minLength}
                options={field.options}
              />
            ))}
            <Button type="submit" disabled={saving}>{saving ? 'Enregistrement...' : createLabel}</Button>
          </form>
        )}

        {!canCreate && creator && (
          <p className="notice">Votre rôle permet la consultation de ce module, sans modification.</p>
        )}

        {message && <p className="alert alert-success" role="status">{message}</p>}
        {error && <p className="alert alert-danger" role="alert">{error}</p>}

        <Table
          columns={columns}
          data={data}
          loading={loading}
          renderActions={renderActions ? (row) => renderActions(row, load) : undefined}
        />
      </Card>
    </div>
  );
}

function buildInitialForm(fields, emptyForm) {
  if (emptyForm) return emptyForm;

  return fields.reduce((acc, field) => {
    acc[field.name] = field.defaultValue ?? '';
    return acc;
  }, {});
}

function normalizePayload(form, fields) {
  return fields.reduce((payload, field) => {
    const value = form[field.name];
    if (value === '' || value === null || value === undefined) return payload;
    if (field.type === 'number') {
      payload[field.name] = Number(value);
    } else if (field.valueType === 'boolean') {
      payload[field.name] = value === true || value === 'true';
    } else {
      payload[field.name] = value;
    }
    return payload;
  }, {});
}
