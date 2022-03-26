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

        ActivitiesOverview activitiesOverview11 = ActitiviesOverviewImpl.builder().courseType(1l).courseSubType(2l).timeInMinutes(50.0).build();
        ActivitiesOverview activitiesOverview12 = ActitiviesOverviewImpl.builder().courseType(1l).courseSubType(2l).timeInMinutes(70.0).build();
        ActivitiesOverview activitiesOverview13 = ActitiviesOverviewImpl.builder().courseType(1l).courseSubType(2l).timeInMinutes(90.0).build();
        ActivitiesOverview activitiesOverview21 = ActitiviesOverviewImpl.builder().courseType(3l).courseSubType(0l).timeInMinutes(10.5).build();
        ActivitiesOverview activitiesOverview22 = ActitiviesOverviewImpl.builder().courseType(3l).courseSubType(0l).timeInMinutes(10.5).build();
        ActivitiesOverview activitiesOverview31 = ActitiviesOverviewImpl.builder().courseType(4l).courseSubType(0l).timeInMinutes(30.2).build();
        ActivitiesOverview activitiesOverview32 = ActitiviesOverviewImpl.builder().courseType(4l).courseSubType(0l).timeInMinutes(30.2).build();
        ActivitiesOverview activitiesOverview33 = ActitiviesOverviewImpl.builder().courseType(4l).courseSubType(0l).timeInMinutes(30.2).build();

        List<ActivitiesOverview> activitiesOverviews = List.of(activitiesOverview11, activitiesOverview12, activitiesOverview13,
                activitiesOverview21, activitiesOverview22, activitiesOverview31, activitiesOverview32, activitiesOverview33);

        new Expectations() {{
            activityRepository.getActivitiesOverview(anyString);
            returns(activitiesOverviews);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        long groupClassesNumObtained = List.of(activitiesOverview11, activitiesOverview12, activitiesOverview13).size();
        long privateClassesNumObtained = 0;
        long levelPassedNumObtained = 0;
        long completedLessonsNumObtained = List.of(activitiesOverview31, activitiesOverview32, activitiesOverview33).size();
        long completedUnitsNumObtained = 0;
        double practiceHoursNumObtained = activitiesOverview21.getTimeInMinutes() + activitiesOverview22.getTimeInMinutes();
        double totalMinutesUsageNumObtained = (groupClassesNumObtained * 60) + (privateClassesNumObtained * 30) +
                ((completedLessonsNumObtained + completedUnitsNumObtained) * 25) + (practiceHoursNumObtained);

        totalMinutesUsageNumObtained = NumberUtils.round(totalMinutesUsageNumObtained / 60, 2);

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
        assertEquals(activitiesOverviewDto.getPracticeHours(), (double)ZERO, 0);
        assertEquals(activitiesOverviewDto.getTotalHoursUsage(), (double) ZERO, 0);

    }
}