package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        final int NUMBER_RECORDS_EXPECTED = 18;
        final long GROUP_CLASSES_NUM_EXPECTED = 0;
        final long PRIVATE_CLASSES_NUM_EXPECTED = 0;
        final long LEVEL_PASSED_NUM_EXPECTED = 0;
        final long COMPLETED_LESSONS_NUM_EXPECTED = 1;
        final long COMPLETED_UNITS_NUM_EXPECTED = 0;
        final long PRACTICE_HOURS_NUM_EXPECTED = 0;
        final double TOTAL_MINUTES_USAGE_NUM_EXPECTED = 25.0;

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, activitiesOverviews.size());
        assertThat(activitiesOverviews.get(0).getGroupClasses(), is(GROUP_CLASSES_NUM_EXPECTED));
        assertThat(activitiesOverviews.get(0).getPrivateClasses(), is(PRIVATE_CLASSES_NUM_EXPECTED));
        assertThat(activitiesOverviews.get(0).getLevelPassed(), is(LEVEL_PASSED_NUM_EXPECTED));
        assertThat(activitiesOverviews.get(0).getCompletedLessons(), is(COMPLETED_LESSONS_NUM_EXPECTED));
        assertThat(activitiesOverviews.get(0).getCompletedUnits(), is(COMPLETED_UNITS_NUM_EXPECTED));
        assertThat(activitiesOverviews.get(0).getPracticeHours(), is(PRACTICE_HOURS_NUM_EXPECTED));
        assertEquals(TOTAL_MINUTES_USAGE_NUM_EXPECTED, activitiesOverviews.get(0).getTotalMinutesUsage() ,0);
    }

    @Test
    public void getActivitiesOverviewEmptyResult() {

        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, activitiesOverviews.size());
    }
}
