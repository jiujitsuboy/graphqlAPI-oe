package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.IPersonsPerLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPersonByDetailsSalesforcePurchaserId(String salesforcePurchaserId);

    @Query("SELECT  l.name AS levelName, count(p.id) AS totalNumber " +
            "FROM Person p JOIN p.workingLevel l " +
            "GROUP BY l.name " +
            "ORDER BY l.name ASC")
    List<IPersonsPerLevel> getAllPersonsPerLevel();
}
