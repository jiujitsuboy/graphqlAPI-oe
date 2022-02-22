package com.openenglish.pp.service.mapper;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MappingConfig {

  @Bean
  public Mapper mapper() {
    List<String> mappingFiles = Arrays.asList("dozer-bean-mappings.xml");
    DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
    dozerBeanMapper.setMappingFiles(mappingFiles);
    return new Mapper(dozerBeanMapper);
  }
}
