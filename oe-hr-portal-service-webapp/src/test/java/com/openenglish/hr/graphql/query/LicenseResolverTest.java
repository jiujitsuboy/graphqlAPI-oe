package com.openenglish.hr.graphql.query;


import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.LicensesOverviewDto;
import com.openenglish.hr.service.LicenseService;
import com.openenglish.hr.service.mapper.MappingConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@Import(MappingConfig.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, LicenseResolver.class})
public class LicenseResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private LicenseService licenseService;

    @Test
    public void testGetLicensesOverview() {

        LicensesOverviewDto statisticsDto = LicensesOverviewDto.builder()
                .availableLicenses(10)
                .assignedLicenses(5)
                .activeLicenses(2)
                .build();

        Mockito.when(licenseService.getLicensesOverview(anyString(), anyString())).thenReturn(
                statisticsDto);

        String query = "{ " +
                "  getLicensesOverview(salesforcePurchaserId:\"0017c00000ubf0O\" , organization:\"Open Mundo\"){ " +
                "    availableLicenses" +
                "    assignedLicenses" +
                "    activeLicenses" +
                "  }" +
                "}";

        String projection = "data.getLicensesOverview";

        LicensesOverviewDto actualDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, LicensesOverviewDto.class);
        Assert.assertEquals(statisticsDto, actualDto);
    }
}
