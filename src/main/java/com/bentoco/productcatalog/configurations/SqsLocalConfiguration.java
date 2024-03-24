package com.bentoco.productcatalog.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import static com.bentoco.productcatalog.constants.AwsConstants.STATIC_CREDENTIALS;
import static com.bentoco.productcatalog.constants.AwsConstants.URI_LOCALSTACK;

@Configuration
public class SqsLocalConfiguration {

    @Bean
    public SqsClient getStsClient() {
        return SqsClient.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(STATIC_CREDENTIALS)
                .endpointOverride(URI_LOCALSTACK)
                .build();
    }
}
