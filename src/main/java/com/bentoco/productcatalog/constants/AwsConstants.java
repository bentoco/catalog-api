package com.bentoco.productcatalog.constants;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.net.URI;

public class AwsConstants {

    public final static String PREFIX_CATEGORY_TITLE = "#title#";
    public final static String PREFIX_CATEGORY_OWNER_ID = "owner_id#";
    public static final URI URI_LOCALSTACK = URI.create("http://localhost:4566");
    public static final StaticCredentialsProvider STATIC_CREDENTIALS = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("default_access_key", "default_secret_key")
    );
}
