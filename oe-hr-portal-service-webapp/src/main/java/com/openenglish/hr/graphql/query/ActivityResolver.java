package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.PersonActivityTotalDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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

    @DgsData(parentType = "Query", field = "getYearActivityStatistics")
    public YearActivityStatisticsDto getYearActivityStatistics(String salesforcePurchaserId, int year, String activity) {

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, year, activity);

        return  mapper.map(yearActivityStatistics, YearActivityStatisticsDto.class);
    }

    @DgsData(parentType = "Query", field = "getTopStudentsByActivityStatistics")
    public List<PersonActivityTotalDto> getTopStudentsByActivityStatistics(String salesforcePurchaserId, int year, int month, String activity, int top) {

        LocalDateTime localDateTime = LocalDateTime.of(year, month, 1, 0, 0, 0);

        LinkedHashMap<Long, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, localDateTime, activity, top);

        return personsTop.entrySet().stream()
                .map(entry -> PersonActivityTotalDto
                        .builder()
                        .personId(entry.getKey())
                        .totalActivities(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
