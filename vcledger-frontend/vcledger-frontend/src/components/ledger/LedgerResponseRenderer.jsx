import { useCallback } from 'react';
import { resolveResponseComponent } from './ledgerResponseRegistry';

/**
 * LedgerResponseRenderer
 * ----------------------
 * The only place that turns a backend response into a component. It is
 * deliberately tiny: look up `entry.response.responseType`, render it,
 * and forward the card's actions up with the entry id attached.
 *
 * Every card receives the same three props, so the contract is uniform:
 *   response  — the raw backend payload for this entry
 *   uiState   — { status, action, error } owned by the conversation layer
 *   onAction  — (action) => void
 */
export default function LedgerResponseRenderer({ entry, onAction }) {
  const { response, uiState } = entry;
  const ResponseComponent = resolveResponseComponent(response?.responseType);

  const handleAction = useCallback(
    (action) => onAction?.(entry.id, action),
    [onAction, entry.id]
  );

  return (
    <ResponseComponent
      response={response}
      uiState={uiState}
      onAction={handleAction}
    />
  );
}