package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.enums.CourseTypeEnum;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.CustomActivityRepository;
import com.openenglish.hr.service.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    public final int ONE_RECORD = 1;
    public static final int SECONDS_IN_HOUR = 60;
    public static final int LESSON_UNIT_ASSESSMENT_CONVERSION_FACTOR = 1500;
    public static final int PRIVATE_CLASS_CONVERSION_FACTOR = 1800;
    public static final int LIVE_CLASS_CONVERSION_FACTOR = 3600;
    private static final Set PRACTICE_COURSE_TYPES = Set.of(CourseTypeEnum.PRACTICE.getValue(),
            CourseTypeEnum.NEWS.getValue(),
            CourseTypeEnum.IDIOMS.getValue());
    private final CustomActivityRepository customActivityRepository;

    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        List<ActivitiesOverview> activitiesOverviews = customActivityRepository.getActivitiesOverview(salesforcePurchaserId);

        Map<CourseTypeEnum, Double> courseTypeCounting = activitiesOverviews.stream()
                .collect(Collectors.groupingBy(activitiesOverview ->  CourseTypeEnum.getCourseTypeByValue(activitiesOverview.getCourseType()),
                        Collectors.summingDouble(activitiesOverview ->
                        PRACTICE_COURSE_TYPES.contains(activitiesOverview.getCourseType()) ? activitiesOverview.getTimeInSeconds() : ONE_RECORD
                )));

        double totalTimeInHours = courseTypeCounting.entrySet().stream()
                .mapToDouble(ActivityService::convertActivitiesOccurrenceToMinutes)
                .sum() / SECONDS_IN_HOUR;

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

    private static Double convertActivitiesOccurrenceToMinutes(Map.Entry<CourseTypeEnum, Double> entry) {
        double timeInMinutes = 0;
        switch (entry.getKey()) {
            case LIVE_CLASS:
                timeInMinutes = entry.getValue() * LIVE_CLASS_CONVERSION_FACTOR;
                break;
            case PRIVATE_CLASS:
                timeInMinutes = entry.getValue() * PRIVATE_CLASS_CONVERSION_FACTOR;
                break;
            case LESSON:
            case UNIT_ASSESSMENT:
                timeInMinutes = entry.getValue() * LESSON_UNIT_ASSESSMENT_CONVERSION_FACTOR;
                break;
            case PRACTICE:
                timeInMinutes = entry.getValue();
                break;
        }
        return timeInMinutes;
    }
}
