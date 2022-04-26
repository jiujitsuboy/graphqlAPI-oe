package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevels;
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
                   "((pca.dateCompleted BETWEEN :startDate AND :endDate) OR (pca.dateStarted BETWEEN :startDate AND :endDate))", nativeQuery = true)
    List<PersonCourseAudit> findActivityStatistics (@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                    @Param("startDate")LocalDateTime startDate,
                                                    @Param("endDate")LocalDateTime endDate,
                                                    @Param("courseTypeIds") Set<Long> courseTypeIds);


    @Query(value="select p.id as personId," +
            "p.firstname as firstname," +
            "p.lastname as lastname," +
            "max(case when c.coursetype_id in (3,8,10) then pca.datestarted else pca.datecompleted end) as lastActivity " +
            "from person p " +
            "inner join person_detail pd on p.id = pd.person_id " +
            "inner join personcourseaudit pca on p.id = pca.person_id " +
            "inner join course c on c.id = pca.course_id " +
            "where pd.salesforce_purchaser_id = :salesforcePurchaserId " +
            "group by p.id,p.firstname, p.lastname;", nativeQuery = true)
    List<UsageLevels> findMaxActivityDateGroupedByPerson(@Param("salesforcePurchaserId") String salesforcePurchaserId);
}
