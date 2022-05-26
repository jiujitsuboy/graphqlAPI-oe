package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import java.time.LocalDate;
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
    @Tested
    private PersonService personService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

//        new Expectations() {{
//            personService.getLicences(anyString, anyString);
//            returns(licences);
//        }};

        List<LicenseDto> licenseDtos =  personService.getLicenseInfo(salesforcePurchaserId,organization);

        assertTrue(licenseDtos.size() == TWO_RECORDS);

        for (int licenceIndex = 0; licenceIndex < licenseDtos.size(); licenceIndex++) {
            assertThat(licenseDtos.get(licenceIndex).getId(), is(licences[licenceIndex].getId()));
            assertThat(licenseDtos.get(licenceIndex).getName(), is(licences[licenceIndex].getName()));
            assertThat(licenseDtos.get(licenceIndex).getPrivateClasses(), is(licences[licenceIndex].getPrivateClasses()));
            assertThat(licenseDtos.get(licenceIndex).getOrganization(), is(licences[licenceIndex].getOrganization()));
            assertThat(licenseDtos.get(licenceIndex).getStatus(), is(licences[licenceIndex].getStatus()));
            assertThat(licenseDtos.get(licenceIndex).getStartDate(), is(licences[licenceIndex].getStartDate()));
            assertThat(licenseDtos.get(licenceIndex).getEndDate(), is(licences[licenceIndex].getEndDate()));
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
}