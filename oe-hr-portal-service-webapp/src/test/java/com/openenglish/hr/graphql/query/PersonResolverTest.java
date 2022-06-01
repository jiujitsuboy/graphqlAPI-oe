package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;

import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.MappingConfig;
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
    public void getHRManager() {

        Optional<HRManagerDto> optExpectedHRManager =  Optional.of(HRManagerDto.builder()
            .id("0037c0000155DX4AAM")
            .name("Andrea OM")
            .email("andrea.bragoli+testt@openenglish.com").build());

        Mockito.when(personService.getHRManager(anyString(), anyString())).thenReturn(optExpectedHRManager);

        String query = "{ " +
            "  getHRManager(salesforcePurchaserId:\"12345\", organization: \"Open Mundo\"){ " +
            "    id " +
            "    name " +
            "    email " +
            "    }" +
            "}";
        String projection = "data.getHRManager";

        HRManagerDto hrManagerDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, HRManagerDto.class);

        assertNotNull(hrManagerDto);

        assertThat(hrManagerDto.getId(), is(optExpectedHRManager.get().getId()));
        assertThat(hrManagerDto.getName(), is(optExpectedHRManager.get().getName()));
        assertThat(hrManagerDto.getEmail(), is(optExpectedHRManager.get().getEmail()));


    }
}
