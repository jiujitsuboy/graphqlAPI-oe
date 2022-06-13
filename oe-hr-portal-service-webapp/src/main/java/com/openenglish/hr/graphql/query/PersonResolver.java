package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.Mapper;
import com.openenglish.sfdc.client.dto.SfHrManagerInfoDto;
import java.util.Optional;
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

    @DgsData(parentType = "Query", field = "getLicenseInfo")
    public List<LicenseDto> getLicenseInfo(String salesforcePurchaserId, String organization) {
        return personService.getLicenseInfo(salesforcePurchaserId, organization);
    }

    @DgsData(parentType = "Query", field = "getHRManager")
    public Optional<HRManagerDto> getHRManager(String salesforcePurchaserId, String organization) {

        Optional<HRManagerDto> optHrManagerDto = Optional.empty();

        Optional<SfHrManagerInfoDto> optSfHrManagerInfoDto = personService.getHRManager(salesforcePurchaserId, organization);

        return optSfHrManagerInfoDto.map(sfHrManagerInfoDto -> Optional.of(mapper.map(sfHrManagerInfoDto, HRManagerDto.class)))
            .orElse(optHrManagerDto);
    }
}