package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;
  private final SalesforceClient salesforceClient;
  private final ActivityService activityService;
  private final Clock clock;

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

    LocalDateTime currentTime = LocalDateTime.now(clock);
    SfLicenseDto[] sfLicenseDtos = salesforceClient.getPurchaserLicenses(salesforcePurchaserId, organization);

    Set<String> contactsId = Arrays.stream(sfLicenseDtos)
        .map(sfLicenseDto -> {
          SfLicenseDto.StudentDto studentDto = sfLicenseDto.getStudent();
          return studentDto != null ? studentDto.getContactId() : "";
        }).collect(Collectors.toSet());

    List<UsageLevel> usageLevels = activityService.getMaxActivityDateGroupedByPerson(salesforcePurchaserId, contactsId);

    Map<String, LocalDateTime> lastActivityByContactId = usageLevels.stream()
        .collect(Collectors.toMap(UsageLevel::getContactId, UsageLevel::getLastActivity));

    return Arrays.stream(sfLicenseDtos)
        .map(this::mapLicenseDto)
        .map(licenseDto -> mapInactiveDaysToLicense(licenseDto, lastActivityByContactId, currentTime))
        .collect(Collectors.toList());
  }

  /**
   * Calculate and set the inactive days for certain license
   * @param licenseDto license dto
   * @param lastActivityByContactId map with all the contacts id with their respective last activity time
   * @param currentTime the current time of the system.
   * @return LicenseDto
   */
  private LicenseDto mapInactiveDaysToLicense(LicenseDto licenseDto, Map<String, LocalDateTime> lastActivityByContactId, LocalDateTime currentTime){
    final long NO_INACTIVE_DAYS = -1;

    LocalDateTime lastActivity = lastActivityByContactId.get(licenseDto.getPerson().getContactId());
    licenseDto.setInactiveDays(lastActivity != null ? lastActivity.until(currentTime, ChronoUnit.DAYS): NO_INACTIVE_DAYS);

    return licenseDto;
  }

  /**
   * Map from SfLicenseDto to LicenseDto object
   *
   * @param sfLicenseDto license information
   * @return LicenseDto
   */
  private LicenseDto mapLicenseDto(SfLicenseDto sfLicenseDto) {
    final String ONE_THOUSAND_FIVE_HUNDRED = "1500";
    final String UNLIMITED = "Unlimited";

    String[] names = sfLicenseDto.getStudent().getName().split(" ");
    String firstName = names.length > 0 ? names[0] : "";
    String lastName = names.length > 1 ? names[1] : "";
    String privateClasses = sfLicenseDto.getPrivateClasses();
    String privateClassesHomologate = privateClasses != null && privateClasses.equals(ONE_THOUSAND_FIVE_HUNDRED) ? UNLIMITED
                                      : privateClasses;

    String studentEmail = "";
    String studentContactId = "";

    if(sfLicenseDto.getStudent() != null){
      studentEmail = sfLicenseDto.getStudent().getEmail();
      studentContactId = sfLicenseDto.getStudent().getContactId();
    }

    return LicenseDto.builder()
        .person(PersonDto.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(studentEmail)
            .contactId(studentContactId)
            .build())
        .licenseId(sfLicenseDto.getLicenseId())
        .name(sfLicenseDto.getName())
        .organization(sfLicenseDto.getOrganization())
        .status(homologateStatus(sfLicenseDto.getStatus()))
        .privateClasses(privateClassesHomologate)
        .startDate(convertFromJodaTimeToJavaTime(sfLicenseDto.getStartDate()))
        .endDate(convertFromJodaTimeToJavaTime(sfLicenseDto.getEndDate()))
        .build();
  }

  /**
   * change the status value from saleforce to a standard one form oe
   * @param status salesforce status
   * @return oe status
   */
  public String homologateStatus(String status) {
    String homologatedStatus = null;

    switch (status) {
      case "Active":
        homologatedStatus = "Active";
        break;
      case "Inactive":
        homologatedStatus = "Assigned";
        break;
      case "New":
        homologatedStatus = "Not Assigned";
        break;
    }

    return homologatedStatus;
  }

  /**
   * Convert from joda time to java time
   *
   * @param jodaLocalDate joda localDate
   * @return java time localDate
   */
  private LocalDate convertFromJodaTimeToJavaTime(org.joda.time.LocalDate jodaLocalDate) {

    return jodaLocalDate != null ?
        LocalDate.of(jodaLocalDate.getYear(), jodaLocalDate.getMonthOfYear(),
            jodaLocalDate.getDayOfMonth()) : null;
  }
  public Optional<HRManagerDto> getHRManager(String salesforcePurchaserId, String organization) {
    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(organization), "organization should not be null or empty");

    return getHRManagerInfo(salesforcePurchaserId, organization);
  }

  private Optional<HRManagerDto> getHRManagerInfo(String salesforcePurchaserId, String organization) {
    return Optional.of(HRManagerDto.builder()
        .id("0037c0000155DX4AAM")
        .name("Andrea OM")
        .email("andrea.bragoli+testt@openenglish.com")
        .preferredLanguage("en-US")
        .build());
  }
}