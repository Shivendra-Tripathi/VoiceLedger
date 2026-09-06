package io.github.trip.shiv.vcledger.sarvamai.voicetotext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
 
/**
 * Minimal multipart/form-data builder for use with java.net.http.HttpClient,
 * which does not provide multipart support out of the box.
 */
final class MultipartBodyBuilder {
 
    private final String boundary = "----JavaVoiceToText" + UUID.randomUUID();
    private final List<byte[]> parts = new ArrayList<>();
 
    String getBoundary() {
        return boundary;
    }
 
    MultipartBodyBuilder addFormField(String name, String value) {
        String part = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n";
        parts.add(part.getBytes(StandardCharsets.UTF_8));
        return this;
    }
 
    MultipartBodyBuilder addFile(String fieldName, Path file) throws IOException {
        String contentType = URLConnection.guessContentTypeFromName(file.getFileName().toString());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return addFile(fieldName, file, contentType);
    }
 
    /**
     * Same as {@link #addFile(String, Path)} but with an explicit content type,
     * for cases where Java's built-in MIME guesser gets it wrong (e.g. it
     * labels .mpeg audio files as "video/mpeg" instead of "audio/mpeg").
     */
    MultipartBodyBuilder addFile(String fieldName, Path file, String contentType) throws IOException {
        return addFile(fieldName, file.getFileName().toString(), Files.readAllBytes(file), contentType);
    }
 
    /**
     * Adds a file part directly from an in-memory byte array — no Path/File
     * or temp file needed. Useful when the source is already in memory,
     * e.g. a Spring MultipartFile's bytes from an upload.
     */
    MultipartBodyBuilder addFile(String fieldName, String filename, byte[] fileBytes, String contentType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\""
                + filename + "\"\r\n"
                + "Content-Type: " + contentType + "\r\n\r\n";
        try {
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(fileBytes);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // ByteArrayOutputStream never throws IOException in practice
            throw new UncheckedIOException(e);
        }
 
        parts.add(out.toByteArray());
        return this;
    }
 
    HttpRequest.BodyPublisher build() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            out.write(part);
        }
        out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArray(out.toByteArray());
    }
}