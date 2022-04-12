package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YearActivityStatisticsDto {
    private List<MonthActivityStatisticsDto> monthsActivityStatistics;
    private double total;
}
