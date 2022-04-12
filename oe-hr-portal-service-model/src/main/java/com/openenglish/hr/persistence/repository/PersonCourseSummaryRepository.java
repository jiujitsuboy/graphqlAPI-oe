package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonCourseSummaryRepository extends JpaRepository<PersonCourseSummary, Long> {
    List<PersonCourseSummary> findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(String salesforcePurchaserId);
    List<PersonCourseSummary> findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(String salesforcePurchaserId, LocalDateTime startDate, LocalDateTime endDate, List<Long> courseTypesNames);
}
