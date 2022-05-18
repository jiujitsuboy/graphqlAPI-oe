package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.LevelTest;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LevelTestRepository extends JpaRepository<LevelTest, Long> {

    @Query(value = "SELECT l.person_id AS personId," +
                          "p.contactid as contactId," +
                          "p.firstname AS firstname," +
                          "p.lastname AS lastname," +
                          "COUNT(DISTINCT l.level_id) AS totalNumber " +
            " FROM level_test l" +
            " INNER JOIN person p on p.id = l.person_id " +
            " INNER JOIN person_detail pd on p.id = pd.person_id " +
            " WHERE l.test_type='END_OF_LEVEL' AND l.status='PASSED' AND  " +
            "       pd.salesforce_purchaser_id = :salesforcePurchaserId AND " +
            "       l.updated_date BETWEEN :startDate AND :endDate " +
            "GROUP BY l.person_id,p.contactid,p.firstname,p.lastname", nativeQuery = true)
    List<LevelsPassedByPerson> getLevelTestsByPurchaserIdUpdateDateBetween(@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                                           @Param("startDate") LocalDateTime startDate,
                                                                           @Param("endDate")LocalDateTime endDate);
}
