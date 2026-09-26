import { FileQuestion } from 'lucide-react';

/**
 * FallbackResponseCard
 * --------------------
 * Rendered when the backend sends a responseType no card is registered
 * for. In development it shows the raw payload so you can build the real
 * card against it; in production it degrades to the backend's message.
 *
 * This is what makes the registry safe to ship ahead of the backend: a
 * new response type never blanks the ledger page.
 */
export default function FallbackResponseCard({ response }) {
  const message = typeof response?.message === 'string' ? response.message : 'Done.';
  const isDev = Boolean(import.meta?.env?.DEV);

  return (
    <section className="surface-card animate-stamp p-5">
      <div className="flex items-start gap-3">
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brass/15 text-brass-dark">
          <FileQuestion size={18} strokeWidth={2.25} />
        </span>
        <div className="min-w-0 flex-1">
          <p className="font-body text-sm text-ink">{message}</p>
          {isDev && response?.responseType && (
            <p className="mt-1 font-body text-xs text-ink-soft">
              No card registered for {response.responseType}.
            </p>
          )}
        </div>
      </div>

      {isDev && response && (
        <pre className="surface-pressed mt-4 overflow-x-auto px-4 py-3 font-body text-xs text-ink-soft">
          {safeStringify(response)}
        </pre>
      )}
    </section>
  );
}

function safeStringify(value) {
  try {
    return JSON.stringify(value, null, 2);
  } catch {
    return 'Received a response from the backend.';
  }
}