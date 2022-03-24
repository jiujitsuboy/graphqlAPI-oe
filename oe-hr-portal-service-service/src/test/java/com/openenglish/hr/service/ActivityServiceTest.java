package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.impl.ActitiviesOverviewImpl;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import com.openenglish.hr.service.util.NumberUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ActivityServiceTest {

    @Injectable
    private ActivityRepository activityRepository;
    @Tested
    private ActivityService activityService;

    @Test
    public void getCurrentMonthActivitiesOverview() {

        String salesforcePurchaserId = "12345";

        ActivitiesOverview activitiesOverview1 = ActitiviesOverviewImpl.builder()
                .groupClasses(20).privateClasses(30).completedLessons(40).completedUnits(50).practiceHours(60).levelPassed(70).totalMinutesUsage(80.0333333)
                .build();
        ActivitiesOverview activitiesOverview2 = ActitiviesOverviewImpl.builder()
                .groupClasses(20).privateClasses(30).completedLessons(40).completedUnits(50).practiceHours(60).levelPassed(70).totalMinutesUsage(80.0333333)
                .build();
        ActivitiesOverview activitiesOverview3 = ActitiviesOverviewImpl.builder()
                .groupClasses(20).privateClasses(30).completedLessons(40).completedUnits(50).practiceHours(60).levelPassed(70).totalMinutesUsage(80.0333333)
                .build();

        List<ActivitiesOverview> activitiesOverviews = List.of(activitiesOverview1, activitiesOverview2, activitiesOverview3);

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString);
            returns(activitiesOverviews);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        long groupClassesNumObtained = activitiesOverview1.getGroupClasses() + activitiesOverview2.getGroupClasses() + activitiesOverview3.getGroupClasses();
        long privateClassesNumObtained = activitiesOverview1.getPrivateClasses() + activitiesOverview2.getPrivateClasses() + activitiesOverview3.getPrivateClasses();
        long levelPassedNumObtained = activitiesOverview1.getLevelPassed() + activitiesOverview2.getLevelPassed() + activitiesOverview3.getLevelPassed();
        long completedLessonsNumObtained = activitiesOverview1.getCompletedLessons() + activitiesOverview2.getCompletedLessons() + activitiesOverview3.getCompletedLessons();
        long completedUnitsNumObtained = activitiesOverview1.getCompletedUnits() + activitiesOverview2.getCompletedUnits() + activitiesOverview3.getCompletedUnits();
        long practiceHoursNumObtained = activitiesOverview1.getPracticeHours() + activitiesOverview2.getPracticeHours() + activitiesOverview3.getPracticeHours();
        double totalMinutesUsageNumObtained = NumberUtils.round((activitiesOverview1.getTotalMinutesUsage() + activitiesOverview2.getTotalMinutesUsage() + activitiesOverview3.getTotalMinutesUsage())/60,2);

        assertThat(groupClassesNumObtained, is(activitiesOverviewDto.getGroupClasses()));
        assertThat(privateClassesNumObtained, is(activitiesOverviewDto.getPrivateClasses()));
        assertThat(levelPassedNumObtained, is(activitiesOverviewDto.getLevelPassed()));
        assertThat(completedLessonsNumObtained, is(activitiesOverviewDto.getCompletedLessons()));
        assertThat(completedUnitsNumObtained, is(activitiesOverviewDto.getCompletedUnits()));
        assertThat(practiceHoursNumObtained, is(activitiesOverviewDto.getPracticeHours()));
        assertThat(totalMinutesUsageNumObtained, is(activitiesOverviewDto.getTotalHoursUsage()));
    }

    @Test
    public void getCurrentMonthActivitiesOverviewEmpty() {

        String salesforcePurchaserId = "12347";
        final long ZERO = 0l;

        List<ActivitiesOverview> activitiesOverviews = new ArrayList<>();

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString);
            returns(activitiesOverviews);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertEquals(activitiesOverviewDto.getGroupClasses(), ZERO);
        assertEquals(activitiesOverviewDto.getPrivateClasses(), ZERO);
        assertEquals(activitiesOverviewDto.getLevelPassed(), ZERO);
        assertEquals(activitiesOverviewDto.getCompletedUnits(), ZERO);
        assertEquals(activitiesOverviewDto.getCompletedLessons(), ZERO);
        assertEquals(activitiesOverviewDto.getPracticeHours(), ZERO);
        assertEquals(activitiesOverviewDto.getTotalHoursUsage(), (double) ZERO, 0);

    }
}