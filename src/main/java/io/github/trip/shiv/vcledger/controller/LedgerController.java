package io.github.trip.shiv.vcledger.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * LedgerController
 *
 * Skeleton REST controller for ledger-wide balance/summary endpoints.
 * NOTE: No business logic, service calls, or repository calls are implemented here.
 * All endpoints return dummy responses for API-contract testing purposes only.
 */
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    /**
     * GET /api/ledger/balance
     * Get the overall ledger balance.
     * Requires authentication (JWT).
     */
    @GetMapping("/balance")
    public ResponseEntity<String> getOverallBalance(Authentication authentication) {
        // TODO: calculate overall ledger balance via service layer
        return ResponseEntity.ok("Overall ledger balance endpoint working");
    }

    /**
     * GET /api/ledger/summary
     * Get a summary of the user's ledger.
     * Requires authentication (JWT).
     */
    @GetMapping("/summary")
    public ResponseEntity<String> getLedgerSummary(Authentication authentication) {
        // TODO: build ledger summary via service layer
        return ResponseEntity.ok("Ledger summary endpoint working");
    }
}