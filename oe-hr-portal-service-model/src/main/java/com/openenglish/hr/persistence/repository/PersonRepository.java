package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPersonByDetailsSalesforcePurchaserId(String salesforcePurchaserId);
}
