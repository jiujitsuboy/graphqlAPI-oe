package com.openenglish.hr.persistence.entity.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ActivityStatistics {
    private int month;
    private double value;
}
