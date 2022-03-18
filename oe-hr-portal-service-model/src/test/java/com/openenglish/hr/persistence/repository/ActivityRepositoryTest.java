package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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
                .filter(activitiesOverview -> activitiesOverview.getPeriod().equals(String.format("%d-%02d",currentMonthDate.getYear(),currentMonthDate.getMonthValue())))
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
}
