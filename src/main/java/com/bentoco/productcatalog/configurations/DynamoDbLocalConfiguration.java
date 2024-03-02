package com.bentoco.productcatalog.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Profile("default") //todo: change-it
 @Configuration
public class DynamoDbLocalConfiguration {

    private final URI URI_LOCALSTACK = URI.create("http://localhost:4566");
    private final StaticCredentialsProvider STATIC_CREDENTIALS = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("default_access_key", "default_secret_key")
    );

    @Bean
    public DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(STATIC_CREDENTIALS)
                .endpointOverride(URI_LOCALSTACK)
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }
}
