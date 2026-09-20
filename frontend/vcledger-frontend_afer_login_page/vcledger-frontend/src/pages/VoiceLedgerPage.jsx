import { useEffect, useRef, useState } from 'react';
import { BookMarked } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import RecordButton from '../components/voice/RecordButton';
import SendButton from '../components/voice/SendButton';
import MessageBubble from '../components/voice/MessageBubble';
import { useAudioRecorder } from '../hooks/useAudioRecorder';
import { sendVoiceCommand } from '../api/voiceService';

/**
 * VoiceLedgerPage
 * -----------------
 * The page the shopkeeper lands on right after logging in (spec item 1.4).
 * Blank ledger page above, Record + Send controls fixed at the bottom
 * (spec items 1.2–1.3).
 *
 * Flow: press Record -> speak -> press Record again to stop -> press Send
 * -> clip is POSTed to /voicecommand/process. What comes back is shown as
 * a "Ledger entry" bubble; since the response contract isn't finalized yet
 * (per your note), it's currently just stringified JSON — swap the
 * `renderSystemContent` bit once that shape is confirmed.
 */
export default function VoiceLedgerPage() {
  const [messages, setMessages] = useState([]);
  const [isSending, setIsSending] = useState(false);
  const scrollRef = useRef(null);

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
  }, [messages]);

  const handleRecordClick = () => {
    if (isRecording) {
      stopRecording();
    } else {
      startRecording();
    }
  };

  const handleSend = async () => {
    if (!audioBlob) return;

    const userMessageId = crypto.randomUUID();
    const pendingId = crypto.randomUUID();

    setMessages((prev) => [
      ...prev,
      { id: userMessageId, role: 'user', content: 'Voice command sent' },
      { id: pendingId, role: 'system', status: 'pending' },
    ]);
    setIsSending(true);

    try {
      const responseData = await sendVoiceCommand(audioBlob);
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === pendingId
            ? { ...msg, status: 'done', content: renderSystemContent(responseData) }
            : msg
        )
      );
    } catch (err) {
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === pendingId
            ? { ...msg, status: 'error', content: 'The backend could not process that command. Try again.' }
            : msg
        )
      );
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
            {messages.length === 0 ? (
              <div className="flex flex-col items-center justify-center text-center gap-3 py-24 opacity-70">
                <BookMarked size={32} className="text-brass-dark" strokeWidth={1.5} />
                <p className="font-display text-lg text-ink">The page is blank — for now.</p>
                <p className="font-body text-sm text-ink-soft max-w-xs">
                  Press the mic, speak a transaction, and send it. It'll show up here.
                </p>
              </div>
            ) : (
              messages.map((msg) => <MessageBubble key={msg.id} {...msg} />)
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
 * Best-effort rendering of whatever /voicecommand/process returns, until
 * its real shape is defined. Handles plain strings and generic objects.
 */
function renderSystemContent(responseData) {
  if (typeof responseData === 'string') return responseData;
  if (responseData == null) return 'Done.';
  try {
    return JSON.stringify(responseData, null, 2);
  } catch {
    return 'Received a response from the backend.';
  }
}
