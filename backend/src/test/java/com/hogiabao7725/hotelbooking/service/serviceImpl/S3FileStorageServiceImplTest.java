package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.S3Properties;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileStorageServiceImplTest {

    @Mock
    private S3Client s3Client;

    private S3Properties props;
    private S3FileStorageServiceImpl s3FileStorageService;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        props = new S3Properties(
                URI.create("http://localhost:9000"),
                "us-east-1",
                "test-bucket",
                "accessKey",
                "secretKey",
                true,
                URI.create("https://cdn.example.com")
        );
        s3FileStorageService = new S3FileStorageServiceImpl(s3Client, props);
    }

    // ===== store Tests =====

    @Test
    void store_shouldUploadFileAndReturnObjectKey_whenFileIsValid() throws IOException {
        // Arrange
        String location = "hotel-booking/facilities";
        String originalFilename = "test-icon.png";
        byte[] content = "dummy file content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getSize()).thenReturn((long) content.length);
        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getContentType()).thenReturn("image/png");

        // Act
        String resultKey = s3FileStorageService.store(file, location);

        // Assert
        assertThat(resultKey).startsWith("hotel-booking/facilities/");
        assertThat(resultKey).endsWith(".png");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest putRequest = requestCaptor.getValue();
        assertThat(putRequest.bucket()).isEqualTo("test-bucket");
        assertThat(putRequest.key()).isEqualTo(resultKey);
        assertThat(putRequest.contentType()).isEqualTo("image/png");
        assertThat(putRequest.contentLength()).isEqualTo((long) content.length);
    }

    @Test
    void store_shouldThrowAppException_whenFileIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.store(null, "location"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_EMPTY);

        verifyNoInteractions(s3Client);
    }

    @Test
    void store_shouldThrowAppException_whenFileIsEmpty() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.store(file, "location"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_EMPTY);

        verifyNoInteractions(s3Client);
    }

    @Test
    void store_shouldThrowAppException_whenLocationIsInvalid() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.store(file, "   "))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_INVALID_PATH);

        verifyNoInteractions(s3Client);
    }

    @Test
    void store_shouldThrowAppException_whenS3UploadFails() throws IOException {
        // Arrange
        String location = "hotel-booking/facilities";
        String originalFilename = "test.png";
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getSize()).thenReturn(100L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[100]));
        when(file.getContentType()).thenReturn("image/png");

        doThrow(SdkException.builder().message("S3 error").build())
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.store(file, location))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_UPLOAD_FAILED);
    }

    // ===== resolveUrl Tests =====

    @Test
    void resolveUrl_shouldReturnFullUrl_whenPathIsValid() {
        // Arrange
        String path = "hotel-booking/facilities/icon.png";

        // Act
        String resolvedUrl = s3FileStorageService.resolveUrl(path);

        // Assert
        assertThat(resolvedUrl).isEqualTo("https://cdn.example.com/hotel-booking/facilities/icon.png");
    }

    @Test
    void resolveUrl_shouldReturnNull_whenPathIsEmpty() {
        // Act & Assert
        assertThat(s3FileStorageService.resolveUrl("")).isNull();
        assertThat(s3FileStorageService.resolveUrl(null)).isNull();
    }

    @Test
    void resolveUrl_shouldThrowAppException_whenPathIsInvalid() {
        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.resolveUrl(".."))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_INVALID_PATH);
    }

    // ===== delete Tests =====

    @Test
    void delete_shouldCallS3Delete_whenPathIsValid() {
        // Arrange
        String path = "hotel-booking/facilities/icon.png";

        // Act
        s3FileStorageService.delete(path);

        // Assert
        verify(s3Client).deleteObject(any(Consumer.class));
    }

    @Test
    void delete_shouldDoNothing_whenPathIsEmpty() {
        // Act
        s3FileStorageService.delete("");
        s3FileStorageService.delete(null);

        // Assert
        verifyNoInteractions(s3Client);
    }

    @Test
    void delete_shouldThrowAppException_whenS3DeleteFails() {
        // Arrange
        String path = "hotel-booking/facilities/icon.png";
        doThrow(SdkException.builder().message("Delete failed").build())
                .when(s3Client).deleteObject(any(Consumer.class));

        // Act & Assert
        assertThatThrownBy(() -> s3FileStorageService.delete(path))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_DELETE_FAILED);
    }
}
