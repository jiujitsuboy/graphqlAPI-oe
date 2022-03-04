package com.openenglish.hr;


import com.openenglish.hr.persistence.PersistenceConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.hr.service"})
@EnableAutoConfiguration
@Import({PersistenceConfig.class})
public class ServiceConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(@Value("${aws.credentials.access.token.id}") String awsAccessID,
                                                                       @Value("${aws.credentials.secret.token.key}") String awsSecretAccessKey,
                                                                       @Value("${aws.credentials.region}") String awsRegion ){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(awsAccessID, awsSecretAccessKey);
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

}