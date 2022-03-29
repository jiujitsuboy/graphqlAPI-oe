package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Person, Long> {

    @Query(value ="select c.coursetype_id as courseType, " +
                "         c.coursesubtype_id as courseSubType, " +
                "         pcs.timeontask as timeInSeconds " +
                "from person p " +
                "inner join person_detail pd on p.id = pd.person_id " +
                "left join personcoursesummary pcs on p.id = pcs.person_id " +
                "left join course c on c.id = pcs.course_id " +
                "left join coursetype ct on ct.id = c.coursetype_id " +
                "left join coursesubtype cst on cst.id = c.coursesubtype_id " +
                "where pd.salesforce_purchaser_id = :salesforcePurchaserId " , nativeQuery = true)
    List<ActivitiesOverview> getActivitiesOverview(@Param("salesforcePurchaserId") String salesforcePurchaserId);


}
