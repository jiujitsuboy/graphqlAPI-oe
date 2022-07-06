package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OldestActivityDto {
    private String activityName;
    private String oldestActivityDate;
}
