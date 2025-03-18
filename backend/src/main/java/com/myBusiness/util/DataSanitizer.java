package com.myBusiness.util;

import java.util.regex.Pattern;

/**
 * DataSanitizer provides utility methods for sanitizing input data.
 * It removes potentially harmful HTML tags and trims leading and trailing whitespace.
 */
public final class DataSanitizer {

    // Precompiled pattern for matching HTML tags
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<.*?>");

    // Private constructor to prevent instantiation of this utility class
    private DataSanitizer() {
        throw new AssertionError("Cannot instantiate DataSanitizer");
    }

    /**
     * Sanitizes the given input string by removing HTML tags and trimming whitespace.
     *
     * @param input the string to sanitize.
     * @return a sanitized version of the input string, or null if the input is null.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Remove HTML tags using the precompiled pattern and trim the string.
        return HTML_TAG_PATTERN.matcher(input).replaceAll("").trim();
    }
}
