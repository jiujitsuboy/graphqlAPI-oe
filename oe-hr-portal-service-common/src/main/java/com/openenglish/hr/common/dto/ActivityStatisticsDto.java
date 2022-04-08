package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityStatisticsDto {
    private int month;
    private double value;
}
