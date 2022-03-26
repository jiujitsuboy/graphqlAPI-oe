package com.openenglish.hr.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitiesOverviewDto {
    private long groupClasses;
    private long privateClasses;
    private long completedLessons;
    private long completedUnits;
    private double practiceHours;
    private long levelPassed;
    private double totalHoursUsage;
}
