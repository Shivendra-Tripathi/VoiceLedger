package io.github.trip.shiv.vcledger.core.ledger.processors.impls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.requestparsers.LedgerIntentRequestParser;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.MissingLedgerIntentRequestParserException;
import io.github.trip.shiv.vcledger.core.ledger.processors.interfaces.LedgerIntentRequestParserDelegator;

@Component
public class LedgerIntentRequestParserDelegatorImpl implements LedgerIntentRequestParserDelegator{
	
	
	private final Map<String,LedgerIntentRequestParser<?>> parsers;
	
	//Automatically injects the List<LedgerIntentRequestParser>
	public LedgerIntentRequestParserDelegatorImpl(List<LedgerIntentRequestParser<?>> parsers) {
		
		this.parsers = 
				parsers
				.stream()
				.collect(Collectors.toMap(
						parser -> parser.getIntentKey(), 
						parser -> parser)
						);
	}
	
	
	@Override
	public LedgerIntentRequest delegate(JsonNode json) {
		
		//fetch the intent
		JsonNode intentNode = json.get("intent");

		if (intentNode == null || intentNode.isNull()) {
		    throw new IllegalArgumentException(
		            "Missing 'intent' field in ledger intent request"
		    );
		}

		String intent = intentNode.asText();
		
		//Fetch the proper parser
		LedgerIntentRequestParser<?> parser = parsers.get(intent);
		
		//Check if no proper parser is found
		if(parser==null) {
			throw new MissingLedgerIntentRequestParserException("The Parser(json intent ---> LedgerIntentRequest) is missing for intent : "
					+intent);
		}
		
		
		//call the parser to parse
		return parser.parse(json);
		
		
	}
	

}
