package com.openenglish.hr.persistence;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.hr.persistence"})
@EntityScan("com.openenglish.hr.persistence")
@EnableJpaRepositories
@EnableAutoConfiguration
public class PersistenceConfig{
}