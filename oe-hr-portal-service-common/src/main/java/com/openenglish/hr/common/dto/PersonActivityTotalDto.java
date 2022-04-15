package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonActivityTotalDto {
    private long personId;
    private double totalActivities;
}
