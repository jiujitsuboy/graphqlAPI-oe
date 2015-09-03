package com.openenglish.pp.persistence;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"com.openenglish.pp.persistence"})
@EntityScan("com.openenglish.pp.persistence")
@EnableJpaRepositories
@EnableAutoConfiguration
public class PersistenceConfig{
}