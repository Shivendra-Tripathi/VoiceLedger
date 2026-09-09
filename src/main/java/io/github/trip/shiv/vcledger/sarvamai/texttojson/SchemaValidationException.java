package io.github.trip.shiv.vcledger.sarvamai.texttojson;

import java.util.Collections;
import java.util.Set;
 
/**
 * Thrown when the JSON produced by the extraction step does not validate
 * against the caller-supplied JSON Schema (e.g. the model picked no matching
 * {@code oneOf} branch, or left out a required field).
 */
public class SchemaValidationException extends StructuredJsonExtractionException {
 
    private final Set<String> validationErrors;
 
    public SchemaValidationException(String message, Set<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(validationErrors);
    }
 
    /** Human-readable validation error messages, one per schema violation. */
    public Set<String> getValidationErrors() {
        return validationErrors;
    }
}
 