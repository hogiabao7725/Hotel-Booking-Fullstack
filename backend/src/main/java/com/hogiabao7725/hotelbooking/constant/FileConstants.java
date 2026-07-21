package com.hogiabao7725.hotelbooking.constant;

import java.util.Set;

public final class FileConstants {

    private FileConstants() {
    }

    public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L; // 10 MB

    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/svg+xml"
    );
}
