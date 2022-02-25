package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:personData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personData.xml", type = DatabaseOperation.DELETE)
public class PersonRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void findPersonByDetailsSalesforcePurchaserIdIn(){
        String salesforcePurchaserId = "12346";
        int expectedPersons = 3;
        List<Person> persons =  personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
        assertNotNull(persons);
        assertEquals(expectedPersons,persons.size());
        assertThat(persons.get(0).getFirstName(),is("joseph"));
        assertThat(persons.get(0).getLastName(),is("peterson"));
        assertThat(persons.get(0).getEmail(),is("josephp2@unknowdomain.com"));
    }
}
