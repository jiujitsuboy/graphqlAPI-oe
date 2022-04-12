package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
public interface PersonCourseAuditRepository extends JpaRepository<PersonCourseAudit, Long> {
    @Query(value = "SELECT * " +
                   "FROM personcourseaudit pca " +
                   "INNER JOIN person p ON pca.person_id = p.id " +
                   "INNER JOIN person_detail pd ON p.id = pd.person_id " +
                   "INNER JOIN course c ON c.id = pca.course_id " +
                   "WHERE c.coursetype_id =:courseTypeId AND pd.salesforce_purchaser_id = :salesforcePurchaserId AND " +
                   "((pca.dateCompleted BETWEEN :startDate AND :endDate) OR (pca.dateStarted BETWEEN :startDate AND :endDate))", nativeQuery = true)
    List<PersonCourseAudit> findActivityStatistics (@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                    @Param("startDate")LocalDateTime startDate,
                                                    @Param("endDate")LocalDateTime endDate,
                                                    @Param("courseTypeId")long courseTypeId);
}
