package io.github.trip.shiv.vcledger.controller.voicecommand;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.sarvamai.texttojson.TextToStructuredJsonService;
import io.github.trip.shiv.vcledger.sarvamai.voicetotext.SarvamVoiceToTextService;
import io.github.trip.shiv.vcledger.sarvamai.voicetotext.TranscriptionException;
import io.github.trip.shiv.vcledger.sarvamai.voicetotext.VoiceToTextService;
import io.github.trip.shiv.vcledger.security.SecurityUtils;

@RestController
@RequestMapping("/api/voicecommand")
public class VoiceCommandController {
	
	
	SecurityUtils securityUtils;
	ObjectMapper objectMapper;
	TextToStructuredJsonService textToStructuredJsonService;
	final int AUDIO_FILE_SIZE_MAX = 5;			//MB
	
	//SCHEMA FOR THE LLM OUTPUT
	// SCHEMA FOR THE LLM OUTPUT
	final String schema = """
	{
  "name": "voice_ledger_command",
  "description": "Extract the ledger operation and its required fields from a voice command.",
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
        "description": "The ledger operation requested by the user."
      },
      "customerName": {
        "type": ["string", "null"],
        "description": "Customer involved in the operation. Required for CREATE_TRANSACTION and CUSTOMER_BALANCE."
      },
      "amount": {
        "type": ["number", "null"],
        "description": "Transaction amount in INR. Required only for CREATE_TRANSACTION."
      },
      "transactionType": {
        "type": ["string", "null"],
        "enum": [
          "DEBIT",
          "CREDIT",
          null
        ],
        "description": "Transaction direction. DEBIT means customer owes the shopkeeper; CREDIT means customer paid the shopkeeper. Required only for CREATE_TRANSACTION."
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
	public VoiceCommandController(
			SecurityUtils utils,
			TextToStructuredJsonService textToStructuredJsonService) {
		this.securityUtils = utils;
		this.textToStructuredJsonService = textToStructuredJsonService;
		objectMapper = new ObjectMapper();
	}
	
	
	
	@PostMapping(value = "/process", consumes = "multipart/form-data")
    public ResponseEntity<String> processVoiceCommand(@RequestParam("audio") MultipartFile audio) throws 
    JsonMappingException, JsonProcessingException 
	{
       
		//Get the Authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Transcripting
		String transcript = transcribe(audio);
		
		System.out.print(transcript);
		
		//Fetch Action to be done as Json
		JsonNode actionNode = getRequiredAction(transcript);
		
		String res = objectMapper.writeValueAsString(actionNode);
		
		System.out.println(res);
		
		return ResponseEntity.ok(res);
    }
	
	
	
	
	
	/*
	 * Transcribe the Audio
	 */
	private String transcribe(MultipartFile audio) {
		String apiKey = System.getenv("SARVAM_API_KEY");
		String transcript ;
		
		if (apiKey == null || apiKey.isBlank()) {
			throw new RuntimeException("API KEY IS NOT SET.....");
		}
       
		//Transcribe the Audio 
		if (audio.getSize() > AUDIO_FILE_SIZE_MAX * 1024 * 1024) {
		    throw new RuntimeException("Audio file cannot exceed 5 MB");
		}
		
        if (audio == null || audio.isEmpty()) {
        	throw new RuntimeException("NO AUDIO FILE UPLOADED.....");
        }
 
        try {
            byte[] audioBytes = audio.getBytes();
            String filename = audio.getOriginalFilename() != null ? audio.getOriginalFilename() : "audio";
 
            VoiceToTextService service = new SarvamVoiceToTextService(apiKey, "unknown", "translate");
            transcript = service.transcribe(audioBytes, filename);
 
        } catch (TranscriptionException e) {
            throw new RuntimeException("PROBLEM OCCURRED WHILE TRANSCRIPTING THE AUDIO", e);
        } catch (IOException e) {
        	throw new RuntimeException("PROBLEM OCCURRED WHILE TRANSCRIPTING THE AUDIO", e);
        }
        
        return transcript;
	}
	
	
	
	/*
	 * To get the Structured JSON INTENT from LLM
	 */
	private JsonNode getRequiredAction(String text) throws JsonMappingException, JsonProcessingException  {
	
		JsonNode jsonNode = objectMapper.readTree(schema);
		
		JsonNode result =
		        textToStructuredJsonService.extractStructuredJson(text, jsonNode);
		return result;
	}

	
}
