import { Send, Loader2 } from 'lucide-react';

/**
 * SendButton
 * -----------
 * The graphical "Send" control (spec item 1.2) that ships the recorded
 * clip to the backend. Disabled until a recording exists.
 */
export default function SendButton({ onClick, disabled, isSending }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled || isSending}
      aria-label="Send voice command"
      className="w-14 h-14 rounded-full flex items-center justify-center bg-gradient-to-b from-maroon-light to-maroon border-2 border-maroon-dark shadow-raised active:shadow-pressed active:translate-y-px transition-all duration-150 disabled:opacity-40 disabled:cursor-not-allowed"
    >
      {isSending ? (
        <Loader2 size={20} className="text-paper-card animate-spin" />
      ) : (
        <Send size={20} className="text-paper-card" strokeWidth={2.25} />
      )}
    </button>
  );
}
