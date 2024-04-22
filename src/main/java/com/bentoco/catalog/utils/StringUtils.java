package com.bentoco.catalog.utils;

public class StringUtils {

    public static String prefixedId(String value, String prefix) {
        return value.contains(prefix) ? value : prefix + value;
    }

    public static String removePrefix(String value, String prefix) {
        return value.replace(prefix, "");
    }

}
