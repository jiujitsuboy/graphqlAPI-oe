package com.openenglish.hr.persistence.repository.impl;

import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.repository.PersonCourseSummaryRepository;
import com.openenglish.hr.persistence.repository.CustomActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomActivityRepositoryImpl implements CustomActivityRepository {
    private final PersonCourseSummaryRepository personCourseSummaryRepository;

    @Override
    public List<ActivitiesOverview> getActivitiesOverview(String salesforcePurchaserId) {

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        List<ActivitiesOverview> actitiviesOverviews = personCourseSummaries.stream().map(personCourseSummary -> ActivitiesOverview
                        .builder()
                        .courseType(personCourseSummary.getCourse().getCourseType().getId())
                        .timeInSeconds((double) personCourseSummary.getTimeontask()).build())
                .collect(Collectors.toList());

        return actitiviesOverviews;
    }
}
