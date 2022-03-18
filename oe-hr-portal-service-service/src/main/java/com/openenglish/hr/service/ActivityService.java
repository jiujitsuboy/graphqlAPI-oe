package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.ActivitiesOverviewWithIncrementsDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public Optional<ActivitiesOverviewWithIncrementsDto> getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Optional<ActivitiesOverviewWithIncrementsDto> activitiesOverviewWithIncrementsDto = Optional.empty();

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        LocalDate currentMonthDate = LocalDate.now();
        LocalDate previousMonthDate = LocalDate.of(currentMonthDate.getYear(), currentMonthDate.getMonthValue(), 1).minusMonths(12);

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);

        if (activitiesOverviews.size() == 2) {

            activitiesOverviewWithIncrementsDto = Optional.of(ActivitiesOverviewWithIncrementsDto.builder()
                    .groupClasses(activitiesOverviews.get(1).getGroupClasses())
                    .privateClasses(activitiesOverviews.get(1).getPrivateClasses())
                    .completedLessons(activitiesOverviews.get(1).getCompletedLessons())
                    .completedUnits(activitiesOverviews.get(1).getCompletedUnits())
                    .practiceHours(activitiesOverviews.get(1).getPracticeHours())
                    .levelPassed(activitiesOverviews.get(1).getLevelPassed())
                    .totalHoursUsage(activitiesOverviews.get(1).getTotalHoursUsage())
                    .groupClassesIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getGroupClasses(), activitiesOverviews.get(0).getGroupClasses()))
                    .privateClassesIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getPrivateClasses(), activitiesOverviews.get(0).getPrivateClasses()))
                    .completedLessonsIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getCompletedLessons(), activitiesOverviews.get(0).getCompletedLessons()))
                    .completedUnitsIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getCompletedUnits(), activitiesOverviews.get(0).getCompletedUnits()))
                    .practiceHoursIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getPracticeHours(), activitiesOverviews.get(0).getPracticeHours()))
                    .levelPassedIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getLevelPassed(), activitiesOverviews.get(0).getLevelPassed()))
                    .totalHoursUsageIncrement(calculatePercentagesIncrement(activitiesOverviews.get(1).getTotalHoursUsage(), activitiesOverviews.get(0).getTotalHoursUsage()))
                    .period(activitiesOverviews.get(1).getPeriod())
                    .build());
        }

        return activitiesOverviewWithIncrementsDto;
    }

    private double calculatePercentagesIncrement(long currentValue, long previousValue) {
        return currentValue > 0 ? Math.round((((((double) currentValue) / previousValue) - 1) * 100) * 100.0) / 100.0 : 0;
    }
}
