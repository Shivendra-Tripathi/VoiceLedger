/**
 * Button
 * -------
 * The one embossed-button component the whole app shares. Three variants:
 *   - "brass"   primary actions (Create, Save)
 *   - "maroon"  actions inside the dark sidebar
 *   - "ghost"   low-emphasis actions (Cancel, secondary links)
 *   - "danger"  destructive actions (Delete customer)
 *
 * Every variant uses the same raised/pressed shadow language so buttons
 * feel like physical embossed surfaces rather than flat SaaS chips.
 */
const VARIANTS = {
  brass:
    'bg-gradient-to-b from-brass-light to-brass text-maroon-dark shadow-raised-sm hover:from-brass hover:to-brass-dark active:shadow-pressed active:translate-y-px border border-brass-dark/40',
  maroon:
    'bg-gradient-to-b from-maroon-light to-maroon text-paper-card shadow-raised-sm hover:to-maroon-dark active:shadow-pressed active:translate-y-px border border-maroon-dark/60',
  ghost:
    'bg-transparent text-ink-soft hover:bg-paper-line/50 border border-transparent',
  danger:
    'bg-gradient-to-b from-debit to-[#8f3113] text-paper-card shadow-raised-sm hover:brightness-105 active:shadow-pressed active:translate-y-px border border-[#7a2a10]',
};

export default function Button({
  children,
  variant = 'brass',
  type = 'button',
  disabled = false,
  onClick,
  className = '',
  icon: Icon,
}) {
  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={`inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 font-body font-semibold text-sm transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed disabled:active:translate-y-0 ${VARIANTS[variant]} ${className}`}
    >
      {Icon && <Icon size={17} strokeWidth={2.25} />}
      {children}
    </button>
  );
}
