package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@DatabaseSetup(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseAuditRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseAuditRepository personCourseAuditRepository;

    @Test
    public void findPersonCourseAuditForCertainYearAndLiveClasses() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 4;
        final long LIVE_CLASS_COURSE_TYPE = 1l;
        Set<Long> courseTypeIds = Set.of(LIVE_CLASS_COURSE_TYPE);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateCompleted().getYear()));

    }

    @Test
    public void findPersonCourseAuditForCertainYearAndPractices() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 8;
        final long PRACTICE_COURSE_TYPE = 3l;
        Set<Long> courseTypeIds = Set.of(PRACTICE_COURSE_TYPE);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateStarted().getYear()));

    }

    @Test
    public void findPersonCourseAuditEmptyResultForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        Set<Long> courseTypeIds = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }

    @Test
    public void findMaxActivityDateGroupedByPerson(){
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 3;
        List<UsageLevel> usageLevels =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, usageLevels.size());
    }

    @Test
    public void findMaxActivityDateGroupedByPersonNonExistingPurchaseId(){
        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;
        List<UsageLevel> usageLevels =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, usageLevels.size());
    }

    @Test
    public void findMaxActivityDateByPerson(){
        String salesforcePurchaserId = "12347";
        final long PERSON_ID = 1111004;
        LocalDateTime LAST_ACTIVITY_DATE =  LocalDateTime.of(2022,5,1,17,50,52,235000000);

        UsageLevel usageLevel =  personCourseAuditRepository.findMaxActivityDateByPerson(salesforcePurchaserId, PERSON_ID);

        assertEquals(PERSON_ID, usageLevel.getPersonId());
        assertEquals(LAST_ACTIVITY_DATE, usageLevel.getLastActivity());
    }

    @Test
    public void findMaxActivityDateByPersonEmptyResult(){
        String salesforcePurchaserId = "12347";
        final long PERSON_ID = 1111008;

        UsageLevel usageLevel =  personCourseAuditRepository.findMaxActivityDateByPerson(salesforcePurchaserId, PERSON_ID);

        assertNull(usageLevel);
    }

    @Test
    public void findActivityStatisticsByPersonForCertainYear(){

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 3;
        Set<Long> courseTypeIds = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);
        long personId = 1111004;

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatisticsByPerson(salesforcePurchaserId, startDate, endDate, courseTypeIds, personId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateCompleted().getYear()));
    }

    @Test
    public void findActivityStatisticsByPersonEmptyResult(){

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        Set<Long> courseTypeIds = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);
        long personId = 2111009;

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatisticsByPerson(salesforcePurchaserId, startDate, endDate, courseTypeIds, personId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }
}
