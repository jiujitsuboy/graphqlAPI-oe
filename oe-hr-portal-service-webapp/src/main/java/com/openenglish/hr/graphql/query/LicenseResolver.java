package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.LicensesOverviewDto;
import com.openenglish.hr.service.LicenseService;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class LicenseResolver {

    private final LicenseService licenseService;

    @DgsData(parentType = "Query", field = "getLicensesOverview")
    public LicensesOverviewDto getLicensesOverview(String salesforcePurchaserId, String organization) {
        return licenseService.getLicensesOverview(salesforcePurchaserId, organization);
    }
}
