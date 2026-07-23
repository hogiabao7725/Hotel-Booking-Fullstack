package com.hogiabao7725.hotelbooking.constant;

import java.util.Set;

public final class FileConstants {

    private FileConstants() {
    }

    public static final long MAX_ICON_SIZE = 500L * 1024;
    public static final long MAX_AVATAR_SIZE = 2L * 1024 * 1024;
    public static final long MAX_ROOM_IMAGE_SIZE = 10L * 1024 * 1024;

    public static final String SVG = "image/svg+xml";

    public static final String[] IMAGE_TYPES = {
            "image/jpeg",
            "image/png",
            "image/webp"
    };
}
