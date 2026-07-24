package com.hogiabao7725.hotelbooking.dto.request.feature;

import com.hogiabao7725.hotelbooking.constant.FileConstants;
import com.hogiabao7725.hotelbooking.utils.StringNormalizer;
import com.hogiabao7725.hotelbooking.validation.file.AllowedFileTypes;
import com.hogiabao7725.hotelbooking.validation.file.MaxFileSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record FeatureUpdateRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 5, max = 55, message = "Name must be between {min} and {max} characters")
        String name,

        String description,

        @MaxFileSize(FileConstants.MAX_ICON_SIZE)
        @AllowedFileTypes(FileConstants.SVG)
        MultipartFile icon
) {
    public FeatureUpdateRequest {
        name = StringNormalizer.trimToNull(name);
        description = StringNormalizer.trimToNull(description);
    }
}
