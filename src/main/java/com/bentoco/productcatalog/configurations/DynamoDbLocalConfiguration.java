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

import static com.bentoco.productcatalog.constants.AwsConstants.STATIC_CREDENTIALS;
import static com.bentoco.productcatalog.constants.AwsConstants.URI_LOCALSTACK;

@Profile("default")
@Configuration
public class DynamoDbLocalConfiguration {

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
