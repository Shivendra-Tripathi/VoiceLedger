import { Mic, Square } from 'lucide-react';

/**
 * RecordButton
 * -------------
 * The big graphical "Record Voice" control (spec item 1.2) — a brass-rimmed
 * button styled after a physical dictaphone record button: idle it sits
 * raised in brass, while recording it turns rust-red, sinks in slightly,
 * and gets a pulsing ring to make the "you are live" state unmistakable.
 */
export default function RecordButton({ isRecording, elapsedSeconds, onClick, disabled }) {
  const minutes = String(Math.floor(elapsedSeconds / 60)).padStart(2, '0');
  const seconds = String(elapsedSeconds % 60).padStart(2, '0');

  return (
    <div className="relative flex flex-col items-center">
      <button
        type="button"
        onClick={onClick}
        disabled={disabled}
        aria-label={isRecording ? 'Stop recording' : 'Record voice command'}
        aria-pressed={isRecording}
        className={`relative w-16 h-16 rounded-full flex items-center justify-center border-2 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed ${
          isRecording
            ? 'bg-gradient-to-b from-debit to-[#8f3113] border-[#7a2a10] shadow-pressed'
            : 'bg-gradient-to-b from-brass-light to-brass border-brass-dark shadow-raised active:shadow-pressed active:translate-y-px'
        }`}
      >
        {isRecording && (
          <span className="absolute inset-0 rounded-full bg-debit/60 animate-pulseRing" aria-hidden="true" />
        )}
        {isRecording ? (
          <Square size={22} className="text-paper-card relative" fill="currentColor" />
        ) : (
          <Mic size={24} className="text-maroon-dark relative" strokeWidth={2.25} />
        )}
      </button>

      {isRecording && (
        <span className="absolute -bottom-6 text-xs font-semibold text-debit tabular-nums">
          {minutes}:{seconds}
        </span>
      )}
    </div>
  );
}
