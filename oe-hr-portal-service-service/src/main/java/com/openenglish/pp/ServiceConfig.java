package com.openenglish.pp;


import com.openenglish.pp.persistence.PersistenceConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.pp.service"})
@EnableAutoConfiguration
@Import({PersistenceConfig.class})
public class ServiceConfig {

}