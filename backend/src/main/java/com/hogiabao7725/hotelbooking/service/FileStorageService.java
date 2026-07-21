package com.hogiabao7725.hotelbooking.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Common service for file storage operations.
 * <p>
 * Handles uploading and deleting various file types
 * (such as images, documents, or videos) across storage providers.
 */
public interface FileStorageService {

    /**
     * Uploads a file to the storage provider.
     *
     * @param file     the multipart file to upload
     * @param folder   the destination folder path
     * @param publicId the custom public identifier for the file
     * @return the public access URL of the uploaded file
     */
    String upload(MultipartFile file, String folder, String publicId);

    /**
     * Deletes a file from the storage provider using its access URL.
     *
     * @param fileUrl the public URL of the file to be deleted
     */
    void delete(String fileUrl);
}
