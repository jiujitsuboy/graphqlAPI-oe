package com.openenglish.hr.service.util;

import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityTypeMapper {
    private static Map<ActivityTypeEnum, Set<CourseTypeEnum>> activityTypeLookupMap =
            Map.of(
                    ActivityTypeEnum.PRACTICE, Set.of(CourseTypeEnum.PRACTICE, CourseTypeEnum.IDIOMS, CourseTypeEnum.NEWS),
                    ActivityTypeEnum.LESSON, Set.of(CourseTypeEnum.LESSON),
                    ActivityTypeEnum.LIVE_CLASS, Set.of(CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.PRIVATE_CLASS),
                    ActivityTypeEnum.PRIVATE_CLASS, Set.of(CourseTypeEnum.PRIVATE_CLASS),
                    ActivityTypeEnum.LEVEL, Set.of(CourseTypeEnum.LEVEL_ASSESSMENT),
                    ActivityTypeEnum.UNIT, Set.of(CourseTypeEnum.UNIT_ASSESSMENT),
                    ActivityTypeEnum.ACTIVE_HOURS, Set.of(CourseTypeEnum.PRACTICE, CourseTypeEnum.IDIOMS, CourseTypeEnum.NEWS,CourseTypeEnum.LESSON, CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.PRIVATE_CLASS)
            );

    public static Set<CourseTypeEnum> mapToCourseTypes(ActivityTypeEnum activity) {
        return activityTypeLookupMap.get(activity);
    }

    public static Set<CourseTypeEnum> convertActivityTypeToCourseType(List<String> activities) {
      return activities.stream().map(ActivityTypeEnum::valueOf)
          .flatMap(activityTypeEnum -> ActivityTypeMapper.mapToCourseTypes(activityTypeEnum).stream())
          .collect(Collectors.toSet());
    }
}
