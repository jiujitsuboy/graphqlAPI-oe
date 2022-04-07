package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DatabaseSetup(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseAuditRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseAuditRepository personCourseAuditRepository;

    @Test
    public void findPersonCourseAuditForCertainYear() {
        String salesforcePurchaserId = "12345";
        final int NUMBER_RECORDS_EXPECTED = 13;
        List<Long> courseTypes = List.of(1L, 2L, 3L, 4L, 5L, 8L, 10L);
        LocalDateTime startDate = LocalDateTime.of(2022, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findPersonCourseAuditByPersonDetailsSalesforcePurchaserIdAndDateCompletedBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypes);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateCompleted().getYear()));

    }

    @Test
    public void findPersonCourseAuditEmptyResultForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        List<Long> courseTypes = List.of(1L, 2L, 3L, 4L, 5L, 8L, 10L);
        LocalDateTime startDate = LocalDateTime.of(2021, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findPersonCourseAuditByPersonDetailsSalesforcePurchaserIdAndDateCompletedBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypes);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }
}
