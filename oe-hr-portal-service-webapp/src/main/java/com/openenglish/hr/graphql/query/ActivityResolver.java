package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityTypeEnum;
import com.openenglish.hr.common.dto.*;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.PersonActivityTotalDto;
import com.openenglish.hr.common.dto.UsageLevelOverviewDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.Mapper;
import com.openenglish.hr.service.util.ActivityTypeMapper;
import java.util.Optional;
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
    public YearActivityStatisticsDto getYearActivityStatistics(String salesforcePurchaserId, int year, ActivityTypeEnum activity, Long studentId) {

        Set<CourseTypeEnum> courseTypeEnums =  ActivityTypeMapper.mapToCourseTypes(activity);

        return activityService.getActivityStatistics(salesforcePurchaserId, year, courseTypeEnums, studentId);
    }

    @DgsData(parentType = "Query", field = "getTopStudentsByActivityStatistics")
    public List<PersonActivityTotalDto> getTopStudentsByActivityStatistics(String salesforcePurchaserId, int year, int month, List<String> activities, int top) {

        LocalDateTime localDateTime = LocalDateTime.of(year, month, 1, 0, 0, 0);

        Set<CourseTypeEnum> courseTypeEnums =  activities.stream().map(ActivityTypeEnum::valueOf)
                .flatMap(activityTypeEnum ->  ActivityTypeMapper.mapToCourseTypes(activityTypeEnum).stream())
                .collect(Collectors.toSet());

        LinkedHashMap<PersonDto, Double> personsTop = activityService.getTopStudentsByActivityStatistics(salesforcePurchaserId, localDateTime, courseTypeEnums, top);

        return personsTop.entrySet().stream()
                .map(entry -> PersonActivityTotalDto
                        .builder()
                        .person(entry.getKey())
                        .totalActivities(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @DgsData(parentType = "Query", field = "getUsageLevelOverview")
    public UsageLevelOverviewDto getUsageLevelOverview(String salesforcePurchaserId) {
        return activityService.getUsageLevelOverview(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getLeastActiveStudents")
    public List<PersonUsageLevelDto> getLeastActiveStudents(String salesforcePurchaserId) {
        return activityService.getLeastActiveStudents(salesforcePurchaserId);
    }

    @DgsData(parentType = "Query", field = "getUsageLevelOverviewPerPerson")
    public Optional<PersonUsageLevelDto> getUsageLevelOverviewPerPerson(String salesforcePurchaserId, String contactId) {
        return activityService.getUsageLevelOverviewPerPerson(salesforcePurchaserId, contactId);
    }

}
