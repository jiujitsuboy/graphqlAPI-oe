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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        Map<CourseTypeEnum, Double> courseTypeCounting = activitiesOverviews.stream()
                .collect(Collectors.groupingBy(activitiesOverview -> {
                    //default value, which is not used to calculate anything
                    CourseTypeEnum courseTypeEnum = CourseTypeEnum.LEVEL_ZERO;
                    if (activitiesOverview.getCourseType() != null) {
                        if (activitiesOverview.getCourseType() != null && activitiesOverview.getCourseType() == 1
                                && activitiesOverview.getCourseSubType() != null && List.of(1l, 2l).contains(activitiesOverview.getCourseSubType())) {
                            courseTypeEnum = CourseTypeEnum.LIVE_CLASS;
                        } else if (activitiesOverview.getCourseType() == 2 && activitiesOverview.getCourseSubType() == 4) {
                            courseTypeEnum = CourseTypeEnum.PRIVATE_CLASS;
                        } else if (activitiesOverview.getCourseType() == 4) {
                            courseTypeEnum = CourseTypeEnum.LESSON;
                        } else if (activitiesOverview.getCourseType() == 5) {
                            courseTypeEnum = CourseTypeEnum.UNIT_ASSESSMENT;
                        } else if (activitiesOverview.getCourseType() == 6) {
                            courseTypeEnum = CourseTypeEnum.LEVEL_ASSESSMENT;
                        } else if (List.of(3l, 8l, 10l).contains(activitiesOverview.getCourseType())) {
                            courseTypeEnum = CourseTypeEnum.PRACTICE;
                        }
                    }
                    return courseTypeEnum;
                }, Collectors.summingDouble(activitiesOverview ->
                        activitiesOverview.getCourseType() != null && List.of(3l, 8l, 10l).contains(activitiesOverview.getCourseType()) ? activitiesOverview.getTimeInMinutes() : 1
                )));

        double totalTimeInHours = courseTypeCounting.entrySet().stream().mapToDouble(entry -> {
            double timeHours = 0;
            switch (entry.getKey()) {
                case LIVE_CLASS:
                    timeHours = entry.getValue() * 60;
                    break;
                case PRIVATE_CLASS:
                    timeHours = entry.getValue() * 30;
                    break;
                case LESSON:
                case UNIT_ASSESSMENT:
                    timeHours = entry.getValue() * 25;
                    break;
                case PRACTICE:
                    timeHours = entry.getValue();
                    break;
            }
            return timeHours;
        }).sum() / 60;

        return ActivitiesOverviewDto.builder()
                .groupClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.LIVE_CLASS, 0.0).longValue())
                .privateClasses(courseTypeCounting.getOrDefault(CourseTypeEnum.PRIVATE_CLASS, 0.0).longValue())
                .completedLessons(courseTypeCounting.getOrDefault(CourseTypeEnum.LESSON, 0.0).longValue())
                .completedUnits(courseTypeCounting.getOrDefault(CourseTypeEnum.UNIT_ASSESSMENT, 0.0).longValue())
                .practiceHours(courseTypeCounting.getOrDefault(CourseTypeEnum.PRACTICE, 0.0).longValue())
                .levelPassed(courseTypeCounting.getOrDefault(CourseTypeEnum.LEVEL_ASSESSMENT, 0.0).longValue())
                .totalHoursUsage(NumberUtils.round(totalTimeInHours, 2))
                .build();
    }
}
