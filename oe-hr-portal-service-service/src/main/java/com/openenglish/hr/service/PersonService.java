package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getPersons(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
    }

    public List<PersonsPerLevel> getAllPersonsByLevel(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.getAllPersonsPerLevel(salesforcePurchaserId);
    }

    public List<ActivitiesOverview> getCurrentMonthActivitiesOverview(String salesforcePurchaserId) {

        List<ActivitiesOverview> activityOverviewPlusLastMonthPercentages = new ArrayList<>();

        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        LocalDate currentMonthDate = LocalDate.now();
        LocalDate previousMonthDate = LocalDate.of(currentMonthDate.getYear(), currentMonthDate.getMonthValue(), 1).minusMonths(12);

        List<ActivitiesOverview> activitiesOverviews = personRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);

        if (activitiesOverviews.size() == 2) {
            ActivitiesOverview porcentagesComparitionBetweenCurrentAndPreviousMonth = new
                    ActivitiesOverview() {
                        @Override
                        public long getGroupClasses() {

                            long groupClassesPrevious =  activitiesOverviews.get(0).getGroupClasses();
                            long groupClassesCurrent = activitiesOverviews.get(1).getGroupClasses();

                            return groupClassesCurrent > 0 ? (long)(((((double)groupClassesCurrent)/groupClassesPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getPrivateClasses() {

                            long privateClassesPrevious =  activitiesOverviews.get(0).getPrivateClasses();
                            long privateClassesCurrent = activitiesOverviews.get(1).getPrivateClasses();

                            return privateClassesCurrent > 0 ? (long)(((((double)privateClassesCurrent)/privateClassesPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getCompletedLessons() {

                            long completedLessonsPrevious =  activitiesOverviews.get(0).getCompletedLessons();
                            long completedLessonsCurrent = activitiesOverviews.get(1).getCompletedLessons();

                            return completedLessonsCurrent > 0 ? (long)(((((double)completedLessonsCurrent)/completedLessonsPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getCompletedUnits() {
                            long completedUnitsPrevious =  activitiesOverviews.get(0).getCompletedUnits();
                            long completedUnitsCurrent = activitiesOverviews.get(1).getCompletedUnits();

                            return completedUnitsCurrent > 0 ? (long)(((((double)completedUnitsCurrent)/completedUnitsPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getPracticeHours() {

                            long practiceHoursPrevious =  activitiesOverviews.get(0).getPracticeHours();
                            long practiceHoursCurrent = activitiesOverviews.get(1).getPracticeHours();

                            return practiceHoursCurrent > 0 ? (long)(((((double)practiceHoursCurrent)/practiceHoursPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getLevelPassed() {

                            long levelPassedPrevious =  activitiesOverviews.get(0).getLevelPassed();
                            long levelPassedCurrent = activitiesOverviews.get(1).getLevelPassed();

                            return levelPassedCurrent > 0 ? (long)(((((double)levelPassedCurrent)/levelPassedPrevious)-1)*100) : 0;
                        }

                        @Override
                        public long getTotalHoursUsage() {

                            long totalHoursUsagePrevious =  activitiesOverviews.get(0).getTotalHoursUsage();
                            long totalHoursUsageCurrent = activitiesOverviews.get(1).getTotalHoursUsage();

                            return totalHoursUsageCurrent > 0 ? (long)(((((double)totalHoursUsageCurrent)/totalHoursUsagePrevious)-1)*100) : 0;
                        }

                        @Override
                        public String getPeriod() {
                            return activitiesOverviews.get(0).getPeriod();
                        }
                    };

            activityOverviewPlusLastMonthPercentages.add(porcentagesComparitionBetweenCurrentAndPreviousMonth);
            activityOverviewPlusLastMonthPercentages.add(activitiesOverviews.get(1));
        }

        return activityOverviewPlusLastMonthPercentages;
    }
}
