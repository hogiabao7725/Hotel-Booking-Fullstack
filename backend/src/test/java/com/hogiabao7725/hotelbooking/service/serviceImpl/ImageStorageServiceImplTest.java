package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.hogiabao7725.hotelbooking.constant.CloudinaryConstants;
import com.hogiabao7725.hotelbooking.constant.FileConstants;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStorageServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageStorageServiceImpl imageStorageService;

    // ===== Upload Tests =====
    @Test
    void upload_shouldReturnSecureUrl_whenFileIsValid() throws IOException {
        // Arrange
        String folder = "hotel-booking/test";
        String publicId = "test-image-id";
        String expectedSecureUrl = "https://res.cloudinary.com/demo/image/upload/v12345/hotel-booking/test/test-image-id.png";

        InputStream inputStream = new ByteArrayInputStream("dummy image content".getBytes());

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(inputStream);

        Map<String, Object> expectedUploadOptions = Map.of(
                CloudinaryConstants.FOLDER, folder,
                CloudinaryConstants.PUBLIC_ID, publicId,
                CloudinaryConstants.RESOURCE_TYPE, CloudinaryConstants.IMAGE
        );

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(InputStream.class), eq(expectedUploadOptions)))
                .thenReturn(Map.of(CloudinaryConstants.SECURE_URL, expectedSecureUrl));

        // Act
        String result = imageStorageService.upload(file, folder, publicId);

        // Assert
        assertThat(result).isEqualTo(expectedSecureUrl);
        verify(uploader).upload(any(InputStream.class), eq(expectedUploadOptions));
    }

    @Test
    void upload_shouldThrowAppException_whenFileIsEmpty() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.upload(file, "folder", "publicId"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_EMPTY);

        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_shouldThrowAppException_whenFileSizeExceedsLimit() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(FileConstants.MAX_IMAGE_SIZE + 1);

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.upload(file, "folder", "publicId"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_SIZE_EXCEEDED);

        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_shouldThrowAppException_whenFileTypeIsInvalid() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("application/pdf");

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.upload(file, "folder", "publicId"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_INVALID_TYPE);

        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_shouldThrowAppException_whenCloudinaryUploadFails() throws IOException {
        // Arrange
        InputStream inputStream = new ByteArrayInputStream("dummy content".getBytes());

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getInputStream()).thenReturn(inputStream);

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(InputStream.class), anyMap())).thenThrow(new IOException("Cloudinary error"));

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.upload(file, "folder", "publicId"))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_UPLOAD_FAILED);
    }

    // ===== Delete Tests =====

    @Test
    void delete_shouldDoNothing_whenFileUrlIsNullOrEmpty() {
        // Act
        imageStorageService.delete(null);
        imageStorageService.delete("   ");

        // Assert
        verifyNoInteractions(cloudinary);
    }

    @Test
    void delete_shouldDeleteFileSuccessfully_whenFileUrlIsValid() throws IOException {
        // Arrange
        String fileUrl = "https://res.cloudinary.com/demo/image/upload/v1712345/hotel-booking/profiles/123/avatar.jpg";
        String expectedPublicId = "hotel-booking/profiles/123/avatar";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq(expectedPublicId), eq(Collections.emptyMap())))
                .thenReturn(Map.of("result", "ok"));

        // Act
        imageStorageService.delete(fileUrl);

        // Assert
        verify(uploader).destroy(eq(expectedPublicId), eq(Collections.emptyMap()));
    }

    @Test
    void delete_shouldThrowAppException_whenUrlIsInvalid() {
        // Arrange
        String invalidUrl = "https://res.cloudinary.com/demo/image/invalid-path/avatar.jpg";

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.delete(invalidUrl))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_INVALID_URL);

        verifyNoInteractions(cloudinary);
    }

    @Test
    void delete_shouldThrowAppException_whenCloudinaryDestroyFails() throws IOException {
        // Arrange
        String fileUrl = "https://res.cloudinary.com/demo/image/upload/v1712345/hotel-booking/test/image.png";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException("Delete failed"));

        // Act & Assert
        assertThatThrownBy(() -> imageStorageService.delete(fileUrl))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_DELETE_FAILED);
    }
}
