package com.openenglish.hr.common.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OldestActivityDto {
    private LocalDateTime oldestActivityDate;
}
