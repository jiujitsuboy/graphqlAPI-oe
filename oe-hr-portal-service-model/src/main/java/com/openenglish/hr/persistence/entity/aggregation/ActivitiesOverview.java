package com.openenglish.hr.persistence.entity.aggregation;

public interface ActivitiesOverview {

    long getGroupClasses();
    long getPrivateClasses();
    long getCompletedLessons();
    long getCompletedUnits();
    long getPracticeHours();
    long getLevelPassed();
    long getTotalHoursUsage();
    String getPeriod();
}
