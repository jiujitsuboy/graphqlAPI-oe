package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsageLevelOverviewDto {
    private Long high;
    private Long mediumHigh;
    private Long mediumLow;
    private Long low;
}

