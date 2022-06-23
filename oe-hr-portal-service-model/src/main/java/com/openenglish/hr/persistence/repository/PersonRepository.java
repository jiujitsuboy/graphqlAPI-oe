package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ContactBelongPurchaserId;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findPersonByDetailsSalesforcePurchaserId(String salesforcePurchaserId);

    @Query(value = "select l.name AS levelName, count(*) AS totalNumber " +
            "from person p " +
            "left join person_detail pd on p.id = pd.person_id " +
            "left join level l on p.workinglevel_id  = l.id " +
            "where pd.salesforce_purchaser_id = :salesforcePurchaserId " +
            "group by l.id " +
            "order by l.id asc", nativeQuery = true)
    List<PersonsPerLevel> getAllPersonsPerLevel(@Param("salesforcePurchaserId")String salesforcePurchaserId);

    @Query(value ="SELECT p.contactid AS contactId, "
        + "pd.salesforce_purchaser_id AS salesForcePurchaserId, "
        + "CASE WHEN pd.salesforce_purchaser_id=:salesforcePurchaserId THEN true ELSE false END AS matchSalesForcePurchaserId "
        + "FROM person p "
        + "INNER JOIN person_detail pd ON p.id = pd.person_id "
        + "WHERE p.contactid IN (:contactsId)", nativeQuery = true)
    List<ContactBelongPurchaserId> findIfContactsIdBelongsToSalesforcePurchaserId(@Param("salesforcePurchaserId")String salesforcePurchaserId,
        @Param("contactsId") Set<String> contactsId);
}
