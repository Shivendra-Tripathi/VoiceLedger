package io.github.trip.shiv.vcledger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * VoiceController
 *
 * Skeleton REST controller for voice-driven ledger features.
 * NOTE: No speech-to-text, NLP, intent extraction, or transaction processing
 * is implemented here. All endpoints return dummy responses for API-contract
 * testing purposes only. MultipartFile parameters are wired up so the
 * endpoints are ready for future implementation.
 */
@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    /**
     * POST /api/voice/transcribe
     * Accepts an audio file (multipart/form-data) and will eventually return
     * its transcribed text. For now, speech-to-text is NOT performed.
     * Requires authentication (JWT).
     */
    @PostMapping(value = "/transcribe", consumes = "multipart/form-data")
    public ResponseEntity<String> transcribeAudio(@RequestParam("audio") MultipartFile audio,
                                                    Authentication authentication) {
        // TODO: perform speech-to-text on the uploaded audio file
        return ResponseEntity.ok("Voice transcription endpoint working");
    }

    /**
     * POST /api/voice/transaction
     * Accepts an audio file (multipart/form-data) that will eventually flow
     * through: Audio -> Speech-to-Text -> NLP -> Intent extraction -> Transaction creation.
     * None of that pipeline is implemented yet.
     * Requires authentication (JWT).
     */
    @PostMapping(value = "/transaction", consumes = "multipart/form-data")
    public ResponseEntity<String> createTransactionFromVoice(@RequestParam("audio") MultipartFile audio,
                                                                Authentication authentication) {
        // TODO: process audio into a ledger transaction via service layer
        return ResponseEntity.ok("Voice transaction endpoint working");
    }

    /**
     * POST /api/voice/command
     * Accepts an audio file (multipart/form-data) representing a general
     * voice command. NLP/command processing is NOT implemented yet.
     * Requires authentication (JWT).
     */
    @PostMapping(value = "/command", consumes = "multipart/form-data")
    public ResponseEntity<String> processVoiceCommand(@RequestParam("audio") MultipartFile audio,
                                                         Authentication authentication) {
        // TODO: process voice command via service layer
        return ResponseEntity.ok("Voice command endpoint working");
    }
}