package com.bentoco.catalog.constants;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.net.URI;

public class AwsConstants {

    public static final String PREFIX_CATEGORY_TITLE = "#title#";
    public static final String PREFIX_CATEGORY_OWNER_ID = "owner_id#";
    public static final String CATEGORIES_TABLE_NAME = "categories";
    public static final String PRODUCT_TABLE_NAME = "products";
    public static final URI URI_LOCALSTACK = URI.create("http://localhost:4566");
    public static final StaticCredentialsProvider STATIC_CREDENTIALS = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("default_access_key", "default_secret_key")
    );
}
