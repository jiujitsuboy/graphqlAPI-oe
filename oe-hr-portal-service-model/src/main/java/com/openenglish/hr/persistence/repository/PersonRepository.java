package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByContactId(String contactId);

    Set<Person> findPersonByDetailsSalesforcePurchaserIdIn(Long salesforcePurchaserId);
}
