import { useCallback, useRef, useState } from 'react';

/**
 * useAudioRecorder
 * -----------------
 * Wraps the browser's MediaRecorder API behind a small, page-friendly
 * interface: call `startRecording()`, then `stopRecording()`, and read
 * back `audioBlob` once it resolves. `elapsedSeconds` powers the little
 * timer shown on the Record button while it's active.
 *
 * Kept as its own hook (rather than inline in VoiceLedgerPage) so the
 * recording logic can be reused or unit-tested independently of the UI.
 */
export function useAudioRecorder() {
  const [isRecording, setIsRecording] = useState(false);
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [audioBlob, setAudioBlob] = useState(null);
  const [error, setError] = useState(null);

  const mediaRecorderRef = useRef(null);
  const chunksRef = useRef([]);
  const timerRef = useRef(null);
  const streamRef = useRef(null);

  const startRecording = useCallback(async () => {
    setError(null);
    setAudioBlob(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      chunksRef.current = [];

      const recorder = new MediaRecorder(stream);
      mediaRecorderRef.current = recorder;

      recorder.ondataavailable = (event) => {
        if (event.data.size > 0) chunksRef.current.push(event.data);
      };

      recorder.onstop = () => {
        const blob = new Blob(chunksRef.current, { type: 'audio/webm' });
        setAudioBlob(blob);
        streamRef.current?.getTracks().forEach((track) => track.stop());
      };

      recorder.start();
      setIsRecording(true);
      setElapsedSeconds(0);
      timerRef.current = setInterval(() => {
        setElapsedSeconds((prev) => prev + 1);
      }, 1000);
    } catch (err) {
      setError('Microphone access was denied or is unavailable.');
    }
  }, []);

  const stopRecording = useCallback(() => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();
    }
    setIsRecording(false);
    clearInterval(timerRef.current);
  }, []);

  const resetRecording = useCallback(() => {
    setAudioBlob(null);
    setElapsedSeconds(0);
  }, []);

  return {
    isRecording,
    elapsedSeconds,
    audioBlob,
    error,
    startRecording,
    stopRecording,
    resetRecording,
  };
}
