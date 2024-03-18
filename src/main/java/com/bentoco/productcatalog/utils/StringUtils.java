package com.bentoco.productcatalog.utils;

import java.util.UUID;

public class StringUtils {

    public static String prefixedId(String id, String prefix) {
        return id.contains(prefix) ? id : prefix + id;
    }

    public static UUID removePrefix(String id, String prefix) {
        return UUID.fromString(id.replace(prefix, ""));
    }

}
