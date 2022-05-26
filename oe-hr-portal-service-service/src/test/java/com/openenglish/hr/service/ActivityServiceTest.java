package com.openenglish.hr.service;

import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonUsageLevelDto;
import com.openenglish.hr.common.dto.UsageLevelOverviewDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.*;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.persistence.repository.LevelTestRepository;
import com.openenglish.hr.persistence.repository.PersonCourseAuditRepository;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.InterfaceUtil;
import com.openenglish.hr.service.util.NumberUtils;
import mockit.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.*;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ActivityServiceTest {

    @Injectable
    private PersonCourseSummaryRepository personCourseSummaryRepository;
    @Injectable
    private PersonCourseAuditRepository personCourseAuditRepository;
    @Injectable
    private LevelTestRepository levelTestRepository;
    @Tested
    private ActivityService activityService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Injectable
    private Clock clock;

    @Test
    public void getCurrentMonthActivitiesOverview() {

        String salesforcePurchaserId = "12345";

        PersonCourseSummary personCourseSummary11 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(50)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary12 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(70)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary13 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
                .timeontask(90)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();

        PersonCourseSummary personCourseSummary21 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .timeontask(10)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary22 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
                .timeontask(10)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary23 = PersonCourseSummary.builder()
            .course(Course.builder().courseType(CourseType.builder().id(8L).build()).build())
            .timeontask(10)
            .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
            .build();
        PersonCourseSummary personCourseSummary24 = PersonCourseSummary.builder()
            .course(Course.builder().courseType(CourseType.builder().id(10L).build()).build())
            .timeontask(10)
            .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
            .build();

        PersonCourseSummary personCourseSummary31 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .timeontask(30)
                .lastDateCompleted(LocalDateTime.of(2022,2,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary32 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .timeontask(30)
                .lastDateCompleted(LocalDateTime.of(2022,6,15, 0 ,0))
                .build();
        PersonCourseSummary personCourseSummary33 = PersonCourseSummary.builder()
                .course(Course.builder().courseType(CourseType.builder().id(4L).build()).build())
                .lastDateCompleted(LocalDateTime.of(2022,6,15, 0 ,0))
                .timeontask(30)
                .build();

        List<PersonCourseSummary> personCourseSummaries = List.of(personCourseSummary11, personCourseSummary12, personCourseSummary13,
                personCourseSummary21, personCourseSummary22, personCourseSummary23, personCourseSummary24, personCourseSummary31,
                personCourseSummary32, personCourseSummary33);

        new Expectations() {{
            personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(anyString);
            returns(personCourseSummaries);
        }};

        ActivitiesOverviewDto activitiesOverviewDto = activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);

        long groupClassesNumObtained = List.of(personCourseSummary11, personCourseSummary12, personCourseSummary13).size();
        long privateClassesNumObtained = 0;
        long levelPassedNumObtained = 0;
        long completedUnitsNumObtained = 0;
        double practiceHoursNumObtained = personCourseSummary21.getTimeontask() + personCourseSummary22.getTimeontask() +
            personCourseSummary23.getTimeontask() + personCourseSummary24.getTimeontask();
        long completedLessonsNumObtainedBeforeJun2022 = List.of(personCourseSummary31).size();
        long completedLessonsNumObtainedAfterMay2022 = List.of(personCourseSummary32, personCourseSummary33).size();
        long completedLessonsNumObtained = completedLessonsNumObtainedBeforeJun2022 + completedLessonsNumObtainedAfterMay2022;

        double totalMinutesUsageNumObtained = (groupClassesNumObtained * NumberUtils.toSeconds(ActivityService.MINUTES_PER_LIVE_CLASS)) +
            (privateClassesNumObtained * NumberUtils.toSeconds(ActivityService.MINUTES_PER_PRIVATE_CLASS)) +
            ((completedLessonsNumObtainedBeforeJun2022 + completedUnitsNumObtained) * NumberUtils.toSeconds(ActivityService.MINUTES_PER_LESSON_UNIT_ASSESSMENT_BEFORE_JUN2022)) +
            (completedLessonsNumObtainedAfterMay2022 * NumberUtils.toSeconds(ActivityService.MINUTES_PER_LESSON_UNIT_ASSESSMENT_AFTER_JUN2022)) +
        (practiceHoursNumObtained);

        practiceHoursNumObtained = NumberUtils.round(NumberUtils.convertSecondsToHours(practiceHoursNumObtained));
        totalMinutesUsageNumObtained = NumberUtils.round(NumberUtils.convertSecondsToHours(totalMinutesUsageNumObtained));

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
        final Long PERSON_ID = null;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);
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
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES, PERSON_ID);

        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());
        assertEquals(januaryTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);

    }

    @Test
    public void getPracticeStatistics() {
        String salesforcePurchaserId = "12345";
        final Long PERSON_ID = null;
        final Set<CourseTypeEnum> PRACTICE = Set.of(CourseTypeEnum.PRACTICE);
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

        double januaryTotalHours = NumberUtils.round((personCourseAuditJAN1.getTimeontask() + personCourseAuditJAN2.getTimeontask() + personCourseAuditJAN3.getTimeontask() + personCourseAuditJAN4.getTimeontask()) / 3600.0);
        double februaryTotalHours = NumberUtils.round((personCourseAuditFEB1.getTimeontask() + personCourseAuditFEB2.getTimeontask() + personCourseAuditFEB3.getTimeontask()) / 3600.0);
        double marchTotalHours = NumberUtils.round((personCourseAuditMAR1.getTimeontask()) / 3600.0);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, PRACTICE, PERSON_ID);
        assertNotNull(yearActivityStatisticsDto);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());

        assertEquals(januaryTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);
    }

    @Test
    public void getActivitiesStatisticsEmpty() {
        String salesforcePurchaserId = "12345";
        final Long PERSON_ID = null;
        final double ZERO = 0.0;
        final int MONTHS_OF_YEAR = 12;
        final int YEAR = 2022;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES, PERSON_ID);

        assertNotNull(yearActivityStatisticsDto);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());
        yearActivityStatisticsDto.getMonthsActivityStatistics()
                .stream()
                .forEach(activityStatistic -> assertThat(activityStatistic.getValue(), equalTo(ZERO)));
    }

    @Test
    public void getStatisticsInvalidSalesforcePurchaserId() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");

        String salesforcePurchaserId = "";
        final Long PERSON_ID = null;
        final int YEAR = 2022;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES, PERSON_ID);
    }

    @Test
    public void getStatisticsInvalidActivity() {

        String salesforcePurchaserId = "12345";
        final Long PERSON_ID = null;
        final int YEAR = 2022;
        final Set<CourseTypeEnum> INVALID_ACTIVITY_ID = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("courseTypesEnum should not be null or empty");

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        activityService.getActivityStatistics(salesforcePurchaserId, YEAR, INVALID_ACTIVITY_ID, PERSON_ID);
    }

    @Test
    public void getTopThreeStudentsByPracticeActivityStatistics() {

        final int PERSONS_SIZE = 3;
        final Set<CourseTypeEnum> PRACTICE = Set.of(CourseTypeEnum.PRACTICE);
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
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        LinkedHashMap<PersonDto, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, PRACTICE, PERSONS_SIZE);
        Iterator<PersonDto> persons = personsTop.keySet().iterator();

        assertThat(personsTop.size(), equalTo(PERSONS_SIZE));
        assertThat(persons.next().getId(), is(TOP1));
        assertThat(persons.next().getId(), is(TOP2));
        assertThat(persons.next().getId(), is(TOP3));
    }

    @Test
    public void getTopThreeStudentsByLiveClassesActivityStatistics() {

        final int PERSONS_SIZE = 3;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);
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
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
                .timeontask(50)
                .build();
        PersonCourseAudit person1CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
                .timeontask(70)
                .build();
        PersonCourseAudit person1CourseAudit3 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
                .timeontask(90)
                .build();

        PersonCourseAudit person1CourseAudit4 = PersonCourseAudit.builder()
                .person(Person.builder().id(1L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person2CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(2L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 22, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person2CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(2L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 23, 10, 0, 0))
                .timeontask(10)
                .build();
        PersonCourseAudit person4CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
                .timeontask(10)
                .build();

        PersonCourseAudit person4CourseAudit2 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person4CourseAudit3 = PersonCourseAudit.builder()
                .person(Person.builder().id(4L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 5, 8, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person3CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(3L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
                .timeontask(30)
                .build();
        PersonCourseAudit person5CourseAudit1 = PersonCourseAudit.builder()
                .person(Person.builder().id(5L).build())
                .course(Course.builder().courseType(CourseType.builder().id(CourseTypeEnum.LIVE_CLASS.getValue()).build()).build())
                .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
                .timeontask(30)
                .build();

        List<PersonCourseAudit> personCourseAudits = List.of(person1CourseAudit1, person1CourseAudit2, person1CourseAudit3, person1CourseAudit4,
                person2CourseAudit1, person2CourseAudit2,
                person4CourseAudit1, person4CourseAudit2, person4CourseAudit3,
                person3CourseAudit1,
                person5CourseAudit1);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatistics(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any);
            returns(personCourseAudits);
        }};

        LinkedHashMap<PersonDto, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, LIVE_CLASSES, PERSONS_SIZE);
        Iterator<PersonDto> persons = personsTop.keySet().iterator();

        assertThat(personsTop.size(), equalTo(PERSONS_SIZE));
        assertThat(persons.next().getId(), is(TOP1));
        assertThat(persons.next().getId(), is(TOP2));
        assertThat(persons.next().getId(), is(TOP3));
    }

    @Test
    public void getTopThreeStudentsByLevelAssessmentsActivityStatistics() {

        final int PERSONS_SIZE = 3;
        final Set<CourseTypeEnum> LEVEL_ASSESSMENT = Set.of(CourseTypeEnum.LEVEL_ASSESSMENT);
        final long TOP1 = 110005L;
        final long TOP2 = 110002L;
        final long TOP3 = 110001L;
        final int YEAR = 2022;
        final int FEBRUARY = 2;

        String salesforcePurchaserId = "12345";

        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 1, 0, 0, 0);

        LevelsPassedByPerson levelsPassedByPerson1 = InterfaceUtil.createLevelsPassedByPerson(110001, "","","",10);
        LevelsPassedByPerson levelsPassedByPerson2 = InterfaceUtil.createLevelsPassedByPerson(110002, "","","",20);
        LevelsPassedByPerson levelsPassedByPerson3 = InterfaceUtil.createLevelsPassedByPerson(110003, "","","",5);
        LevelsPassedByPerson levelsPassedByPerson4 = InterfaceUtil.createLevelsPassedByPerson(110004, "","","",1);
        LevelsPassedByPerson levelsPassedByPerson5 = InterfaceUtil.createLevelsPassedByPerson(110005, "","","",30);

        List<LevelsPassedByPerson> levelsPassedByPersons = List.of(levelsPassedByPerson1, levelsPassedByPerson2, levelsPassedByPerson3, levelsPassedByPerson4, levelsPassedByPerson5);

        new Expectations() {{
            levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(anyString, (LocalDateTime) any, (LocalDateTime) any);
            returns(levelsPassedByPersons);
        }};

        LinkedHashMap<PersonDto, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, LEVEL_ASSESSMENT, PERSONS_SIZE);
        Iterator<PersonDto> persons = personsTop.keySet().iterator();

        assertThat(personsTop.size(), equalTo(PERSONS_SIZE));
        assertThat(persons.next().getId(), is(TOP1));
        assertThat(persons.next().getId(), is(TOP2));
        assertThat(persons.next().getId(), is(TOP3));
    }

    @Test
    public void getTopThreeStudentsInvalidSalesforcePurchaserId() {

        final int PERSONS_SIZE = 3;
        final Set<CourseTypeEnum> LEVEL_ASSESSMENT = Set.of(CourseTypeEnum.LEVEL_ASSESSMENT);
        final int YEAR = 2022;
        final int FEBRUARY = 2;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");


        String salesforcePurchaserId = "";

        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 1, 0, 0, 0);

        List<LevelsPassedByPerson> levelsPassedByPersons = new ArrayList<>();

        new Expectations() {{
            levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(anyString, (LocalDateTime) any, (LocalDateTime) any);
            returns(levelsPassedByPersons);
        }};

        activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, LEVEL_ASSESSMENT, PERSONS_SIZE);
    }

    @Test
    public void getTopThreeStudentsInvalidActivity() {

        final int PERSONS_SIZE = 3;
        final Set<CourseTypeEnum> INVALID_ACTIVITY = null;
        final int YEAR = 2022;
        final int FEBRUARY = 2;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("courseTypesEnum should not be null or empty");


        String salesforcePurchaserId = "12345";

        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 1, 0, 0, 0);

        List<LevelsPassedByPerson> levelsPassedByPersons = new ArrayList<>();

        new Expectations() {{
            levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(anyString, (LocalDateTime) any, (LocalDateTime) any);
            returns(levelsPassedByPersons);
        }};

        activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, startDate, INVALID_ACTIVITY, PERSONS_SIZE);
    }

    @Test
    public void getPersonsPerUserLevelOverview() {

        final long HIGH_AMOUNT = 1;
        final long MEDIUM_HIGH_AMOUNT = 1;
        final long MEDIUM_LOW_AMOUNT = 1;
        final long LOW_AMOUNT = 3;

        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        String salesforcePurchaserId = "12345";

        List<UsageLevel> usageLevels = List.of(
            InterfaceUtil.createUsageLevel(110001, "Patrik", "Smith","sf_synegen123", LocalDateTime.of(2022, 4, 15, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110002, "Michale", "Bale","sf_synegen321", LocalDateTime.of(2022, 3, 23, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110003, "Jake", "Sullivan", "sf_synegen456",LocalDateTime.of(2022, 2, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110004, "Claire", "Redfield","sf_synegen654", LocalDateTime.of(2021, 12, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110005, "Ana", "Stuart", "sf_synegen789",LocalDateTime.of(2022, 1, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110006, "Sam", "Walmart", "sf_synegen987",LocalDateTime.of(2022, 3, 12, 0, 0, 0))
        );

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

            personCourseAuditRepository.findMaxActivityDateGroupedByPerson(anyString, anyString);
            returns(usageLevels);
        }};

        UsageLevelOverviewDto usageLevelsDto = activityService.getUsageLevelOverview(salesforcePurchaserId);

        assertThat(usageLevelsDto.getHigh(), is(HIGH_AMOUNT));
        assertThat(usageLevelsDto.getMediumHigh(), is(MEDIUM_HIGH_AMOUNT));
        assertThat(usageLevelsDto.getMediumLow(), is(MEDIUM_LOW_AMOUNT));
        assertThat(usageLevelsDto.getLow(), is(LOW_AMOUNT));

    }

    @Test
    public void getLeastActiveStudents(){

        final int PERSONS_SIZE = 4;

        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        String salesforcePurchaserId = "12345";

        List<UsageLevel> usageLevels = List.of(
            InterfaceUtil.createUsageLevel(110001, "Patrik", "Smith","sf_synegen123", LocalDateTime.of(2022, 4, 15, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110002, "Michale", "Bale","sf_synegen321", LocalDateTime.of(2022, 3, 23, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110003, "Jake", "Sullivan", "sf_synegen456",LocalDateTime.of(2022, 2, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110004, "Claire", "Redfield","sf_synegen654", LocalDateTime.of(2021, 12, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110005, "Ana", "Stuart", "sf_synegen789",LocalDateTime.of(2022, 1, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110006, "Sam", "Walmart","sf_synegen987", LocalDateTime.of(2022, 3, 12, 0, 0, 0))
        );

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

            personCourseAuditRepository.findMaxActivityDateGroupedByPerson(anyString, anyString);
            returns(usageLevels);
        }};

        List<PersonUsageLevelDto> personUsageLevelOverviewDtos = activityService.getLeastActiveStudents(salesforcePurchaserId);
        assertEquals(PERSONS_SIZE, personUsageLevelOverviewDtos.size());

        personUsageLevelOverviewDtos.stream()
                .forEach(personUsageLevelDto -> ActivityService.LOW_USAGE_TYPES.contains(personUsageLevelDto.getUsageLevel()));

    }

    @Test
    public void getLeastActiveStudentsEmpty(){

        final int EMPTY_SIZE = 0;

        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        String salesforcePurchaserId = "12345";

        List<UsageLevel> usageLevels = List.of(
            InterfaceUtil.createUsageLevel(110001, "Patrik", "Smith","sf_synegen123", LocalDateTime.of(2022, 4, 15, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110002, "Michale", "Bale", "sf_synegen321",LocalDateTime.of(2022, 3, 23, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110003, "Jake", "Sullivan","sf_synegen456", LocalDateTime.of(2022, 3, 20, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110004, "Claire", "Redfield","sf_synegen654", LocalDateTime.of(2023, 4, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110005, "Ana", "Stuart", "sf_synegen789",LocalDateTime.of(2022, 4, 2, 0, 0, 0)),
            InterfaceUtil.createUsageLevel(110006, "Sam", "Walmart", "sf_synegen987",LocalDateTime.of(2022, 3, 29, 0, 0, 0))
        );

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

            personCourseAuditRepository.findMaxActivityDateGroupedByPerson(anyString, anyString);
            returns(usageLevels);
        }};

        List<PersonUsageLevelDto> personUsageLevelOverviewDtos = activityService.getLeastActiveStudents(salesforcePurchaserId);
        assertEquals(EMPTY_SIZE, personUsageLevelOverviewDtos.size());
    }

    @Test
    public void getUsageLevelOverviewPerPerson(){

        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        String salesforcePurchaserId = "12345";
        String contactId = "sf_synegen123";
        final long INACTIVE_DAYS = 1L;
        UsageLevelEnum expectedUsageLevel = UsageLevelEnum.HIGH;

        UsageLevel usageLevel = InterfaceUtil.createUsageLevel(110001, "Patrik", "Smith","sf_synegen123", LocalDateTime.of(2022, 4, 15, 0, 0, 0));

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

            personCourseAuditRepository.findMaxActivityDateGroupedByPerson(anyString, anyString);
            returns(List.of(usageLevel));
        }};

        Optional<PersonUsageLevelDto> optPersonUsageLevelDto = activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId,contactId);

        assertTrue(optPersonUsageLevelDto.isPresent());

        PersonUsageLevelDto personUsageLevelDto = optPersonUsageLevelDto.get();

        assertThat(personUsageLevelDto.getPerson().getContactId(), is(contactId));
        assertThat(personUsageLevelDto.getUsageLevel(), is(expectedUsageLevel));
        assertThat(personUsageLevelDto.getInactiveDays(), is(INACTIVE_DAYS));
    }

    @Test
    public void getUsageLevelOverviewPerPersonEmpty(){

        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        String salesforcePurchaserId = "12345";
        String contactId = "sf_synegen123";

        List<UsageLevel> usageLevel = Collections.EMPTY_LIST;

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

            personCourseAuditRepository.findMaxActivityDateGroupedByPerson(anyString, anyString);
            returns(usageLevel);
        }};

        Optional<PersonUsageLevelDto> optPersonUsageLevelDto = activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId,contactId);

        assertFalse(optPersonUsageLevelDto.isPresent());
    }

    @Test
    public void getUsageLevelOverviewPerPersonInvalidSalesforcePurchaserId() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");

        String salesforcePurchaserId = "";
        String contactId = "110001";
        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

        }};

        activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId,contactId);
    }

    @Test
    public void getUsageLevelOverviewPerPersonNullContactId() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("personId should not be null or empty");

        String salesforcePurchaserId = "12347";
        String contactId = null;
        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

        }};

        activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId,contactId);
    }

    @Test
    public void getUsageLevelOverviewPerPersonWithContactIdEmpty() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("personId should not be null or empty");

        String salesforcePurchaserId = "12347";
        String contactId = "";
        LocalDate currentTime = LocalDate.of(2022,4,16);
        Clock fixedClock = Clock.fixed(currentTime.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        new Expectations() {{
            clock.instant();
            returns(fixedClock.instant());
            clock.getZone();
            returns(fixedClock.getZone());

        }};

        activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId,contactId);
    }

    @Test
    public void getLiveClassesStatisticsByPersonId() {
        String salesforcePurchaserId = "12345";
        final long PERSON_ID = 11L;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;
        final int JANUARY_INDEX = 0;
        final int FEBRUARY_INDEX = 1;
        final int MARCH_INDEX = 2;
        final int MONTHS_OF_YEAR = 12;

        PersonCourseAudit personCourseAudit1 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
            .timeontask(50)
            .build();
        PersonCourseAudit personCourseAudit2 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
            .timeontask(70)
            .build();
        PersonCourseAudit personCourseAudit3 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
            .timeontask(90)
            .build();

        PersonCourseAudit personCourseAudit4 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
            .timeontask(10)
            .build();
        PersonCourseAudit personCourseAudit5 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
            .timeontask(10)
            .build();

        PersonCourseAudit personCourseAudit6 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
            .timeontask(30)
            .build();
        PersonCourseAudit personCourseAudit7 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(1L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
            .timeontask(30)
            .build();
        PersonCourseAudit personCourseAudit8 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
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
            personCourseAuditRepository.findActivityStatisticsByPerson(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES, PERSON_ID);

        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());
        assertEquals(januaryTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalCount, yearActivityStatisticsDto.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);

    }

    @Test
    public void getPracticeStatisticsByPersonId() {
        String salesforcePurchaserId = "12345";
        final Long PERSON_ID = 11L;
        final Set<CourseTypeEnum> PRACTICE = Set.of(CourseTypeEnum.PRACTICE);
        final int MONTHS_OF_YEAR = 12;
        final int YEAR = 2022;
        final int JANUARY = 1;
        final int FEBRUARY = 2;
        final int MARCH = 3;
        final int JANUARY_INDEX = 0;
        final int FEBRUARY_INDEX = 1;
        final int MARCH_INDEX = 2;


        PersonCourseAudit personCourseAuditJAN1 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 15, 12, 0, 0))
            .timeontask(50)
            .build();
        PersonCourseAudit personCourseAuditJAN2 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 19, 14, 0, 0))
            .timeontask(70)
            .build();
        PersonCourseAudit personCourseAuditJAN3 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 20, 12, 0, 0))
            .timeontask(90)
            .build();

        PersonCourseAudit personCourseAuditJAN4 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, JANUARY, 21, 10, 0, 0))
            .timeontask(10)
            .build();
        PersonCourseAudit personCourseAuditFEB1 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 15, 12, 0, 0))
            .timeontask(10)
            .build();

        PersonCourseAudit personCourseAuditFEB2 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 8, 0, 0))
            .timeontask(30)
            .build();
        PersonCourseAudit personCourseAuditFEB3 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, FEBRUARY, 7, 12, 0, 0))
            .timeontask(30)
            .build();
        PersonCourseAudit personCourseAuditMAR1 = PersonCourseAudit.builder()
            .person(Person.builder().id(PERSON_ID).firstName("John").lastName("Sanchez").email("johnsanchez@gmail.com").build())
            .course(Course.builder().courseType(CourseType.builder().id(3L).build()).build())
            .dateCompleted(LocalDateTime.of(YEAR, MARCH, 1, 12, 0, 0))
            .timeontask(30)
            .build();

        List<PersonCourseAudit> personCourseAudits = List.of(personCourseAuditJAN1, personCourseAuditJAN2, personCourseAuditJAN3,
            personCourseAuditJAN4, personCourseAuditFEB1, personCourseAuditFEB2, personCourseAuditFEB3, personCourseAuditMAR1);

        double januaryTotalHours = NumberUtils.round((personCourseAuditJAN1.getTimeontask() + personCourseAuditJAN2.getTimeontask() + personCourseAuditJAN3.getTimeontask() + personCourseAuditJAN4.getTimeontask()) / 3600.0);
        double februaryTotalHours = NumberUtils.round((personCourseAuditFEB1.getTimeontask() + personCourseAuditFEB2.getTimeontask() + personCourseAuditFEB3.getTimeontask()) / 3600.0);
        double marchTotalHours = NumberUtils.round((personCourseAuditMAR1.getTimeontask()) / 3600.0);

        new Expectations() {{
            personCourseAuditRepository.findActivityStatisticsByPerson(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, PRACTICE, PERSON_ID);
        assertNotNull(yearActivityStatisticsDto);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());

        assertEquals(januaryTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(JANUARY_INDEX).getValue(), 0);
        assertEquals(februaryTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(FEBRUARY_INDEX).getValue(), 0);
        assertEquals(marchTotalHours, yearActivityStatisticsDto.getMonthsActivityStatistics().get(MARCH_INDEX).getValue(), 0);
    }

    @Test
    public void getActivitiesStatisticsByPersonIdEmpty() {
        String salesforcePurchaserId = "12345";
        final Long PERSON_ID = 12L;
        final double ZERO = 0.0;
        final int MONTHS_OF_YEAR = 12;
        final int YEAR = 2022;
        final Set<CourseTypeEnum> LIVE_CLASSES = Set.of(CourseTypeEnum.LIVE_CLASS);

        List<PersonCourseAudit> personCourseAudits = new ArrayList<>();

        new Expectations() {{
            personCourseAuditRepository.findActivityStatisticsByPerson(anyString, (LocalDateTime) any, (LocalDateTime) any, (Set<Long>) any, anyLong);
            returns(personCourseAudits);
        }};

        YearActivityStatisticsDto yearActivityStatisticsDto = activityService.getActivityStatistics(salesforcePurchaserId, YEAR, LIVE_CLASSES, PERSON_ID);

        assertNotNull(yearActivityStatisticsDto);
        assertEquals(MONTHS_OF_YEAR, yearActivityStatisticsDto.getMonthsActivityStatistics().size());
        yearActivityStatisticsDto.getMonthsActivityStatistics()
            .stream()
            .forEach(activityStatistic -> assertThat(activityStatistic.getValue(), equalTo(ZERO)));
    }
}