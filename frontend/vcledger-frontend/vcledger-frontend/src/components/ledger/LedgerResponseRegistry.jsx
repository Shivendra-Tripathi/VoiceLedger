import CustomerSelectionCard from './responses/CustomerSelectionCard';
import TransactionConfirmationCard from './responses/TransactionConfirmationCard';
import CustomerBalanceCard from './responses/CustomerBalanceCard';
import FallbackResponseCard from './responses/FallbackResponseCard';

/**
 * ledgerResponseRegistry
 * ----------------------
 * Single source of truth mapping a backend `responseType` to the React
 * component that renders it. Adding a new interactive card is two steps:
 *
 *   1. write the component under ./responses/
 *   2. add one line here
 *
 * Nothing in VoiceLedgerPage changes.
 *
 * Keys are normalised to UPPER_SNAKE so the registry is tolerant of the
 * backend sending `SELECTION`, `selection`, or a future rename to
 * `SELECT_CUSTOMER` — all three resolve to the same card.
 */
const registry = {
  // Customer disambiguation. The sample payload uses "SELECTION"; the
  // alias is kept so a backend rename doesn't break the UI.
  SELECTION: CustomerSelectionCard,
  SELECT_CUSTOMER: CustomerSelectionCard,

  // Transaction confirmation. The sample payload uses "CONFIRMATION"; the
  // alias is kept so a backend rename to CONFIRM_TRANSACTION doesn't break the UI.
  CONFIRMATION: TransactionConfirmationCard,
  CONFIRM_TRANSACTION: TransactionConfirmationCard,

  // Customer balance lookup. The sample payload uses "INFORMATION"; the
  // alias is kept so a backend rename to CUSTOMER_BALANCE doesn't break the UI.
  CUSTOMER_BALANCE_INFORMATION: CustomerBalanceCard,
  CUSTOMER_BALANCE: CustomerBalanceCard,

  // Future cards — uncomment as they are built:
  // TRANSACTION_HISTORY: TransactionHistoryCard,
  // DELETE_TRANSACTION: DeleteTransactionCard,
};

const normalise = (type) => String(type ?? '').trim().toUpperCase();

/** Register a card at runtime (useful for tests or lazily loaded features). */
export function registerResponseComponent(responseType, Component) {
  registry[normalise(responseType)] = Component;
}

/** Look up the component for a responseType, falling back to a safe card. */
export function resolveResponseComponent(responseType) {
  return registry[normalise(responseType)] ?? FallbackResponseCard;
}

/** True when the backend sent something we know how to render interactively. */
export function isKnownResponseType(responseType) {
  return Boolean(registry[normalise(responseType)]);
}

export default registry;