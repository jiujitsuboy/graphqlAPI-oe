package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonCourseSummaryRepository extends JpaRepository<PersonCourseSummary, Long> {
    List<PersonCourseSummary> findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(String salesforcePurchaserId);
}
