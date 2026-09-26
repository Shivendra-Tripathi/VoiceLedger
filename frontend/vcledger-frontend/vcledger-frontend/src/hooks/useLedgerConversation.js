import { useCallback, useEffect, useRef, useState } from 'react';
import { resolveActionHandler } from '../components/ledger/ledgerActions';

/**
 * useLedgerConversation
 * ---------------------
 * Owns the ledger feed and the lifecycle of every interactive card in it.
 *
 * An entry is one of:
 *   { id, role: 'user',   content }                      — what the shopkeeper did
 *   { id, role: 'system', status: 'pending' }            — waiting on the backend
 *   { id, role: 'system', status: 'error', content }     — transport failure
 *   { id, role: 'system', status: 'done', response, uiState }
 *
 * `uiState` is the explicit state machine for an interactive card:
 *   { status: 'active' | 'submitting' | 'locked' | 'error', action, error }
 *
 * Persistence:
 *   The conversation is stored in sessionStorage.
 *
 *   This means:
 *   - Navigation between pages preserves the conversation.
 *   - Component unmount/remount preserves the conversation.
 *   - Page refresh preserves the conversation.
 *   - Closing the browser tab clears the conversation.
 *   - Opening the application in a new tab starts a new conversation.
 */

const STORAGE_KEY = 'vcledger:conversation:v1';

const createId = () =>
  globalThis.crypto?.randomUUID?.() ??
  `entry-${Date.now()}-${Math.random().toString(16).slice(2)}`;

const initialUiState = () => ({
  status: 'active',
  action: null,
  error: null,
});

/**
 * Load conversation from sessionStorage.
 *
 * Any operation that was still pending/submitting when the page was
 * previously unloaded is converted into an error state so the UI
 * never displays a permanently frozen spinner.
 */
function loadPersistedEntries() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);

    if (!raw) {
      return [];
    }

    const parsed = JSON.parse(raw);

    if (!Array.isArray(parsed)) {
      return [];
    }

    const lastResponseIndex = parsed.map((e) => Boolean(e.response)).lastIndexOf(true);

    return parsed.map((entry, index) => {
      // An older response card that wasn't locked becomes dormant
      if (
        entry.response &&
        index < lastResponseIndex &&
        entry.uiState?.status !== 'locked'
      ) {
        return {
          ...entry,
          uiState: {
            ...entry.uiState,
            status: 'dormant',
          },
        };
      }

      // A backend request may have been interrupted when the page
      // was unloaded. Don't restore it as permanently pending.
      if (
        entry.role === 'system' &&
        entry.status === 'pending'
      ) {
        return {
          ...entry,
          status: 'error',
          content:
            'Lost connection before this finished. Try again.',
        };
      }

      // An interactive card may have been submitting an action
      // when the page was unloaded.
      if (entry.uiState?.status === 'submitting') {
        return {
          ...entry,
          uiState: {
            ...entry.uiState,
            status: 'error',
            error:
              'This was interrupted before it finished. Try again.',
          },
        };
      }

      return entry;
    });
  } catch {
    return [];
  }
}

export function useLedgerConversation() {
  /**
   * sessionStorage is tab-scoped.
   *
   * Therefore this state survives route navigation and refresh,
   * while the browser automatically removes it when the tab is closed.
   */
  const [entries, setEntries] = useState(loadPersistedEntries);

  /**
   * Mirror of `entries` so async handlers can read the latest value
   * without re-creating callbacks on every render.
   */
  const entriesRef = useRef(entries);

  /**
   * Entry ids with a request in flight — duplicate-submit guard.
   */
  const inFlightRef = useRef(new Set());

  /**
   * Persist conversation whenever it changes.
   */
  useEffect(() => {
    try {
      sessionStorage.setItem(
        STORAGE_KEY,
        JSON.stringify(entries)
      );
    } catch {
      // Storage may be unavailable or full.
      // The application should continue working normally.
    }
  }, [entries]);

  const commit = useCallback((updater) => {
    setEntries((prev) => {
      const next = updater(prev);
      entriesRef.current = next;
      return next;
    });
  }, []);

  const patchEntry = useCallback(
    (entryId, patch) => {
      commit((prev) =>
        prev.map((entry) =>
          entry.id === entryId
            ? {
                ...entry,
                ...(typeof patch === 'function'
                  ? patch(entry)
                  : patch),
              }
            : entry
        )
      );
    },
    [commit]
  );

  const patchUiState = useCallback(
    (entryId, patch) => {
      patchEntry(entryId, (entry) => ({
        uiState: {
          ...(entry.uiState ?? initialUiState()),
          ...(typeof patch === 'function'
            ? patch(
                entry.uiState ?? initialUiState()
              )
            : patch),
        },
      }));
    },
    [patchEntry]
  );

  const appendUserEntry = useCallback(
    (content) => {
      const id = createId();

      commit((prev) => [
        ...prev,
        {
          id,
          role: 'user',
          content,
        },
      ]);

      return id;
    },
    [commit]
  );

  const appendPendingEntry = useCallback(() => {
    const id = createId();

    commit((prev) => [
      ...prev.map((entry) => {
        if (entry.response && entry.uiState?.status !== 'locked') {
          return {
            ...entry,
            uiState: {
              ...entry.uiState,
              status: 'dormant',
            },
          };
        }
        return entry;
      }),
      {
        id,
        role: 'system',
        status: 'pending',
      },
    ]);

    return id;
  }, [commit]);

  const appendResponseEntry = useCallback(
    (response) => {
      const id = createId();

      commit((prev) => [
        ...prev.map((entry) => {
          if (entry.response && entry.uiState?.status !== 'locked') {
            return {
              ...entry,
              uiState: {
                ...entry.uiState,
                status: 'dormant',
              },
            };
          }
          return entry;
        }),
        {
          id,
          role: 'system',
          status: 'done',
          response,
          uiState: initialUiState(),
        },
      ]);

      return id;
    },
    [commit]
  );

  /**
   * Turn a pending entry into a rendered backend response.
   */
  const resolveEntry = useCallback(
    (entryId, response) => {
      commit((prev) =>
        prev.map((entry) => {
          if (entry.id === entryId) {
            return {
              ...entry,
              status: 'done',
              response,
              uiState: initialUiState(),
            };
          }

          if (entry.response && entry.uiState?.status !== 'locked') {
            return {
              ...entry,
              uiState: {
                ...entry.uiState,
                status: 'dormant',
              },
            };
          }

          return entry;
        })
      );
    },
    [commit]
  );

  /**
   * Turn a pending entry into a plain error bubble.
   */
  const failEntry = useCallback(
    (entryId, message) => {
      patchEntry(entryId, {
        status: 'error',
        content: message,
      });
    },
    [patchEntry]
  );

  /**
   * Run a card's action through its handler and move that card through:
   *
   * active -> submitting -> locked
   *
   * or:
   *
   * active -> submitting -> error
   *
   * Any follow-up response returned by the backend is appended
   * as the next conversation entry.
   */
  const dispatchAction = useCallback(
    async (entryId, action) => {
      // Guard: Only the latest response card can be interacted with
      const latestResponseEntry = [...entriesRef.current]
        .reverse()
        .find((item) => Boolean(item.response));

      if (latestResponseEntry && latestResponseEntry.id !== entryId) {
        return;
      }

      const entry = entriesRef.current.find(
        (item) => item.id === entryId
      );

      if (!entry) {
        return;
      }

      const currentStatus = entry.uiState?.status;

      if (
        inFlightRef.current.has(entryId) ||
        currentStatus === 'submitting' ||
        currentStatus === 'locked' ||
        currentStatus === 'dormant'
      ) {
        return;
      }

      const handler = resolveActionHandler(action?.type);

      if (!handler) {
        patchUiState(entryId, {
          status: 'error',
          action,
          error: `No handler registered for ${
            action?.type ?? 'this action'
          }.`,
        });

        return;
      }

      inFlightRef.current.add(entryId);

      patchUiState(entryId, {
        status: 'submitting',
        action,
        error: null,
      });

      try {
        const nextResponse = await handler({
          action,
          entry,
        });

        patchUiState(entryId, {
          status: 'locked',
          action,
          error: null,
        });

        if (nextResponse) {
          appendResponseEntry(nextResponse);
        }
      } catch (err) {
        patchUiState(entryId, {
          status: 'error',
          action,
          error:
            err?.message ||
            'That action could not be completed. Try again.',
        });
      } finally {
        inFlightRef.current.delete(entryId);
      }
    },
    [appendResponseEntry, patchUiState]
  );

  return {
    entries,
    appendUserEntry,
    appendPendingEntry,
    appendResponseEntry,
    resolveEntry,
    failEntry,
    dispatchAction,
  };
}

export default useLedgerConversation;