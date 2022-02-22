package com.openenglish.hr;


import com.openenglish.hr.persistence.PersistenceConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.hr.service"})
@EnableAutoConfiguration
@Import({PersistenceConfig.class})
public class ServiceConfig {

}