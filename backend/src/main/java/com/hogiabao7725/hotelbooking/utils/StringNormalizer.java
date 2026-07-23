package com.hogiabao7725.hotelbooking.utils;

public final class StringNormalizer {

    private StringNormalizer() {
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.strip();
        return normalized.isEmpty() ? null : normalized;
    }
}
