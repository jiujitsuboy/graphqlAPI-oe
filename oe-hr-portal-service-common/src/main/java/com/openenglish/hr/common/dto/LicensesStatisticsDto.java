package com.openenglish.hr.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicensesStatisticsDto {
    private int availableLicenses;
    private int assignedLicenses;
    private int activeLicenses;
}
