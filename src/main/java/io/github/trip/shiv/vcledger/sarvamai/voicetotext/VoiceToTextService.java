package io.github.trip.shiv.vcledger.sarvamai.voicetotext;

import java.io.File;
import java.io.IOException;
 
/**
 * Service abstraction for converting an audio file into transcribed text.
 * Implementations may call a cloud API (Whisper, Google Speech-to-Text, etc.)
 * or a local/offline speech recognition engine.
 */
public interface VoiceToTextService {
 
    /**
     * Transcribes the given audio file into text.
     *
     * @param audioFile the audio file to transcribe (e.g. mp3, wav, m4a)
     * @return the transcribed text
     * @throws IOException              if the file cannot be read or the network call fails
     * @throws TranscriptionException   if the transcription provider returns an error
     */
    /**
     * Transcribes the given audio file into text.
     *
     * @param audioFile the audio file to transcribe (e.g. mp3, wav, m4a)
     * @return the transcribed text
     * @throws IOException              if the file cannot be read or the network call fails
     * @throws TranscriptionException   if the transcription provider returns an error
     */
    String transcribe(File audioFile) throws IOException, TranscriptionException;
 
    /**
     * Transcribes audio held entirely in memory — no temp file needed.
     * Useful for web upload handlers (e.g. Spring's MultipartFile) where
     * the bytes are already available.
     *
     * @param audioBytes the raw audio bytes
     * @param filename   original filename (its extension is used to infer
     *                   content type; the rest of the name does not matter)
     * @return the transcribed text
     * @throws IOException              if the network call fails
     * @throws TranscriptionException   if the transcription provider returns an error
     */
    String transcribe(byte[] audioBytes, String filename) throws IOException, TranscriptionException;
}