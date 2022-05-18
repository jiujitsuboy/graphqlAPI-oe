package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PersonCourseAuditRepository extends JpaRepository<PersonCourseAudit, Long> {
    @Query(value = "SELECT * " +
                   "FROM personcourseaudit pca " +
                   "INNER JOIN person p ON pca.person_id = p.id " +
                   "INNER JOIN person_detail pd ON p.id = pd.person_id " +
                   "INNER JOIN course c ON c.id = pca.course_id " +
                   "WHERE c.coursetype_id in (:courseTypeIds) AND pd.salesforce_purchaser_id = :salesforcePurchaserId AND " +
                   "((pca.dateCompleted BETWEEN :startDate AND :endDate AND c.coursetype_id NOT IN (3,8,10)) OR " +
                   "(pca.dateStarted BETWEEN :startDate AND :endDate AND c.coursetype_id IN (3,8,10)))", nativeQuery = true)
    List<PersonCourseAudit> findActivityStatistics (@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                    @Param("startDate")LocalDateTime startDate,
                                                    @Param("endDate")LocalDateTime endDate,
                                                    @Param("courseTypeIds") Set<Long> courseTypeIds);


    @Query(value = "SELECT * " +
        "FROM personcourseaudit pca " +
        "INNER JOIN person p ON pca.person_id = p.id " +
        "INNER JOIN person_detail pd ON p.id = pd.person_id " +
        "INNER JOIN course c ON c.id = pca.course_id " +
        "WHERE c.coursetype_id in (:courseTypeIds) AND pd.salesforce_purchaser_id = :salesforcePurchaserId AND " +
        "((pca.dateCompleted BETWEEN :startDate AND :endDate) OR (pca.dateStarted BETWEEN :startDate AND :endDate)) AND " +
        "p.id=:personId", nativeQuery = true)
    List<PersonCourseAudit> findActivityStatisticsByPerson (@Param("salesforcePurchaserId") String salesforcePurchaserId,
        @Param("startDate")LocalDateTime startDate,
        @Param("endDate")LocalDateTime endDate,
        @Param("courseTypeIds") Set<Long> courseTypeIds,
        @Param("personId") long personId);


    @Query(value="SELECT p.id AS personId," +
            "p.contactid as contactId," +
            "p.firstname AS firstname," +
            "p.lastname AS lastname," +
            "max(CASE WHEN c.coursetype_id IN (3,8,10) THEN pca.datestarted ELSE pca.datecompleted END) AS lastActivity " +
            "FROM person p " +
            "INNER JOIN person_detail pd ON p.id = pd.person_id " +
            "INNER JOIN personcourseaudit pca ON p.id = pca.person_id " +
            "INNER JOIN course c ON c.id = pca.course_id " +
            "WHERE pd.salesforce_purchaser_id = :salesforcePurchaserId AND " +
            "(c.coursetype_id IN (3,8,10) OR (c.coursetype_id NOT IN (3,8,10) AND pca.datecompleted IS NOT NULL)) " +
            "GROUP BY p.id,p.firstname, p.lastname;", nativeQuery = true)
    List<UsageLevel> findMaxActivityDateGroupedByPerson(@Param("salesforcePurchaserId") String salesforcePurchaserId);
}
