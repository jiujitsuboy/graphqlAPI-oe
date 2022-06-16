package com.openenglish.hr.graphql.mutation;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.service.EmailService;
import com.openenglish.hr.service.PersonService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class EmailResolver {

    private final EmailService emailService;

    @DgsData(parentType = "Mutation", field = "sendContactUsEmail")
    public String sendContactUsEmail(String salesforcePurchaserId, String name, String email, String message) {
        return emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);
    }
}
