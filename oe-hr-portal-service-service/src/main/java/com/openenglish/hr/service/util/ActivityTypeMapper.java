package com.openenglish.hr.service.util;

import com.oe.lp2.enums.CourseTypeEnum;
import com.openenglish.hr.common.api.model.ActivityType;

import java.util.Map;
import java.util.Set;

public class ActivityTypeMapper {
    private static Map<ActivityType, Set<CourseTypeEnum>> activityTypeLookupMap =
            Map.of(
                    ActivityType.PRACTICE, Set.of(CourseTypeEnum.PRACTICE, CourseTypeEnum.IDIOMS, CourseTypeEnum.NEWS),
                    ActivityType.LESSON, Set.of(CourseTypeEnum.LESSON),
                    ActivityType.LIVE_CLASS, Set.of(CourseTypeEnum.LIVE_CLASS, CourseTypeEnum.PRIVATE_CLASS),
                    ActivityType.LEVEL, Set.of(CourseTypeEnum.LEVEL_ASSESSMENT),
                    ActivityType.UNIT, Set.of(CourseTypeEnum.UNIT_ASSESSMENT)
            );

    public static Set<CourseTypeEnum> mapToCourseTypes(ActivityType activity) {
        return activityTypeLookupMap.get(activity);
    }
}
