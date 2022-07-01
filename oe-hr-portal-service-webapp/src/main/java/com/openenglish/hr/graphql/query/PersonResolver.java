package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityTypeEnum;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonOldestActivityDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.util.ActivityTypeMapper;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @DgsData(parentType = "Query", field = "getOldestActivity")
    public String getOldestActivity(String salesforcePurchaserId, List<String> activities) {

        Set<Long> courseTypesValues =  activities.stream()
            .map(ActivityTypeEnum::valueOf)
            .flatMap(activityTypeEnum ->  ActivityTypeMapper.mapToCourseTypes(activityTypeEnum).stream())
            .map(CourseTypeEnum::getValue)
            .collect(Collectors.toSet());

        LocalDateTime oldestActivityDateTime = personService.getOldestActivity(salesforcePurchaserId, courseTypesValues);

        return oldestActivityDateTime != null ? oldestActivityDateTime.toString() : "";
    }

}
