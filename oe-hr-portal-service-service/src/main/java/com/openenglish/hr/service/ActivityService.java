package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.service.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    public static final int ONE_ACTIVITY = 1;
    public static final int SECONDS_IN_HOUR = 3600;
    public static final int SECONDS_IN_MINUTE = 60;
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
     * @param salesforcePurchaserId id of the owner of the licencse
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
                .practiceHours(NumberUtils.round(courseTypeCounting.getOrDefault(CourseTypeEnum.PRACTICE, 0).doubleValue()/SECONDS_IN_HOUR,2))
                .levelPassed(courseTypeCounting.getOrDefault(CourseTypeEnum.LEVEL_ASSESSMENT, 0).longValue())
                .totalHoursUsage(NumberUtils.round(totalTimeInHours, 2))
                .build();
    }

    private double getTotalTimeInHours(Map<CourseTypeEnum, Integer> courseTypeCounting) {

        return (double) courseTypeCounting
                .entrySet()
                .stream()
                .mapToInt(this::convertActivitiesOccurrenceToSeconds)
                .sum() / SECONDS_IN_HOUR;
    }

    private Map<CourseTypeEnum, Integer> getSummingTimeByGroupingCourseTypes(List<PersonCourseSummary> personCourseSummaries) {
        return personCourseSummaries
                .stream()
                .filter(personCourseSummary -> COURSE_TYPES_OF_INTEREST.contains(personCourseSummary.getCourse().getCourseType().getId()))
                .collect(Collectors.groupingBy(personCourseSummary -> CourseTypeEnum.getStatusByValue(personCourseSummary.getCourse().getCourseType().getId()),
                        Collectors.summingInt(this::summingTimeForActivities)));
    }

    private int summingTimeForActivities(PersonCourseSummary personCourseSummary) {
        return PRACTICE_COURSE_TYPES.contains(personCourseSummary.getCourse().getCourseType().getId()) ? personCourseSummary.getTimeontask() : ONE_ACTIVITY;
    }

    private Integer convertActivitiesOccurrenceToSeconds(Map.Entry<CourseTypeEnum, Integer> entry) {
        int timeInSeconds = 0;
        switch (entry.getKey()) {
            case LIVE_CLASS:
                timeInSeconds = entry.getValue() * toSeconds(MINUTES_PER_LIVE_CLASS);
                break;
            case PRIVATE_CLASS:
                timeInSeconds = entry.getValue() * toSeconds(MINUTES_PER_PRIVATE_CLASS);
                break;
            case LESSON:
            case UNIT_ASSESSMENT:
                timeInSeconds = entry.getValue() * toSeconds(MINUTES_PER_LESSON_UNIT_ASSESSMENT);
                break;
            case PRACTICE:
                timeInSeconds = entry.getValue();
                break;
        }
        return timeInSeconds;
    }

    public int toSeconds(int minutes) {
        return minutes * SECONDS_IN_MINUTE;
    }
}
