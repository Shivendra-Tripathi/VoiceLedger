package io.github.trip.shiv.vcledger.sarvamai.texttojson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
 
/**
 * Default implementation of {@link TextToStructuredJsonService}, backed by the
 * <a href="https://docs.sarvam.ai">Sarvam AI</a> Chat Completions API's native
 * <b>Structured Outputs</b> feature (no tool/function calling involved).
 *
 * <p>Strategy: the caller-supplied JSON Schema is passed directly as
 * {@code response_format.json_schema.schema} with {@code "strict": true}. Sarvam
 * then guarantees the model's reply is valid JSON conforming to that schema -
 * the reply itself (in {@code choices[0].message.content}) is the structured
 * object, not a function-call argument. The result is still validated locally
 * against the original schema before being returned, as a defensive check.
 *
 * <p>Structured Outputs on Sarvam is currently served only by
 * {@code sarvam-105b} / {@code sarvam-105b-conversations} on the
 * {@code /v1/chat/completions} endpoint, so both are configurable but default
 * to that combination.
 *
 * <p>Thread-safe and stateless aside from configuration - a single instance
 * can be shared/reused (e.g. as a Spring singleton bean).
 */
@Service
public class TextToStructuredJsonServiceImpl implements TextToStructuredJsonService {
 
	 private static final Logger log = LoggerFactory.getLogger(TextToStructuredJsonServiceImpl.class);

    private static final String DEFAULT_API_URL = "https://api.sarvam.ai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "sarvam-105b";
    private static final String SCHEMA_NAME = "extract_structured_data";
 
    private static final String BASE_SYSTEM_PROMPT ="""
    		You are an intent extractor for a shopkeeper's voice-controlled ledger.

    		SPEAKER AND PERSON RULES:
    		- The speaker is ALWAYS the shopkeeper.
    		- "I", "me", "my" and "we" ALWAYS refer to the shopkeeper.
    		- Any explicitly named person ALWAYS refers to a customer, never the shopkeeper.
    		- Never interpret a named customer as the shopkeeper.
    		- If the actor is omitted, assume the shopkeeper is the actor when the sentence naturally describes the shopkeeper performing an action.
    		- Passive constructions must also be interpreted from the shopkeeper's perspective.

    		TRANSACTION RULE:
    		Determine DEBIT or CREDIT based on the direction of money relative to the shopkeeper.

    		DEBIT = money goes FROM the shopkeeper TO the customer.

    		Examples:
    		"I gave Ranjit 500" -> DEBIT
    		"Gave 500 to Ranjit" -> DEBIT
    		"I paid Ranjit 500" -> DEBIT
    		"Paid Ranjit 500" -> DEBIT
    		"I lent Ranjit 500" -> DEBIT
    		"Ranjit was given 500" -> DEBIT
    		"Ranjit got 500" -> DEBIT
    		"Ranjit received 500" -> DEBIT
    		"500 was given to Ranjit" -> DEBIT

    		These sentences imply that the shopkeeper gave the money to Ranjit.

    		CREDIT = money comes FROM the customer TO the shopkeeper.

    		Examples:
    		"Ranjit gave me 500" -> CREDIT
    		"Ranjit paid me 500" -> CREDIT
    		"Ranjit gave 500 to me" -> CREDIT
    		"I received 500 from Ranjit" -> CREDIT
    		"Received 500 from Ranjit" -> CREDIT
    		"Ranjit paid 500" -> CREDIT

    		IMPORTANT:
    		For sentences such as "Ranjit got 500", "Ranjit received 500", or "Ranjit was given 500", the named person is the customer and the shopkeeper is the implicit giver. Therefore the transaction is DEBIT.

    		Never determine DEBIT/CREDIT from the customer's perspective.
    		Always determine it from the shopkeeper's perspective.

    		Classify exactly one intent.
    		Extract only information supported by the command.
    		Do not invent customer names or amounts.
    		Set irrelevant fields to null.
    		""";
 
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final String domainInstructions;
 
    /**
     * @param apiKey             Sarvam API subscription key. If null/blank, falls back to the
     *                           {@code SARVAM_API_KEY} environment variable.
     * @param apiUrl             full Chat Completions endpoint, e.g.
     *                           {@code "https://api.sarvam.ai/v1/chat/completions"}.
     *                           If null/blank, defaults to that same URL.
     * @param model              model id to use, e.g. {@code "sarvam-105b"} or
     *                           {@code "sarvam-105b-conversations"}. If null/blank,
     *                           a sensible default is used.
     * @param domainInstructions optional extra business-rule instructions appended to the
     *                           system prompt, e.g. how to decide CREDIT vs DEBIT in your
     *                           specific ledger's convention. May be null.
     */
    public TextToStructuredJsonServiceImpl(String apiKey, String apiUrl, String model, String domainInstructions) {
        String resolvedKey = (apiKey == null || apiKey.isBlank())
                ? System.getenv("SARVAM_API_KEY")
                : apiKey;
        if (resolvedKey == null || resolvedKey.isBlank()) {
            throw new IllegalStateException(
                    "Sarvam API key not provided and SARVAM_API_KEY environment variable is not set");
        }
        this.apiKey = resolvedKey;
        this.apiUrl = (apiUrl == null || apiUrl.isBlank()) ? DEFAULT_API_URL : apiUrl;
        this.model = (model == null || model.isBlank()) ? DEFAULT_MODEL : model;
        this.domainInstructions = domainInstructions;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
 
    /** Convenience constructor: reads API key from env, uses default endpoint/model, no extra domain rules. */
    public TextToStructuredJsonServiceImpl() {
        this(null, null, null, null);
    }
 
    @Override
    public JsonNode extractStructuredJson(String text, JsonNode schema) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be null or blank");
        }
        if (schema == null || schema.isNull()) {
            throw new IllegalArgumentException("schema must not be null");
        }
 
        ResolvedSchema resolved = resolveSchema(schema);
        JsonNode responseFormatSchema = ensureObjectType(resolved.schema);
        String requestBody = buildRequestBody(text, resolved.name, responseFormatSchema);
        String responseBody = callSarvamApi(requestBody);
        JsonNode extracted = extractStructuredContent(responseBody);
        validateAgainstSchema(extracted, resolved.schema);
        return extracted;
    }
 
    /**
     * Accepts either a bare JSON Schema (e.g. {@code {"type":"object", "properties": {...}}}
     * or {@code {"oneOf": [...]}}), or a full {@code response_format.json_schema}-style wrapper
     * (e.g. {@code {"name": "...", "description": "...", "strict": true, "schema": {...}}},
     * which is what Sarvam's own docs show and is easy to paste in as-is by mistake).
     * In the wrapper case, the actual schema used for both the API call and local
     * validation is the nested {@code schema} field - passing the wrapper's outer
     * object straight through would have no {@code properties}/{@code required} of
     * its own, so the model would satisfy it with {@code {}}.
     */
    private ResolvedSchema resolveSchema(JsonNode input) {
        boolean looksLikeWrapper = input.isObject()
                && input.has("schema") && input.get("schema").isObject()
                && !input.has("properties") && !input.has("oneOf")
                && !input.has("anyOf") && !input.has("allOf");
        if (looksLikeWrapper) {
            String name = input.has("name") && !input.get("name").asText("").isBlank()
                    ? input.get("name").asText()
                    : SCHEMA_NAME;
            return new ResolvedSchema(name, input.get("schema"));
        }
        return new ResolvedSchema(SCHEMA_NAME, input);
    }
 
    private static final class ResolvedSchema {
        final String name;
        final JsonNode schema;
 
        ResolvedSchema(String name, JsonNode schema) {
            this.name = name;
            this.schema = schema;
        }
    }
 
    /**
     * Structured Output schemas are expected to describe an object type. The
     * ledger-style schema in this app's example has a bare {@code oneOf} at the
     * root with no {@code type}. To stay compatible while leaving semantics
     * untouched, we add {@code "type": "object"} at the root only if it's
     * missing - the {@code oneOf} constraint still fully applies.
     */
    private JsonNode ensureObjectType(JsonNode schema) {
        if (!schema.isObject() || schema.has("type")) {
            return schema;
        }
        ObjectNode copy = schema.deepCopy();
        copy.put("type", "object");
        return copy;
    }
 
    private String buildRequestBody(String text, String schemaName, JsonNode responseFormatSchema) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("max_tokens", 1024);
        root.put("temperature", 0);  //CHANGED TEMP
        // Structured field-extraction is a deterministic, low-latency task with no need for
        // chain-of-thought - explicitly disable thinking mode (Sarvam defaults to "medium"
        // reasoning_effort otherwise, which adds latency/cost and can be unreliable when
        // combined with response_format on some models).
        root.putNull("reasoning_effort");
 
        String systemPrompt = (domainInstructions == null || domainInstructions.isBlank())
                ? BASE_SYSTEM_PROMPT
                : BASE_SYSTEM_PROMPT + "\n\nAdditional domain rules:\n" + domainInstructions;
 
        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
 
        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", text);
        messages.add(userMessage);
        root.set("messages", messages);
 
        ObjectNode jsonSchemaWrapper = objectMapper.createObjectNode();
        jsonSchemaWrapper.put("name", schemaName);
        jsonSchemaWrapper.put("strict", true);
        jsonSchemaWrapper.set("schema", responseFormatSchema);
 
        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_schema");
        responseFormat.set("json_schema", jsonSchemaWrapper);
        root.set("response_format", responseFormat);
 
        try {
            return objectMapper.writeValueAsString(root);
        } catch (IOException e) {
            throw new LlmCommunicationException("Failed to serialize request to Sarvam AI API", e);
        }
    }
 
    private String callSarvamApi(String requestBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(30))
                .header("api-subscription-key", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
 
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new LlmCommunicationException("Failed to reach Sarvam AI API", e);
        }
        
        log.debug("Sarvam AI raw response (status {}): {}", response.statusCode(), response.body());

 
        if (response.statusCode() != 200) {
            throw new LlmCommunicationException(
                    "Sarvam AI API returned status " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }
 
    private JsonNode extractStructuredContent(String responseBody) {
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(responseBody);
        } catch (IOException e) {
            log.error("Sarvam AI response was not valid JSON. Raw body: {}", responseBody);
            throw new LlmCommunicationException("Failed to parse Sarvam AI API response as JSON", e);
        }

        JsonNode choices = responseJson.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw new LlmCommunicationException("Sarvam AI response contained no choices: " + responseBody);
        }

        JsonNode choice = choices.get(0);
        String finishReason = choice.path("finish_reason").asText("");
        JsonNode message = choice.path("message");
        String content = message.path("content").asText(null);

        log.info("Sarvam finish_reason={}, content length={}", finishReason,
                content == null ? -1 : content.length());
        log.debug("Sarvam raw content: {}", content);

        if (content == null || content.isBlank()) {
            throw new LlmCommunicationException(
                    "Sarvam AI response contained no message content (empty structured output): " + responseBody);
        }

        if ("length".equals(finishReason)) {
            log.error("Sarvam output truncated (finish_reason=length). Partial content: {}", content);
            throw new LlmCommunicationException(
                    "Sarvam output was truncated by max_tokens (finish_reason=length): " + content);
        }

        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            log.error("Structured output content was not valid JSON. Raw content: {}", content);
            throw new LlmCommunicationException("Structured output content was not valid JSON: " + content, e);
        }
    }
    
    
 
    private void validateAgainstSchema(JsonNode data, JsonNode schema) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema jsonSchema = factory.getSchema(schema);
        Set<ValidationMessage> errors = jsonSchema.validate(data);
        if (!errors.isEmpty()) {
            Set<String> messages = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toSet());
            throw new SchemaValidationException(
                    "Extracted JSON does not conform to the provided schema: " + messages, messages);
        }
    }
}