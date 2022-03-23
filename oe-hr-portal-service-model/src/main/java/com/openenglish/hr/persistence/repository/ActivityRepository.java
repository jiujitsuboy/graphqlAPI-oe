package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Person, Long> {

    @Query(value ="select coalesce(sum(case when c.coursetype_id = 1 and c.coursesubtype_id in (1,2) then 1 else 0 end),0) as groupClasses, " +
            "       coalesce(sum(case when c.coursetype_id = 2 and c.coursesubtype_id = 4 then 1 else 0 end),0) as privateClasses," +
            "       coalesce(sum(case when c.coursetype_id = 4 then 1 else 0 end),0) as completedLessons," +
            "       coalesce(sum(case when c.coursetype_id = 5 then 1 else 0 end),0) as completedUnits," +
            "       coalesce(sum(case when c.coursetype_id in (3,8,10) then pcs.timeontask/3600 else 0 end),0) as practiceHours," +
            "       coalesce(sum(case when c.coursetype_id = 6 then 1 else 0 end),0) as levelPassed," +
            "       coalesce(cast(sum(case when (c.coursetype_id = 1 and c.coursesubtype_id in (1,2)) or c.coursetype_id in (4,5) then 25 " +
            "                when c.coursetype_id = 2 and c.coursesubtype_id = 4 then 30 " +
            "                when c.coursetype_id in (3,8,10) then pcs.timeontask " +
            "                else 0  " +
            "           end) as float)/3600,0) as totalHoursUsage " +
            "from person p " +
            "inner join person_detail pd on p.id = pd.person_id " +
            "left join personcoursesummary pcs on p.id = pcs.person_id " +
            "left join course c on c.id = pcs.course_id " +
            "left join coursetype ct on ct.id = c.coursetype_id " +
            "left join coursesubtype cst on cst.id = c.coursesubtype_id " +
            "where pd.salesforce_purchaser_id = :salesforcePurchaserId ", nativeQuery = true)
    ActivitiesOverview getActivitiesOverview(@Param("salesforcePurchaserId") String salesforcePurchaserId);


}
