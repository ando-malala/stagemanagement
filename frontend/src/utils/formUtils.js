export function buildInitialForm(fields, emptyForm) {
  return emptyForm ?? Object.fromEntries(fields.map((field) => [field.name, field.defaultValue ?? '']));
}

export function normalizePayload(form, fields) {
  return fields.reduce((payload, field) => {
    const value = form[field.name];
    if (value === '' || value === null || value === undefined) return payload;
    if (field.type === 'number' || field.valueType === 'number') {
      const number = Number(value);
      if (!Number.isFinite(number)) throw new Error(`${field.label} doit être un nombre valide.`);
      payload[field.name] = number;
    } else if (field.valueType === 'boolean') {
      payload[field.name] = value === true || value === 'true';
    } else {
      payload[field.name] = value;
    }
    return payload;
  }, {});
}
