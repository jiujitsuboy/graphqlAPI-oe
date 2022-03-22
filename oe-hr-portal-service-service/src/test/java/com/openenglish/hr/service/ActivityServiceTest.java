package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.ActivitiesOverviewWithIncrementsDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    public void getActivitiesStatistics(){
        String salesforcePurchaserId = "12345";
        int year = 2022;
        List<Long> activities = List.of(1l);

        List<ActivityStatistics> activityStatistics = null;

        List<ActivityStatistics> activitiesOverviewsExpected = IntStream.rangeClosed(1,12).boxed().map(number->new ActivityStatistics(){
            @Override
            public String getMonth() {
                return String.format("2022-%02d",number);
            }

            @Override
            public double getHours() {
                return 0;
            }
        }).collect(Collectors.toList());

        new Expectations() {{
            activityRepository.getStaticsPerMonth(anyString, anyInt, (List<Long>) any);
            returns(activitiesOverviewsExpected);
        }};

       List<ActivityStatistics> activityStatisticsResult = activityService.getActivitiesStatistics(salesforcePurchaserId,year,activities);

       assertEquals(activitiesOverviewsExpected.size(),activityStatisticsResult.size());

       for(int index = 1; index< activityStatisticsResult.size(); index++){
           ActivityStatistics currentActivityStatics =  activityStatisticsResult.get(index-1);
           assertEquals(String.format("2022-%02d",index),currentActivityStatics.getMonth());
           assertEquals(activitiesOverviewsExpected.get(index-1).getHours(),currentActivityStatics.getHours(),0);
       }
    }
}