import { Users, Wallet } from 'lucide-react';
import Avatar from '../../common/Avatar';

/**
 * CustomerBalanceCard
 * --------------------
 * Renders an INFORMATION response carrying one or more customer balances
 * — typically several customers that matched the same spoken name (see
 * the sample: three "Ranjit"s). Unlike CustomerSelectionCard or
 * TransactionConfirmationCard, this card has no user action to take and
 * no lifecycle: it's read-only, so there's no `uiState` machine here.
 *
 * It still accepts the full { response, uiState, onAction } contract so
 * LedgerResponseRenderer doesn't need to special-case "informational"
 * cards — it just never calls onAction.
 *
 * Design: one balance is a single BalanceRow; several matches are
 * multiple BalanceRows stacked inside one surface-card "menu", per your
 * spec — a single entity, not N separate cards in the feed.
 */
export default function CustomerBalanceCard({ response }) {
  const customers = Array.isArray(response?.customers) ? response.customers : [];

  return (
    <section className="surface-card animate-stamp p-5" aria-live="polite">
      <header className="flex items-start gap-3">
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brass/15 text-brass-dark">
          <Wallet size={18} strokeWidth={2.25} />
        </span>
        <div className="min-w-0">
          <h3 className="font-display text-lg font-semibold text-ink">
            {response?.message || 'Balance'}
          </h3>
          {customers.length > 1 && (
            <p className="mt-0.5 font-body text-xs text-ink-soft">
              {customers.length} customers match this name.
            </p>
          )}
        </div>
      </header>

      <div className="brass-divider my-4" />

      {customers.length === 0 ? (
        <div className="surface-pressed px-4 py-6 text-center">
          <Users size={24} strokeWidth={1.75} className="mx-auto mb-2 text-ink-soft" />
          <p className="font-body text-sm text-ink">No matching customers.</p>
        </div>
      ) : (
        <div className="flex flex-col divide-y divide-paper-line">
          {customers.map(({ customer, balance }) => (
            <BalanceRow key={customer?.id ?? customer?.name} customer={customer} balance={balance} />
          ))}
        </div>
      )}
    </section>
  );
}

/** One customer's balance — the reusable "single customer" unit the merged card stacks. */
function BalanceRow({ customer, balance }) {
  const amount = Number(balance);
  const isZero = !amount;
  const isCredit = amount > 0; // owes the shopkeeper
  const isDebit = amount < 0; // advance paid / shopkeeper owes customer

  return (
    <div className="flex items-center gap-3 py-3 first:pt-0 last:pb-0">
      <Avatar name={customer?.name} photoUrl={customer?.photoUrl} size="sm" />
      <div className="min-w-0 flex-1">
        <p className="truncate font-body text-sm font-semibold text-ink">
          {customer?.name ?? 'Unknown customer'}
        </p>
        <p className="font-body text-xs text-ink-soft">
          {formatAccountType(customer?.type)} #{customer?.id ?? '—'}
        </p>
      </div>
      <div className="shrink-0 text-right">
        <p
          className={[
            'font-display text-sm font-semibold',
            isZero ? 'text-ink' : isCredit ? 'text-credit' : 'text-debit',
          ].join(' ')}
        >
          {formatAmount(Math.abs(amount))}
        </p>
        <p className="font-body text-xs text-ink-soft">
          {isZero ? 'Settled' : isCredit ? 'Owes' : 'Advance paid'}
        </p>
      </div>
    </div>
  );
}

function formatAccountType(type) {
  if (!type) return 'Account';
  const lower = String(type).toLowerCase().replace(/_/g, ' ');
  return lower.charAt(0).toUpperCase() + lower.slice(1);
}

function formatAmount(amount) {
  if (Number.isNaN(amount)) return '—';
  return `₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}