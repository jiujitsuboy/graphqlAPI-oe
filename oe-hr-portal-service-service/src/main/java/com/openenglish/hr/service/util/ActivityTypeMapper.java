package com.openenglish.hr.service.util;

import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityTypeEnum;

import java.util.Map;
import java.util.Set;

public class ActivityTypeMapper {
    private static Map<ActivityTypeEnum, Set<CourseTypeEnum>> activityTypeLookupMap =
            Map.of(
                    ActivityTypeEnum.PRACTICE, Set.of(CourseTypeEnum.PRACTICE, CourseTypeEnum.IDIOMS, CourseTypeEnum.NEWS),
                    ActivityTypeEnum.LESSON, Set.of(CourseTypeEnum.LESSON),
                    ActivityTypeEnum.LIVE_CLASS, Set.of(CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.PRIVATE_CLASS),
                    ActivityTypeEnum.LEVEL, Set.of(CourseTypeEnum.LEVEL_ASSESSMENT),
                    ActivityTypeEnum.UNIT, Set.of(CourseTypeEnum.UNIT_ASSESSMENT)
            );

    public static Set<CourseTypeEnum> mapToCourseTypes(ActivityTypeEnum activity) {
        return activityTypeLookupMap.get(activity);
    }
}