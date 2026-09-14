package io.github.trip.shiv.vcledger.business.processors.interfaces;


import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;

public interface LedgerIntentRequestParser {

    LedgerIntentRequest parse(JsonNode json);
}