package com.sirnommington.squid.common;

public class StringUtil {
    public static boolean isNullOrWhitespace(String s) {
        return s == null || s.trim().isEmpty();
    }
}
