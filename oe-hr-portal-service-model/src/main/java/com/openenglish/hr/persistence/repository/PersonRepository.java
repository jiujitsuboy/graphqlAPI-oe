package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
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

    @Query(value ="select sum(case when c.coursetype_id = 1 and c.coursesubtype_id in (1,2) then 1 else 0 end) as groupClasses," +
            "       sum(case when c.coursetype_id = 2 and c.coursesubtype_id = 4 then 1 else 0 end) as privateClasses," +
            "       sum(case when c.coursetype_id = 4 then 1 else 0 end) as learnedLessons," +
            "       sum(case when c.coursetype_id = 5 then 1 else 0 end) as completedUnits," +
            "       sum(case when c.coursetype_id in (3,8,10) then pcs.timeontask else 0 end) as practiceHours," +
            "       sum(case when c.coursetype_id = 6 then 1 else 0 end) as levelPassed, " +
            "       0 as totalHoursUsage," +
            "       to_char(pcs.createddate, 'YYYY-MM') as period " +
            "from person p " +
            "inner join person_detail pd on p.id = pd.person_id " +
            "left join personcoursesummary pcs on p.id = pcs.person_id " +
            "left join course c on c.id = pcs.course_id " +
            "left join coursetype ct on ct.id = c.coursetype_id " +
            "left join coursesubtype cst on cst.id = c.coursesubtype_id " +
            "where to_char(pcs.createddate, 'YYYY-MM') in (:previousMonthDate, :currentDate) and " +
            "      pd.salesforce_purchaser_id = :salesforcePurchaserId " +
            "group by to_char(pcs.createddate, 'YYYY-MM') " +
            "order by to_char(pcs.createddate, 'YYYY-MM') asc", nativeQuery = true)
    List<ActivitiesOverview> getActivitiesOverview(@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                   @Param("previousMonthDate") String previousMonthDate,
                                                   @Param("currentDate") String currentDate);


}
