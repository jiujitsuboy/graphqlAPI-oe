package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPersonByDetailsSalesforcePurchaserId(String salesforcePurchaserId);

    @Query("SELECT  l.name AS levelName, count(p.id) AS totalNumber " +
            "FROM Person p JOIN p.details d JOIN p.workingLevel l " +
            "WHERE d.salesforcePurchaserId=:salesforcePurchaserId " +
            "GROUP BY l.name " +
            "ORDER BY l.name ASC")
    List<PersonsPerLevel> getAllPersonsPerLevel(@Param("salesforcePurchaserId")String salesforcePurchaserId);
}
