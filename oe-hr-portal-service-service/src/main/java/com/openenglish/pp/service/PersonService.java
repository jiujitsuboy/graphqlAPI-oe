package com.openenglish.pp.service;

import com.google.common.base.Preconditions;
import com.openenglish.pp.persistence.entity.Person;
import com.openenglish.pp.persistence.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    public Set<Person> getStudentsByPurchaserId(Long purchaserId){
        Preconditions.checkArgument(purchaserId != null, "purchaserId should be non null");
        return personRepository.findPersonByDetailsPurchaserIdIn(purchaserId);
    }
}
