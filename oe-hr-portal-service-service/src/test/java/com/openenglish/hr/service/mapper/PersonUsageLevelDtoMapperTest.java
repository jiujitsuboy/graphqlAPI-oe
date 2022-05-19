package com.openenglish.hr.service.mapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.PersonUsageLevelDto;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.service.util.InterfaceUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.Test;

public class PersonUsageLevelDtoMapperTest {

  @Test
  public void map(){

    LocalDateTime currentTime = LocalDateTime.of(2022,04,16, 0 , 0, 0);

    long personId = 1;
    String firstname = "Mark";
    String lastname = "Stuart";
    String contactId = "";
    LocalDateTime lastActivity = LocalDateTime.of(2022,04,02,0,0,0);


    UsageLevel usageLevel = InterfaceUtil.createUsageLevel(personId, firstname, lastname, contactId, lastActivity);
    Optional<PersonUsageLevelDto> optPersonUsageLevelDto =  PersonUsageLevelDtoMapper.map(usageLevel, currentTime, (personUsageLevel)-> UsageLevelEnum.HIGH);

    assertTrue(optPersonUsageLevelDto.isPresent());

    PersonUsageLevelDto personUsageLevelDto = optPersonUsageLevelDto.get();

    assertThat(personUsageLevelDto.getPerson().getFirstName(), is(firstname));
    assertThat(personUsageLevelDto.getPerson().getLastName(), is(lastname));
    assertThat(personUsageLevelDto.getPerson().getContactId(), is(contactId));
    assertThat(personUsageLevelDto.getUsageLevel(), is(UsageLevelEnum.HIGH));
    assertThat(personUsageLevelDto.getInactiveDays(), is(lastActivity.until(currentTime, ChronoUnit.DAYS)));
  }

  @Test
  public void mapLastActivityDateIsToday(){

    final long ZERO_INACTIVE_DAYS = 0;
    LocalDateTime currentTime = LocalDateTime.of(2022,04,16, 0 , 0, 0);

    long personId = 1;
    String firstname = "Mark";
    String lastname = "Stuart";
    String contactId = "";
    LocalDateTime lastActivity = LocalDateTime.of(2022,04,16,0,0,0);


    UsageLevel usageLevel = InterfaceUtil.createUsageLevel(personId, firstname, lastname, contactId, lastActivity);
    Optional<PersonUsageLevelDto> optPersonUsageLevelDto = PersonUsageLevelDtoMapper.map(usageLevel, currentTime, (personUsageLevel)-> UsageLevelEnum.HIGH);

    assertTrue(optPersonUsageLevelDto.isPresent());

    PersonUsageLevelDto personUsageLevelDto = optPersonUsageLevelDto.get();

    assertThat(personUsageLevelDto.getPerson().getFirstName(), is(firstname));
    assertThat(personUsageLevelDto.getPerson().getLastName(), is(lastname));
    assertThat(personUsageLevelDto.getPerson().getContactId(), is(contactId));
    assertThat(personUsageLevelDto.getUsageLevel(), is(UsageLevelEnum.HIGH));
    assertThat(personUsageLevelDto.getInactiveDays(), is(ZERO_INACTIVE_DAYS));
  }

  @Test
  public void populator(){

    LocalDateTime currentTime = LocalDateTime.of(2022,04,16, 0 , 0, 0);
    LocalDate licenceStartDate = LocalDate.of(2021,06,01);
    LocalDate licenceExpirationDate = LocalDate.of(2022,05,31);

    long personId = 1;
    String firstname = "Mark";
    String lastname = "Stuart";
    String contactId = "";
    LocalDateTime lastActivity = LocalDateTime.of(2022,04,02,0,0,0);

    UsageLevel usageLevel = InterfaceUtil.createUsageLevel(personId, firstname, lastname, contactId, lastActivity);
    Optional<PersonUsageLevelDto> optPersonUsageLevelDto = PersonUsageLevelDtoMapper.map(usageLevel, currentTime, (personUsageLevel)-> UsageLevelEnum.HIGH);

    assertTrue(optPersonUsageLevelDto.isPresent());

    PersonUsageLevelDto personUsageLevelDto = optPersonUsageLevelDto.get();
    PersonUsageLevelDtoMapper.populator(personUsageLevelDto, this::populateLicenceInfo);

    assertThat(personUsageLevelDto.getStart(), is(licenceStartDate));
    assertThat(personUsageLevelDto.getExpiration(), is(licenceExpirationDate));
    assertThat(personUsageLevelDto.getRemainingDays(), is(licenceExpirationDate.until(licenceStartDate, ChronoUnit.DAYS)));

  }

  private void populateLicenceInfo(PersonUsageLevelDto personUsageLevelDto){

    LocalDate licenceStartDate = LocalDate.of(2021,06,01);
    LocalDate licenceExpirationDate = LocalDate.of(2022,05,31);

    personUsageLevelDto.setStart(licenceStartDate);
    personUsageLevelDto.setExpiration(licenceExpirationDate);
    personUsageLevelDto.setRemainingDays(licenceExpirationDate.until(licenceStartDate, ChronoUnit.DAYS));
  }

}