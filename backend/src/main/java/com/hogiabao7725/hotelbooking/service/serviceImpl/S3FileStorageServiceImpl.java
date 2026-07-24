package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.S3Properties;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final S3Properties props;

    @Override
    public String store(MultipartFile file, String location) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        String objectKey = createObjectKey(file.getOriginalFilename(), location);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(objectKey)
                .contentType(getContentType(file))
                .contentLength(file.getSize())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );

            return objectKey;
        } catch (IOException | SdkException exception) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, exception);
        }
    }

    @Override
    public String resolveUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        String objectKey = cleanObjectPath(path);

        return UriComponentsBuilder
                .fromUri(props.publicUrl())
                .pathSegment(objectKey.split("/"))
                .build()
                .toUriString();
    }

    @Override
    public void delete(String path) {
        if (!StringUtils.hasText(path)) {
            return;
        }

        String objectKey = cleanObjectPath(path);

        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(props.bucket())
                    .key(objectKey)
            );
        } catch (SdkException exception) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED, exception);
        }
    }

    private String createObjectKey(String originalFilename, String location) {
        String filename = UUID.randomUUID().toString();
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (StringUtils.hasText(extension)) {
            filename += "." + extension.toLowerCase(Locale.ROOT);
        }

        return String.join("/", cleanObjectPath(location), filename);
    }

    private String cleanObjectPath(String path) {
        if (!StringUtils.hasText(path)) {
            throw new AppException(ErrorCode.FILE_INVALID_PATH);
        }

        String cleanPath = StringUtils.cleanPath(path.trim());

        cleanPath = StringUtils.trimLeadingCharacter(cleanPath, '/');
        cleanPath = StringUtils.trimTrailingCharacter(cleanPath, '/');

        if (!StringUtils.hasText(cleanPath) || cleanPath.contains("..")) {
            throw new AppException(ErrorCode.FILE_INVALID_PATH);
        }

        return cleanPath;
    }

    private String getContentType(MultipartFile file) {
        return StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}