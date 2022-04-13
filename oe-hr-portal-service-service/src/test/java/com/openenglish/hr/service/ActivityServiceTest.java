package com.openenglish.hr.service;

import com.openenglish.hr.persistence.entity.*;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.persistence.repository.PersonCourseAuditRepository;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.NumberUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        final long LIVE_CLASSES_ID = 1;
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


        double januaryTotalCount = personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == JANUARY).count();
        double februaryTotalCount = personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == FEBRUARY).count();
        double marchTotalCount = personCourseAudits.stream().filter(personCourseAudit -> personCourseAudit.getDateCompleted().getMonth().getValue() == MARCH).count();


        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES_ID);

        assertEquals(MONTHS_OF_YEAR, yearActivityStatistics.getMonthsActivityStatistics().size());
        assertEquals(januaryTotalCount, yearActivityStatistics.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalCount, yearActivityStatistics.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalCount, yearActivityStatistics.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);

    }

    @Test
    public void getPracticeStatistics() {
        String salesforcePurchaserId = "12345";
        final long PRACTICE_ID = 3;
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

        double januaryTotalHours = NumberUtils.round((personCourseAuditJAN1.getTimeontask() + personCourseAuditJAN2.getTimeontask() + personCourseAuditJAN3.getTimeontask() + personCourseAuditJAN4.getTimeontask()) / 3600.0, 2);
        double februaryTotalHours = NumberUtils.round((personCourseAuditFEB1.getTimeontask() + personCourseAuditFEB2.getTimeontask() + personCourseAuditFEB3.getTimeontask()) / 3600.0, 2);
        double marchTotalHours = NumberUtils.round((personCourseAuditMAR1.getTimeontask()) / 3600.0, 2);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, PRACTICE_ID);
        assertNotNull(yearActivityStatistics);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatistics.getMonthsActivityStatistics().size());

        assertEquals(januaryTotalHours, yearActivityStatistics.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalHours, yearActivityStatistics.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalHours, yearActivityStatistics.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);
    }

    @Test
    public void getActivitiesStatisticsEmpty() {
        String salesforcePurchaserId = "12345";
        final double ZERO = 0.0;
        final int MONTHS_OF_YEAR = 12;
        final int YEAR = 2022;
        final long LIVE_CLASSES_ID = 1;

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES_ID);

        assertNotNull(yearActivityStatistics);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatistics.getMonthsActivityStatistics().size());
        yearActivityStatistics.getMonthsActivityStatistics()
                .stream()
                .forEach(activityStatistic -> assertThat(activityStatistic.getValue(), equalTo(ZERO)));
    }

    @Test
    public void getStatisticsInvalidSalesforcePurchaserId() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");

        String salesforcePurchaserId = "";
        final int YEAR = 2022;
        final long LIVE_CLASSES_ID = 1;

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES_ID);
    }

    @Test
    public void getStatisticsInvalidActivity() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("courseTypeId should be a value among [1,2,3,4,5,6,8,9,10]");

        String salesforcePurchaserId = "12345";
        final int YEAR = 2022;
        final long INVALID_ACTIVITY_ID = 7;

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        activityService.getActivityStatistics(salesforcePurchaserId, YEAR, INVALID_ACTIVITY_ID);
    }

    @Test
    public void getTopThreeStudentsByPracticeActivityStatistics() {

        final int PERSONS_SIZE = 3;
        final long PRACTICE_ID = 3;
        final long TOP1 = 1L;
        final long TOP2 = 4L;
        final long TOP3 = 2L;
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;

        String salesforcePurchaserId = "12345";

        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 1, 0, 0, 0);

        PersonCourseAudit person1CourseAudit = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
                .timeontask(50)
                .build();

        PersonCourseAudit person2CourseAudit = PersonCourseAudit.builder()
                .person(Person.builder().id(2L).build())
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 22, 10, 0, 0))
                .timeontask(30)
                .build();

        PersonCourseAudit person4CourseAudit = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
                .timeontask(40)
                .build();

        PersonCourseAudit person3CourseAudit = PersonCourseAudit.builder()
                .person(Person.builder().id(3L).build())
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
                .timeontask(20)
                .build();

        PersonCourseAudit person5CourseAudit = PersonCourseAudit.builder()
                .person(Person.builder().id(5L).build())
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
                .timeontask(10)
                .build();

        List<PersonCourseAudit> personCourseAudits = List.of(person1CourseAudit,
                person2CourseAudit,
                person4CourseAudit,
                person3CourseAudit,
                person5CourseAudit);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        LinkedHashMap<Person, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, PRACTICE_ID, PERSONS_SIZE);
        Iterator<Person> persons = personsTop.keySet().iterator();

        assertThat(personsTop.size(), equalTo(PERSONS_SIZE));
        assertThat(persons.next().getId(), is(TOP1));
        assertThat(persons.next().getId(), is(TOP2));
        assertThat(persons.next().getId(), is(TOP3));
    }

    @Test
    public void getTopThreeStudentsByLiveClassesActivityStatistics() {

        final int PERSONS_SIZE = 3;
        final long LIVE_CLASSES_ID = 1;
        final long TOP1 = 1L;
        final long TOP2 = 4L;
        final long TOP3 = 2L;
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;

        String salesforcePurchaserId = "12345";

        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 1, 0, 0, 0);

        PersonCourseAudit person1CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
                .timeontask(50)
                .build();
        PersonCourseAudit person1CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
                .timeontask(70)
                .build();
        PersonCourseAudit person1CourseAudit3 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
                .timeontask(90)
                .build();

        PersonCourseAudit person1CourseAudit4 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person2CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(2L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 22, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person2CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(2L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 23, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person4CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
                .timeontask(10)
                .build();

        PersonCourseAudit person4CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person4CourseAudit3 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 5, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person3CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(3L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person5CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(5L).build())
                .course(Course.builder().courseType(CourseType.builder().id(LIVE_CLASSES_ID).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
                .timeontask(30)
                .build();

        List<PersonCourseAudit> personCourseAudits = List.of(person1CourseAudit1, person1CourseAudit2, person1CourseAudit3, person1CourseAudit4,
                person2CourseAudit1, person2CourseAudit2,
                person4CourseAudit1, person4CourseAudit2, person4CourseAudit3,
                person3CourseAudit1,
                person5CourseAudit1);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, anyLong);
            returns(personCourseAudits);
        }};

        LinkedHashMap<Person, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, LIVE_CLASSES_ID, PERSONS_SIZE);
        Iterator<Person> persons = personsTop.keySet().iterator();

        assertThat(personsTop.size(), equalTo(PERSONS_SIZE));
        assertThat(persons.next().getId(), is(TOP1));
        assertThat(persons.next().getId(), is(TOP2));
        assertThat(persons.next().getId(), is(TOP3));
    }
}