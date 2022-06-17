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

import java.time.Clock;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.hr.service"})
@EnableAutoConfiguration
@Import({PersistenceConfig.class})
public class ServiceConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(@Value("${COGNITO_AWS_HR_PORTAL_ACCESS_KEY_ID}") String awsAccessID,
                                                                       @Value("${COGNITO_AWS_HR_PORTAL_ACCESS_SECRET_KEY}") String awsSecretAccessKey){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(awsAccessID, awsSecretAccessKey);
        return CognitoIdentityProviderClient.builder()
                //Region not specified explicity, it is grab of the default region configured in the credentials file of the user profile
                //TODO : setup env vars. LE: it does not pick up the region in ~/oe-hr.props
                .region(Region.EU_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    @Bean
    public Clock clockForLocalDateTimes(){
        return Clock.systemDefaultZone();
    }

}