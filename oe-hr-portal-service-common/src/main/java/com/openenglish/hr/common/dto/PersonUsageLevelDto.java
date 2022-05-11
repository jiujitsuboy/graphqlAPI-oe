package com.openenglish.hr.common.dto;

import com.openenglish.hr.common.api.model.UsageLevelEnum;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonUsageLevelDto {

    private PersonDto person;
    private LocalDate start;
    private LocalDate expiration;
    private UsageLevelEnum usageLevel;
    private long remainingDays;
    private long inactiveDays;
}
