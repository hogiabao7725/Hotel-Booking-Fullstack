package com.hogiabao7725.hotelbooking.constant;

public final class StorageConstants {

    public static final String FACILITIES = "facilities";
    public static final String FEATURES = "features";

    private StorageConstants() {
    }

    public static String profile(Long accountId) {
        return "profiles/" + accountId;
    }

    public static String roomTypeImages(Long roomTypeId) {
        return "room-types/" + roomTypeId + "/images";
    }
}
