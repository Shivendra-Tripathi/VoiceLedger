package io.github.trip.shiv.vcledger.controller;

import java.io.IOException;

import org.slf4j.Logger;
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


import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.exceptions.TranscriptionException;
import io.github.trip.shiv.vcledger.business.groq.GroqStructuredOutputService;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestExecutorDelegator;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestParserDelegator;
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
	LedgerIntentRequestParserDelegator ledgerIntentRequestParser;
	LedgerIntentRequestExecutorDelegator ledgerIntentRequestExecutorDelegator;
	PendingOperationService pendingOperationService;
	VoiceToTextService voiceToTextService;
	
	private static final Logger logger =
			org.slf4j.LoggerFactory.getLogger(VoiceCommandController.class);
	
	@PostMapping(value = "/process", consumes = "multipart/form-data")
    public ResponseEntity<LedgerResponse> processVoiceCommand(@RequestParam("audio") MultipartFile audio) throws 
    IOException, InterruptedException, TranscriptionException 
	{
		
		logger.info("Got the Voice Request	,inside {}","processVoiceCommand() controller");
		
		try {
		//Transcripting
		String transcript = voiceToTextService.transcribe(audio);
		
		logger.trace("Transacibed : {}",transcript);
		
		JsonNode actionNode = groqStructuredOutputService.extractStructuredJson(transcript);
		

		logger.trace("Json Output from Grok : {}",objectMapper.writeValueAsString(actionNode));
		
		LedgerIntentRequest request = ledgerIntentRequestParser.delegate(actionNode);
		
		
		return ResponseEntity.ok(
				ledgerIntentRequestExecutorDelegator.delegate(request)
				);
		}catch (Exception e) {
			logger.error("Exception Occurred : {} ",e);
		}
		
		return null;
		
    }
	
	
	@PostMapping("/confirm")
	public ResponseEntity<LedgerResponse> confirmOperation(@ RequestParam("operationId") String operationId) throws JsonMappingException, JsonProcessingException{
		
		logger.info("Operation Confirmation Request got with op id {}",operationId);
		
		User user = securityUtils.getAuthenticatedUser();
		
		LedgerResponse ledgerResponse = pendingOperationService.confirm(operationId, user.getId());
		return ResponseEntity.ok(ledgerResponse);
	}
	
	
	@PostMapping("/cancel")
	public ResponseEntity<String> rejectOperation(@ RequestParam("operationId") String operationId) throws JsonMappingException, JsonProcessingException{
		
		logger.info("Operation Rejection Request got with op id {}",operationId);
		User user = securityUtils.getAuthenticatedUser();
		
		pendingOperationService.cancel(operationId, user.getId());
		return ResponseEntity.ok("Transaction cancelled.");
	}
	
	
	@PostMapping("/confirmcustomer")
	public ResponseEntity<LedgerResponse> confirmCustomer(
			@ RequestParam("operationId") String operationId,
			@RequestParam("customerId") Long customerId) throws JsonMappingException, JsonProcessingException{
		
		logger.info("Customer Confirmation Request got with op id {} , cutomer id {}",operationId,customerId);
		User user = securityUtils.getAuthenticatedUser();
		
		LedgerResponse ledgerResponse = pendingOperationService.selectCustomer(operationId, user.getId(), customerId);
		return ResponseEntity.ok(ledgerResponse);
	}
	
	
}
