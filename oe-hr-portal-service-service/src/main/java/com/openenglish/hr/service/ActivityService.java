package com.openenglish.hr.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.PersonUsageLevelDto;
import com.openenglish.hr.common.dto.UsageLevelOverviewDto;
import com.openenglish.hr.persistence.entity.Course;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.persistence.repository.LevelTestRepository;
import com.openenglish.hr.persistence.repository.PersonCourseAuditRepository;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.mapper.PersonUsageLevelDtoMapper;
import com.openenglish.hr.service.util.NumberUtils;
import com.openenglish.hr.persistence.entity.aggregation.MonthActivityStatistics;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BinaryOperator;
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
    public  static final Set<UsageLevelEnum> LOW_USAGE_TYPES = Set.of(UsageLevelEnum.MEDIUM_LOW, UsageLevelEnum.LOW);

    private final PersonCourseSummaryRepository personCourseSummaryRepository;
    private final PersonCourseAuditRepository personCourseAuditRepository;
    private final LevelTestRepository levelTestRepository;
    private final Clock clock;


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
                .practiceHours(NumberUtils.round(NumberUtils.convertSecondsToHours(courseTypeCounting.getOrDefault(CourseTypeEnum.PRACTICE, 0).doubleValue())))
                .levelPassed(courseTypeCounting.getOrDefault(CourseTypeEnum.LEVEL_ASSESSMENT, 0).longValue())
                .totalHoursUsage(NumberUtils.round(totalTimeInHours))
                .build();
    }

    /**
     * Group and sum the time of each CourseType activity by month
     *
     * @param salesforcePurchaserId id of the owner of the license
     * @param year                  target year
     * @param courseTypeEnums       target activities
     * @return the total sum of all activities by  month
     */
    public YearActivityStatistics getActivityStatistics(String salesforcePurchaserId, int year, Set<CourseTypeEnum> courseTypeEnums) {

        final int MONTH = 1;
        final int DAY_OF_MONTH = 1;
        final int HOUR = 0;
        final int MINUTE = 0;

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(courseTypeEnums), "courseTypesEnum should not be null or empty");

        Set<Long> courseTypeIds = getCourseTypesIds(courseTypeEnums);

        LocalDateTime startDate = LocalDateTime.of(year, MONTH, DAY_OF_MONTH, HOUR, MINUTE);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudit = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        Map<Integer, Double> courseTypeCounting = (Map<Integer, Double>)
                getTotalActivityCountGroupedByCustomCriteria(personCourseAudit, (CourseTypeEnum) getFirstElementFromSet(courseTypeEnums),
                        this::getActivityMonth);


        List<MonthActivityStatistics> activityStatistics = mapActivityStatisticsToMonthsOfYear(courseTypeCounting);

        double yearActivityValue = activityStatistics.stream()
                .mapToDouble(monthStatistic -> monthStatistic.getValue())
                .sum();

        return YearActivityStatistics.builder()
                .monthsActivityStatistics(activityStatistics)
                .total(yearActivityValue).build();
    }

    /**
     * Retrieve the top number of students with more activities done on the specified date
     *
     * @param salesforcePurchaserId Id of the owner of the license
     * @param startDate             Date to filter the top students
     * @param courseTypeEnums       target activities
     * @param top                   number of students to return
     * @return Map with each student and his number of activities
     */
    public LinkedHashMap<Long, Double> getTopStudentsByActivityStatistics(String salesforcePurchaserId, LocalDateTime startDate, Set<CourseTypeEnum> courseTypeEnums, int top) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(courseTypeEnums), "courseTypesEnum should not be null or empty");

        Map<Long, Double> courseTypeCountingByPerson = null;
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        Set<Long> courseTypeIds = getCourseTypesIds(courseTypeEnums);

        if (isLevelAssessment(courseTypeEnums)) {
            List<LevelsPassedByPerson> levelsPassedByPersons = levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(salesforcePurchaserId, startDate, endDate);
            courseTypeCountingByPerson = levelsPassedByPersons.stream().collect(Collectors.toMap(entry -> entry.getPersonId(), entry -> entry.getTotalNumber()));
        } else {
            List<PersonCourseAudit> personCoursesAudit = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds);
            courseTypeCountingByPerson = (Map<Long, Double>) getTotalActivityCountGroupedByCustomCriteria(personCoursesAudit, (CourseTypeEnum) getFirstElementFromSet(courseTypeEnums), (PersonCourseAudit personCourseAudit) -> personCourseAudit.getPerson().getId());
        }

        return this.getTopStudents(courseTypeCountingByPerson, top);

    }

    /**
     * Retrieve the number of students and group then by their corresponding UsageLevel
     * <p>
     * HIGH:  last activity completed in the last 10 days
     * MEDIUM HIGH: last activity completed betwenn last 11 and 30 days
     * MEDIUM LOW last activity completed betwenn last 31 and 60 days
     * LOW: last activity completed after the last 61 days
     *
     * @param salesforcePurchaserId Id of the owner of the license
     * @return UsageLevelsDto
     */
    public UsageLevelOverviewDto getUsageLevelOverview(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        List<UsageLevel> usageLevels = personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId);

        Map<UsageLevelEnum, Long> usageLevelCountingByPersons = usageLevels.stream()
            .collect(Collectors.groupingBy(this::mapStudentsToUsageLevel,
                Collectors.summingLong(personCourseAudit -> ONE_ACTIVITY)));

        return UsageLevelOverviewDto.builder()
                .high(usageLevelCountingByPersons.getOrDefault(UsageLevelEnum.HIGH, 0L))
                .mediumHigh(usageLevelCountingByPersons.getOrDefault(UsageLevelEnum.MEDIUM_HIGH, 0L))
                .mediumLow(usageLevelCountingByPersons.getOrDefault(UsageLevelEnum.MEDIUM_LOW, 0L))
                .low(usageLevelCountingByPersons.getOrDefault(UsageLevelEnum.LOW, 0L))
                .build();
    }

    public List<PersonUsageLevelDto> getLeastActiveStudents(String salesforcePurchaserId) {

        LocalDateTime currentTime = LocalDateTime.now(clock);

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        List<UsageLevel> usageLevels = personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId);

        List<PersonUsageLevelDto> personUsageLevelDtos = usageLevels.stream()
                .map(usageLevel ->  PersonUsageLevelDtoMapper.map(usageLevel, currentTime, this::mapStudentsToUsageLevel))
                .filter(personUsageLevelDto -> LOW_USAGE_TYPES.contains(personUsageLevelDto.getUsageLevel()))
                .collect(Collectors.toList());

        return personUsageLevelDtos;
    }

    /**
     * Sort the students from most active to less and retrieve the specified number
     *
     * @param courseTypeCountingByPerson Map with students and their respective number of activities
     * @param top                        number of students to return
     * @return ordered map with students and their number of activities
     */
    private LinkedHashMap<Long, Double> getTopStudents(Map<Long, Double> courseTypeCountingByPerson, int top) {

        BinaryOperator<Double> mappingFunction = (key, value) -> value;

        return courseTypeCountingByPerson.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(top)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> NumberUtils.round(entry.getValue()), mappingFunction::apply, LinkedHashMap::new));
    }

    /**
     * Map to each month the corresponding statictis from the year searched.
     *
     * @param courseTypeCounting Map with every activity and the amount of minutes per month
     * @return List with a MonthActivityStatistics per each month of the year.
     */
    private List<MonthActivityStatistics> mapActivityStatisticsToMonthsOfYear(Map<Integer, Double> courseTypeCounting) {
        final int JANUARY = 1;
        final int DECEMBER = 12;

        return IntStream.rangeClosed(JANUARY, DECEMBER)
                .boxed()
                .map(month ->
                        MonthActivityStatistics.builder()
                                .month(month)
                                .value(NumberUtils.round(courseTypeCounting.getOrDefault(month, 0.0)))
                                .build()
                ).collect(Collectors.toList());
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
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(this.getCourseTypeEnum(personCourseSummary.getCourse()).getValue()))
                .collect(Collectors.groupingBy(personCourseSummary -> getCourseTypeEnum(personCourseSummary.getCourse()),
                        Collectors.summingInt(this::getAmountOfTimePerActivity)));
    }

    /**
     * * Group Persons by custom criteria
     *
     * @param personCourseAudits personCourseAudits List of student course activities
     * @param courseTypeEnum     type of activity
     * @param groupingCriteria   Custom grouping criteria
     * @return Person's Map with the custom criteria result
     */
    private Map<?, ? extends Number> getTotalActivityCountGroupedByCustomCriteria(List<PersonCourseAudit> personCourseAudits, CourseTypeEnum courseTypeEnum, Function<PersonCourseAudit, ?> groupingCriteria) {

        //Select strategy to sum the activities according to the type (Practice: sum timeontask and the total is converted to hours, other Activities: sum the number of occurrence)
        Collector<PersonCourseAudit, ?, Double> collectorStatistics = PRACTICE_COURSE_TYPES.contains(courseTypeEnum.getValue()) ?
                Collectors.collectingAndThen(Collectors.summingDouble(PersonCourseAudit::getTimeontask), NumberUtils::convertSecondsToHours) :
                Collectors.summingDouble((PersonCourseAudit personCourseAudit) -> ONE_ACTIVITY);


        return personCourseAudits
                .stream()
                .filter(personCourseAudit -> COURSE_TYPES_OF_INTEREST.contains(this.getCourseTypeEnum(personCourseAudit.getCourse()).getValue()))
                .collect(Collectors.groupingBy(groupingCriteria::apply, collectorStatistics));
    }

    /**
     * map students by usage level
     * @param usageLevel usage level of the student
     * @return UsageLevelEnum
     */
    private UsageLevelEnum  mapStudentsToUsageLevel(UsageLevel usageLevel) {

        LocalDateTime currentTime = LocalDateTime.now(clock);

        UsageLevelEnum usageLevelEnum = UsageLevelEnum.LOW;

        long daysBetweenDates = usageLevel.getLastActivity().until(currentTime, ChronoUnit.DAYS);

        if (daysBetweenDates <= 10) {
            usageLevelEnum = UsageLevelEnum.HIGH;
        } else if (daysBetweenDates >= 11 && daysBetweenDates <= 30) {
            usageLevelEnum = UsageLevelEnum.MEDIUM_HIGH;
        } else if (daysBetweenDates >= 31 && daysBetweenDates <= 60) {
            usageLevelEnum = UsageLevelEnum.MEDIUM_LOW;
        }
        return usageLevelEnum;

    }

    /**
     * return the amount of seconds for Practice activities and return 1 for every other activity
     *
     * @param personCourseSummary List of student course activities
     * @return time for activity
     */
    private int getAmountOfTimePerActivity(PersonCourseSummary personCourseSummary) {
        return PRACTICE_COURSE_TYPES.contains(this.getCourseTypeEnum(personCourseSummary.getCourse()).getValue()) ? personCourseSummary.getTimeontask() : ONE_ACTIVITY;
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
     * Retrieve the first element of the specified set
     *
     * @param set collection
     * @return first element
     */
    private Object getFirstElementFromSet(Set<?> set) {
        return set.stream().iterator().next();
    }

    /**
     * validate if the specified course type is a level course
     *
     * @param courseTypesEnum target activity
     * @return boolean
     */
    private boolean isLevelAssessment(Set<CourseTypeEnum> courseTypesEnum) {
        return courseTypesEnum.contains(CourseTypeEnum.LEVEL_ASSESSMENT);
    }

    /**
     * Get from the course the name of the course type
     *
     * @param course Course instance
     * @return Course type enum
     */
    private CourseTypeEnum getCourseTypeEnum(Course course) {
        return CourseTypeEnum.getStatusByValue(course.getCourseType().getId());
    }

    /**
     * Gets a set with the ids of the corresponding CourseTypeEnums in the received Set
     *
     * @param courseTypeEnums Set of CourseTypeEnums
     * @return Set of CourseTypeEnum Ids
     */
    private Set<Long> getCourseTypesIds(Set<CourseTypeEnum> courseTypeEnums) {
        return courseTypeEnums.stream().map(CourseTypeEnum::getValue).collect(Collectors.toSet());
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
