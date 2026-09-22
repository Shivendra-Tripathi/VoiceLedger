export default function LedgerField({
  label,
  type = 'text',
  name,
  value,
  onChange,
  autoComplete,
  error,
  required = true,
}) {
  return (
    <label className="ledger-field">
      <span className="ledger-field__label">{label}</span>
      <input
        className={`ledger-field__input${error ? ' ledger-field__input--error' : ''}`}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
        required={required}
      />
      {error ? <span className="ledger-field__error">{error}</span> : null}
    </label>
  )
}
