package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.persistence.entity.enums.CourseTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:personCourseSummaryData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseSummaryData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseSummaryRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseSummaryRepository personCourseSummaryRepository;

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 16;
        final int COURSE_TYPE_EXPECTED = 4;
        final double TOTAL_SECONDS_USAGE_NUM_EXPECTED = 340;

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());

        personCourseSummaries.stream()
                .filter(personCourseSummary -> personCourseSummary.getCourse().getCourseType().getId() == COURSE_TYPE_EXPECTED)
                .forEach(personCourseSummary -> {
                    assertThat(personCourseSummary.getCourse().getCourseType().getId(), is(COURSE_TYPE_EXPECTED));
                    assertEquals(TOTAL_SECONDS_USAGE_NUM_EXPECTED, personCourseSummary.getTimeontask(), 0);
                });
    }

    @Test
    public void getActivitiesOverviewEmptyResult() {

        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());
    }

//    @Test
//    public void getStaticsPerMonthForMarch() {
//
//        final int NUMBER_OF_MONTHS = 12;
//        final String MARCH_DATE = "2022-03";
//        final double MARCH_HOURS =  50;
//        String salesforcePurchaserId = "12347";
//        int year = 2022;
//        List<CourseTypeEnum> courseTypeEnums = List.of(CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.LESSON);
//
//        List<Long> courseTypeValues = courseTypeEnums.stream().map(courseTypeEnum -> courseTypeEnum.getValue()).collect(Collectors.toList());
//
//        List<ActivityStatistics> activityStatistics = personCourseSummaryRepository.getStaticsPerMonth(salesforcePurchaserId, year, courseTypeValues);
//
//        assertEquals(NUMBER_OF_MONTHS, activityStatistics.size());
//
//        assertEquals(MARCH_DATE, activityStatistics.get(2).getMonth());
//        assertEquals(MARCH_HOURS, activityStatistics.get(2).getHours(),0);
//    }
//
//    @Test
//    public void getStaticsPerMonthEmpty() {
//
//        final int NUMBER_OF_MONTHS = 12;
//        String salesforcePurchaserId = "12347";
//        int year = 2022;
//        List<CourseTypeEnum> courseTypeEnums = List.of(CourseTypeEnum.PRIVATE_CLASS);
//
//        List<Long> courseTypeValues = courseTypeEnums.stream().map(courseTypeEnum -> courseTypeEnum.getValue()).collect(Collectors.toList());
//
//        List<ActivityStatistics> activityStatistics = personCourseSummaryRepository.getStaticsPerMonth(salesforcePurchaserId, year, courseTypeValues);
//
//        assertEquals(NUMBER_OF_MONTHS, activityStatistics.size());
//
//        activityStatistics.forEach(activityStatistic -> assertEquals(0, activityStatistic.getHours(),0));
//
//    }

    @Test
    public void findPersonCourseSummaryForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 16;
        List<Integer> courseTypes = List.of(1, 2, 3, 4, 5, 8, 10);
        LocalDateTime startDate = LocalDateTime.of(2022, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypes);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());

        personCourseSummaries.stream().forEach(personCourseSummary -> assertEquals(startDate.getYear(), personCourseSummary.getCreatedDate().getYear()));

    }

    @Test
    public void findPersonCourseSummaryEmptyResultForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        List<Integer> courseTypes = List.of(1, 2, 3, 4, 5, 8, 10);
        LocalDateTime startDate = LocalDateTime.of(2021, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypes);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());
    }
}
