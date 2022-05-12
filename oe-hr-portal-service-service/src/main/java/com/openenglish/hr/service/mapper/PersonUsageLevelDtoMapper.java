package com.openenglish.hr.service.mapper;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonUsageLevelDto;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class PersonUsageLevelDtoMapper {

  public static PersonUsageLevelDto map(
      UsageLevel usageLevel, LocalDateTime currentTime,
      Function<UsageLevel, UsageLevelEnum> mapStudentsToUsageLevel) {
    return PersonUsageLevelDto
        .builder()
        .person(
            PersonDto.builder()
                .firstName(usageLevel.getFirstname())
                .lastName(usageLevel.getLastname())
                .contactId(usageLevel.getContactId())
                .build())
        .usageLevel(mapStudentsToUsageLevel.apply(usageLevel))
        .inactiveDays(usageLevel.getLastActivity().until(currentTime, ChronoUnit.DAYS))
        .build();
  }

  public static void populator(PersonUsageLevelDto personUsageLevelDto,
      Consumer<PersonUsageLevelDto> mapLicenseInfoToUsageLevel) {
    mapLicenseInfoToUsageLevel.accept(personUsageLevelDto);
  }
}
