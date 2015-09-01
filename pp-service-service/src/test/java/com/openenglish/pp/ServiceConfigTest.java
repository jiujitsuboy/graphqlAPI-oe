package com.openenglish.pp;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
//@ImportResource("classpath:common-datasource.xml")
@Import({ServiceConfig.class})
public class ServiceConfigTest {
    //Need to have this to resolve the variable replacement for the datasource.
//    @Bean
//    public static EnvironmentPropertyConfigurer environmentConfigurer(){
//        EnvironmentPropertyConfigurer environmentPropertyConfigurer = new EnvironmentPropertyConfigurer();
//        environmentPropertyConfigurer.setPropFileName("pp-service.properties");
//        environmentPropertyConfigurer.setOrder(1);
//        return environmentPropertyConfigurer;
//    }
//
//    @Bean
//    public static DbPropertySourcesPlaceholderConfigurer dbPropertySourcesPlaceholderConfigurer(){
//        DbPropertySourcesPlaceholderConfigurer
//            dbPropertySourcesPlaceholderConfigurer =
//            new DbPropertySourcesPlaceholderConfigurer();
//        dbPropertySourcesPlaceholderConfigurer.setOrder(2);
//        return dbPropertySourcesPlaceholderConfigurer;
//    }
}