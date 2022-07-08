package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonsPerLevelDto {
    private String levelUuid;
    private long totalNumber;
}
