package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivitiesOverviewDto {
    private long groupClasses;
    private long privateClasses;
    private long learnedLessons;
    private long completedUnits;
    private long practiceHours;
    private long levelPassed;
    private long totalHoursUsage;
    private String period;
}
