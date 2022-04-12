package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.Course;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.persistence.repository.PersonCourseAuditRepository;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.NumberUtils;
import com.openenglish.hr.persistence.entity.aggregation.MonthActivityStatistics;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ActivityService {

    public static final int ONE_ACTIVITY = 1;
    public static final int MINUTES_PER_LESSON_UNIT_ASSESSMENT = 25;
    public static final int MINUTES_PER_PRIVATE_CLASS = 30;
    public static final int MINUTES_PER_LIVE_CLASS = 60;
    private static final Set<Long> PRACTICE_COURSE_TYPES = Set.of(CourseTypeEnum.PRACTICE.getValue(),
            CourseTypeEnum.NEWS.getValue(),
            CourseTypeEnum.IDIOMS.getValue());
    private static final Set<Long> COURSE_TYPES_OF_INTEREST = Set.of(
            CourseTypeEnum.LIVE_CLASS.getValue(),
            CourseTypeEnum.PRIVATE_CLASS.getValue(),
            CourseTypeEnum.LESSON.getValue(),
            CourseTypeEnum.UNIT_ASSESSMENT.getValue(),
            CourseTypeEnum.LEVEL_ASSESSMENT.getValue(),
            CourseTypeEnum.PRACTICE.getValue(),
            CourseTypeEnum.NEWS.getValue(),
            CourseTypeEnum.IDIOMS.getValue()
    );

    private final PersonCourseSummaryRepository personCourseSummaryRepository;
    private final PersonCourseAuditRepository personCourseAuditRepository;


    /**
     * Group and sum the time of each CourseType activity
     *
     * @param salesforcePurchaserId id of the owner of the license
     * @return total time in hours of usage for every CourseType activity
     */
    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        Map<CourseTypeEnum, Integer> courseTypeCounting = getSummingTimeByGroupingCourseTypesCourseSummary(personCourseSummaries);

        double totalTimeInHours = getTotalTimeInHours(courseTypeCounting);

        return ActivitiesOverviewDto.builder()
                .groupClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.LIVE_CLASS, 0).longValue())
                .privateClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.PRIVATE_CLASS, 0).longValue())
                .completedLessons(courseTypeCounting.getOrDefault(CourseTypeEnum.LESSON, 0).longValue())
                .completedUnits(courseTypeCounting.getOrDefault(CourseTypeEnum.UNIT_ASSESSMENT, 0).longValue())
                .practiceHours(NumberUtils.round(NumberUtils.convertSecondsToHours(courseTypeCounting.getOrDefault(CourseTypeEnum.PRACTICE, 0).doubleValue()), 2))
                .levelPassed(courseTypeCounting.getOrDefault(CourseTypeEnum.LEVEL_ASSESSMENT, 0).longValue())
                .totalHoursUsage(NumberUtils.round(totalTimeInHours, 2))
                .build();
    }

    /**
     * Group and sum the time of each CourseType activity by month
     *
     * @param salesforcePurchaserId id of the owner of the license
     * @param year                  target year
     * @param courseTypeId          target activities
     * @return the total sum of all activities by  month
     */
    public YearActivityStatistics getActivityStatistics(String salesforcePurchaserId, int year, long courseTypeId) {

        final int MONTH = 1;
        final int DAY_OF_MONTH = 1;
        final int HOUR = 0;
        final int MINUTE = 0;

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        Preconditions.checkArgument(COURSE_TYPES_OF_INTEREST.contains(courseTypeId),"courseTypeId should be a value among [1,2,3,4,5,6,8,9,10]");

        CourseTypeEnum courseTypeEnum = CourseTypeEnum.getStatusByValue(courseTypeId);

        LocalDateTime startDate = LocalDateTime.of(year, MONTH, DAY_OF_MONTH, HOUR, MINUTE);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudit = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeId);

        Map<Integer, Double> courseTypeCounting = getSummingTimeByGroupingPerMonthCourseAudit(personCourseAudit, courseTypeEnum);

        List<MonthActivityStatistics> activityStatistics = mapActivityStatisticsToMonthsOfYear(courseTypeCounting);

        double yearActivityValue = activityStatistics.stream()
                .mapToDouble(monthStatistic -> monthStatistic.getValue())
                .sum();

        return YearActivityStatistics.builder()
                .monthsActivityStatistics(activityStatistics)
                .total(yearActivityValue).build();
    }

    /**
     * Map to each month the corresponding statictis from the year searched.
     * @param courseTypeCounting Map with every activity and the amount of minutes per month
     * @return List with a MonthActivityStatistics per each month of the year.
     */
    private List<MonthActivityStatistics> mapActivityStatisticsToMonthsOfYear(Map<Integer, Double> courseTypeCounting){
        final int JANUARY = 1;
        final int DECEMBER = 12;

        return IntStream.rangeClosed(JANUARY, DECEMBER)
                .boxed()
                .map(month ->
                MonthActivityStatistics.builder()
                        .month(month)
                        .value(NumberUtils.round(courseTypeCounting.getOrDefault(month, 0.0), 2))
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * Retrieve the top number of students with more activities done on the specified date
     * @param salesforcePurchaserId d of the owner of the license
     * @param startDate Date to filter the top students
     * @param courseTypesNames target activities
     * @param top number of students to return
     * @return Map with each student and his number of activities
     */
    public  LinkedHashMap<Person, Long> getTopStudentsByActivityStatistics(String salesforcePurchaserId, LocalDateTime startDate, List<Long> courseTypesNames, int top){

        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository
                .findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypesNames);

        Map<Person, Long> courseTypeCountingByPerson = this.getTotalActivityCountGroupedByPerson(personCourseSummaries);

        return this.getTopStudents(courseTypeCountingByPerson, top);

    }

    /**
     * Sort the students from most active to less and retrieve the specified number
     * @param courseTypeCountingByPerson Map with students and their respective number of activities
     * @param top number of students to return
     * @return ordered map with students and their number of activities
     */
    private  LinkedHashMap<Person, Long> getTopStudents(Map<Person, Long> courseTypeCountingByPerson, int top){
        return courseTypeCountingByPerson.entrySet().stream()
                .sorted((entry1,entry2)->entry2.getValue().compareTo(entry1.getValue()))
                .limit(top)
                .collect(Collectors.toMap(entry->entry.getKey(), entry->entry.getValue(),(x,y)->y, LinkedHashMap::new));
    }

    /**
     * Group Person by activity total count
     * @param personCourseSummaries List of student course activities
     * @return Map with every activity total count by Person
     */
    private Map<Person, Long> getTotalActivityCountGroupedByPerson(List<PersonCourseSummary> personCourseSummaries) {
        return personCourseSummaries
                .stream()
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(this.getCourseTypeId(personCourseSummary.getCourse())))
                .collect(Collectors.groupingBy(personCourseSummary -> personCourseSummary.getPerson(), Collectors.counting()));
    }

    /**
     * Calculate the total time in hours of each course type activity
     *
     * @param courseTypeCounting Map with every activity and the amount of minutes and seconds for each one
     * @return the total sum of all activities
     */
    private double getTotalTimeInHours(Map<CourseTypeEnum, Integer> courseTypeCounting) {

        return NumberUtils.convertSecondsToHours((double) courseTypeCounting
                .entrySet()
                .stream()
                .mapToInt(this::convertActivitiesOccurrenceToSeconds)
                .sum());
    }

    /**
     * Group and sum every activity and all their time
     *
     * @param personCourseSummaries List of student course activities
     * @return Map with every activity and the amount of minutes
     */
    private Map<CourseTypeEnum, Integer> getSummingTimeByGroupingCourseTypesCourseSummary(List<PersonCourseSummary> personCourseSummaries) {
        return personCourseSummaries
                .stream()
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(this.getCourseTypeId(personCourseSummary.getCourse())))
                .collect(Collectors.groupingBy(personCourseSummary -> CourseTypeEnum.getStatusByValue(this.getCourseTypeId(personCourseSummary.getCourse())),
                        Collectors.summingInt(this::getAmountOfTimePerActivity)));
    }

    /**
     * Group and sum every activity by month and year
     *
     * @param personCoursesAudit List of student course activities
     * @return Map with every activity and the amount of minutes per month
     */
    private Map<Integer, Double> getSummingTimeByGroupingPerMonthCourseAudit(List<PersonCourseAudit> personCoursesAudit, CourseTypeEnum courseTypeEnum) {

        //Select strategy to sum the activities according to the type (Practice: sum timeontask and the total is converted to hours, other Activities: sum the number of occurrence)
        Collector<PersonCourseAudit, ?, Double> collectorStatistics = courseTypeEnum == CourseTypeEnum.PRACTICE ?
                Collectors.collectingAndThen(Collectors.summingDouble(PersonCourseAudit::getTimeontask), NumberUtils::convertSecondsToHours) :
                Collectors.summingDouble((PersonCourseAudit personCourseAudit) -> ONE_ACTIVITY);

        return personCoursesAudit
                .stream()
                .filter(personCourseAudit -> COURSE_TYPES_OF_INTEREST.contains(this.getCourseTypeId(personCourseAudit.getCourse())))
                .collect(Collectors.groupingBy(this::getActivityMonth, collectorStatistics));
    }

    /**
     * return the amount of seconds for Practice activities and return 1 for every other activity
     *
     * @param personCourseSummary List of student course activities
     * @return time for activity
     */
    private int getAmountOfTimePerActivity(PersonCourseSummary personCourseSummary) {
        return PRACTICE_COURSE_TYPES.contains(this.getCourseTypeId(personCourseSummary.getCourse())) ? personCourseSummary.getTimeontask() : ONE_ACTIVITY;
    }

    /**
     * For every type of activity calculate the corresponding amount of time in seconds
     *
     * @param entry Map with every activity and the amount of minutes
     * @return amount of time in seconds
     */
    private int convertActivitiesOccurrenceToSeconds(Map.Entry<CourseTypeEnum, Integer> entry) {
        return convertActivitiesOccurrenceToSeconds(entry.getKey(), entry.getValue());
    }

    /**
     * For every type of activity calculate the corresponding amount of time in seconds
     *
     * @param courseTypeEnum type of activity
     * @param numberOfTimes  number of time the same activity is present
     * @return amount of time in seconds
     */
    private int convertActivitiesOccurrenceToSeconds(CourseTypeEnum courseTypeEnum, int numberOfTimes) {
        int timeInSeconds = 0;
        switch (courseTypeEnum) {
            case LIVE_CLASS:
                timeInSeconds = numberOfTimes * NumberUtils.toSeconds(MINUTES_PER_LIVE_CLASS);
                break;
            case PRIVATE_CLASS:
                timeInSeconds = numberOfTimes * NumberUtils.toSeconds(MINUTES_PER_PRIVATE_CLASS);
                break;
            case LESSON:
            case UNIT_ASSESSMENT:
                timeInSeconds = numberOfTimes * NumberUtils.toSeconds(MINUTES_PER_LESSON_UNIT_ASSESSMENT);
                break;
            case PRACTICE:
                timeInSeconds = numberOfTimes;
                break;
        }
        return timeInSeconds;
    }

    /**
     * gets the course type id from a course
     *
     * @param course course
     * @return course type id
     */
    private long getCourseTypeId(Course course) {
        return course.getCourseType().getId();
    }

    /**
     * Get the month of the activity from dateCompleted or from dateStarted
     *
     * @param personCourseAudit List of student course activities
     * @return month value number
     */
    private int getActivityMonth(PersonCourseAudit personCourseAudit) {
        return personCourseAudit.getDateCompleted() == null ?
                personCourseAudit.getDateStarted().getMonth().getValue() :
                personCourseAudit.getDateCompleted().getMonth().getValue();
    }
}
