package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
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
    public void getHRManager(){
        String salesforcePurchaserId = "12345";
        String organization = "Open Mundo";
        HRManagerDto expectedHRManager =  HRManagerDto.builder()
            .id("0037c0000155DX4AAM")
            .name("Andrea OM")
            .email("andrea.bragoli+testt@openenglish.com").build();


        Optional<HRManagerDto> optHrManagerDto = personService.getHRManager(salesforcePurchaserId,organization);
        assertTrue(optHrManagerDto.isPresent());

        HRManagerDto hrManagerDto = optHrManagerDto.get();

        assertThat(hrManagerDto.getId(), is(expectedHRManager.getId()));
        assertThat(hrManagerDto.getName(), is(expectedHRManager.getName()));
        assertThat(hrManagerDto.getEmail(), is(expectedHRManager.getEmail()));
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