package io.github.trip.shiv.vcledger.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.exceptions.TranscriptionException;
import io.github.trip.shiv.vcledger.business.groq.GroqStructuredOutputService;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestParser;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestProcessor;
import io.github.trip.shiv.vcledger.business.sarvamai.impls.SarvamVoiceToTextService;
import io.github.trip.shiv.vcledger.business.sarvamai.interfaces.VoiceToTextService;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.PendingOperationService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/voicecommand")
@AllArgsConstructor
public class VoiceCommandController {
	
	
	SecurityUtils securityUtils;
	ObjectMapper objectMapper;
	GroqStructuredOutputService groqStructuredOutputService;
	LedgerIntentRequestParser ledgerIntentRequestParser;
	LedgerIntentRequestProcessor ledgerIntentRequestProcessor;
	PendingOperationService pendingOperationService;
	
	final int AUDIO_FILE_SIZE_MAX = 5;			//MB
	
	
	
//	 public VoiceCommandController(
//	        SecurityUtils securityUtils,
//	        ObjectMapper objectMapper,
//	        GroqStructuredOutputService groqStructuredOutputService,
//	        LedgerIntentRequestParser ledgerIntentRequestParser,
//	        LedgerIntentRequestProcessor ledgerIntentRequestProcessor) {
//
//	    this.securityUtils = securityUtils;
//	    this.objectMapper = objectMapper;
//	    this.groqStructuredOutputService = groqStructuredOutputService;
//	    this.ledgerIntentRequestParser = ledgerIntentRequestParser;
//	    this.ledgerIntentRequestProcessor = ledgerIntentRequestProcessor;
//	}
	
	
	@PostMapping(value = "/process", consumes = "multipart/form-data")
    public ResponseEntity<LedgerResponse> processVoiceCommand(@RequestParam("audio") MultipartFile audio) throws 
    IOException, InterruptedException 
	{
       
		//Get the Authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Transcripting
		String transcript = transcribe(audio);
		
		System.out.print(transcript);
		
		JsonNode actionNode = groqStructuredOutputService.extractStructuredJson(transcript);
		

		LedgerIntentRequest request = ledgerIntentRequestParser.parse(actionNode);
		
		
		
		return ResponseEntity.ok(
				ledgerIntentRequestProcessor.process(request)
				);
		
    }
	
	
	@PostMapping("/confirm")
	public ResponseEntity<LedgerResponse> confirmOperation(@ RequestParam("operationId") String operationId){
		User user = securityUtils.getAuthenticatedUser();
		
		LedgerResponse ledgerResponse = pendingOperationService.confirm(operationId, user.getId());
		return ResponseEntity.ok(ledgerResponse);
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
	
	
	
	
}
