package com.openenglish.hr.service.mapper;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonUsageLevelDto;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevels;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class PersonUsageLevelDtoMapper {

  public static PersonUsageLevelDto map(UsageLevels usageLevel, LocalDateTime currentTime, Function<UsageLevels,UsageLevelEnum> mapStudentsToUsageLevel){
      return PersonUsageLevelDto
          .builder()
          .person(
              PersonDto.builder()
                  .firstName(usageLevel.getFirstname())
                  .lastName(usageLevel.getLastname())
                  .build())
          .usageLevel(mapStudentsToUsageLevel.apply(usageLevel))
          .inactiveDays(usageLevel.getLastActivity().until(currentTime, ChronoUnit.DAYS))
          .build();
  }
}
