package com.openenglish.hr.persistence.entity.aggregation.impl;

import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActitiviesOverviewImpl implements ActivitiesOverview {
    private Long courseType;
    private Long courseSubType;
    private Double timeInMinutes;
}
