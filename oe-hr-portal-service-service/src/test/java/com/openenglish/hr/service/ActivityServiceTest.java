package com.openenglish.hr.service;

import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.Course;
import com.openenglish.hr.persistence.entity.CourseType;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
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
    private PersonCourseSummaryRepository personCourseSummaryRepository;
    @Tested
    private ActivityService activityService;

    @Test
    public void getCurrentMonthActivitiesOverview() {

        String salesforcePurchaserId = "12345";

        PersonCourseSummary personCourseSummary11 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(50)
                .build();
        PersonCourseSummary personCourseSummary12 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(70)
                .build();
        PersonCourseSummary personCourseSummary13 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(90)
                .build();

        PersonCourseSummary personCourseSummary21 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .timeontask(10)
                .build();
        PersonCourseSummary personCourseSummary22 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .timeontask(10)
                .build();

        PersonCourseSummary personCourseSummary31 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .timeontask(30)
                .build();
        PersonCourseSummary personCourseSummary32 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .timeontask(30)
                .build();
        PersonCourseSummary personCourseSummary33 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .timeontask(30)
                .build();

        List<PersonCourseSummary> personCourseSummaries = List.of(personCourseSummary11, personCourseSummary12, personCourseSummary13,
                personCourseSummary21, personCourseSummary22, personCourseSummary31, personCourseSummary32, personCourseSummary33);

        new Expectations() {{
            personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(anyString);
            returns(personCourseSummaries);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        long groupClassesNumObtained = List.of(personCourseSummary11, personCourseSummary12, personCourseSummary13).size();
        long privateClassesNumObtained = 0;
        long levelPassedNumObtained = 0;
        long completedLessonsNumObtained = List.of(personCourseSummary31, personCourseSummary32, personCourseSummary33).size();
        long completedUnitsNumObtained = 0;
        double practiceHoursNumObtained = personCourseSummary21.getTimeontask() + personCourseSummary22.getTimeontask();
        double totalMinutesUsageNumObtained = (groupClassesNumObtained * activityService.toSeconds(ActivityService.MINUTES_PER_LIVE_CLASS)) + (privateClassesNumObtained * activityService.toSeconds(ActivityService.MINUTES_PER_PRIVATE_CLASS)) +
                ((completedLessonsNumObtained + completedUnitsNumObtained) * activityService.toSeconds(ActivityService.MINUTES_PER_LESSON_UNIT_ASSESSMENT)) + (practiceHoursNumObtained);

        practiceHoursNumObtained = NumberUtils.round(practiceHoursNumObtained / ActivityService.SECONDS_IN_HOUR, 2);
        totalMinutesUsageNumObtained = NumberUtils.round(totalMinutesUsageNumObtained / ActivityService.SECONDS_IN_HOUR, 2);

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

        List<PersonCourseSummary> personCourseSummaries = new ArrayList<>();

        new Expectations() {{
            personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(anyString);
            returns(personCourseSummaries);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        assertEquals(activitiesOverviewDto.getGroupClasses(), ZERO);
        assertEquals(activitiesOverviewDto.getPrivateClasses(), ZERO);
        assertEquals(activitiesOverviewDto.getLevelPassed(), ZERO);
        assertEquals(activitiesOverviewDto.getCompletedUnits(), ZERO);
        assertEquals(activitiesOverviewDto.getCompletedLessons(), ZERO);
        assertEquals(activitiesOverviewDto.getPracticeHours(), (double) ZERO, 0);
        assertEquals(activitiesOverviewDto.getTotalHoursUsage(), (double) ZERO, 0);

    }
}