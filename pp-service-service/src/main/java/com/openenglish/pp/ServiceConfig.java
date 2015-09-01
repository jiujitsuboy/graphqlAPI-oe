package com.openenglish.pp;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.pp.service"})
@EnableAutoConfiguration
//@Import({PersistenceConfig.class})
public class ServiceConfig {

}