package io.github.trip.shiv.vcledger.sarvamai.voicetotext;

/**
 * Thrown when a transcription provider fails to process an audio file
 * (e.g. bad response, unsupported format, API error).
 */
public class TranscriptionException extends Exception {
 
    private static final long serialVersionUID = 1L;

	public TranscriptionException(String message) {
        super(message);
    }
 
    public TranscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
 