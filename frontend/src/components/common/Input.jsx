export default function Input({
  label,
  name,
  value,
  onChange,
  type = 'text',
  placeholder,
  required,
  options,
  minLength
}) {
  if (type === 'textarea') {
    return (
      <label className="field">
        <span>{label}</span>
        <textarea
          name={name}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
        />
      </label>
    );
  }

  if (type === 'select') {
    return (
      <label className="field">
        <span>{label}</span>
        <select name={name} value={value} onChange={onChange} required={required}>
          <option value="">Sélectionner</option>
          {options?.map((option) => (
            <option key={option.value ?? option} value={option.value ?? option}>
              {option.label ?? option}
            </option>
          ))}
        </select>
      </label>
    );
  }

  return (
    <label className="field">
      <span>{label}</span>
      <input
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        minLength={minLength}
      />
    </label>
  );
}
