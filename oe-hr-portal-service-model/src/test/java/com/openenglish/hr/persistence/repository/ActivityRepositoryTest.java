package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        long completedUnitsNum = 1;
        long practiceHoursNum = 0;
        double totalHoursUsageNum = 0.03;

        ActivitiesOverview activitiesOverview = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertThat(activitiesOverview.getGroupClasses(), is(groupClassesNum));
        assertThat(activitiesOverview.getPrivateClasses(), is(privateClassesNum));
        assertThat(activitiesOverview.getLevelPassed(), is(levelPassedNum));
        assertThat(activitiesOverview.getCompletedLessons(), is(completedLessonsNum));
        assertThat(activitiesOverview.getCompletedUnits(), is(completedUnitsNum));
        assertThat(activitiesOverview.getPracticeHours(), is(practiceHoursNum));
        assertEquals(totalHoursUsageNum, Math.round(activitiesOverview.getTotalHoursUsage() * 100.0)/100.0 ,0);
    }

    @Test
    public void getActivitiesOverviewEmptyResult() {

        String salesforcePurchaserId = "12348";
        final long ZERO = 0l;

        ActivitiesOverview activitiesOverview = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertThat(activitiesOverview.getGroupClasses(), is(ZERO));
        assertThat(activitiesOverview.getPrivateClasses(), is(ZERO));
        assertThat(activitiesOverview.getLevelPassed(), is(ZERO));
        assertThat(activitiesOverview.getCompletedLessons(), is(ZERO));
        assertThat(activitiesOverview.getCompletedUnits(), is(ZERO));
        assertThat(activitiesOverview.getPracticeHours(), is(ZERO));
        assertThat(activitiesOverview.getTotalHoursUsage()  ,is((double)ZERO));
    }
}
