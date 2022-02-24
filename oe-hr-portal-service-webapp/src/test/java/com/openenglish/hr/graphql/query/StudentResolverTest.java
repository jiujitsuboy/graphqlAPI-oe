package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsQueryExecutor;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
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

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
                        .email("fake1@openenglish.com")
                        .contactId("2")
                        .details(PersonDetail.builder()
                                .id(12L)
                                .salesforcePurchaserId("12345")
                                .build())
                        .build(),
                Person.builder()
                        .id(2L)
                        .email("fake2@openenglish.com")
                        .contactId("3")
                        .details(PersonDetail.builder()
                                .id(22L)
                                .salesforcePurchaserId("12345")
                                .build())
                        .build());

        new Expectations(){{
            personService.getStudentsBySalesforcePurchaserId(anyString);
            returns(persons);
        }};

        String query = "{\n" +
                "  getStudentsBySalesforcePurchaserId(salesforcePurchaserId:\"12345\"){\n" +
                "    email\n" +
                "    contactId\n" +
                "    salesforcePurchaserId\n" +
                "  }\n" +
                "}";
        String proyection = "data.getStudentsBySalesforcePurchaserId[*].email";

        List<String> studentsEmail =  dgsQueryExecutor.executeAndExtractJsonPath(query, proyection);

        assertNotNull(studentsEmail);
        persons.forEach(person -> assertTrue(studentsEmail.contains(person.getEmail())));

    }
}
