package io.github.trip.shiv.vcledger.business.groq;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GroqStructuredOutputService {

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;
    
    
    
    private final String message = """

    	    You are a voice command parser for a shopkeeper ledger application.

    	    Identify the intent expressed by the shopkeeper and extract the required fields.

    	    IMPORTANT CONTEXT:
    	    - "content" contains the exact words spoken by the shopkeeper.
    	    - "I", "me", "maine", "mujhe" refer to the shopkeeper.
    	    - "customerName" refers to the other person.
    	    - Determine DEBIT/CREDIT from the shopkeeper's perspective.
    	    - Do NOT use conventional accounting meanings of DEBIT/CREDIT.

    	    INTENT RULES:
    	    1. A new transaction/action to be recorded -> CREATE_TRANSACTION.
    	    2. A question about an existing balance -> CUSTOMER_BALANCE.
    	    3. Never classify a transaction/action statement as CUSTOMER_BALANCE.

    	    For CREATE_TRANSACTION:
    	    - customerName = customer involved.
    	    - amount = transaction amount.
    	    - transactionType = DEBIT or CREDIT.

    	    For CUSTOMER_BALANCE:
    	    - customerName = customer involved.
    	    - amount = null.
    	    - transactionType = null.

    	    TRANSACTION TYPE RULES:

    	    DEBIT = value moves from shopkeeper to customer.
    	    This includes:
    	    - shopkeeper gives money to customer
    	    - shopkeeper gives goods/groceries to customer
    	    - customer takes goods/groceries from shopkeeper
    	    - customer owes the shopkeeper because goods/value were given to the customer

    	    CREDIT = value moves from customer to shopkeeper.
    	    This includes:
    	    - customer gives money to shopkeeper
    	    - customer pays money to shopkeeper

    	    IMPORTANT "OWES" RULE:
    	    If the customer is described as owing money to the shopkeeper,
    	    this means the customer received value from the shopkeeper.
    	    Therefore, it is DEBIT.

    	    Examples:
    	    - "Amit owes 1500 rupees" -> CREATE_TRANSACTION, Amit, 1500, DEBIT
    	    - "Amit owes me 1500 rupees" -> CREATE_TRANSACTION, Amit, 1500, DEBIT
    	    - "Amit has to pay me 1500 rupees" -> CREATE_TRANSACTION, Amit, 1500, DEBIT
    	    - "Amit par 1500 rupaye baki hai" -> CREATE_TRANSACTION, Amit, 1500, DEBIT

    	    EXAMPLES:

    	    User:
    	    "Maine Amit ko 100 rupaye diye"

    	    Output:
    	    {
    	      "intent": "CREATE_TRANSACTION",
    	      "customerName": "Amit",
    	      "amount": 100,
    	      "transactionType": "DEBIT"
    	    }

    	    User:
    	    "Amit ne mujhe 100 rupaye diye"

    	    Output:
    	    {
    	      "intent": "CREATE_TRANSACTION",
    	      "customerName": "Amit",
    	      "amount": 100,
    	      "transactionType": "CREDIT"
    	    }

    	    User:
    	    "Amit ne 500 ka maal liya"

    	    Output:
    	    {
    	      "intent": "CREATE_TRANSACTION",
    	      "customerName": "Amit",
    	      "amount": 500,
    	      "transactionType": "DEBIT"
    	    }

    	    User:
    	    "Amit ne mujhse 500 ka maal liya"

    	    Output:
    	    {
    	      "intent": "CREATE_TRANSACTION",
    	      "customerName": "Amit",
    	      "amount": 500,
    	      "transactionType": "DEBIT"
    	    }

    	    User:
    	    "Amit owes fifteen hundred rupees"

    	    Output:
    	    {
    	      "intent": "CREATE_TRANSACTION",
    	      "customerName": "Amit",
    	      "amount": 1500,
    	      "transactionType": "DEBIT"
    	    }

    	    User:
    	    "Amit ka balance kitna hai?"

    	    Output:
    	    {
    	      "intent": "CUSTOMER_BALANCE",
    	      "customerName": "Amit",
    	      "amount": null,
    	      "transactionType": null
    	    }

    	    User:
    	    "Amit mujhe kitna dena hai?"

    	    Output:
    	    {
    	      "intent": "CUSTOMER_BALANCE",
    	      "customerName": "Amit",
    	      "amount": null,
    	      "transactionType": null
    	    }

    	    User:
    	    "Amit ke account mein kitna baki hai?"

    	    Output:
    	    {
    	      "intent": "CUSTOMER_BALANCE",
    	      "customerName": "Amit",
    	      "amount": null,
    	      "transactionType": null
    	    }

    	    FINAL REQUIREMENT:
    	    Return only JSON that follows the provided JSON schema.
    	    Do not return explanations, markdown, or additional text.

    	    """;
    
 // SCHEMA FOR THE LLM OUTPUT
 	final String schemaText = """
 			{
 			  "type": "object",

 			  "properties": {

 			    "intent": {
 			      "type": "string",
 			      "enum": [
 			        "CREATE_TRANSACTION",
 			        "CUSTOMER_BALANCE"
 			      ],
 			      "description": "The requested ledger operation."
 			    },

 			    "customerName": {
 			      "type": ["string", "null"],
 			      "description": "Customer name. Required for both supported intents."
 			    },

 			    "amount": {
 			      "type": ["number", "null"],
 			      "description": "Amount in INR. Required for CREATE_TRANSACTION, otherwise null."
 			    },

 			    "transactionType": {
 			      "type": ["string", "null"],
 			      "enum": [
 			        "DEBIT",
 			        "CREDIT",
 			        null
 			      ],
 			      "description": "DEBIT when value moves from the shopkeeper to the customer, including money or goods given by the shopkeeper. CREDIT when value moves from the customer to the shopkeeper, including money paid or given by the customer."
 			    }
 			  },

 			  "required": [
 			    "intent",
 			    "customerName",
 			    "amount",
 			    "transactionType"
 			  ],

 			  "additionalProperties": false
 			}
 			""";
 	
 	private final JsonNode schema ;
 	
    public GroqStructuredOutputService(ObjectMapper objectMapper) throws JsonMappingException, JsonProcessingException {

        this.objectMapper = objectMapper;
		
        schema = objectMapper.readTree(schemaText);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public JsonNode extractStructuredJson(
            String text) throws IOException, InterruptedException  {

        ObjectNode requestBody = objectMapper.createObjectNode();

        requestBody.put("model", model);
        requestBody.put("temperature", 0);

        /*
         * Messages
         */

        var messages = requestBody.putArray("messages");

        /*
         * System message
         */

        ObjectNode systemMessage = messages.addObject();

        systemMessage.put("role", "system");

        systemMessage.put("content", message);

        /*
         * User message
         */

        ObjectNode userMessage = messages.addObject();

        userMessage.put("role", "user");
        userMessage.put("content", text);

        /*
         * Structured output
         */

        ObjectNode responseFormat =
                requestBody.putObject("response_format");

        responseFormat.put("type", "json_schema");

        ObjectNode jsonSchema =
                responseFormat.putObject("json_schema");

        jsonSchema.put("name", "structured_extraction");
        jsonSchema.put("strict", true);

        jsonSchema.set("schema", schema);

        /*
         * HTTP request
         */

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new RuntimeException(
                    "Groq API error. Status: "
                    + response.statusCode()
                    + ", body: "
                    + response.body()
            );
        }

        JsonNode responseJson =
                objectMapper.readTree(response.body());

        JsonNode choices =
                responseJson.path("choices");

        if (!choices.isArray() || choices.isEmpty()) {

            throw new RuntimeException(
                    "Groq returned no choices: "
                    + response.body()
            );
        }

        String content = choices
                .get(0)
                .path("message")
                .path("content")
                .asText();

        if (content == null || content.isBlank()) {

            throw new RuntimeException(
                    "Groq returned empty structured output"
            );
        }

        return objectMapper.readTree(content);
    }
    
    
    
    
}