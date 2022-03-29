package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.enums.CourseTypeEnum;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import com.openenglish.hr.service.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    public final int ONE_MINUTE = 1;
    public static final int HOURS_MINUTES = 60;
    public static final int MINUTES_SECONDS = 60;
    public static final int MINUTES_FACTOR_25 = 25;
    public static final int MINUTES_FACTOR_30 = 30;
    public static final int MINUTES_FACTOR_60 = 60;
    private static final List PRACTICE_COURSE_TYPES = List.of(CourseTypeEnum.PRACTICE.getValue(),
            CourseTypeEnum.NEWS.getValue(),
            CourseTypeEnum.IDIOMS.getValue());
    private final ActivityRepository activityRepository;

    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        Map<CourseTypeEnum, Double> courseTypeCounting = activitiesOverviews.stream()
                .collect(Collectors.groupingBy(ActivityService::classifyByCourseType, Collectors.summingDouble(activitiesOverview ->
                        CollectionUtils.contains(PRACTICE_COURSE_TYPES.iterator(), activitiesOverview.getCourseType()) ? activitiesOverview.getTimeInSeconds() / MINUTES_SECONDS : ONE_MINUTE
                )));

        double totalTimeInHours = courseTypeCounting.entrySet().stream()
                .mapToDouble(ActivityService::convertActivitiesOccurrenceToMinutes)
                .sum() / HOURS_MINUTES;

        return ActivitiesOverviewDto.builder()
                .groupClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.LIVE_CLASS, 0.0).longValue())
                .privateClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.PRIVATE_CLASS, 0.0).longValue())
                .completedLessons(courseTypeCounting.getOrDefault(CourseTypeEnum.LESSON, 0.0).longValue())
                .completedUnits(courseTypeCounting.getOrDefault(CourseTypeEnum.UNIT_ASSESSMENT, 0.0).longValue())
                .practiceHours(courseTypeCounting.getOrDefault(CourseTypeEnum.PRACTICE, 0.0).doubleValue())
                .levelPassed(courseTypeCounting.getOrDefault(CourseTypeEnum.LEVEL_ASSESSMENT, 0.0).longValue())
                .totalHoursUsage(NumberUtils.round(totalTimeInHours, 2))
                .build();
    }

    private static CourseTypeEnum classifyByCourseType(ActivitiesOverview activitiesOverview) {
        //default value, which is not used to calculate anything
        CourseTypeEnum courseTypeEnum = CourseTypeEnum.LEVEL_ZERO;
        if (activitiesOverview.getCourseType() != null) {
            courseTypeEnum = CourseTypeEnum.getCourseTypeByValue(activitiesOverview.getCourseType());
        }
        return courseTypeEnum != null ? courseTypeEnum : CourseTypeEnum.PRACTICE;
    }

    private static Double convertActivitiesOccurrenceToMinutes(Map.Entry<CourseTypeEnum, Double> entry) {
        double timeHours = 0;
        switch (entry.getKey()) {
            case LIVE_CLASS:
                timeHours = entry.getValue() * MINUTES_FACTOR_60;
                break;
            case PRIVATE_CLASS:
                timeHours = entry.getValue() * MINUTES_FACTOR_30;
                break;
            case LESSON:
            case UNIT_ASSESSMENT:
                timeHours = entry.getValue() * MINUTES_FACTOR_25;
                break;
            case PRACTICE:
                timeHours = entry.getValue();
                break;
        }
        return timeHours;
    }
}
