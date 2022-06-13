package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.hr.service.util.InterfaceUtil;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfHrManagerInfoDto;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
import com.openenglish.sfdc.client.dto.SfLicenseDto.StudentDto;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PersonServiceTest {

    @Injectable
    private PersonRepository personRepository;
    @Injectable
    private SalesforceClient salesforceClient;
    @Injectable
    private ActivityService activityService;
    @Tested
    private PersonService personService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Injectable
    private Clock clock;

    @Test
    public void getPersons(){
        String salesforcePurchaserId = "12345";

        List<Person> personsExpected = List.of(Person.builder()
                .id(1L)
                .firstName("joseph")
                .lastName("murray")
                .email("fake1@openenglish.com")
                .contactId("2")
                .details(PersonDetail.builder()
                    .id(12L)
                    .salesforcePurchaserId("12345")
                    .build())
                .workingLevel(Level.builder()
                    .id(1L)
                    .active(true)
                    .description("description1")
                    .highScoreBoundary(new BigDecimal(100))
                    .lowScoreBoundary(new BigDecimal(0))
                    .levelNum("1")
                    .name("level 1")
                    .numImmersionRequired(123)
                    .numLiveRequired(456)
                    .sequence(111)
                    .build())
                .build(),
            Person.builder()
                .id(2L)
                .firstName("mary")
                .lastName("jonshon")
                .email("fake2@openenglish.com")
                .contactId("3")
                .details(PersonDetail.builder()
                    .id(22L)
                    .salesforcePurchaserId("12345")
                    .build())
                .workingLevel(Level.builder()
                    .id(2L)
                    .active(true)
                    .description("description2")
                    .highScoreBoundary(new BigDecimal(100))
                    .lowScoreBoundary(new BigDecimal(0))
                    .levelNum("2")
                    .name("level 2")
                    .numImmersionRequired(321)
                    .numLiveRequired(654)
                    .sequence(222)
                    .build())
                .build());

        new Expectations() {{
            personRepository.findPersonByDetailsSalesforcePurchaserId(anyString);
            returns(personsExpected);
        }};

        List<Person> persons =  personService.getPersons(salesforcePurchaserId);

        assertNotNull(persons);
    }

    @Test
    public void getPersonsEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        List<Person> persons =  personService.getPersons(salesforcePurchaserId);
    }

    @Test
    public void getAllPersonsByLevel(){

        String salesforcePurchaserId = "12345";

        List<PersonsPerLevel> personsPerLevelExpected = List.of(new PersonsPerLevel() {
            @Override
            public String getLevelName() {
                return "Level 1";
            }

            @Override
            public long getTotalNumber() {
                return 42;
            }
        }, new PersonsPerLevel() {
            @Override
            public String getLevelName() {
                return "Level 2";
            }

            @Override
            public long getTotalNumber() {
                return 56;
            }
        });

        new Expectations() {{
            personRepository.getAllPersonsPerLevel(anyString);
            returns(personsPerLevelExpected);
        }};

        List<PersonsPerLevel> personsPerLevels =  personService.getAllPersonsByLevel(salesforcePurchaserId);

        assertNotNull(personsPerLevels);

    }

    @Test
    public void getAllPersonsByLevelEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        List<PersonsPerLevel> personsPerLevels =  personService.getAllPersonsByLevel(salesforcePurchaserId);
    }

    @Test
    public void getLicenseInfo(){
        final int TWO_RECORDS = 2;
        String salesforcePurchaserId = "12345";
        String organization = "Open Mundo";

        LocalDateTime currentTime = LocalDateTime.of(2022,6,10, 0, 0, 0);
        Clock fixedClock = Clock.fixed(currentTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        SfLicenseDto sfLicenseDto1 = new SfLicenseDto();

        SfLicenseDto.StudentDto studentDto1 = new StudentDto();
        studentDto1.setContactId("sf_synegen123");
        studentDto1.setName("Brian Redfield");
        studentDto1.setEmail("brianred@gmail.com");

        sfLicenseDto1.setStartDate(new org.joda.time.LocalDate (2020,1,1));
        sfLicenseDto1.setEndDate(new org.joda.time.LocalDate(2024,1,1));
        sfLicenseDto1.setStudent(studentDto1);
        sfLicenseDto1.setOrganization("Open Mundo");
        sfLicenseDto1.setName("PLID-1489253");
        sfLicenseDto1.setStatus("Active");
        sfLicenseDto1.setLicenseId("a0a7c000004NaGGAA0");
        sfLicenseDto1.setPrivateClasses("10");


        SfLicenseDto sfLicenseDto2 = new SfLicenseDto();

        SfLicenseDto.StudentDto studentDto2 = new StudentDto();
        studentDto2.setContactId("sf_synegen321");
        studentDto2.setName("Ryan Cooperfiled");
        studentDto2.setEmail("ryancop@gmail.com");

        sfLicenseDto2.setStartDate(new org.joda.time.LocalDate (2021,1,1));
        sfLicenseDto2.setEndDate(new org.joda.time.LocalDate(2022,1,1));
        sfLicenseDto2.setStudent(studentDto2);
        sfLicenseDto2.setOrganization("Open Mundo");
        sfLicenseDto2.setName("PLID-1233253");
        sfLicenseDto2.setStatus("Inactive");
        sfLicenseDto2.setLicenseId("b0a8c3068904NaGGAA0");
        sfLicenseDto2.setPrivateClasses("20");

        SfLicenseDto [] licences = new SfLicenseDto[] {sfLicenseDto1, sfLicenseDto2};

        List<UsageLevel> usageLevels = List.of(
            InterfaceUtil.createUsageLevel(110001, "Patrik", "Smith","sf_synegen123", LocalDateTime.of(2022, 4, 15, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110002, "Michale", "Bale", "sf_synegen321",LocalDateTime.of(2022, 3, 23, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110003, "Jake", "Sullivan","sf_synegen456", LocalDateTime.of(2022, 3, 20, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110004, "Claire", "Redfield","sf_synegen654", LocalDateTime.of(2023, 4, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110005, "Ana", "Stuart", "sf_synegen789",LocalDateTime.of(2022, 4, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110006, "Sam", "Walmart", "sf_synegen987",LocalDateTime.of(2022, 3, 29, 0, 0, 0))
        );

        Map<String,Long> InactiveDaysByContactID = usageLevels.stream().collect(Collectors.toMap(UsageLevel::getContactId, value -> value.getLastActivity().until(currentTime, ChronoUnit.DAYS)));

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());
            salesforceClient.getPurchaserLicenses(anyString, anyString);
            returns(licences);
            activityService.getMaxActivityDateGroupedByPerson(anyString, (Set<String>)any);
            returns(usageLevels);
        }};

        List<LicenseDto> licenseDtos =  personService.getLicenseInfo(salesforcePurchaserId,organization);

        assertEquals(licenseDtos.size(), TWO_RECORDS);

        for (int licenceIndex = 0; licenceIndex < licenseDtos.size(); licenceIndex++) {
            assertThat(licenseDtos.get(licenceIndex).getLicenseId(), is(licences[licenceIndex].getLicenseId()));
            assertThat(licenseDtos.get(licenceIndex).getName(), is(licences[licenceIndex].getName()));
            assertThat(licenseDtos.get(licenceIndex).getPrivateClasses(), is(licences[licenceIndex].getPrivateClasses()));
            assertThat(licenseDtos.get(licenceIndex).getOrganization(), is(licences[licenceIndex].getOrganization()));
            assertThat(licenseDtos.get(licenceIndex).getStatus(), is(personService.homologateStatus(licences[licenceIndex].getStatus())));
            assertThat(licenseDtos.get(licenceIndex).getStartDate().toString(), is(licences[licenceIndex].getStartDate().toString()));
            assertThat(licenseDtos.get(licenceIndex).getEndDate().toString(), is(licences[licenceIndex].getEndDate().toString()));
            assertThat(licenseDtos.get(licenceIndex).getPerson().getFirstName(), is(licences[licenceIndex].getStudent().getName().split(" ")[0]));
            assertThat(licenseDtos.get(licenceIndex).getPerson().getLastName(), is(licences[licenceIndex].getStudent().getName().split(" ")[1]));
            assertThat(licenseDtos.get(licenceIndex).getPerson().getEmail(), is(licences[licenceIndex].getStudent().getEmail()));
            assertThat(licenseDtos.get(licenceIndex).getInactiveDays(), is(InactiveDaysByContactID.get(licenseDtos.get(licenceIndex).getPerson().getContactId())));
        }
    }

    @Test
    public void getLicenseInfoEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        String organization = "Open Mundo";
        personService.getLicenseInfo(salesforcePurchaserId,organization);
    }

    @Test
    public void getLicenseInfoEmptyOrganization(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("organization should not be null or empty");
        String salesforcePurchaserId = "12345";
        String organization = "";
        personService.getLicenseInfo(salesforcePurchaserId,organization);
    }

    @Test
    public void getHRManager(){
        String salesforcePurchaserId = "12345";
        String organization = "Open Mundo";

        SfHrManagerInfoDto expectedSfHrManagerInfoDto = new SfHrManagerInfoDto();
        expectedSfHrManagerInfoDto.setContactId("0037c0000155DX4AAM");
        expectedSfHrManagerInfoDto.setName("Andrea OM");
        expectedSfHrManagerInfoDto.setEmail("andrea.bragoli+testt@openenglish.com");
        expectedSfHrManagerInfoDto.setPreferredLanguage("es-US");

        new Expectations() {{
            salesforceClient.getHrManagerInfo(anyString, anyString);
            returns(expectedSfHrManagerInfoDto);
        }};

        Optional<SfHrManagerInfoDto> optHrManagerDto = personService.getHRManager(salesforcePurchaserId,organization);
        assertTrue(optHrManagerDto.isPresent());

        SfHrManagerInfoDto hrManagerDto = optHrManagerDto.get();

        assertThat(hrManagerDto.getContactId(), is(expectedSfHrManagerInfoDto.getContactId()));
        assertThat(hrManagerDto.getName(), is(expectedSfHrManagerInfoDto.getName()));
        assertThat(hrManagerDto.getEmail(), is(expectedSfHrManagerInfoDto.getEmail()));
        assertThat(hrManagerDto.getPreferredLanguage(), is(expectedSfHrManagerInfoDto.getPreferredLanguage()));
    }

    @Test
    public void getHRManagerEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        String organization = "Open Mundo";
        personService.getHRManager(salesforcePurchaserId,organization);
    }

    @Test
    public void getHRManagerEmptyOrganization(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("organization should not be null or empty");
        String salesforcePurchaserId = "12345";
        String organization = "";
        personService.getHRManager(salesforcePurchaserId,organization);
    }
}