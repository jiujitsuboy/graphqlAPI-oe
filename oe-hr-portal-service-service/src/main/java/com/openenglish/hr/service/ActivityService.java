package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.NumberUtils;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

    /**
     * Group and sum the time of each CourseType activity
     *
     * @param salesforcePurchaserId id of the owner of the license
     * @return total time in hours of usage for every CourseType activity
     */
    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        Map<CourseTypeEnum, Integer> courseTypeCounting = getSummingTimeByGroupingCourseTypes(personCourseSummaries);

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
     * @param courseTypesNames      target activities
     * @return the total sum of all activities by  month
     */
    public List<ActivityStatistics> getActivitiesStatistics(String salesforcePurchaserId, int year, List<Integer> courseTypesNames) {

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypesNames);

        Map<Integer, Double> courseTypeCounting = getSummingTimeByGroupingPerMonth(personCourseSummaries);

        //Generate all 12 months
        return IntStream.rangeClosed(1, 12).boxed().map(month ->
                ActivityStatistics.builder()
                        .month(month)
                        .hours(NumberUtils.round(courseTypeCounting.getOrDefault(month, 0.0), 2))
                        .build()
        ).collect(Collectors.toList());
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
    private Map<CourseTypeEnum, Integer> getSummingTimeByGroupingCourseTypes(List<PersonCourseSummary> personCourseSummaries) {
        return personCourseSummaries
                .stream()
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(personCourseSummary.getCourse().getCourseType().getId().longValue()))
                .collect(Collectors.groupingBy(personCourseSummary -> CourseTypeEnum.getStatusByValue(personCourseSummary.getCourse().getCourseType().getId().longValue()),
                        Collectors.summingInt(this::getAmountOfTimePerActivity)));
    }

    /**
     * Group and sum every activity by month and year
     *
     * @param personCourseSummaries List of student course activities
     * @return Map with every activity and the amount of minutes per month
     */
    private Map<Integer, Double> getSummingTimeByGroupingPerMonth(List<PersonCourseSummary> personCourseSummaries) {
        return personCourseSummaries
                .stream()
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(personCourseSummary.getCourse().getCourseType().getId().longValue()))
                .collect(Collectors.groupingBy(personCourseSummary -> personCourseSummary.getCreatedDate().getMonth().getValue(),
                                Collectors.collectingAndThen(
                                        Collectors.summingDouble(this::convertActivitiesOccurrenceToSeconds), NumberUtils::convertSecondsToHours)
                        )
                );
    }

    /**
     * return the amount of seconds for Practice activities and return 1 for every other activity
     *
     * @param personCourseSummary List of student course activities
     * @return time for activity
     */
    private int getAmountOfTimePerActivity(PersonCourseSummary personCourseSummary) {
        return PRACTICE_COURSE_TYPES.contains(personCourseSummary.getCourse().getCourseType().getId().longValue()) ? personCourseSummary.getTimeontask() : ONE_ACTIVITY;
    }

    /**
     * For every type of activity calculate the corresponding amount of time in seconds
     *
     * @param entry Map with every activity and the amount of minutes
     * @return amount of time in seconds
     */
    private Integer convertActivitiesOccurrenceToSeconds(Map.Entry<CourseTypeEnum, Integer> entry) {
        return convertActivitiesOccurrenceToSeconds(entry.getKey(), entry.getValue());
    }

    /**
     * For every type of activity calculate the corresponding amount of time in seconds
     *
     * @param personCourseSummary List of student course activities
     * @return amount of time in seconds
     */
    private int convertActivitiesOccurrenceToSeconds(PersonCourseSummary personCourseSummary) {
        CourseTypeEnum courseTypeEnum = CourseTypeEnum.getStatusByValue(personCourseSummary.getCourse().getCourseType().getId().longValue());
        return convertActivitiesOccurrenceToSeconds(courseTypeEnum, courseTypeEnum == CourseTypeEnum.PRACTICE ? personCourseSummary.getTimeontask() : 1);
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
}
