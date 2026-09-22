import { useMemo } from 'react';
import { Check, Loader2, RotateCcw, Users } from 'lucide-react';
import Avatar from '../../common/Avatar';
import Button from '../../common/Button';
import { LEDGER_ACTIONS } from '../ledgerActions';

/**
 * CustomerSelectionCard
 * ---------------------
 * Renders a SELECTION response so the shopkeeper can disambiguate which
 * customer a spoken command referred to.
 *
 * This component is *controlled*: it owns no selection state of its own.
 * `uiState` comes from the conversation layer, which means the locked
 * result survives re-mounts, list re-keys and any future persistence of
 * the ledger feed. The card only renders and reports.
 *
 * Status contract (uiState.status):
 *   'active'     — options selectable
 *   'submitting' — request in flight, everything disabled
 *   'locked'     — confirmed; collapses to the chosen customer, permanent
 *   'error'      — request failed; selection preserved, retry offered
 */

export const SELECTION_STATUS = {
  ACTIVE: 'active',
  SUBMITTING: 'submitting',
  LOCKED: 'locked',
  ERROR: 'error',
};

export default function CustomerSelectionCard({ response, uiState, onAction }) {
  const status = uiState?.status ?? SELECTION_STATUS.ACTIVE;
  const error = uiState?.error ?? null;
  const selectedId = uiState?.action?.customerId ?? null;

  const customers = Array.isArray(response?.customers) ? response.customers : [];
  console.log('CUSTOMERS:', customers);
  const selectedCustomer = useMemo(
    () => customers.find((customer) => customer.id === selectedId) ?? null,
    [customers, selectedId]
  );

  const isSubmitting = status === SELECTION_STATUS.SUBMITTING;
  const isLocked = status === SELECTION_STATUS.LOCKED;
  const isSelectable = status === SELECTION_STATUS.ACTIVE || status === SELECTION_STATUS.ERROR;

  const emitSelection = (customer) => {
    // Hard guard: even a double-click that lands mid-render can't get past
    // this, and the conversation layer guards again with an in-flight ref.
    if (!isSelectable || !customer) return;
    onAction?.({
      type: LEDGER_ACTIONS.CUSTOMER_SELECTED,
      customerId: customer.id,
      customerName: customer.name,
    });
  };

  const heading = isLocked
    ? 'Customer selected'
    : response?.message || 'Select the appropriate customer';

  return (
    <section
      className="surface-card animate-stamp p-5"
      aria-busy={isSubmitting}
      aria-live="polite"
    >
      <header className="flex items-start gap-3">
        <span
          className={[
            'flex h-9 w-9 shrink-0 items-center justify-center rounded-full',
            isLocked ? 'bg-credit-soft text-credit' : 'bg-brass/15 text-brass-dark',
          ].join(' ')}
        >
          {isLocked ? (
            <Check size={18} strokeWidth={2.25} />
          ) : (
            <Users size={18} strokeWidth={2.25} />
          )}
        </span>
        <div className="min-w-0">
          <h3 className="font-display text-lg font-semibold text-ink">{heading}</h3>
          {!isLocked && customers.length > 1 && (
            <p className="mt-0.5 font-body text-xs text-ink-soft">
              {customers.length} customers share this name — pick the right one.
            </p>
          )}
        </div>
      </header>

      <div className="brass-divider my-4" />

      {isLocked ? (
        <LockedSummary customer={selectedCustomer} fallbackName={uiState?.action?.customerName} />
      ) : (
        <CustomerOptions
          customers={customers}
          selectedId={selectedId}
          selectable={isSelectable}
          submitting={isSubmitting}
          onSelect={emitSelection}
        />
      )}

      {isSubmitting && (
        <p className="mt-4 flex items-center gap-2 font-body text-xs text-ink-soft">
          <Loader2 size={13} strokeWidth={2.25} className="animate-spin text-brass-dark" />
          Confirming {selectedCustomer?.name ?? uiState?.action?.customerName ?? 'selection'}…
        </p>
      )}

      {status === SELECTION_STATUS.ERROR && (
        <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <p className="font-body text-xs font-medium text-debit">
            {error || 'That selection could not be saved. Try again.'}
          </p>
          <Button
            variant="brass"
            icon={RotateCcw}
            onClick={() => emitSelection(selectedCustomer)}
            disabled={!selectedCustomer}
          >
            Try again
          </Button>
        </div>
      )}

      {isLocked && (
        <p className="mt-3 font-body text-xs text-ink-soft">
          Selection recorded for this entry.
        </p>
      )}
    </section>
  );
}

function CustomerOptions({ customers, selectedId, selectable, submitting, onSelect }) {
  if (customers.length === 0) {
    return (
      <div className="surface-pressed px-4 py-6 text-center">
        <p className="font-body text-sm text-ink">No matching customers.</p>
        <p className="mt-1 font-body text-xs text-ink-soft">
          Record the command again with the full name, or add the customer first.
        </p>
      </div>
    );
  }

  return (
    <div
      role="radiogroup"
      aria-label="Customers to choose from"
      className="grid grid-cols-2 gap-3 sm:grid-cols-3"
    >
      {customers.map((customer) => {
        const isSelected = customer.id === selectedId;
        return (
          <button
            key={customer.id}
            type="button"
            role="radio"
            aria-checked={isSelected}
            disabled={!selectable}
            onClick={() => onSelect(customer)}
            className={[
              'surface-pressed flex items-center gap-2.5 px-3 py-2.5 text-left',
              'transition-all duration-150 active:translate-y-px',
              'disabled:cursor-not-allowed disabled:opacity-50',
              isSelected ? 'ring-2 ring-brass' : '',
            ].join(' ')}
          >
            <Avatar name={customer.name} photoUrl={customer.photoUrl} size="sm" />
            <span className="min-w-0 flex-1">
              <span className="block truncate font-body text-sm font-semibold text-ink">
                {customer.name}
              </span>
              <span className="block font-body text-xs text-ink-soft">
                {formatAccountType(customer.type)} #{customer.id}
              </span>
            </span>
            {isSelected && submitting && (
              <Loader2 size={14} strokeWidth={2.25} className="shrink-0 animate-spin text-brass-dark" />
            )}
          </button>
        );
      })}
    </div>
  );
}

function LockedSummary({ customer, fallbackName }) {
  const name = customer?.name ?? fallbackName ?? 'Customer';
  return (
    <div className="surface-pressed flex items-center gap-3 px-4 py-3">
      <Avatar name={name} photoUrl={customer?.photoUrl ?? null} size="sm" />
      <span className="min-w-0 flex-1">
        <span className="block truncate font-body text-sm font-semibold text-ink">{name}</span>
        {customer && (
          <span className="block font-body text-xs text-ink-soft">
            {formatAccountType(customer.type)} #{customer.id}
          </span>
        )}
      </span>
      <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-credit-soft text-credit">
        <Check size={14} strokeWidth={2.25} />
      </span>
    </div>
  );
}

/** "CUSTOMER" -> "Customer" — sentence case, per the design system. */
function formatAccountType(type) {
  if (!type) return 'Account';
  const lower = String(type).toLowerCase().replace(/_/g, ' ');
  return lower.charAt(0).toUpperCase() + lower.slice(1);
}