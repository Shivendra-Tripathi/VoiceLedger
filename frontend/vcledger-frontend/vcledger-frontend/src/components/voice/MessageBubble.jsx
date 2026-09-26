import { Mic, BookOpen, AlertTriangle } from 'lucide-react';

/**
 * MessageBubble
 * --------------
 * One entry in the voice-command thread (spec item 1.3 — "visual responses
 * sent by the backend"). `role` is 'user' (the shopkeeper's spoken command)
 * or 'system' (whatever comes back from /voicecommand/process).
 *
 * The system response shape isn't finalized yet (per your note), so this
 * renders three simple states: pending, plain text/JSON, or error — swap
 * the "system" branch for real structured rendering once that contract
 * is defined.
 */
export default function MessageBubble({ role, status = 'done', content }) {
  const isUser = role === 'user';

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'} animate-stamp`}>
      <div
        className={`max-w-[85%] sm:max-w-[70%] rounded-2xl px-4 py-3 border ${
          isUser
            ? 'bg-gradient-to-b from-maroon-light to-maroon text-paper-card border-maroon-dark shadow-raised-sm'
            : 'surface-card'
        }`}
      >
        <div className="flex items-center gap-2 mb-1 opacity-80">
          {isUser ? <Mic size={13} /> : status === 'error' ? <AlertTriangle size={13} /> : <BookOpen size={13} />}
          <span className="text-[11px] font-semibold uppercase tracking-wide">
            {isUser ? 'You said' : status === 'error' ? 'Could not process' : 'Ledger entry'}
          </span>
        </div>

        {status === 'pending' ? (
          <div className="flex items-center gap-1.5 py-0.5">
            <span className="w-1.5 h-1.5 rounded-full bg-current animate-bounce [animation-delay:-0.3s]" />
            <span className="w-1.5 h-1.5 rounded-full bg-current animate-bounce [animation-delay:-0.15s]" />
            <span className="w-1.5 h-1.5 rounded-full bg-current animate-bounce" />
          </div>
        ) : (
          <p className={`font-body text-sm leading-relaxed ${isUser ? 'text-paper-card' : 'text-ink'}`}>
            {content}
          </p>
        )}
      </div>
    </div>
  );
}
