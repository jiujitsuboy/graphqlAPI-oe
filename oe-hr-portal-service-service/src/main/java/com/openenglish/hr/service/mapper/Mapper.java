package com.openenglish.pp.service.mapper;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.List;

public class Mapper {

  private final org.dozer.Mapper dozerMapper;

  public Mapper(org.dozer.Mapper dozerMapper) {
    this.dozerMapper = dozerMapper;
  }

  public <TARGET> TARGET map(Object source, Class<TARGET> targetClass) {
    return dozerMapper.map(source, targetClass);
  }

  public <TARGET> List<TARGET> mapCollection(Collection<?> sources, Class<TARGET> targetClass) {
    return FluentIterable.from(sources)
        .transform(mappingTransformer(targetClass))
        .toList();
  }

  private <TARGET> Function<Object, TARGET> mappingTransformer(final Class<TARGET> targetClass) {
    return new Function<Object, TARGET>() {
      @Override
      public TARGET apply(Object entity) {
        return map(entity, targetClass);
      }
    };
  }
}