/**
 * EmptyState
 * -----------
 * Shared "nothing here yet" / error panel. Used for an empty customer
 * list, an empty transaction history, or a failed fetch — pass `tone`
 * to shift the accent color for genuine errors.
 */
export default function EmptyState({ icon: Icon, title, description, tone = 'neutral', action }) {
  const accent = tone === 'error' ? 'text-debit' : 'text-brass-dark';

  return (
    <div className="surface-card flex flex-col items-center text-center gap-3 py-14 px-6">
      {Icon && <Icon size={30} className={accent} strokeWidth={1.75} />}
      <h3 className="font-display text-lg font-semibold text-ink">{title}</h3>
      {description && <p className="font-body text-sm text-ink-soft max-w-sm">{description}</p>}
      {action}
    </div>
  );
}
