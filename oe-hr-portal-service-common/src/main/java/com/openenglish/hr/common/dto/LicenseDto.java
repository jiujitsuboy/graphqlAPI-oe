package com.openenglish.hr.common.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LicenseDto {
    private PersonDto person;
    private String status;
    private LocalDate startDate;
    private String organization;
    private String name;
    private String licenseId;
    private long inactiveDays;
    private LocalDate endDate;
    private String privateClasses;
}

