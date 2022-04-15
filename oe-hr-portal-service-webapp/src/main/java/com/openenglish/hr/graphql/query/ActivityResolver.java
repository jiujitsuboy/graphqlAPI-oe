package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityType;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.PersonActivityTotalDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.Mapper;
import com.openenglish.hr.service.util.ActivityTypeMapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
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
    public YearActivityStatisticsDto getYearActivityStatistics(String salesforcePurchaserId, int year, ActivityType activity) {

        Set<CourseTypeEnum> courseTypeEnums =  ActivityTypeMapper.mapToCourseTypes(activity);

        YearActivityStatistics yearActivityStatistics = activityService.getActivityStatistics(salesforcePurchaserId, year, courseTypeEnums);

        return  mapper.map(yearActivityStatistics, YearActivityStatisticsDto.class);
    }

    @DgsData(parentType = "Query", field = "getTopStudentsByActivityStatistics")
    public List<PersonActivityTotalDto> getTopStudentsByActivityStatistics(String salesforcePurchaserId, int year, int month, ActivityType activity, int top) {

        LocalDateTime localDateTime = LocalDateTime.of(year, month, 1, 0, 0, 0);

        Set<CourseTypeEnum> courseTypeEnums =  ActivityTypeMapper.mapToCourseTypes(activity);

        LinkedHashMap<Long, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, localDateTime, courseTypeEnums, top);

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
