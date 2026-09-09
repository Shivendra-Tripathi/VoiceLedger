package io.github.trip.shiv.vcledger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.sarvamai.texttojson.TextToStructuredJsonService;
import io.github.trip.shiv.vcledger.sarvamai.texttojson.TextToStructuredJsonServiceImpl;
import jakarta.validation.Valid;

@RestController
public class TextToStructuredJsonController {
	
	ObjectMapper objectMapper;
	TextToStructuredJsonService textToStructuredJsonService;
	
	final String schema = """
{
  "name": "voice_ledger_command",
  "description": "Extract a ledger operation from a shopkeeper's voice command. The speaker is always the shopkeeper.",
  "strict": true,
  "schema": {
    "type": "object",
    "properties": {
      "intent": {
        "type": "string",
        "enum": [
          "CREATE_TRANSACTION",
          "CUSTOMER_BALANCE"
        ],
        "description": "The ledger operation requested by the shopkeeper."
      },
      "customerName": {
        "type": ["string", "null"],
        "description": "Customer involved in the operation. Pronouns 'he', 'him' or names refer to the customer. 'I', 'me' and 'my' refer to the shopkeeper."
      },
      "amount": {
        "type": ["number", "null"],
        "description": "Transaction amount in INR."
      },
      "transactionType": {
        "type": ["string", "null"],
        "enum": [
          "DEBIT",
          "CREDIT",
          null
        ],
        "description": "Determine transaction type from the SHOPKEEPER'S perspective. CREDIT means money comes to the shopkeeper from the customer. DEBIT means money goes from the shopkeeper to the customer. 'I gave Sumit 100' = DEBIT. 'Gave 100 to Sumit' = DEBIT because the omitted subject 'I' is the shopkeeper. 'Sumit gave me 100' = CREDIT. 'I received 100 from Sumit' = CREDIT."
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
}
			""";
	
	public TextToStructuredJsonController(
			TextToStructuredJsonServiceImpl service) {
		// TODO Auto-generated constructor stub
		objectMapper = new ObjectMapper();
		this.textToStructuredJsonService = service;
	}
	
	
	@PostMapping("/api/text-to-json")
	public ResponseEntity<String> getJson(
			@Valid @RequestBody TextToJsonRequest request) throws JsonMappingException, JsonProcessingException{
		
		String text = request.getText();
		
		JsonNode jsonNode = objectMapper.readTree(schema);
		
		JsonNode result =
		        textToStructuredJsonService.extractStructuredJson(text, jsonNode);

		String str = objectMapper.writeValueAsString(result);

		
		System.out.println("JSON :  "+str);
		return ResponseEntity
				.ok(str);
		
	}

}
