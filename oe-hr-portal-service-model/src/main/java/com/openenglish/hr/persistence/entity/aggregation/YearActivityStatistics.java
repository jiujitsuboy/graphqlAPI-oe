package com.openenglish.hr.persistence.entity.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class YearActivityStatistics {
    private List<MonthActivityStatistics> monthsActivityStatistics ;
    private double total;
}
