package com.openenglish.hr.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitiesOverviewWithIncrementsDto {
    private long groupClasses;
    private long privateClasses;
    private long completedLessons;
    private long completedUnits;
    private long practiceHours;
    private long levelPassed;
    private long totalHoursUsage;
    private double groupClassesIncrement;
    private double privateClassesIncrement;
    private double completedLessonsIncrement;
    private double completedUnitsIncrement;
    private double practiceHoursIncrement;
    private double levelPassedIncrement;
    private double totalHoursUsageIncrement;
    private String period;
}
