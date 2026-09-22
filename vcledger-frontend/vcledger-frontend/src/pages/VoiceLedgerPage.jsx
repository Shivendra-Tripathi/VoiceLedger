import { useEffect, useRef, useState } from 'react';
import { BookMarked } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import RecordButton from '../components/voice/RecordButton';
import SendButton from '../components/voice/SendButton';
import MessageBubble from '../components/voice/MessageBubble';
import LedgerResponseRenderer from '../components/ledger/LedgerResponseRenderer';
import { useAudioRecorder } from '../hooks/useAudioRecorder';
import { useLedgerConversation } from '../hooks/useLedgerConversation';
import { sendVoiceCommand } from '../api/voiceService';

/**
 * VoiceLedgerPage
 * -----------------
 * The page the shopkeeper lands on right after logging in (spec item 1.4).
 * Blank ledger page above, Record + Send controls fixed at the bottom
 * (spec items 1.2–1.3).
 *
 * Orchestrator only: it records audio, hands the clip to the API, and
 * appends whatever comes back to the conversation. It does not know what
 * a SELECTION response is — `LedgerResponseRenderer` resolves that from
 * the registry, so new response types need no change to this file.
 */
export default function VoiceLedgerPage() {
  const [isSending, setIsSending] = useState(false);
  const scrollRef = useRef(null);

  const {
    entries,
    appendUserEntry,
    appendPendingEntry,
    resolveEntry,
    failEntry,
    dispatchAction,
  } = useLedgerConversation();

  const {
    isRecording,
    elapsedSeconds,
    audioBlob,
    error: recordingError,
    startRecording,
    stopRecording,
    resetRecording,
  } = useAudioRecorder();

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
  }, [entries]);

  const handleRecordClick = () => {
    if (isRecording) {
      stopRecording();
    } else {
      startRecording();
    }
  };

  const handleSend = async () => {
    if (!audioBlob || isSending) return;

    appendUserEntry('Voice command sent');
    const pendingId = appendPendingEntry();
    setIsSending(true);

    try {
      const responseData = await sendVoiceCommand(audioBlob);
      resolveEntry(pendingId, normaliseResponse(responseData));
    } catch {
      failEntry(pendingId, 'The backend could not process that command. Try again.');
    } finally {
      setIsSending(false);
      resetRecording();
    }
  };

  return (
    <AppShell>
      <div className="h-full flex flex-col">
        {/* Blank ledger page / response area (spec item 1.3) */}
        <div ref={scrollRef} className="flex-1 overflow-y-auto px-4 sm:px-8 py-8">
          <div className="max-w-2xl mx-auto flex flex-col gap-4">
            {entries.length === 0 ? (
              <div className="flex flex-col items-center justify-center text-center gap-3 py-24 opacity-70">
                <BookMarked size={32} className="text-brass-dark" strokeWidth={1.5} />
                <p className="font-display text-lg text-ink">The page is blank — for now.</p>
                <p className="font-body text-sm text-ink-soft max-w-xs">
                  Press the mic, speak a transaction, and send it. It'll show up here.
                </p>
              </div>
            ) : (
              entries.map((entry) =>
                entry.response ? (
                  <LedgerResponseRenderer key={entry.id} entry={entry} onAction={dispatchAction} />
                ) : (
                  <MessageBubble key={entry.id} {...entry} />
                )
              )
            )}
          </div>
        </div>

        {/* Fixed bottom controls (spec item 1.2) */}
        <div className="border-t border-paper-line bg-paper-card/80 backdrop-blur-sm px-4 sm:px-8 py-5">
          <div className="max-w-2xl mx-auto flex items-center justify-center gap-6">
            <RecordButton
              isRecording={isRecording}
              elapsedSeconds={elapsedSeconds}
              onClick={handleRecordClick}
              disabled={isSending}
            />
            <SendButton onClick={handleSend} disabled={!audioBlob || isRecording} isSending={isSending} />
          </div>
          {recordingError && (
            <p className="text-center text-xs text-debit mt-3 font-medium">{recordingError}</p>
          )}
          {!recordingError && audioBlob && !isRecording && (
            <p className="text-center text-xs text-ink-soft mt-3">Recording ready — press send.</p>
          )}
        </div>
      </div>
    </AppShell>
  );
}

/**
 * The backend is expected to return an object carrying `responseType`.
 * If it ever returns a bare string, wrap it so the renderer still gets a
 * consistent shape and the fallback card can display it.
 */
function normaliseResponse(responseData) {
  if (responseData && typeof responseData === 'object') return responseData;
  if (typeof responseData === 'string') return { responseType: 'TEXT', message: responseData };
  return { responseType: 'TEXT', message: 'Done.' };
}