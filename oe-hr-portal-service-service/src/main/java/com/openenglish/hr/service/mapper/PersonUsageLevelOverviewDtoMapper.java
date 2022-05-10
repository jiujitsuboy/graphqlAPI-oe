package com.openenglish.hr.service.mapper;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.PersonUsageLevelOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevels;
import java.util.function.Function;

public class PersonUsageLevelOverviewDtoMapper {

  public static PersonUsageLevelOverviewDto map(UsageLevels usageLevel, Function<UsageLevels,UsageLevelEnum> mapStudentsToUsageLevel){
      return PersonUsageLevelOverviewDto
          .builder()
          .name(String.format("%s %s ",usageLevel.getFirstname(),usageLevel.getLastname()))
          .usageLevel(mapStudentsToUsageLevel.apply(usageLevel))
          .build();
  }
}
