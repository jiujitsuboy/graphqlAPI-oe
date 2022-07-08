package com.openenglish.hr.common.dto;

import com.openenglish.hr.common.api.model.ActivityTypeEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OldestActivityDto {
    private ActivityTypeEnum activityType;
    private LocalDateTime oldestActivityDate;
    private String oldestActivityStr;
}
