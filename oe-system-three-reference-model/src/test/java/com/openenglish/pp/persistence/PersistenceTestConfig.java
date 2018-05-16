package com.openenglish.pp.persistence;

import com.openenglish.substrate.database.DatabaseConfig;
import com.openenglish.substrate.environment.EnvironmentPropertyConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersistenceConfig.class, DatabaseConfig.class})
public class PersistenceTestConfig {

  @Bean
  public static EnvironmentPropertyConfigurer envPropertyPlaceholderConfigurer(){
    EnvironmentPropertyConfigurer environmentPropertyConfigurer = new EnvironmentPropertyConfigurer();
    environmentPropertyConfigurer.setGlobalFileName("pp-global-config.properties");
    environmentPropertyConfigurer.setPropFileName("oe-system-three-reference.properties");
    environmentPropertyConfigurer.setOrder(1);
    return environmentPropertyConfigurer;
  }
}
