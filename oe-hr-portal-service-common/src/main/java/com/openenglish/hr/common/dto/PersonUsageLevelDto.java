package com.openenglish.hr.common.dto;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonUsageLevelDto {

    private String name;
    private UsageLevelEnum usageLevel;
}
