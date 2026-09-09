package io.github.trip.shiv.vcledger.sarvamai.texttojson;

/**
 * Base exception for all failures that occur while turning text into a
 * schema-conformant structured JSON object.
 */
public class StructuredJsonExtractionException extends RuntimeException {
 
    public StructuredJsonExtractionException(String message) {
        super(message);
    }
 
    public StructuredJsonExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
 