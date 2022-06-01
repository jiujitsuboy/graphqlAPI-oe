package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HRManagerDto {
    private String id;
    private String name;
    private String email;
}
