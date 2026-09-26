package io.github.trip.shiv.vcledger.core.ledger.processors.interfaces;


import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;

public interface LedgerIntentRequestParserDelegator {

    LedgerIntentRequest delegate(JsonNode json);
}