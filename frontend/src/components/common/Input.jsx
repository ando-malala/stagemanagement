import { useId } from 'react';

export default function Input({ label, name, type = 'text', options, error, hint, emptyOptionLabel = 'Sélectionner', ...props }) {
  const id = useId();
  const describedBy = [error && `${id}-error`, hint && `${id}-hint`].filter(Boolean).join(' ') || undefined;
  const controlProps = { ...props, id, name, 'aria-invalid': Boolean(error), 'aria-describedby': describedBy };
  return (
    <div className="field">
      <label htmlFor={id}>{label}{props.required && <span className="required-indicator"> *</span>}</label>
      {type === 'textarea' ? <textarea {...controlProps} /> : type === 'select' ? (
        <select {...controlProps}>
          <option value="">{emptyOptionLabel}</option>
          {options?.map((option) => (
            <option key={option.value ?? option} value={option.value ?? option} disabled={option.disabled}>{option.label ?? option}</option>
          ))}
        </select>
      ) : <input {...controlProps} type={type} />}
      {hint && <small id={`${id}-hint`}>{hint}</small>}
      {error && <small id={`${id}-error`} className="field-error" role="alert">{error}</small>}
    </div>
  );
}
