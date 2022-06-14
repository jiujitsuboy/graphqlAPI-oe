package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LicensesOverviewDto;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private static final String LICENSE_STATUS_ACTIVE = "Active";
    private static final String LICENSE_STATUS_INACTIVE = "Inactive";

    private final SalesforceClient salesforceClient;

    /**
     * Retrieves the licenses from Salesforce for the given salesforcePurchaserId and organization
     *
     * @param salesforcePurchaserId id of the owner of the licenses
     * @param organization          the name of the organization for the licenses
     * @return available, assigned and active licenses
     */
    SfLicenseDto[] retrieveLicenses(String salesforcePurchaserId, String organization) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(organization), "organization cannot be null or empty");

        return salesforceClient.getPurchaserLicenses(salesforcePurchaserId, organization);
    }

    /**
     * Retrieves the licenses from Salesforce and computes the available, assigned and active licenses
     *
     * @param salesforcePurchaserId id of the owner of the licenses
     * @param organization          the name of the organization for the licenses
     * @return available, assigned and active licenses
     */
    public LicensesOverviewDto getLicensesOverview(String salesforcePurchaserId, String organization) {
        return countLicenses(retrieveLicenses(salesforcePurchaserId, organization));
    }

    private LicensesOverviewDto countLicenses(SfLicenseDto[] licenses) {
        if (ArrayUtils.isEmpty(licenses)) {
            return new LicensesOverviewDto();
        }

        int inactive = 0;
        int active = 0;

        for (SfLicenseDto license : licenses) {
            if (LICENSE_STATUS_INACTIVE.equals(license.getStatus())) {
                inactive++;
            } else if (LICENSE_STATUS_ACTIVE.equals(license.getStatus())) {
                active++;
            }
        }

        return LicensesOverviewDto.builder()
                .availableLicenses(licenses.length)
                .assignedLicenses(active + inactive)
                .activeLicenses(active)
                .build();
    }
}
