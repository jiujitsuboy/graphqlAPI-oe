package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseAudit;
import com.openenglish.hr.persistence.entity.aggregation.OldestActivity;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import java.util.Collections;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@DatabaseSetup(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseAuditData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseAuditRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseAuditRepository personCourseAuditRepository;

    @Test
    public void findPersonCourseAuditForCertainYearAndLiveClasses() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 4;
        final long LIVE_CLASS_COURSE_TYPE = 1L;
        Set<Long> courseTypeIds = Set.of(LIVE_CLASS_COURSE_TYPE);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds, Collections.emptySet());

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());

        personCourseAudits.stream().forEach(personCourseAudit -> assertEquals(startDate.getYear(), personCourseAudit.getDateCompleted().getYear()));

    }

    @Test
    public void findPersonCourseAuditForCertainYearAndPractices() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 8;
        final long PRACTICE_COURSE_TYPE = 3L;
        Set<Long> courseTypeIds = Set.of(PRACTICE_COURSE_TYPE);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds, Collections.emptySet());

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

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds, Collections.emptySet());

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }

    @Test
    public void findMaxActivityDateGroupedByPerson(){
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 3;
        List<UsageLevel> usageLevels =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId, Collections.emptySet());

        assertEquals(NUMBER_RECORDS_EXPECTED, usageLevels.size());
    }

    @Test
    public void findMaxActivityDateGroupedByPersonNonExistingPurchaseId(){
        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;
        List<UsageLevel> usageLevels =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId, Collections.emptySet());

        assertEquals(NUMBER_RECORDS_EXPECTED, usageLevels.size());
    }

    @Test
    public void findMaxActivityDateByPerson(){
        String salesforcePurchaserId = "12347";
        String contact_id = "sf_synegen801";
        final int ONE_RECORD = 1;
        LocalDateTime LAST_ACTIVITY_DATE =  LocalDateTime.of(2022,5,1,17,50,52,235000000);

        List<UsageLevel> usageLevel =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId, Set.of(contact_id));

        assertTrue(usageLevel.size() == ONE_RECORD);
        assertEquals(contact_id, usageLevel.get(0).getContactId());
//        assertEquals(LAST_ACTIVITY_DATE, usageLevel.getLastActivity());
    }

    @Test
    public void findMaxActivityDateByPersonEmptyResult(){
        String salesforcePurchaserId = "12347";
        String contact_id = "sf_synegen802";
        final int ZERO_RECORD = 0;

        List<UsageLevel> usageLevel =  personCourseAuditRepository.findMaxActivityDateGroupedByPerson(salesforcePurchaserId, Set.of(contact_id));

        assertTrue(usageLevel.size() == ZERO_RECORD);
    }

    @Test
    public void findActivityStatisticsByPersonForCertainYear(){

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 3;
        Set<Long> courseTypeIds = Set.of(1L);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);
        String contactId = "sf_synegen801";

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds, Set.of(contactId));

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
        String contactId = "sf_synegen802";

        List<PersonCourseAudit> personCourseAudits = personCourseAuditRepository.findActivityStatistics(salesforcePurchaserId, startDate, endDate, courseTypeIds, Set.of(contactId));

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseAudits.size());
    }

    @Test
    public void findMinActivityDate(){
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 8;

        List<OldestActivity> oldestActivities = personCourseAuditRepository.findMinActivityDate(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, oldestActivities.size());
    }

    @Test
    public void findMinActivityDateEmptyResult(){
        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 8;

        List<OldestActivity> oldestActivities = personCourseAuditRepository.findMinActivityDate(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, oldestActivities.size());

        oldestActivities.stream().forEach(oldestActivity -> {
            assertNull(oldestActivity.getOldestActivityDate());
        });
    }
}
