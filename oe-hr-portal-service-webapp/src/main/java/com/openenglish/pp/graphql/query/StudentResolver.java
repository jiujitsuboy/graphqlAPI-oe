package com.openenglish.pp.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.pp.common.dto.PersonDto;
import com.openenglish.pp.persistence.entity.Person;
import com.openenglish.pp.service.PersonService;
import com.openenglish.pp.service.mapper.PersonMapper;

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

    @DgsData(parentType = "Query", field = "getStudentsByPurchaserId")
    public Set<PersonDto> getStudentsByPurchaserId(Long purchaserId){
        Set<Person> personSet =  personService.getStudentsByPurchaserId(purchaserId);
        return personSet.stream().map(person -> personMapper.toModel(person)).collect(Collectors.toSet());
    }
}
