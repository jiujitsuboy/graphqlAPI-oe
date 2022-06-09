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
    String privateClasses = sfLicenseDto.getPrivateClasses();
    String privateClassesHomologate = privateClasses != null && privateClasses.equals(ONE_THOUSAND_FIVE_HUNDRED) ? UNLIMITED : privateClasses;
    String studentEmail =  sfLicenseDto.getStudent() != null ? sfLicenseDto.getStudent().getEmail() : "";

    return LicenseDto.builder()
        .person(PersonDto.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(studentEmail)
            .build())
        .licenseId(sfLicenseDto.getLicenseId())
        .name(sfLicenseDto.getName())
        .organization(sfLicenseDto.getOrganization())
        .status(sfLicenseDto.getStatus())
        .privateClasses(privateClassesHomologate)
        .startDate(convertFromJodaTimeToJavaTime(sfLicenseDto.getStartDate()))
        .endDate(convertFromJodaTimeToJavaTime(sfLicenseDto.getEndDate()))
        .build();
  }

  /**
   * Convert from joda time to java time
   * @param jodaLocalDate joda localDate
   * @return java time localDate
   */
  private LocalDate convertFromJodaTimeToJavaTime(org.joda.time.LocalDate jodaLocalDate) {

    return jodaLocalDate != null ?
        LocalDate.of(jodaLocalDate.getYear(), jodaLocalDate.getMonthOfYear(), jodaLocalDate.getDayOfMonth()) : null;
  }
}
