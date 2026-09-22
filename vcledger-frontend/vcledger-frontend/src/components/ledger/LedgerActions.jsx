import { confirmCustomer, confirmTransaction, rejectTransaction } from '../../api/ledgerActionService';

/**
 * ledgerActions
 * -------------
 * The mirror image of ledgerResponseRegistry: that one maps a backend
 * responseType -> component, this one maps a user action -> API call.
 *
 * Cards never import the API layer. They emit
 *   onAction({ type: LEDGER_ACTIONS.CUSTOMER_SELECTED, customerId })
 * and the conversation layer resolves the handler here. That keeps the
 * card reusable anywhere (including Storybook/tests) and keeps the page
 * free of per-response-type branching.
 *
 * A handler receives `{ action, entry }` and returns either:
 *   - the next backend response object (rendered as a new ledger entry), or
 *   - null/undefined (action succeeded, nothing further to render).
 * Throwing puts the originating card into its ERROR state.
 */
export const LEDGER_ACTIONS = {
  CUSTOMER_SELECTED: 'CUSTOMER_SELECTED',
  TRANSACTION_CONFIRMED: 'TRANSACTION_CONFIRMED',
  TRANSACTION_REJECTED: 'TRANSACTION_REJECTED',
};

const handlers = {
  [LEDGER_ACTIONS.CUSTOMER_SELECTED]: ({ action, entry }) =>
    confirmCustomer({
      operationId: entry?.response?.operationId,
      customerId: action.customerId,
    }),

  [LEDGER_ACTIONS.TRANSACTION_CONFIRMED]: ({ entry }) =>
    confirmTransaction({ operationId: entry?.response?.operationId }),

  [LEDGER_ACTIONS.TRANSACTION_REJECTED]: ({ entry }) =>
    rejectTransaction({ operationId: entry?.response?.operationId }),
};

export function registerActionHandler(actionType, handler) {
  handlers[actionType] = handler;
}

export function resolveActionHandler(actionType) {
  return handlers[actionType] ?? null;
}

export default handlers;