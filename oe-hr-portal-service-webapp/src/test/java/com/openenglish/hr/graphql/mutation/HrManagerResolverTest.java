package com.openenglish.hr.graphql.mutation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.LicenseAssigneeDto;
import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.service.HrManagerService;
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
@SpringBootTest(classes = {DgsAutoConfiguration.class, HrManagerResolver.class})
public class HrManagerResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private HrManagerService managerService;

    @Test
    public void sendContactUsMessage() {
        MutationResultDto expectedMutationResultDto = MutationResultDto.builder()
            .success(true)
            .build();

        Mockito.when(
            managerService.sendContactUsMessage(anyString(),anyString(),anyString(),anyString())).thenReturn(expectedMutationResultDto);

        String mutation = "mutation {"
            + "  sendContactUsMessage(salesforcePurchaserId:\"12345\", name: \"name\", email: \"email\", message: \"message\"){"
            + "    success"
            + "    message"
            + "  }"
            + "}";

        String projection = "data.sendContactUsMessage";

        MutationResultDto result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(mutation, projection, MutationResultDto.class);

        assertTrue(result.isSuccess());
        assertNull(result.getMessage());

    }

    @Test
    public void sendEncouragementEmails() {
        MutationResultDto expectedMutationResultDto = MutationResultDto.builder()
            .success(true)
            .build();

        Mockito.when(
            managerService.sendEncouragementEmails(anyString(),anyString(),any(),anyString(),anyString())).thenReturn(expectedMutationResultDto);

        String mutation = "mutation {"
            + "  sendEncouragementEmails(salesforcePurchaserId:\"12345\", managerId: \"Q12qwe3333\", contactsId: [\"sf_synegen801\"], message: \"message\", language:\"en-EU\"){"
            + "    success"
            + "    message"
            + "  }"
            + "}";

        String projection = "data.sendEncouragementEmails";

        MutationResultDto result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(mutation, projection, MutationResultDto.class);

        assertTrue(result.isSuccess());
        assertNull(result.getMessage());

    }

    @Test
    public void reassignLicense() {
        MutationResultDto expectedMutationResultDto = MutationResultDto.builder()
            .success(true)
            .build();

        Mockito.when(
            managerService.reassignLicense(anyString(), anyString(), anyString(),any(LicenseAssigneeDto.class), any(LicenseAssigneeDto.class))).thenReturn(expectedMutationResultDto);

        String mutation = "mutation {"
            + "  reassignLicense(salesforcePurchaserId:\"12345\","
            + "                  licenseId:\"1234545\", "
            + "                  managerId: \"Q12qwe3333\", "
            + "                  currentAssignee: {"
            + "                       firstName: \"Lindsay\","
            + "                       lastName: \"Guerrero\","
            + "                       email: \"mgcontradorpublico@gmail.com\""
            + "                  }, "
            + "                  newAssignee: {"
            + "                       firstName: \"Jose\","
            + "                       lastName: \"Nino\","
            + "                       email: \"josenino@gmail.com\""
            + "                   }"
            + "                  ){"
            + "    success"
            + "    message"
            + "  }"
            + "}";

        String projection = "data.reassignLicense";

        MutationResultDto result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(mutation, projection, MutationResultDto.class);

        assertTrue(result.isSuccess());
        assertNull(result.getMessage());

    }
}
