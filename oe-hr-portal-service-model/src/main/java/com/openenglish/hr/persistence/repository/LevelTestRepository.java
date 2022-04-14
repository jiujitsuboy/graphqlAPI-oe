package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.LevelTest;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LevelTestRepository extends JpaRepository<LevelTest, Long> {

    @Query(value = "SELECT person_id AS personId, COUNT(DISTINCT level_id) AS totalNumber " +
            "       FROM level_test " +
            "       WHERE test_type='END_OF_LEVEL' AND status='PASSED' AND  " +
            "             updated_date BETWEEN :startDate AND :endDate " +
            "       GROUP BY person_id ", nativeQuery = true)
    List<LevelsPassedByPerson> getPersonLevelIdByUpdateDateBetween(@Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate")LocalDateTime endDate);
}
