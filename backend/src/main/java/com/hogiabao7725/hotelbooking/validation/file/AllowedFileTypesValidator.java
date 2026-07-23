package com.hogiabao7725.hotelbooking.validation.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedFileTypesValidator
        implements ConstraintValidator<AllowedFileTypes, MultipartFile> {

    private Set<String> allowedTypes;

    @Override
    public void initialize(AllowedFileTypes constraintAnnotation) {
        this.allowedTypes = Arrays.stream(constraintAnnotation.value())
                .filter(type -> type != null && !type.isBlank())
                .map(this::normalize)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        return allowedTypes.contains(normalize(contentType));
    }

    private String normalize(String contentType) {
        return contentType
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
