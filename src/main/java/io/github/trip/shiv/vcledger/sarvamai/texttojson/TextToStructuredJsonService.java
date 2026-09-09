package io.github.trip.shiv.vcledger.sarvamai.texttojson;


import com.fasterxml.jackson.databind.JsonNode;
 
/**
 * Converts free-form natural language text into a structured JSON object that
 * conforms to a caller-supplied JSON Schema.
 *
 * <p>Typical flow in a voice-controlled ledger app:
 * <pre>
 *   speech --(STT)--&gt; text --(this service + schema)--&gt; structured JSON --&gt; backend command handler
 * </pre>
 *
 * The schema is expected to describe a discriminated union of possible intents
 * (e.g. via {@code oneOf}), each with an {@code intent} constant and a
 * {@code data} payload. The service does not know anything about ledger
 * semantics itself - all domain knowledge lives in the schema (and optionally
 * in domain instructions passed to the implementation).
 */
public interface TextToStructuredJsonService {
 
    /**
     * Extracts a structured JSON object from {@code text} that satisfies {@code schema}.
     *
     * @param text   the raw natural language input (e.g. transcribed speech)
     * @param schema a JSON Schema (draft-07 compatible) describing the allowed
     *               shape(s) of the output, typically a {@code oneOf} of intents
     * @return a {@link JsonNode} that conforms to {@code schema}
     * @throws StructuredJsonExtractionException if the text cannot be turned into
     *         a schema-conformant structure (LLM failure, no matching intent, etc.)
     * @throws SchemaValidationException if the model's output does not validate
     *         against the supplied schema
     * @throws IllegalArgumentException if {@code text} or {@code schema} is null/blank
     */
    JsonNode extractStructuredJson(String text, JsonNode schema);
}