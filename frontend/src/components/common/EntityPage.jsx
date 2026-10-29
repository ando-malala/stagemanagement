/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useMemo, useState } from 'react';
import { useFetch } from '../../hooks/useFetch.js';
import { buildInitialForm, normalizePayload } from '../../utils/formUtils.js';
import ErrorMessage from './ErrorMessage.jsx';
import Button from './Button.jsx';
import Card from './Card.jsx';
import Input from './Input.jsx';
import Table from './Table.jsx';

const EMPTY_FIELDS = Object.freeze([]);

export default function EntityPage({
  title,
  description,
  columns,
  fields = EMPTY_FIELDS,
  fetcher,
  creator,
  canCreate = false,
  createLabel = 'Enregistrer',
  renderActions,
  afterCreate,
  emptyForm,
  renderCreate
}) {
  const initialForm = useMemo(() => buildInitialForm(fields, emptyForm), [emptyForm, fields]);
  const { data, loading, error: loadError, reload: load } = useFetch(fetcher);
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    setForm(initialForm);
  }, [initialForm]);

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
      await afterCreate?.();
    } catch (e) {
      setError(e);
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

        {canCreate && renderCreate?.(load)}
        {canCreate && creator && !renderCreate && (
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
                maxLength={field.maxLength}
                min={field.min}
                max={field.max}
                step={field.step}
                pattern={field.pattern}
                autoComplete={field.autoComplete}
                disabled={saving || field.disabled}
                error={error?.fieldErrors?.[field.name]}
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
        <ErrorMessage error={error || loadError} />
        {!loading && !loadError && data != null && !Array.isArray(data) && <ErrorMessage error="Le serveur n’a pas renvoyé la liste attendue." />}

        <Table
          columns={columns}
          data={Array.isArray(data) ? data : []}
          error={loadError || (data != null && !Array.isArray(data))}
          caption={title}
          loading={loading}
          renderActions={renderActions ? (row) => renderActions(row, load) : undefined}
        />
      </Card>
    </div>
  );
}
