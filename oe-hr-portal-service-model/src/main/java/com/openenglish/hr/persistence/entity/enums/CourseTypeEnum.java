package com.openenglish.hr.persistence.entity.enums;

public enum CourseTypeEnum {
    LIVE_CLASS(1L, "Live Class", 2),
    PRIVATE_CLASS(2L, "Private Class", 3),
    PRACTICE(3L, "Practice", 1),
    LESSON(4L, "Lesson", 1),
    UNIT_ASSESSMENT(5L, "Unit Assessment", 1),
    LEVEL_ASSESSMENT(6L, "Level Assessment", 3),
    NEWS(8L, "News", 1),
    LEVEL_ZERO(9L, "Level Zero", 1),
    IDIOMS(10L, "Idioms", 1);

    Long value;
    String name;
    Integer groupType;

    private CourseTypeEnum(Long i, String name, Integer groupType) {
        this.value = i;
        this.name = name;
        this.groupType = groupType;
    }

    public Long getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Integer getGroupType() {
        return this.groupType;
    }

    public static CourseTypeEnum getStatusByValue(Long value) {
        CourseTypeEnum[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            CourseTypeEnum s = arr$[i$];
            if (s.getValue().equals(value)) {
                return s;
            }
        }

        return null;
    }
}
