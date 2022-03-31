package com.openenglish.hr.persistence.entity.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitiesOverview {
    private long courseType;
    private double timeInSeconds;
}
