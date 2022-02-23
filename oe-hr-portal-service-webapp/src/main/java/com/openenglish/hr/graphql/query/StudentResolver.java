package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.PersonMapper;

import java.util.Set;
import java.util.stream.Collectors;

@DgsComponent
public class StudentResolver {

    private PersonService personService;
    private PersonMapper personMapper;

    public StudentResolver(PersonService personService,PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @DgsData(parentType = "Query", field = "getStudentsBySalesforcePurchaserId")
    public Set<PersonDto> getStudentsBySalesforcePurchaserId(Long salesforcePurchaserId){
        Set<Person> personSet =  personService.getStudentsBySalesforcePurchaserId(salesforcePurchaserId);
        return personSet.stream().map(person -> personMapper.toModel(person)).collect(Collectors.toSet());
    }
}
