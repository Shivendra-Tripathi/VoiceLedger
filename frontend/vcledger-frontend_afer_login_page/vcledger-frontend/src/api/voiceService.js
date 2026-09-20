import { apiClient } from './client';

/**
 * Sends a recorded voice command to the backend as multipart audio.
 *
 * POST /voicecommand/process
 *
 * The backend's response handling is intentionally left for you to wire up
 * later, per your note — this function just returns whatever comes back so
 * VoiceLedgerPage can decide what to do with it once that contract exists.
 *
 * @param {Blob} audioBlob - recorded audio, produced by useAudioRecorder
 * @returns {Promise<any>} raw backend response data
 */
export async function sendVoiceCommand(audioBlob) {
  const formData = new FormData();
  // "audio" is the most common multipart field name for this kind of
  // endpoint — rename this key if your @RequestParam expects something else.
  formData.append('audio', audioBlob, `command-${Date.now()}.webm`);

  const response = await apiClient.post('/voicecommand/process', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
}
