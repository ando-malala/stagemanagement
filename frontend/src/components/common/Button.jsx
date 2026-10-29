export default function Button({
  children,
  type = 'button',
  variant = 'primary',
  onClick,
  disabled,
  className = '',
  ...props
}) {
  return (
    <button
      {...props}
      type={type}
      className={`btn btn-${variant} ${className}`}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
}
