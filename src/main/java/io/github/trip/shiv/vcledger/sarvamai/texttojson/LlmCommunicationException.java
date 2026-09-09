package io.github.trip.shiv.vcledger.sarvamai.texttojson;
/**
 * Thrown when the underlying LLM (Sarvam AI Chat Completions API) call fails outright:
 * network error, non-2xx response, missing/malformed tool-use block, etc.
 * This is distinct from {@link SchemaValidationException}, which is thrown
 * when the LLM responded but its output does not satisfy the schema.
 */
public class LlmCommunicationException extends StructuredJsonExtractionException {
 
    public LlmCommunicationException(String message) {
        super(message);
    }
 
    public LlmCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
 