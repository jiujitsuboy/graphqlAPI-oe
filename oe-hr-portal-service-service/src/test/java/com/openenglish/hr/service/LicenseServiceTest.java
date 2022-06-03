package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.LicensesOverviewDto;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LicenseServiceTest {

    public static final String SALESFORCE_PURCHASER_ID = "0017c00000ubf0O";
    public static final String ORGANIZATION = "Open Mundo";

    @Injectable
    private SalesforceClient salesforceClient;
    @Tested
    private LicenseService licenseService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getLicensesOverview() {

        SfLicenseDto[] licenseDtos = new SfLicenseDto[]{
                new SfLicenseDto() // assigned and active
                        .setStudent(new SfLicenseDto.StudentDto()
                                .setName("Student 0")
                        )
                        .setStatus("Active"),
                new SfLicenseDto() // assigned and active
                        .setStudent(new SfLicenseDto.StudentDto()
                                .setName("Student 1")
                        )
                        .setStatus("Active"),
                new SfLicenseDto(), // unassigned
                new SfLicenseDto()  // inactive or new
                        .setStudent(new SfLicenseDto.StudentDto()
                                .setName("Student 3")
                        )
                        .setStatus("New"),
                new SfLicenseDto()  // inactive or new
                        .setStudent(new SfLicenseDto.StudentDto()
                                .setName("Student 4")
                        )
                        .setStatus("Inactive")

        };

        new Expectations() {{
            salesforceClient.getPurchaserLicenses(anyString, anyString);
            returns(licenseDtos);
        }};

        LicensesOverviewDto actualDto = licenseService.getLicensesOverview(SALESFORCE_PURCHASER_ID, ORGANIZATION);

        LicensesOverviewDto expectedDto = LicensesOverviewDto.builder()
                .availableLicenses(5)
                .assignedLicenses(4)
                .activeLicenses(2)
                .build();

        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void getLicensesOverviewEmpty() {

        LicensesOverviewDto zeroedDto = new LicensesOverviewDto();

        new Expectations() {{
            salesforceClient.getPurchaserLicenses(anyString, anyString);
            returns(new SfLicenseDto[0]);
        }};

        LicensesOverviewDto actualDto = licenseService.getLicensesOverview(SALESFORCE_PURCHASER_ID, ORGANIZATION);
        Assert.assertEquals(zeroedDto, actualDto);


        new Expectations() {{
            salesforceClient.getPurchaserLicenses(anyString, anyString);
            returns(null);
        }};

        actualDto = licenseService.getLicensesOverview(SALESFORCE_PURCHASER_ID, ORGANIZATION);
        Assert.assertEquals(zeroedDto, actualDto);

    }
}