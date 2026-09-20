/**
 * Formats a balance as Indian Rupees, e.g. -450 -> "-₹450.00".
 * Negative = customer owes the shopkeeper. Positive = advance paid.
 */
export function formatCurrency(amount) {
  const value = Number(amount ?? 0);
  const formatted = new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(Math.abs(value));
  return value < 0 ? `-${formatted}` : formatted;
}

/** Formats an ISO timestamp as "15 Sep 2026, 4:18 PM". */
export function formatDateTime(isoString) {
  if (!isoString) return '';
  const date = new Date(isoString);
  return new Intl.DateTimeFormat('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(date);
}

/** Returns initials for an avatar fallback, e.g. "Ajeet Kumar" -> "AK". */
export function getInitials(name) {
  if (!name) return '?';
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('');
}
