package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@DgsComponent
@RequiredArgsConstructor
public class StudentResolver {

    private final PersonService personService;
    private final Mapper mapper;

    @DgsData(parentType = "Query", field = "getStudentsBySalesforcePurchaserId")
    public List<PersonDto> getStudentsBySalesforcePurchaserId(String salesforcePurchaserId){
        List<Person> persons =  personService.getStudentsBySalesforcePurchaserId(salesforcePurchaserId);
        return  persons.stream()
                .map(person -> mapper.map(person, PersonDto.class))
                .collect(Collectors.toList());
    }
}
