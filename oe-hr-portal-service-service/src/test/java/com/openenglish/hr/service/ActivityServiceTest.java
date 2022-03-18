package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.ActivitiesOverviewWithIncrementsDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ActivityServiceTest {

    @Injectable
    private ActivityRepository activityRepository;
    @Tested
    private ActivityService activityService;

    @Test
    public void getCurrentMonthActivitiesOverview(){

        String salesforcePurchaserId = "12345";

        List<ActivitiesOverview> activitiesOverviews = List.of(new ActivitiesOverview(){
            @Override
            public long getGroupClasses() {
                return 10;
            }

            @Override
            public long getPrivateClasses() {
                return 20;
            }

            @Override
            public long getCompletedLessons() {
                return 30;
            }

            @Override
            public long getCompletedUnits() {
                return 40;
            }

            @Override
            public long getPracticeHours() {
                return 50;
            }

            @Override
            public long getLevelPassed() {
                return 60;
            }

            @Override
            public long getTotalHoursUsage() {
                return 70;
            }

            @Override
            public String getPeriod() {
                return "2022-02";
            }
        }, new ActivitiesOverview(){
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
            public long getTotalHoursUsage() {
                return 80;
            }

            @Override
            public String getPeriod() {
                return "2022-03";
            }
        });

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString, (LocalDate) any, (LocalDate) any);
            returns(activitiesOverviews);
        }};

        Optional<ActivitiesOverviewWithIncrementsDto> activitiesOverviewWithIncrementsDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertTrue(activitiesOverviewWithIncrementsDto.isPresent());

    }

    @Test
    public void getCurrentMonthActivitiesOverviewEmpty(){

        String salesforcePurchaserId = "12345";

        List<ActivitiesOverview> activitiesOverviews = Collections.emptyList();

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString, (LocalDate) any, (LocalDate) any);
            returns(activitiesOverviews);
        }};

        Optional<ActivitiesOverviewWithIncrementsDto> activitiesOverviewWithIncrementsDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertFalse(activitiesOverviewWithIncrementsDto.isPresent());

    }
}