package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.ActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.aggregation.ActivityStatistics;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@DgsComponent
@RequiredArgsConstructor
public class ActivityResolver {

    private final ActivityService activityService;
    private final Mapper mapper;

    @DgsData(parentType = "Query", field = "getAllActivitiesOverview")
    public ActivitiesOverviewDto getAllActivitiesOverview(String salesforcePurchaserId) {
        return activityService.getCurrentMonthActivitiesOverview(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getActivitiesStatistics")
    public List<ActivityStatisticsDto> getActivitiesStatistics(String salesforcePurchaserId, int year, List<Integer> activities) {

        //Cast from int to long so hibernate don't complain, because graphql resolver don't respect the contract of List<Long> and replace it by List<Integer>
        List<Long> activitiesCasted = activities.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        List<ActivityStatistics> activityStatistics = activityService.getActivitiesStatistics(salesforcePurchaserId, year, activitiesCasted);

        return activityStatistics.stream()
                .map(activityStatistic -> mapper.map(activityStatistic, ActivityStatisticsDto.class))
                .collect(Collectors.toList());
    }
}
