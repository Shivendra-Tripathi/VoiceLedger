package io.github.trip.shiv.vcledger.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.sarvamai.voicetotext.SarvamVoiceToTextService;
import io.github.trip.shiv.vcledger.sarvamai.voicetotext.TranscriptionException;
import io.github.trip.shiv.vcledger.sarvamai.voicetotext.VoiceToTextService;

import java.io.IOException;
import java.util.Map;
 
/**
 * REST endpoint that accepts an uploaded audio file and returns it
 * transcribed into English text, using the existing SarvamVoiceToTextService
 * (mode = "translate", so output is always English regardless of the
 * spoken language).
 *
 * The upload is handled entirely in memory — no temp file is written to
 * disk, since MultipartFile already holds the bytes.
 *
 * Endpoint:
 *   POST /api/v1/voice-to-text
 *   multipart/form-data body, field name "file"
 *
 * Response:
 *   200 OK -> {"transcript": "..."}
 *   4xx/5xx -> {"error": "..."}
 *
 * Configuration:
 *   Reads the Sarvam API key from the SARVAM_API_KEY environment variable.
 */
@RestController
public class VoiceToTextController {
 
    @PostMapping(value = "/api/v1/voice-to-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> transcribe(@RequestParam("file") MultipartFile file) {
        String apiKey = System.getenv("SARVAM_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server is not configured with SARVAM_API_KEY"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No audio file uploaded"));
        }
 
        try {
            byte[] audioBytes = file.getBytes();
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio";
 
            VoiceToTextService service = new SarvamVoiceToTextService(apiKey, "unknown", "translate");
            String transcript = service.transcribe(audioBytes, filename);
 
            return ResponseEntity.ok(Map.of("transcript", transcript));
 
        } catch (TranscriptionException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to read uploaded file: " + e.getMessage()));
        }
    }
}