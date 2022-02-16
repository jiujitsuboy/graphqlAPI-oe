package com.openenglish.pp.graphql.query;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import static org.junit.jupiter.api.Assertions.*;

import com.openenglish.pp.persistence.entity.Person;
import com.openenglish.pp.persistence.entity.PersonDetail;
import com.openenglish.pp.persistence.repository.PersonRepository;
import com.openenglish.pp.service.PersonService;
import com.openenglish.pp.service.mapper.PersonMapper;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, PersonMapper.class, PersonService.class,PersonRepository.class, StudentResolver.class})
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
                        .details(Set.of(PersonDetail.builder()
                                .detailsId(12L)
                                .salesforcePurchaserId(12345L)
                                .build()))
                        .build(),
                Person.builder()
                        .id(2L)
                        .contactId("3")
                        .details(Set.of(PersonDetail.builder()
                                .detailsId(22L)
                                .salesforcePurchaserId(12345L)
                                .build()))
                        .build());

        new Expectations(){{
            personService.getStudentsBySalesforcePurchaserId(anyLong);
            returns(persons);
        }};

        String query = "{\n" +
                "  getStudentsBySalesforcePurchaserId(salesforcePurchaserId:12345){\n" +
                "    id\n" +
                "    contactId\n" +
                "    details{\n" +
                "      detailsId\n" +
                "      salesforcePurchaserId\n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "}";
        String proyection = "data.getStudentsBySalesforcePurchaserId[*].id";

        List<String> studentIds =  dgsQueryExecutor.executeAndExtractJsonPath(query, proyection);

        assertNotNull(studentIds);
        persons.forEach(person -> assertTrue(studentIds.contains(person.getId().toString())));

    }
}
