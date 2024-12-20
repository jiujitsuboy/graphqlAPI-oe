package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.PersonCourseSummary;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:personCourseSummaryData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personCourseSummaryData.xml", type = DatabaseOperation.DELETE)
public class PersonCourseSummaryRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonCourseSummaryRepository personCourseSummaryRepository;

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 16;
        final long COURSE_TYPE_EXPECTED = 4L;
        final double TOTAL_SECONDS_USAGE_NUM_EXPECTED = 340;

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());

        personCourseSummaries.stream()
                .filter(personCourseSummary -> personCourseSummary.getCourse().getCourseType().getId() == COURSE_TYPE_EXPECTED)
                .forEach(personCourseSummary -> {
                    assertThat(personCourseSummary.getCourse().getCourseType().getId(), is(COURSE_TYPE_EXPECTED));
                    assertEquals(TOTAL_SECONDS_USAGE_NUM_EXPECTED, personCourseSummary.getTimeontask(), 0);
                });
    }

    @Test
    public void getActivitiesOverviewEmptyResult() {

        String salesforcePurchaserId = "12348";
        final int NUMBER_RECORDS_EXPECTED = 0;

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());
    }

    @Test
    public void findPersonCourseSummaryForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 16;
        List<Long> courseTypeIds = List.of(1L, 2L, 3L, 4L, 5L, 8L, 10L);
        LocalDateTime startDate = LocalDateTime.of(2022, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());

        personCourseSummaries.stream().forEach(personCourseSummary -> assertEquals(startDate.getYear(), personCourseSummary.getCreatedDate().getYear()));

    }

    @Test
    public void findPersonCourseSummaryEmptyResultForCertainYear() {
        String salesforcePurchaserId = "12347";
        final int NUMBER_RECORDS_EXPECTED = 0;
        List<Long> courseTypeIds = List.of(1L, 2L, 3L, 4L, 5L, 8L, 10L);
        LocalDateTime startDate = LocalDateTime.of(2021, 01, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<PersonCourseSummary> personCourseSummaries = personCourseSummaryRepository.findPersonCourseSummaryByPersonDetailsSalesforcePurchaserIdAndCreatedDateBetweenAndCourseCourseTypeIdIn(salesforcePurchaserId, startDate, endDate, courseTypeIds);

        assertEquals(NUMBER_RECORDS_EXPECTED, personCourseSummaries.size());
    }
}
