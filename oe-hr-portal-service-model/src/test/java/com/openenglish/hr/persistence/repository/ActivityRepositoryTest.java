package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:activityData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:activityData.xml", type = DatabaseOperation.DELETE)
public class ActivityRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private CustomActivityRepository customActivityRepository;

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 16;
        final long COURSE_TYPE_EXPECTED = 4;
        final double TOTAL_SECONDS_USAGE_NUM_EXPECTED = 340;

        List<ActivitiesOverview> activitiesOverviews = customActivityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, activitiesOverviews.size());

        activitiesOverviews.stream()
                .filter(activitiesOverview -> activitiesOverview.getCourseType() == COURSE_TYPE_EXPECTED)
                .forEach(activitiesOverview -> {
                    assertThat(activitiesOverview.getCourseType(), is(COURSE_TYPE_EXPECTED));
                    assertEquals(TOTAL_SECONDS_USAGE_NUM_EXPECTED, activitiesOverview.getTimeInSeconds(), 0);
                });
    }

    @Test
    public void getActivitiesOverviewEmptyResult() {

        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;

        List<ActivitiesOverview> activitiesOverviews = customActivityRepository.getActivitiesOverview(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, activitiesOverviews.size());
    }
}
