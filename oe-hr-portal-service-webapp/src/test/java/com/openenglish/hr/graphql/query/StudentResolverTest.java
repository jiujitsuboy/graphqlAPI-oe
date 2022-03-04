package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;

import com.openenglish.hr.common.dto.PersonPerLevelDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.IPersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.Mapper;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class StudentResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @Injectable
    private Mapper mapper;
    @MockBean
    private PersonRepository personRepository;
    @Mocked
    private PersonService personService;

    @Test
    public void getStudentsByPurchaserId() {
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

        new Expectations() {{
            personService.getStudentsBySalesforcePurchaserId(anyString);
            returns(persons);
        }};

        String query = "{\n" +
                "  getStudentsBySalesforcePurchaserId(salesforcePurchaserId:\"12345\"){\n" +
                "    email\n" +
                "    }\n" +
                "}";
        String projection = "data.getStudentsBySalesforcePurchaserId[*].email";

        List<String> studentsEmail = dgsQueryExecutor.executeAndExtractJsonPath(query, projection);

        assertNotNull(studentsEmail);
        persons.forEach(person -> assertTrue(studentsEmail.contains(person.getEmail())));

    }

    @Test
    public void getAllStudentsByLevel() {

        List<IPersonsPerLevel> personsPerLevelExpected = List.of(new IPersonsPerLevel() {
            @Override
            public String getLevelName() {
                return "Level 1";
            }

            @Override
            public long getTotalNumber() {
                return 42;
            }
        }, new IPersonsPerLevel() {
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
            personService.getAllPersonsByLevel();
            returns(personsPerLevelExpected);
        }};

        String query = "{\n" +
                "  getAllStudentsByLevel{\n" +
                "    levelName\n" +
                "    totalNumber\n" +
                "  }\n" +
                "}";
        String projection = "data.getAllStudentsByLevel[*]";


        List<PersonPerLevelDto> studentsPerLevel = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertNotNull(studentsPerLevel);

        for (int index = 0; index < studentsPerLevel.size(); index++) {

            IPersonsPerLevel expected = personsPerLevelExpected.get(index);
            PersonPerLevelDto received = studentsPerLevel.get(index);

            assertEquals(expected.getLevelName(), received.getLevelName());
            assertEquals(expected.getTotalNumber(), received.getTotalNumber());
        }

    }
}
