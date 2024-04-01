package com.bentoco.productcatalog.utils;

import java.util.UUID;

public class StringUtils {

    public static String prefixedId(String value, String prefix) {
        return value.contains(prefix) ? value : prefix + value;
    }

    public static UUID removePrefix(String value, String prefix) {
        return UUID.fromString(value.replace(prefix, ""));
    }

}
