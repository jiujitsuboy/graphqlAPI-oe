package com.openenglish.hr.graphql.query;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.graphql.mutation.EmailResolver;
import com.openenglish.hr.service.EmailService;
import com.openenglish.hr.service.mapper.MappingConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(MappingConfig.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, EmailResolver.class})
public class EmailResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private EmailService emailService;

    @Test
    public void sendContactUsEmail() {
        String expectedResultMessage = "Message from name, successfully sent.";
        Mockito.when(emailService.sendContactUsEmail(anyString(),anyString(),anyString(),anyString())).thenReturn(expectedResultMessage);

        String mutation = " mutation {"
            + "sendContactUsEmail(salesforcePurchaserId:\"12345\", name: \"name\", email: \"email\", message: \"message\") "
            + "}";

        String projection = "data.sendContactUsEmail";

        String  resultMessage = dgsQueryExecutor.executeAndExtractJsonPath(mutation, projection);

        assertNotNull(resultMessage);
        assertEquals(expectedResultMessage,resultMessage);

    }
}
