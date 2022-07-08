package com.openenglish.hr.service.util;

import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityTypeEnum;

import java.util.HashSet;
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

    public static Set<ActivityTypeEnum> mapToActivityType(CourseTypeEnum courseType) {

      Set<ActivityTypeEnum> activityTypeEnum = new HashSet<>();

      switch (courseType){
        case PRACTICE:
        case IDIOMS:
        case NEWS:
          activityTypeEnum.add(ActivityTypeEnum.PRACTICE);
          break;
        case LESSON:
          activityTypeEnum.add(ActivityTypeEnum.LESSON);
          break;
        case LIVE_CLASS:
          activityTypeEnum.add(ActivityTypeEnum.LIVE_CLASS);
          break;
        case PRIVATE_CLASS:
          activityTypeEnum.add(ActivityTypeEnum.PRIVATE_CLASS);
          activityTypeEnum.add(ActivityTypeEnum.LIVE_CLASS);
          break;
        case LEVEL_ASSESSMENT:
          activityTypeEnum.add(ActivityTypeEnum.LEVEL);
          break;
        case UNIT_ASSESSMENT:
          activityTypeEnum.add(ActivityTypeEnum.UNIT);
          break;
      }

      return activityTypeEnum;
    }


    public static Set<CourseTypeEnum> convertActivityTypeToCourseType(List<String> activities) {
      return activities.stream().map(ActivityTypeEnum::valueOf)
          .flatMap(activityTypeEnum -> ActivityTypeMapper.mapToCourseTypes(activityTypeEnum).stream())
          .collect(Collectors.toSet());
    }
}
