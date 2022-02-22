package com.openenglish.pp.persistence.repository;

import com.openenglish.pp.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
  List<Person> findPersonByDetailsSalesforcePurchaserId(Long salesforcePurchaserId);
}
