package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Person, Long> {

    @Query(value ="select sum(case when c.coursetype_id = 1 and c.coursesubtype_id in (1,2) then 1 else 0 end) as groupClasses," +
            "       sum(case when c.coursetype_id = 2 and c.coursesubtype_id = 4 then 1 else 0 end) as privateClasses," +
            "       sum(case when c.coursetype_id = 4 then 1 else 0 end) as completedLessons," +
            "       sum(case when c.coursetype_id = 5 then 1 else 0 end) as completedUnits," +
            "       sum(case when c.coursetype_id in (3,8,10) then pcs.timeontask/3600 else 0 end) as practiceHours," +
            "       sum(case when c.coursetype_id = 6 then 1 else 0 end) as levelPassed, " +
            "       sum(case when (c.coursetype_id = 1 and c.coursesubtype_id in (1,2)) or c.coursetype_id in (4,5) then 25 " +
            "                when c.coursetype_id = 2 and c.coursesubtype_id = 4 then 30 " +
            "                when c.coursetype_id in (3,8,10) then pcs.timeontask/3600" +
            "                else 0" +
            "           end) as totalHoursUsage," +
            "       to_char(pcs.createddate, 'YYYY-MM') as period " +
            "from person p " +
            "inner join person_detail pd on p.id = pd.person_id " +
            "left join personcoursesummary pcs on p.id = pcs.person_id " +
            "left join course c on c.id = pcs.course_id " +
            "left join coursetype ct on ct.id = c.coursetype_id " +
            "left join coursesubtype cst on cst.id = c.coursesubtype_id " +
            "where pcs.createddate  between :previousMonthDate and :currentDate and " +
            "      pd.salesforce_purchaser_id = :salesforcePurchaserId " +
            "group by to_char(pcs.createddate, 'YYYY-MM') " +
            "order by to_char(pcs.createddate, 'YYYY-MM') asc", nativeQuery = true)
    List<ActivitiesOverview> getActivitiesOverview(@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                   @Param("previousMonthDate") LocalDate previousMonthDate,
                                                   @Param("currentDate") LocalDate currentDate);

    @Query(value = "select mon.per as month,coalesce(stats.hours,0) as hours " +
            "from ( " +
            "   select to_char(ym, 'YYYY-MM') as per " +
            "   from generate_series ( cast('2022-01-01' as timestamp), cast('2022-12-01' as timestamp) ,cast('1 month' as interval)) ym " +
            ")  mon " +
            "left join ( " +
            "select  " +
            "       sum( " +
            "       case when (c.coursetype_id = 1 and c.coursesubtype_id in (1,2)) or c.coursetype_id = 4 then 25 " +
            "                when c.coursetype_id in (3,8,10) then pcs.timeontask/3600 " +
            "                else 0 " +
            "           end) as hours,             " +
            "       to_char(pcs.createddate, 'YYYY-MM') as per " +
            "from person p " +
            "left join person_detail pd on p.id = pd.person_id " +
            "left join personcoursesummary pcs on p.id = pcs.person_id " +
            "left join course c on c.id = pcs.course_id " +
            "left join coursetype ct on ct.id = c.coursetype_id " +
            "left join coursesubtype cst on cst.id = c.coursesubtype_id " +
            "where extract(year from pcs.createddate) = :year  and " +
            "      c.coursetype_id in ( :courseTypes ) and " +
            "      pd.salesforce_purchaser_id = :salesforcePurchaserId " +
            "group by to_char(pcs.createddate, 'YYYY-MM')" +
            ") as stats on mon.per = stats.per", nativeQuery = true)
    List<ActivityStatistics> getStaticsPerMonth(@Param("salesforcePurchaserId") String salesforcePurchaserId,
                                                @Param("year") int year,
                                                @Param("courseTypes") List<Long> courseTypes );

}
