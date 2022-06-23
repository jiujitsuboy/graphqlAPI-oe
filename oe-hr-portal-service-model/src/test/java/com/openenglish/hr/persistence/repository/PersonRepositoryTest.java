package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.EmailBelongPurchaserId;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import java.util.Set;
import org.junit.Assert;
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
        String salesforcePurchaserId = "12347";
        String firstNameExpected = "joseph";
        String lastNameExpected = "peterson";
        String contactIdExpected = "sf_synegen801";
        String emailExpected = "josephp430@unknowdomain.com";
        int expectedPersons = 3;

        List<Person> persons =  personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertNotNull(persons);
        assertEquals(expectedPersons,persons.size());

        persons.stream().filter(person -> person.getContactId().equals(contactIdExpected)).findFirst().ifPresentOrElse(person -> {
            assertThat(person.getFirstName(),is(firstNameExpected));
            assertThat(person.getLastName(),is(lastNameExpected));
            assertThat(person.getContactId(),is(contactIdExpected));
            assertThat(person.getEmail(),is(emailExpected));
        }, Assert::fail);
    }

    @Test
    public void getAllPersonsPerLevel(){

        String levelName = "Level 100";
        String salesforcePurchaserId = "12347";
        long totalNumber = 3;

        List<PersonsPerLevel> personsPerLevel =  personRepository.getAllPersonsPerLevel(salesforcePurchaserId);

        assertNotNull(personsPerLevel);

        personsPerLevel.stream()
                .filter(temp -> temp.getLevelName().equals(levelName))
                .findFirst()
                .ifPresentOrElse(level->{
                    assertThat(level.getLevelName(),is(levelName));
                    assertThat(level.getTotalNumber(),is(totalNumber));
                },Assert::fail);
    }

    @Test
    public void getAllPersonsPerLevelEmpty(){
        String salesforcePurchaserId = "12348";
        final long ZERO_RECORDS = 0;

        List<PersonsPerLevel> personsPerLevel =  personRepository.getAllPersonsPerLevel(salesforcePurchaserId);

        assertNotNull(personsPerLevel);
        assertEquals(ZERO_RECORDS,personsPerLevel.size());
    }

    @Test
    public void findIfEmailsBelongsToSalesforcePurchaserId(){
        final int FOURTH_RECORDS  = 4;
        String salesforcePurchaserId = "12347";
        Set<String> contactId = Set.of("sf_synegen801", "sf_synegen091", "sf_synegen1001", "sf_synegen2002");
        List<EmailBelongPurchaserId> emailBelongPurchaserIds =  personRepository.findIfEmailsBelongsToSalesforcePurchaserId(salesforcePurchaserId, contactId);

        assertEquals(emailBelongPurchaserIds.size(), FOURTH_RECORDS);
        emailBelongPurchaserIds.forEach(emailBelongPurchaserId -> {
            if(emailBelongPurchaserId.getEmail().equals("jack_sullivan@unknowdomain.com")){
                assertFalse(emailBelongPurchaserId.isMatchSalesForcePurchaserId());
            }
            else{
                assertTrue(emailBelongPurchaserId.isMatchSalesForcePurchaserId());
            }
        });
    }

    @Test
    public void findIfEmailsBelongsToSalesforcePurchaserIdNonExisting(){

        String salesforcePurchaserId = "12347";
        Set<String> contactId = Set.of("sf_synegen8010", "sf_synegen0910", "sf_synegen10010", "sf_synegen20020");
        List<EmailBelongPurchaserId> emailBelongPurchaserIds =  personRepository.findIfEmailsBelongsToSalesforcePurchaserId(salesforcePurchaserId, contactId);

        assertTrue(emailBelongPurchaserIds.isEmpty());
    }
}
