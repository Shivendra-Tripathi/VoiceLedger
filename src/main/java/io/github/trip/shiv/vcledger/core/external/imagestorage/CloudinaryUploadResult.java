package io.github.trip.shiv.vcledger.core.external.imagestorage;


import java.util.Map;

/**
 * Result of a successful Cloudinary upload.
 *
 * Persist secureUrl into your Customer.photoUrl column, and publicId
 * into something like Customer.photoPublicId -- deletion/replacement
 * needs the publicId, not the URL.
 */
public class CloudinaryUploadResult {

    private final String secureUrl;
    private final String publicId;

    private CloudinaryUploadResult(String secureUrl, String publicId) {
        this.secureUrl = secureUrl;
        this.publicId = publicId;
    }

    public static CloudinaryUploadResult from(Map<String, Object> raw) {
        return new CloudinaryUploadResult(
                String.valueOf(raw.get("secure_url")),
                String.valueOf(raw.get("public_id"))
        );
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public String getPublicId() {
        return publicId;
    }
}