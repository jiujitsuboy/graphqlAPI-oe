package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@DgsComponent
@RequiredArgsConstructor
public class PersonResolver {

    private final PersonService personService;
    private final Mapper mapper;

    @DgsData(parentType = "Query", field = "getPersons")
    public List<PersonDto> getPersons(String salesforcePurchaserId) {
        List<Person> persons = personService.getPersons(salesforcePurchaserId);
        return persons.stream()
                .map(person -> mapper.map(person, PersonDto.class))
                .collect(Collectors.toList());
    }

    @DgsData(parentType = "Query", field = "getAllPersonsByLevel")
    public List<PersonsPerLevelDto> getAllPersonsByLevel(String salesforcePurchaserId) {
        List<PersonsPerLevel> personsByLevel = personService.getAllPersonsByLevel(salesforcePurchaserId);

        return personsByLevel.stream()
                .map(personByLevel -> mapper.map(personByLevel, PersonsPerLevelDto.class))
                .collect(Collectors.toList());
    }

    @DgsData(parentType = "Query", field = "getAllActivitiesOverview")
    public List<ActivitiesOverviewDto> getAllActivitiesOverview(String salesforcePurchaserId) {

        List<ActivitiesOverview> activitiesOverviews = personService.getAllActivitiesOverview(salesforcePurchaserId);

        return activitiesOverviews.stream()
                .map(activitiesOverview -> mapper.map(activitiesOverview, ActivitiesOverviewDto.class))
                .collect(Collectors.toList());
    }
}
