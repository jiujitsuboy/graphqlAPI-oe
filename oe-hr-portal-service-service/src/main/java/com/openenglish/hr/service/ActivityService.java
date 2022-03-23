package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        ActivitiesOverview activitiesOverview = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        return ActivitiesOverviewDto.builder()
                .groupClasses(activitiesOverview.getGroupClasses())
                .privateClasses(activitiesOverview.getPrivateClasses())
                .completedLessons(activitiesOverview.getCompletedLessons())
                .completedUnits(activitiesOverview.getCompletedUnits())
                .practiceHours(activitiesOverview.getPracticeHours())
                .levelPassed(activitiesOverview.getLevelPassed())
                .totalHoursUsage(Math.round(activitiesOverview.getTotalHoursUsage() * 100.0) / 100.0)
                .build();
    }

    private double calculatePercentagesIncrement(double currentValue, double previousValue) {

        double porcentageWithTwoDecimalPositions = 0.0;

        if (previousValue > 0) {
            // rule of 3
            double porcentageRatio = ((double) currentValue) / previousValue;
            //Indicates if the difference is negative or positive against the last month
            double porcentageDifference = (porcentageRatio - 1) * 100;
            // Round the result value to two decimal positions.
            porcentageWithTwoDecimalPositions = Math.round(porcentageDifference * 100.0) / 100.0;
        }

        return porcentageWithTwoDecimalPositions;
//        return previousValue > 0 ? Math.round((((((double) currentValue) / previousValue) - 1) * 100) * 100.0) / 100.0 : 0;
    }
}
