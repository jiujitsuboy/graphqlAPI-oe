package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
public interface PersonCourseAuditRepository extends JpaRepository<PersonCourseAudit, Long> {

    List<PersonCourseAudit> findPersonCourseAuditByPersonDetailsSalesforcePurchaserIdAndDateCompletedBetweenAndCourseCourseTypeIdIn(String salesforcePurchaserId, LocalDateTime startDate, LocalDateTime endDate, List<Long> courseTypes);
}
