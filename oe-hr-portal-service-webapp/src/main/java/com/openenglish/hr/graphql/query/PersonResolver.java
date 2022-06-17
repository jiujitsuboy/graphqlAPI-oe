package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.service.PersonService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class PersonResolver {

    private final PersonService personService;

    @DgsData(parentType = "Query", field = "getPersons")
    public List<PersonDto> getPersons(String salesforcePurchaserId) {
        return personService.getPersons(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getAllPersonsByLevel")
    public List<PersonsPerLevelDto> getAllPersonsByLevel(String salesforcePurchaserId) {
        return personService.getAllPersonsByLevel(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getLicenseInfo")
    public List<LicenseDto> getLicenseInfo(String salesforcePurchaserId, String organization) {
        return personService.getLicenseInfo(salesforcePurchaserId, organization);
    }

    @DgsData(parentType = "Query", field = "getHRManager")
    public Optional<HRManagerDto> getHRManager(String salesforcePurchaserId, String organization) {
        return personService.getHRManager(salesforcePurchaserId, organization);
    }

    @DgsData(parentType = "Query", field = "hello")
    public String hello(String salesforcePurchaserId) {
        return "Hello " + salesforcePurchaserId + "!";
    }
}
