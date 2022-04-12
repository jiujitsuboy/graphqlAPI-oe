package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonthActivityStatisticsDto {
    private int month;
    private double value;
}
