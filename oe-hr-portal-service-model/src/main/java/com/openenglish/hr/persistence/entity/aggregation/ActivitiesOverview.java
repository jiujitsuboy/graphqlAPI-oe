package com.openenglish.hr.persistence.entity.aggregation;

public interface ActivitiesOverview {

    long getGroupClasses();
    long getPrivateClasses();
    long getLearnedLessons();
    long getCompletedUnits();
    long getPracticeHours();
    long getLevelPassed();
    long getTotalHoursUsage();
    String getPeriod();
}
