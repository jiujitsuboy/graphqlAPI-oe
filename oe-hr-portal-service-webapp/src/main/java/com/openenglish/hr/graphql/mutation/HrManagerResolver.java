package com.openenglish.hr.graphql.mutation;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.service.HrManagerService;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class HrManagerResolver {

    private final HrManagerService managerService;

    @DgsData(parentType = "Mutation", field = "sendContactUsMessage")
    public MutationResultDto sendContactUsMessage(String salesforcePurchaserId, String name, String email, String message) {
        return managerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }
}
