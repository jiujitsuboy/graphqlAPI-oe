package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    public Set<Person> getStudentsBySalesforcePurchaserId(Long salesforcePurchaserId){
        Preconditions.checkArgument(salesforcePurchaserId != null, "salesforcePurchaserId should be non null");
        return personRepository.findPersonByDetailsSalesforcePurchaserIdIn(salesforcePurchaserId);
    }
}
