package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.PersonMapper;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, PersonMapper.class, PersonService.class, PersonRepository.class, StudentResolver.class})
public class StudentResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private PersonRepository personRepository;
    @Mocked
    private PersonService personService;

    @Test
    public void getStudentsByPurchaserId() {
        Set<Person> persons = Set.of(Person.builder()
                        .id(1L)
                        .contactId("2")
                        .workingLevel(Level.builder()
                                .id(3L)
                                .build())
                        .details(Set.of(PersonDetail.builder()
                                .detailsId(12L)
                                .salesforcePurchaserId(12345L)
                                .build()))
                        .build(),
                Person.builder()
                        .id(2L)
                        .contactId("3")
                        .workingLevel(Level.builder()
                                .id(4L)
                                .build())
                        .details(Set.of(PersonDetail.builder()
                                .detailsId(22L)
                                .salesforcePurchaserId(12345L)
                                .build()))
                        .build());

        new Expectations() {{
            personService.getStudentsBySalesforcePurchaserId(anyLong);
            returns(persons);
        }};

        String query = "{\n" +
                "  getStudentsBySalesforcePurchaserId(salesforcePurchaserId:12345){\n" +
                "    id\n" +
                "    contactId\n" +
                "    workingLevel{\n" +
                "      name\n" +
                "      numLiveRequired\n" +
                "    }\n" +
                "    details{\n" +
                "      detailsId\n" +
                "      salesforcePurchaserId\n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "}";
        String proyection = "data.getStudentsBySalesforcePurchaserId[*]";

        List<LinkedHashMap<String, String>> students = dgsQueryExecutor.executeAndExtractJsonPath(query, proyection);

        assertNotNull(students);

        students.forEach(linkMap -> {
                    assertTrue(persons.stream().anyMatch(person -> person.getId().toString().equals(linkMap.get("id"))));
                    assertNotNull(linkMap.get("workingLevel"));
                }
        );
    }
}
