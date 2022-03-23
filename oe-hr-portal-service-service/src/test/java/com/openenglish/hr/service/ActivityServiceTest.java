package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ActivityServiceTest {

    @Injectable
    private ActivityRepository activityRepository;
    @Tested
    private ActivityService activityService;

    @Test
    public void getCurrentMonthActivitiesOverview(){

        String salesforcePurchaserId = "12345";

        ActivitiesOverview activitiesOverviews = new ActivitiesOverview(){
            @Override
            public long getGroupClasses() {
                return 20;
            }

            @Override
            public long getPrivateClasses() {
                return 30;
            }

            @Override
            public long getCompletedLessons() {
                return 40;
            }

            @Override
            public long getCompletedUnits() {
                return 50;
            }

            @Override
            public long getPracticeHours() {
                return 60;
            }

            @Override
            public long getLevelPassed() {
                return 70;
            }

            @Override
            public double getTotalHoursUsage() {
                return 80.0333333;
            }
        };

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString);
            returns(activitiesOverviews);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertThat(activitiesOverviews.getGroupClasses(), is(activitiesOverviewDto.getGroupClasses()));
        assertThat(activitiesOverviews.getPrivateClasses(), is(activitiesOverviewDto.getPrivateClasses()));
        assertThat(activitiesOverviews.getLevelPassed(), is(activitiesOverviewDto.getLevelPassed()));
        assertThat(activitiesOverviews.getCompletedUnits(), is(activitiesOverviewDto.getCompletedUnits()));
        assertThat(activitiesOverviews.getCompletedLessons(), is(activitiesOverviewDto.getCompletedLessons()));
        assertThat(activitiesOverviews.getPracticeHours(), is(activitiesOverviewDto.getPracticeHours()));
        assertThat(Math.round(activitiesOverviews.getTotalHoursUsage()* 100.0)/100.0, is(activitiesOverviewDto.getTotalHoursUsage()));
    }

    @Test
    public void getCurrentMonthActivitiesOverviewEmpty(){

        String salesforcePurchaserId = "12345";
        final long ZERO = 0l;

        ActivitiesOverview activitiesOverviews = new ActivitiesOverview() {
            @Override
            public long getGroupClasses() {
                return 0;
            }

            @Override
            public long getPrivateClasses() {
                return 0;
            }

            @Override
            public long getCompletedLessons() {
                return 0;
            }

            @Override
            public long getCompletedUnits() {
                return 0;
            }

            @Override
            public long getPracticeHours() {
                return 0;
            }

            @Override
            public long getLevelPassed() {
                return 0;
            }

            @Override
            public double getTotalHoursUsage() {
                return 0;
            }
        };

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString);
            returns(activitiesOverviews);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertEquals(activitiesOverviewDto.getGroupClasses(),ZERO);
        assertEquals(activitiesOverviewDto.getPrivateClasses(),ZERO);
        assertEquals(activitiesOverviewDto.getLevelPassed(),ZERO);
        assertEquals(activitiesOverviewDto.getCompletedUnits(),ZERO);
        assertEquals(activitiesOverviewDto.getCompletedLessons(),ZERO);
        assertEquals(activitiesOverviewDto.getPracticeHours(),ZERO);
        assertEquals(activitiesOverviewDto.getTotalHoursUsage(),(double)ZERO,0);

    }
}