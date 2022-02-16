package com.openenglish.pp.graphql.query;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import static org.junit.jupiter.api.Assertions.*;

import com.openenglish.pp.persistence.repository.PersonRepository;
import com.openenglish.pp.service.PersonService;
import com.openenglish.pp.service.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest(classes = {DgsAutoConfiguration.class, PersonMapper.class, PersonService.class, StudentResolver.class})
public class StudentResolverTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    PersonRepository personRepository;

    @Test
    public void getStudentsByPurchaserId() {
        String query = "{\n" +
                "  getStudentsByPurchaserId(purchaserId:12345){\n" +
                "    id\n" +
                "    contactId\n" +
                "    details{\n" +
                "      detailsId\n" +
                "      purchaserId\n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "}";
        String proyection = "data.getStudentsByPurchaserId[*].id";
        List<String> StudentIds = dgsQueryExecutor.executeAndExtractJsonPath(query, proyection);

        assertNotNull(StudentIds);
    }
}
