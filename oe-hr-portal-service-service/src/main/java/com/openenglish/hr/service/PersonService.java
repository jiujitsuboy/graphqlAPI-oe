package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
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
    private final SalesforceClient salesforceClient;

    public List<Person> getPersons(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
    }

    public List<PersonsPerLevel> getAllPersonsByLevel(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.getAllPersonsPerLevel(salesforcePurchaserId);
    }

  public List<LicenseDto>getLicenseInfo(String salesforcePurchaserId, String organization) {
      Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
      Preconditions.checkArgument(StringUtils.isNotBlank(organization), "organization should not be null or empty");

     SfLicenseDto[] sfLicenseDtos = salesforceClient.getPurchaserLicenses(salesforcePurchaserId, organization);

      return Arrays.stream(sfLicenseDtos)
          .map(this::mapLicenseDto)
          .collect(Collectors.toList());
  }

  /**
   * Map from SfLicenseDto to LicenseDto object
   * @param sfLicenseDto license information
   * @return LicenseDto
   */
  private LicenseDto mapLicenseDto(SfLicenseDto sfLicenseDto){
    final String ONE_THOUSAND_FIVE_HUNDRED = "1500";
    final String UNLIMITED = "Unlimited";

    String names[] = sfLicenseDto.getStudent().getName().split(" ");

    String firstName = names != null && names.length > 0 ? names[0] : "";
    String lastName = names != null && names.length > 1  ? names[1] : "";

    String privateClasses = sfLicenseDto.getPrivateClasses().equals(ONE_THOUSAND_FIVE_HUNDRED) ? UNLIMITED : sfLicenseDto.getPrivateClasses();

    org.joda.time.LocalDate startDate = sfLicenseDto.getStartDate();
    org.joda.time.LocalDate endDate = sfLicenseDto.getEndDate();

    return LicenseDto.builder()
        .person(PersonDto.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(sfLicenseDto.getStudent().getEmail())
            .build())
        .licenseId(sfLicenseDto.getLicenseId())
        .name(sfLicenseDto.getName())
        .organization(sfLicenseDto.getOrganization())
        .status(sfLicenseDto.getStatus())
        .privateClasses(privateClasses)
        .startDate(LocalDate.of(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth()))
        .endDate(LocalDate.of(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth()))
        .build();
  }

  private LicenseDto[] getLicences(String salesforcePurchaserId, String organization) {

        LicenseDto[] licences = {LicenseDto.builder()
            .person(PersonDto.builder()
                .id(1234567890)
                .firstName("Brian")
                .lastName("Redfield")
                .email("brianred@gmail.com")
                .build())
            .licenseId("a0a7c000004NaGGAA0")
            .name("PLID-1489253")
            .organization("Open Mundo")
            .status("Active")
            .privateClasses("10")
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
                .licenseId("b0a8c3068904NaGGAA0")
                .name("PLID-1233253")
                .organization("Open Mundo")
                .status("Active")
                .privateClasses("20")
                .startDate(LocalDate.of(2021,01,01))
                .endDate(LocalDate.of(2022,01,01))
                .build()};

        return licences;
    }


}
