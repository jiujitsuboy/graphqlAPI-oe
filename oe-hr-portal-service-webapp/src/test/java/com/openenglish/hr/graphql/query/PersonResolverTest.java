package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;

import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.MappingConfig;
import com.openenglish.sfdc.client.dto.SfHrManagerInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@Import(MappingConfig.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, PersonResolver.class})
public class PersonResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private PersonService personService;

    @Test
    public void getPersons() {
        List<Person> persons = List.of(Person.builder()
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

        Mockito.when(personService.getPersons(anyString())).thenReturn(persons);

        String query = "{ " +
                "  getPersons(salesforcePurchaserId:\"12345\"){ " +
                "    email" +
                "    }" +
                "}";
        String projection = "data.getPersons[*].email";

        List<String> personsEmail = dgsQueryExecutor.executeAndExtractJsonPath(query, projection);

        assertNotNull(personsEmail);
        persons.forEach(person -> assertTrue(personsEmail.contains(person.getEmail())));

    }

    @Test
    public void getAllPersonsByLevel() {

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

        Mockito.when(personService.getAllPersonsByLevel(anyString())).thenReturn(personsPerLevelExpected);

        String query = "{ " +
                "  getAllPersonsByLevel (salesforcePurchaserId: \"12345\"){ " +
                "    levelName " +
                "    totalNumber " +
                "  }" +
                "}";
        String projection = "data.getAllPersonsByLevel[*]";


        List<PersonsPerLevelDto> personsPerLevel = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertNotNull(personsPerLevel);

        for (int index = 0; index < personsPerLevel.size(); index++) {

            PersonsPerLevel expected = personsPerLevelExpected.get(index);
            PersonsPerLevelDto received = personsPerLevel.get(index);

            assertEquals(expected.getLevelName(), received.getLevelName());
            assertEquals(expected.getTotalNumber(), received.getTotalNumber());
        }
    }

    @Test
    public void getLicenseInfo(){

        final int TWO_RECORDS = 2;

        List<LicenseDto> licenseDtoExpected = List.of(LicenseDto.builder()
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
                .build());

        Mockito.when(personService.getLicenseInfo(anyString(),anyString())).thenReturn(licenseDtoExpected);

        String query = "{"
            + "     getLicenseInfo(salesforcePurchaserId:\"12345\", organization: \"Open Mundo\"){"
            + "          person{"
            + "             id"
            + "             firstName"
            + "             lastName"
            + "             email"
            + "         }"
            + "         status"
            + "         organization"
            + "         licenseId"
            + "         name"
            + "         privateClasses"
            + "         startDate"
            + "         endDate"
            + "  }"
            + "}";
        String projection = "data.getLicenseInfo[*]";

        List<LicenseDto> licenseDtos = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertTrue(licenseDtos.size() == TWO_RECORDS);

        for (int licenceIndex = 0; licenceIndex < licenseDtos.size(); licenceIndex++) {
            assertThat(licenseDtos.get(licenceIndex).getLicenseId(), is(licenseDtoExpected.get(licenceIndex).getLicenseId()));
            assertThat(licenseDtos.get(licenceIndex).getName(), is(licenseDtoExpected.get(licenceIndex).getName()));
            assertThat(licenseDtos.get(licenceIndex).getPrivateClasses(), is(licenseDtoExpected.get(licenceIndex).getPrivateClasses()));
            assertThat(licenseDtos.get(licenceIndex).getOrganization(), is(licenseDtoExpected.get(licenceIndex).getOrganization()));
            assertThat(licenseDtos.get(licenceIndex).getStatus(), is(licenseDtoExpected.get(licenceIndex).getStatus()));
        }
    }

    @Test
    public void getHRManager() {

        SfHrManagerInfoDto sfHrManagerInfoDto = new SfHrManagerInfoDto();
        sfHrManagerInfoDto.setContactId("0037c0000155DX4AAM");
        sfHrManagerInfoDto.setName("Andrea OM");
        sfHrManagerInfoDto.setEmail("andrea.bragoli+testt@openenglish.com");
        sfHrManagerInfoDto.setPreferredLanguage("es-US");

        Optional<SfHrManagerInfoDto> optExpectedHRManager =  Optional.of(sfHrManagerInfoDto);

        Mockito.when(personService.getHRManager(anyString(), anyString())).thenReturn(optExpectedHRManager);

        String query = "{ " +
            "  getHRManager(salesforcePurchaserId:\"12345\", organization: \"Open Mundo\"){ " +
            "    id " +
            "    name " +
            "    email " +
            "    preferredLanguage " +
            "    }" +
            "}";
        String projection = "data.getHRManager";

        HRManagerDto hrManagerDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, HRManagerDto.class);

        assertNotNull(hrManagerDto);

        assertThat(hrManagerDto.getId(), is(optExpectedHRManager.get().getContactId()));
        assertThat(hrManagerDto.getName(), is(optExpectedHRManager.get().getName()));
        assertThat(hrManagerDto.getEmail(), is(optExpectedHRManager.get().getEmail()));
        assertThat(hrManagerDto.getPreferredLanguage(), is(optExpectedHRManager.get().getPreferredLanguage()));
    }
}
