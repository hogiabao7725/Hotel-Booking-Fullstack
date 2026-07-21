package com.hogiabao7725.hotelbooking.constant;

public final class CloudinaryConstants {

    // ===== Cloudinary Keys =====
    public static final String FOLDER = "folder";
    public static final String RESOURCE_TYPE = "resource_type";
    public static final String SECURE_URL = "secure_url";
    public static final String PUBLIC_ID = "public_id";

    // ===== Cloudinary Values =====
    public static final String IMAGE = "image";

    // ===== URL =====
    public static final String UPLOAD_SEGMENT = "/upload/";

    // ===== Folder Structure =====
    private static final String ROOT_FOLDER = "hotel-booking";

    public static final String FEATURES = ROOT_FOLDER + "/features";
    public static final String FACILITIES = ROOT_FOLDER + "/facilities";

    public static String profile(Long accountId) {
        return ROOT_FOLDER + "/profiles/" + accountId;
    }

    public static String roomType(Long roomTypeId) {
        return ROOT_FOLDER + "/room-types/" + roomTypeId + "/images";
    }

    private CloudinaryConstants() {}
}