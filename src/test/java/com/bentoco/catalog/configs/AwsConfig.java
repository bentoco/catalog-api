package com.bentoco.catalog.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@TestConfiguration(proxyBeanMethods = false)
public class AwsConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(LocalStackContainer localStack) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey());
        return StaticCredentialsProvider.create(credentials);
    }
}