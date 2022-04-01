package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
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
}
