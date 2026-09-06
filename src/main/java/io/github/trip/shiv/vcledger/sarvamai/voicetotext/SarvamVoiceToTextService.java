package io.github.trip.shiv.vcledger.sarvamai.voicetotext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * VoiceToTextService implementation backed by Sarvam AI's Speech-to-Text
 * REST API (Saaras models), which supports English and 22 Indic languages.
 *
 * Endpoint:  POST https://api.sarvam.ai/speech-to-text
 * Auth:      api-subscription-key header (NOT "Bearer ...")
 * Best for:  clips under ~30s with immediate results. For longer audio,
 *            Sarvam's separate Batch API should be used instead.
 *
 * Usage:
 *   VoiceToTextService service = new SarvamVoiceToTextService(apiKey);
 *   String text = service.transcribe(new File("audio.wav"));
 *
 * With language / mode hints:
 *   VoiceToTextService service = new SarvamVoiceToTextService(apiKey, "hi-IN", "transcribe");
 */
public class SarvamVoiceToTextService implements VoiceToTextService {
 
    private static final String ENDPOINT = "https://api.sarvam.ai/speech-to-text";
    private static final String DEFAULT_MODEL = "saaras:v3";
 
    /**
     * Explicit extension -> MIME type map for Sarvam's allowed content types.
     * Needed because Java's URLConnection.guessContentTypeFromName() gets some
     * of these wrong (e.g. it returns "video/mpeg" for .mpeg/.mp3-adjacent
     * files, which Sarvam's API rejects — it wants "audio/mpeg").
     */
    private static final Map<String, String> EXTENSION_TO_MIME_TYPE = Map.ofEntries(
            Map.entry("mp3", "audio/mpeg"),
            Map.entry("mpeg", "audio/mpeg"),
            Map.entry("mpga", "audio/mpeg"),
            Map.entry("wav", "audio/wav"),
            Map.entry("aac", "audio/aac"),
            Map.entry("aiff", "audio/aiff"),
            Map.entry("aif", "audio/aiff"),
            Map.entry("ogg", "audio/ogg"),
            Map.entry("opus", "audio/opus"),
            Map.entry("flac", "audio/flac"),
            Map.entry("mp4", "audio/mp4"),
            Map.entry("m4a", "audio/x-m4a"),
            Map.entry("amr", "audio/amr"),
            Map.entry("wma", "audio/x-ms-wma"),
            Map.entry("webm", "audio/webm"),
            Map.entry("pcm", "audio/pcm_s16le")
    );
 
    private final String apiKey;
    private final String languageCode; // e.g. "hi-IN", "en-IN", or "unknown" for auto-detect
    private final String mode;         // transcribe | translate | verbatim | translit | codemix
    private final HttpClient httpClient;
 
    public SarvamVoiceToTextService(String apiKey) {
        this(apiKey, "unknown", "transcribe");
    }
 
    public SarvamVoiceToTextService(String apiKey, String languageCode, String mode) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be null or blank");
        }
        this.apiKey = apiKey;
        this.languageCode = languageCode;
        this.mode = mode;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }
 
    @Override
    public String transcribe(File audioFile) throws IOException, TranscriptionException {
        validateFile(audioFile);
        byte[] audioBytes;
        try {
            audioBytes = java.nio.file.Files.readAllBytes(audioFile.toPath());
        } catch (IOException e) {
            throw new IOException("Could not read audio file: " + audioFile, e);
        }
        return transcribe(audioBytes, audioFile.getName());
    }
 
    @Override
    public String transcribe(byte[] audioBytes, String filename) throws IOException, TranscriptionException {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new TranscriptionException("Audio data is empty: " + filename);
        }
 
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder()
                    .addFormField("model", DEFAULT_MODEL)
                    .addFormField("mode", mode)
                    .addFormField("language_code", languageCode)
                    .addFile("file", filename, audioBytes, resolveContentType(filename));
 
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("api-subscription-key", apiKey)
                    .header("Content-Type", "multipart/form-data; boundary=" + bodyBuilder.getBoundary())
                    .timeout(Duration.ofSeconds(60))
                    .POST(bodyBuilder.build())
                    .build();
 
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
 
            if (response.statusCode() != 200) {
                throw new TranscriptionException(
                        "Transcription failed with status " + response.statusCode() + ": " + response.body());
            }
 
            return extractTranscript(response.body());
 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TranscriptionException("Transcription request was interrupted", e);
        }
    }
 
    private String resolveContentType(String filename) {
        String name = filename.toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot + 1) : "";
        return EXTENSION_TO_MIME_TYPE.getOrDefault(extension, "application/octet-stream");
    }
 
    private void validateFile(File audioFile) throws IOException, TranscriptionException {
        if (audioFile == null || !audioFile.exists() || !audioFile.isFile()) {
            throw new IOException("Audio file does not exist: " + audioFile);
        }
        if (audioFile.length() == 0) {
            throw new TranscriptionException("Audio file is empty: " + audioFile.getName());
        }
        // Sarvam's REST endpoint is meant for short clips (<30s) with instant results;
        // longer files should go through their separate Batch API instead.
    }
 
    /**
     * Extracts the "transcript" field from a JSON response like
     * {"request_id": "...", "transcript": "...", "language_code": "hi-IN"}
     * without pulling in a JSON library dependency. Swap for Jackson/Gson
     * if you also need language_code, timestamps, etc.
     */
    private String extractTranscript(String json) throws TranscriptionException {
        Pattern pattern = Pattern.compile("\"transcript\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new TranscriptionException("Could not parse transcription response: " + json);
        }
        return matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}