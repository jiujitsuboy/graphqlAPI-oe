package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.ActivityRepository;
import com.openenglish.hr.service.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivitiesOverviewDto getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");

        long groupClassesNumObtained = 0;
        long privateClassesNumObtained = 0;
        long levelPassedNumObtained = 0;
        long completedLessonsNumObtained = 0;
        long completedUnitsNumObtained = 0;
        long practiceHoursNumObtained = 0;
        double totalMinutesUsageNumObtained = 0.0;

        List<ActivitiesOverview> activitiesOverviews = activityRepository.getActivitiesOverview(salesforcePurchaserId);

        for (ActivitiesOverview activitiesOverview : activitiesOverviews) {
            groupClassesNumObtained += activitiesOverview.getGroupClasses();
            privateClassesNumObtained += activitiesOverview.getPrivateClasses();
            levelPassedNumObtained += activitiesOverview.getLevelPassed();
            completedLessonsNumObtained += activitiesOverview.getCompletedLessons();
            completedUnitsNumObtained += activitiesOverview.getCompletedUnits();
            practiceHoursNumObtained += activitiesOverview.getPracticeHours();
            totalMinutesUsageNumObtained += activitiesOverview.getTotalMinutesUsage();
        };

        return ActivitiesOverviewDto.builder()
                .groupClasses(groupClassesNumObtained)
                .privateClasses(privateClassesNumObtained)
                .completedLessons(completedLessonsNumObtained)
                .completedUnits(completedUnitsNumObtained)
                .practiceHours(practiceHoursNumObtained)
                .levelPassed(levelPassedNumObtained)
                .totalHoursUsage(NumberUtils.round(totalMinutesUsageNumObtained/60,2))
                .build();
    }
}
