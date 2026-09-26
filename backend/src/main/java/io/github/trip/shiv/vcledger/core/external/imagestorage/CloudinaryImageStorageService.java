package io.github.trip.shiv.vcledger.core.external.imagestorage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.ImageStorageException;

/**
 * Handles upload/delete of customer profile photos against Cloudinary.
 *
 * The Cloudinary client is created once in CloudinaryConfig and injected here.
 *
 * The service:
 *  - uploads customer profile photos
 *  - applies a 400x400 face-focused transformation
 *  - returns CloudinaryUploadResult containing secure URL and public ID
 *  - deletes previously uploaded photos using their public ID
 */
@Service
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final String CUSTOMER_PHOTOS_FOLDER =
            "vcledger/customer-photos";

    private static final long MAX_FILE_SIZE_BYTES =
            5L * 1024 * 1024; // 5 MB

    private final Cloudinary cloudinary;

    public CloudinaryImageStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload a photo received as a Spring MultipartFile.
     */
    @Override
    public CloudinaryUploadResult uploadImage(
            MultipartFile file,
            String customerId) {

        validate(
                file == null || file.isEmpty(),
                file == null ? 0 : file.getSize(),
                file == null ? null : file.getOriginalFilename()
        );

        try {

            @SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    buildUploadOptions(customerId)
            );

            return CloudinaryUploadResult.from(result);

        } catch (IOException e) {

            throw new ImageStorageException(
                    "Failed to upload profile photo to Cloudinary",
                    e
            );
        }
    }

    /**
     * Upload a photo already available as a java.io.File.
     */
    @Override
    public CloudinaryUploadResult uploadImage(
            File file,
            String customerId) {

        validate(
                file == null || !file.exists(),
                file == null ? 0 : file.length(),
                file == null ? null : file.getName()
        );

        try {

            Map<String, Object> result = cloudinary.uploader().upload(
                    file,
                    buildUploadOptions(customerId)
            );

            return CloudinaryUploadResult.from(result);

        } catch (IOException e) {

            throw new ImageStorageException(
                    "Failed to upload profile photo to Cloudinary",
                    e
            );
        }
    }

    /**
     * Delete a previously uploaded photo.
     *
     * The value passed here must be the Cloudinary public ID,
     * NOT the secure URL.
     *
     * "ok" and "not found" are both treated as successful deletion.
     */
    @Override
    public boolean deleteImage(String publicId) {

        if (publicId == null || publicId.isBlank()) {
            return false;
        }

        try {

            Map<String, Object> result =
                    cloudinary.uploader().destroy(
                            publicId,
                            ObjectUtils.emptyMap()
                    );

            String status = String.valueOf(result.get("result"));

            return "ok".equals(status)
                    || "not found".equals(status);

        } catch (IOException e) {

            throw new ImageStorageException(
                    "Failed to delete profile photo from Cloudinary",
                    e
            );
        }
    }

    /**
     * Builds the Cloudinary upload options.
     *
     * The transformation is represented by Cloudinary's Transformation
     * object instead of manually constructing a nested Map.
     */
    @SuppressWarnings("unchecked")
	private Map<String, Object> buildUploadOptions(String customerId) {

        /*
         * Stable public ID:
         *
         * customer-123
         *
         * Re-uploading for the same customer therefore replaces the
         * existing image instead of creating another public ID.
         */
        String publicId =
                "customer-" +
                (customerId != null
                        ? customerId
                        : UUID.randomUUID());

        /*
         * Cloudinary transformation:
         *
         * 400 x 400
         * crop = fill
         * gravity = face
         * quality = auto
         * format = auto
         */
        Transformation<?> transformation = new Transformation<>()
                .width(400)
                .height(400)
                .crop("fill")
                .gravity("face")
                .quality("auto")
                .fetchFormat("auto");

        return ObjectUtils.asMap(
                "folder", CUSTOMER_PHOTOS_FOLDER,
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "image",
                "transformation", transformation
        );
    }

    private void validate(
            boolean missing,
            long size,
            String name) {

        if (missing) {

            throw new IllegalArgumentException(
                    "No file provided for upload: " + name
            );
        }

        if (size > MAX_FILE_SIZE_BYTES) {

            throw new IllegalArgumentException(
                    "File '" + name + "' is " + size +
                    " bytes, exceeds " +
                    MAX_FILE_SIZE_BYTES +
                    " byte limit"
            );
        }
    }
}