package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.persistence.entity.enums.CourseTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@DatabaseSetup(value = "classpath:activityData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:activityData.xml", type = DatabaseOperation.DELETE)
public class ActivityRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        long groupClassesNum = 1;
        long privateClassesNum = 0;
        long levelPassedNum = 0;
        long completedLessonsNum = 1;
        long completedUnitsNum = 0;
        long practiceHoursNum = 0;
        long totalHoursUsageNum = 50;


        LocalDate previousMonthDate = LocalDate.parse("2022-02-01");
        LocalDate currentMonthDate = LocalDate.parse("2022-03-31");

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);

        activitiesOverviews.stream()
                .filter(activitiesOverview -> activitiesOverview.getPeriod().equals(String.format("%d-%02d", currentMonthDate.getYear(), currentMonthDate.getMonthValue())))
                .findFirst()
                .ifPresentOrElse(activity -> {
                    assertThat(activity.getGroupClasses(), is(groupClassesNum));
                    assertThat(activity.getPrivateClasses(), is(privateClassesNum));
                    assertThat(activity.getLevelPassed(), is(levelPassedNum));
                    assertThat(activity.getCompletedLessons(), is(completedLessonsNum));
                    assertThat(activity.getCompletedUnits(), is(completedUnitsNum));
                    assertThat(activity.getPracticeHours(), is(practiceHoursNum));
                    assertThat(activity.getTotalHoursUsage(), is(totalHoursUsageNum));
                }, Assert::fail);
    }

    @Test
    public void getStaticsPerMonthForMarch() {

        final int NUMBER_OF_MONTHS = 12;
        final String MARCH_DATE = "2022-03";
        final double MARCH_HOURS =  50;
        String salesforcePurchaserId = "12347";
        int year = 2022;
        List<CourseTypeEnum> courseTypeEnums = List.of(CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.LESSON);

        List<Long> courseTypeValues = courseTypeEnums.stream().map(courseTypeEnum -> courseTypeEnum.getValue()).collect(Collectors.toList());

        List<ActivityStatistics> activityStatistics = activityRepository.getStaticsPerMonth(salesforcePurchaserId, year, courseTypeValues);

        assertEquals(NUMBER_OF_MONTHS, activityStatistics.size());

        assertEquals(MARCH_DATE, activityStatistics.get(2).getMonth());
        assertEquals(MARCH_HOURS, activityStatistics.get(2).getHours(),0);
    }

    @Test
    public void getStaticsPerMonthEmpty() {

        final int NUMBER_OF_MONTHS = 12;
        String salesforcePurchaserId = "12347";
        int year = 2022;
        List<CourseTypeEnum> courseTypeEnums = List.of(CourseTypeEnum.PRIVATE_CLASS);

        List<Long> courseTypeValues = courseTypeEnums.stream().map(courseTypeEnum -> courseTypeEnum.getValue()).collect(Collectors.toList());

        List<ActivityStatistics> activityStatistics = activityRepository.getStaticsPerMonth(salesforcePurchaserId, year, courseTypeValues);

        assertEquals(NUMBER_OF_MONTHS, activityStatistics.size());

        activityStatistics.forEach(activityStatistic -> assertEquals(0, activityStatistic.getHours(),0));

    }
}
