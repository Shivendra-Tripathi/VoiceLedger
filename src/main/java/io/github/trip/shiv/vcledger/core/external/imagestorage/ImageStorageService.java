package io.github.trip.shiv.vcledger.core.external.imagestorage;



import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Storage-provider-agnostic contract for uploading/deleting customer
 * profile photos. CloudinaryImageStorageService is the current
 * implementation; keeping this as an interface means you can swap in a
 * different provider (e.g. Supabase Storage, S3) later without touching
 * any calling code -- controllers/services should depend on this
 * interface, not on CloudinaryImageStorageService directly.
 */
public interface ImageStorageService {

    /**
     * Upload a photo received as a Spring MultipartFile.
     *
     * @param file       the uploaded photo
     * @param customerId used to build a stable identifier so re-uploads
     *                   overwrite the previous photo instead of piling up
     * @return the stored secureUrl (for display) and publicId (for delete/replace)
     */
    CloudinaryUploadResult uploadImage(MultipartFile file, String customerId);

    /**
     * Upload a photo already available as a java.io.File.
     *
     * @param file       the photo file on disk
     * @param customerId used to build a stable identifier so re-uploads
     *                   overwrite the previous photo instead of piling up
     * @return the stored secureUrl (for display) and publicId (for delete/replace)
     */
    CloudinaryUploadResult uploadImage(File file, String customerId);

    /**
     * Delete a previously uploaded photo.
     *
     * @param publicId the identifier returned at upload time (NOT the URL)
     * @return true if deletion was confirmed (already-deleted counts as success)
     */
    boolean deleteImage(String publicId);
}