package com.hogiabao7725.hotelbooking.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Provides common operations for storing and accessing files.
 * Implementation may use S3, MinIO, Cloudinary...
 */
public interface FileStorageService {

    /**
     * Stores a file at the specified storage location.
     *
     * @param file     the file to store
     * @param location the target storage location,
     * @return the stored file path
     */
    String store(MultipartFile file, String location);

    /**
     * Resolves a stored file path into an accessible URL.
     *
     * @param path the stored file path
     * @return the accessible URL, or {@code null} if the path is empty
     */
    String resolveUrl(String path);

    /**
     * Deletes a file from storage.
     *
     * @param path the stored file path
     */
    void delete(String path);
}