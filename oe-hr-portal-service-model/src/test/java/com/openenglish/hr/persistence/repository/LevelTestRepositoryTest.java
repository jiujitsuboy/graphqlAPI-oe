package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:levelTestData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:levelTestData.xml", type = DatabaseOperation.DELETE)
public class LevelTestRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private LevelTestRepository levelTestRepository;
    private final int YEAR = 2022;
    private final int FEBRUARY = 2;
    private final int MARCH = 3;

    @Test
    public void getDistinctTestTypesInFebruary() {
        final int NUMBER_RECORDS_EXPECTED = 3;
        String salesforcePurchaserId = "12347";
        LocalDateTime startDate = LocalDateTime.of(YEAR, FEBRUARY, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<LevelsPassedByPerson> levelsPassedByPersons = levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(salesforcePurchaserId, startDate, endDate);
        assertEquals(NUMBER_RECORDS_EXPECTED, levelsPassedByPersons.size());

    }

    @Test
    public void getDistinctTestTypesInMarchEmpty() {
        final int NUMBER_RECORDS_EXPECTED = 0;
        String salesforcePurchaserId = "12347";
        LocalDateTime startDate = LocalDateTime.of(YEAR, MARCH, 01, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusSeconds(1);

        List<LevelsPassedByPerson> levelsPassedByPersons = levelTestRepository.getLevelTestsByPurchaserIdUpdateDateBetween(salesforcePurchaserId, startDate, endDate);
        assertEquals(NUMBER_RECORDS_EXPECTED, levelsPassedByPersons.size());

    }
}