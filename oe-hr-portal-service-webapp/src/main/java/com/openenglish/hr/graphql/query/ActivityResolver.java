package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class ActivityResolver {

    private final ActivityService activityService;
    private final Mapper mapper;

    @DgsData(parentType = "Query", field = "getAllActivitiesOverview")
    public ActivitiesOverviewDto getAllActivitiesOverview(String salesforcePurchaserId) {
        return activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getYearActivityStatistics")
    public YearActivityStatisticsDto getYearActivityStatistics(String salesforcePurchaserId, int year, long activity) {

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, year, activity);

        return  mapper.map(yearActivityStatistics, YearActivityStatisticsDto.class);
    }
}
