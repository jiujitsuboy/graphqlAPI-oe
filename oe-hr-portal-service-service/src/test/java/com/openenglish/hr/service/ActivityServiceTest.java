package com.openenglish.hr.service;

import com.openenglish.hr.persistence.entity.Course;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.CourseType;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.repository.PersonCourseAuditRepository;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.NumberUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ActivityServiceTest {

    @Injectable
    private PersonCourseSummaryRepository personCourseSummaryRepository;
    @Injectable
    private PersonCourseAuditRepository personCourseAuditRepository;
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
        double totalMinutesUsageNumObtained = (groupClassesNumObtained * NumberUtils.toSeconds(ActivityService.MINUTES_PER_LIVE_CLASS)) + (privateClassesNumObtained * NumberUtils.toSeconds(ActivityService.MINUTES_PER_PRIVATE_CLASS)) +
                ((completedLessonsNumObtained + completedUnitsNumObtained) * NumberUtils.toSeconds(ActivityService.MINUTES_PER_LESSON_UNIT_ASSESSMENT)) + (practiceHoursNumObtained);

        practiceHoursNumObtained = NumberUtils.round(NumberUtils.convertSecondsToHours(practiceHoursNumObtained), 2);
        totalMinutesUsageNumObtained = NumberUtils.round(NumberUtils.convertSecondsToHours(totalMinutesUsageNumObtained), 2);

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
        final long ZERO = 0L;

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

    @Test
    public void getLiveClassesStatistics() {
        String salesforcePurchaserId = "12345";
        final long LIVE_CLASSES_TYPE = 1;
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;
        final int JANUARY_INDEX = 0;
        final int FEBRUARY_INDEX = 1;
        final int MARCH_INDEX = 2;
        final int MONTHS_OF_YEAR = 12;

        PersonCourseAudit personCourseAudit1 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
                .timeontask(50)
                .build();
        PersonCourseAudit personCourseAudit2 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
                .timeontask(70)
                .build();
        PersonCourseAudit personCourseAudit3 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
                .timeontask(90)
                .build();

        PersonCourseAudit personCourseAudit4 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit personCourseAudit5 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
                .timeontask(10)
                .build();

        PersonCourseAudit personCourseAudit6 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit personCourseAudit7 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit personCourseAudit8 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
                .timeontask(30)
                .build();

        List<PersonCourseAudit> personCourseAudits = List.of(personCourseAudit1, personCourseAudit2, personCourseAudit3,
                personCourseAudit4, personCourseAudit5, personCourseAudit6, personCourseAudit7, personCourseAudit8);


        double januaryTotalCount =  personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == JANUARY).count();
        double februaryTotalCount =  personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == FEBRUARY).count();
        double marchTotalCount = personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == MARCH).count();


        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        List<ActivityStatistics> activityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, YEAR,LIVE_CLASSES_TYPE);

        assertEquals(MONTHS_OF_YEAR, activityStatistics.size());
        assertEquals(januaryTotalCount , activityStatistics.get(JANUARY_INDEX).getValue(),0);
        assertEquals(februaryTotalCount , activityStatistics.get(FEBRUARY_INDEX).getValue(),0);
        assertEquals(marchTotalCount , activityStatistics.get(MARCH_INDEX).getValue(),0);

    }

    @Test
    public void getPracticeStatistics() {
        String salesforcePurchaserId = "12345";
        final long PRACTICE_TYPE = 3;
        final int MONTHS_OF_YEAR = 12;
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;
        final int JANUARY_INDEX = 0;
        final int FEBRUARY_INDEX = 1;
        final int MARCH_INDEX = 2;


        PersonCourseAudit personCourseAuditJAN1 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
                .timeontask(50)
                .build();
        PersonCourseAudit personCourseAuditJAN2 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
                .timeontask(70)
                .build();
        PersonCourseAudit personCourseAuditJAN3 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
                .timeontask(90)
                .build();

        PersonCourseAudit personCourseAuditJAN4 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit personCourseAuditFEB1 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
                .timeontask(10)
                .build();

        PersonCourseAudit personCourseAuditFEB2 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit personCourseAuditFEB3 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit personCourseAuditMAR1 = PersonCourseAudit.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
                .timeontask(30)
                .build();

        List<PersonCourseAudit> personCourseAudits = List.of(personCourseAuditJAN1, personCourseAuditJAN2, personCourseAuditJAN3,
                personCourseAuditJAN4, personCourseAuditFEB1, personCourseAuditFEB2, personCourseAuditFEB3, personCourseAuditMAR1);

        double januaryTotalHours =  NumberUtils.round((personCourseAuditJAN1.getTimeontask() + personCourseAuditJAN2.getTimeontask() + personCourseAuditJAN3.getTimeontask() + personCourseAuditJAN4.getTimeontask())/3600.0,2);
        double februaryTotalHours =  NumberUtils.round((personCourseAuditFEB1.getTimeontask() + personCourseAuditFEB2.getTimeontask() + personCourseAuditFEB3.getTimeontask())/3600.0,2);
        double marchTotalHours =  NumberUtils.round((personCourseAuditMAR1.getTimeontask())/3600.0,2);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        List<ActivityStatistics> activityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, YEAR,PRACTICE_TYPE);
        assertEquals(MONTHS_OF_YEAR, activityStatistics.size());

        assertEquals(januaryTotalHours , activityStatistics.get(JANUARY_INDEX).getValue(),0);
        assertEquals(februaryTotalHours , activityStatistics.get(FEBRUARY_INDEX).getValue(),0);
        assertEquals(marchTotalHours , activityStatistics.get(MARCH_INDEX).getValue(),0);
    }

    @Test
    public void getActivitiesStatisticsEmpty() {
        String salesforcePurchaserId = "12345";
        final double ZERO = 0.0;
        final int MONTHS_OF_YEAR = 12;

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        List<ActivityStatistics> activityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, 0, 1L);
        assertEquals(MONTHS_OF_YEAR, activityStatistics.size());
        activityStatistics
                .stream()
                .forEach(activityStatistic -> assertThat(activityStatistic.getValue(), equalTo(ZERO)));
    }
}