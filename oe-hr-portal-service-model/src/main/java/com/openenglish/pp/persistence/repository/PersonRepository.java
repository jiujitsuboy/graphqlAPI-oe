package com.openenglish.pp.persistence.repository;

import com.openenglish.pp.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PersonRepository extends JpaRepository<Person, Long> {
  Person findByContactId(String contactId);

  Set<Person> findPersonByDetailsSalesforcePurchaserIdIn(Long salesforcePurchaserId);
}
