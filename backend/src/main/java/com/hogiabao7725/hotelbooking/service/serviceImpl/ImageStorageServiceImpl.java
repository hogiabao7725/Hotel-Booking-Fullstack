package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.hogiabao7725.hotelbooking.constant.CloudinaryConstants;
import com.hogiabao7725.hotelbooking.constant.FileConstants;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file, String folder, String publicId) {
        validate(file);

        try (InputStream inputStream = file.getInputStream()) {
            Map<?, ?> result = cloudinary.uploader().upload(
                    inputStream,
                    buildUploadOptions(folder, publicId)
            );

            return extractSecureUrl(result);
        } catch (IOException ex) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        String publicId = extractPublicId(fileUrl);

        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException ex) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > FileConstants.MAX_IMAGE_SIZE) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !FileConstants.ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE);
        }
    }

    private Map<String, Object> buildUploadOptions(String folder, String publicId) {
        return Map.of(
                CloudinaryConstants.FOLDER, folder,
                CloudinaryConstants.PUBLIC_ID, publicId,
                CloudinaryConstants.RESOURCE_TYPE, CloudinaryConstants.IMAGE
        );
    }

    private String extractSecureUrl(Map<?, ?> result) {
        Object secureUrl = result.get(CloudinaryConstants.SECURE_URL);
        if (secureUrl == null) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return secureUrl.toString();
    }

    /**
     * Extracts public_id from Cloudinary HTTPS URL for file deletion.
     * <p>
     * Example input: "https://res.cloudinary.com/demo/image/upload/v1712345/folder/file-id.jpg"
     * <p>
     * Output: "folder/file-id"
     */
    private String extractPublicId(String fileUrl) {
        int uploadIndex = fileUrl.indexOf(CloudinaryConstants.UPLOAD_SEGMENT);
        if (uploadIndex == -1) {
            throw new AppException(ErrorCode.FILE_INVALID_URL);
        }

        String path = fileUrl.substring(
                uploadIndex + CloudinaryConstants.UPLOAD_SEGMENT.length()
        );

        // Strip version tag (e.g. "v1712345678/") if present
        int firstSlash = path.indexOf('/');
        if (firstSlash > 1) {
            String firstSegment = path.substring(0, firstSlash);
            if (firstSegment.matches("v\\d+")) {
                path = path.substring(firstSlash + 1);
            }
        }

        // Strip file extension (e.g. ".jpg", ".png")
        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex != -1) {
            path = path.substring(0, extensionIndex);
        }

        return path;
    }

}
