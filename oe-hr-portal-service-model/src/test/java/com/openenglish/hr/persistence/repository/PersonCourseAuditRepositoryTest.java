package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@DatabaseSetup(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseAuditRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseAuditRepository personCourseAuditRepository;

    @Test
    public void findPersonCourseAuditForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 4;
        Set<Long> coursesTypeId = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2022, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, coursesTypeId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateCompleted().getYear()));

    }

    @Test
    public void findPersonCourseAuditEmptyResultForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        Set<Long> coursesTypeId = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2021, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, coursesTypeId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }
}
