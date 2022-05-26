package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getPersons(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
    }

    public List<PersonsPerLevel> getAllPersonsByLevel(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.getAllPersonsPerLevel(salesforcePurchaserId);
    }

  public List<LicenseDto> getLicenseInfo(String salesforcePurchaserId, String organization) {
      Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
      Preconditions.checkArgument(StringUtils.isNotBlank(organization), "organization should not be null or empty");

      LicenseDto[] sfLicenseDtos =  getLicences(salesforcePurchaserId, organization);

      return Arrays.stream(sfLicenseDtos).collect(Collectors.toList());

  }

  private LicenseDto[] getLicences(String salesforcePurchaserId, String organization) {

        LicenseDto[] licences = {LicenseDto.builder()
            .person(PersonDto.builder()
                .id(1234567890)
                .firstName("Brian")
                .lastName("Redfield")
                .email("brianred@gmail.com")
                .build())
            .id("a0a7c000004NaGGAA0")
            .name("PLID-1489253")
            .organization("Open Mundo")
            .status("Active")
            .privateClasses(10)
            .startDate(LocalDate.of(2020,01,01))
            .endDate(LocalDate.of(2024,01,01))
            .build(),
            LicenseDto.builder()
                .person(PersonDto.builder()
                    .id(987654321)
                    .firstName("Ryan")
                    .lastName("Cooperfiled")
                    .email("ryancop@gmail.com")
                    .build())
                .id("b0a8c3068904NaGGAA0")
                .name("PLID-1233253")
                .organization("Open Mundo")
                .status("Active")
                .privateClasses(20)
                .startDate(LocalDate.of(2021,01,01))
                .endDate(LocalDate.of(2022,01,01))
                .build()};

        return licences;
    }


}
