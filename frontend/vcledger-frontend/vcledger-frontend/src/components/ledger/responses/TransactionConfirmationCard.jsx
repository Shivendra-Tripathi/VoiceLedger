import { Check, Loader2, RotateCcw, X, ArrowRight, ArrowLeft } from 'lucide-react';
import Avatar from '../../common/Avatar';
import Button from '../../common/Button';
import { LEDGER_ACTIONS } from '../LedgerActions';
import { useAuth } from '../../../context/AuthContext';

/**
 * TransactionConfirmationCard
 * ----------------------------
 * Renders a CONFIRMATION response: "is this transaction correct?" with
 * exactly two actions, Confirm and Reject.
 *
 * Controlled the same way as CustomerSelectionCard — no local state, all
 * lifecycle lives in `uiState` on the conversation entry, so a locked
 * decision can never become editable again after a re-render.
 *
 * VcLedger semantics (NOT payment-app semantics):
 *   CREDIT → customer owes more / goods were taken (shopkeeper gave value)
 *   DEBIT  → customer paid the shopkeeper
 * The arrow always points the way the money/goods actually moved, per
 * `transaction.moneyDirection`.
 *
 * Status contract (uiState.status):
 *   'active'     — Confirm/Reject both enabled
 *   'submitting' — request in flight, everything disabled
 *   'locked'     — decision recorded; permanent
 *   'error'      — request failed; decision preserved, retry offered
 */

export const CONFIRMATION_STATUS = {
  ACTIVE: 'active',
  SUBMITTING: 'submitting',
  LOCKED: 'locked',
  ERROR: 'error',
  DORMANT: 'dormant',
};

export default function TransactionConfirmationCard({ response, uiState, onAction, isDormant = false }) {
  const status = uiState?.status ?? CONFIRMATION_STATUS.ACTIVE;
  const error = uiState?.error ?? null;
  const chosenType = uiState?.action?.type ?? null;
  const isDormantCard = isDormant || status === CONFIRMATION_STATUS.DORMANT;

  const { shopkeeper, customer, amount, moneyDirection } = response?.transaction ?? {};
  const movesToShopkeeper = moneyDirection === 'CUSTOMER_TO_SHOPKEEPER';
  // CREDIT/DEBIT derived from direction per VcLedger's semantics, not stated by the backend.
  const ledgerType = movesToShopkeeper ? 'DEBIT' : 'CREDIT';

  const isSubmitting = status === CONFIRMATION_STATUS.SUBMITTING;
  const isLocked = status === CONFIRMATION_STATUS.LOCKED;
  const isSelectable = !isDormantCard && (status === CONFIRMATION_STATUS.ACTIVE || status === CONFIRMATION_STATUS.ERROR);

  const emit = (type) => {
    // Hard guard: cannot confirm/reject if dormant or submitting or locked
    if (!isSelectable) return;
    onAction?.({ type });
  };

  const heading = isLocked
    ? chosenType === LEDGER_ACTIONS.TRANSACTION_CONFIRMED
      ? 'Transaction confirmed'
      : 'Transaction rejected'
    : response?.message || 'Confirm the transaction';

  return (
    <section
      className={`surface-card p-5 ${
        isDormantCard && !isLocked ? 'opacity-70 bg-paper/50' : 'animate-stamp'
      }`}
      aria-busy={isSubmitting}
      aria-live="polite"
    >
      <header className="flex items-start gap-3">
        <span
          className={[
            'flex h-9 w-9 shrink-0 items-center justify-center rounded-full',
            isLocked
              ? chosenType === LEDGER_ACTIONS.TRANSACTION_CONFIRMED
                ? 'bg-credit-soft text-credit'
                : 'bg-debit-soft text-debit'
              : 'bg-brass/15 text-brass-dark',
          ].join(' ')}
        >
          {isLocked ? (
            chosenType === LEDGER_ACTIONS.TRANSACTION_CONFIRMED ? (
              <Check size={18} strokeWidth={2.25} />
            ) : (
              <X size={18} strokeWidth={2.25} />
            )
          ) : movesToShopkeeper ? (
            <ArrowLeft size={18} strokeWidth={2.25} />
          ) : (
            <ArrowRight size={18} strokeWidth={2.25} />
          )}
        </span>
        <div className="min-w-0">
          <h3 className="font-display text-lg font-semibold text-ink">{heading}</h3>
          <p className="mt-0.5 font-body text-xs text-ink-soft">
            {isDormantCard && !isLocked
              ? 'Dormant — not completed'
              : ledgerType === 'CREDIT'
              ? 'Customer owes more'
              : 'Customer paid'}
          </p>
        </div>
      </header>

      <div className="brass-divider my-4" />

      <TransactionSummary
        shopkeeper={shopkeeper}
        customer={customer}
        amount={amount}
        movesToShopkeeper={movesToShopkeeper}
        ledgerType={ledgerType}
      />

      {!isLocked && (
        isDormantCard ? (
          <div className="mt-4 p-2.5 rounded-xl bg-paper/80 border border-paper-line text-center">
            <p className="font-body text-xs text-ink-soft italic">
              Dormant — superseded by a newer action.
            </p>
          </div>
        ) : (
          <div className="mt-4 flex gap-3">
            <Button
              variant="danger"
              icon={X}
              className="flex-1 justify-center"
              disabled={!isSelectable}
              onClick={() => emit(LEDGER_ACTIONS.TRANSACTION_REJECTED)}
            >
              {isSubmitting && chosenType === LEDGER_ACTIONS.TRANSACTION_REJECTED ? (
                <Loader2 size={16} strokeWidth={2.25} className="animate-spin" />
              ) : (
                'Reject'
              )}
            </Button>
            <Button
              variant="brass"
              icon={Check}
              className="flex-1 justify-center"
              disabled={!isSelectable}
              onClick={() => emit(LEDGER_ACTIONS.TRANSACTION_CONFIRMED)}
            >
              {isSubmitting && chosenType === LEDGER_ACTIONS.TRANSACTION_CONFIRMED ? (
                <Loader2 size={16} strokeWidth={2.25} className="animate-spin" />
              ) : (
                'Confirm'
              )}
            </Button>
          </div>
        )
      )}

      {isSubmitting && (
        <p className="mt-3 flex items-center gap-2 font-body text-xs text-ink-soft">
          <Loader2 size={13} strokeWidth={2.25} className="animate-spin text-brass-dark" />
          {chosenType === LEDGER_ACTIONS.TRANSACTION_CONFIRMED ? 'Confirming' : 'Rejecting'}
          &nbsp;transaction…
        </p>
      )}

      {status === CONFIRMATION_STATUS.ERROR && (
        <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <p className="font-body text-xs font-medium text-debit">
            {error || 'That decision could not be saved. Try again.'}
          </p>
          <Button variant="brass" icon={RotateCcw} onClick={() => emit(chosenType)}>
            Try again
          </Button>
        </div>
      )}

      {isLocked && (
        <p className="mt-3 font-body text-xs text-ink-soft">Decision recorded for this entry.</p>
      )}
    </section>
  );
}

function TransactionSummary({ shopkeeper, customer, amount, movesToShopkeeper, ledgerType }) {
  const { user } = useAuth();
  const shopkeeperWithPhoto = {
    ...shopkeeper,
    name: shopkeeper?.name || user?.name,
    photoUrl: shopkeeper?.photoUrl || user?.photoUrl,
  };

  return (
    <div className="surface-pressed flex items-center justify-between gap-3 px-4 py-4">
      <Party person={shopkeeperWithPhoto} label="Shopkeeper" />

      <div className="flex flex-col items-center gap-1 px-2">
        {movesToShopkeeper ? (
          <ArrowLeft size={20} strokeWidth={2.25} className="text-brass-dark" />
        ) : (
          <ArrowRight size={20} strokeWidth={2.25} className="text-brass-dark" />
        )}
        <span
          className={[
            'font-display text-base font-semibold',
            ledgerType === 'CREDIT' ? 'text-credit' : 'text-debit',
          ].join(' ')}
        >
          {formatAmount(amount)}
        </span>
      </div>

      <Party person={customer} label="Customer" align="right" />
    </div>
  );
}

function Party({ person, label, align = 'left' }) {
  return (
    <div className={['flex min-w-0 flex-col items-center gap-1.5 text-center'].join(' ')}>
      <Avatar name={person?.name} photoUrl={person?.photoUrl} size="md" />
      <span className="max-w-[6.5rem] truncate font-body text-sm font-semibold text-ink">
        {person?.name ?? label}
      </span>
      <span className="font-body text-xs text-ink-soft">{label}</span>
    </div>
  );
}

function formatAmount(amount) {
  const value = Number(amount);
  if (Number.isNaN(value)) return String(amount ?? '—');
  return `₹${value.toLocaleString('en-IN')}`;
}