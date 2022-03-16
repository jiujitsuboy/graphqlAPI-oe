package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DatabaseSetup(value = "classpath:personData.xml", type = DatabaseOperation.INSERT)
@DatabaseTearDown(value = "classpath:personData.xml", type = DatabaseOperation.DELETE)
public class PersonRepositoryTest extends AbstractPersistenceTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void findPersonByDetailsSalesforcePurchaserIdIn(){
        String salesforcePurchaserId = "12347";
        String firstNameExpected = "joseph";
        String lastNameExpected = "peterson";
        String contactIdExpected = "sf_synegen801";
        String emailExpected = "josephp430@unknowdomain.com";
        int expectedPersons = 3;

        List<Person> persons =  personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);

        assertNotNull(persons);
        assertEquals(expectedPersons,persons.size());

        persons.stream().filter(person -> person.getContactId().equals(contactIdExpected)).findFirst().ifPresentOrElse(person -> {
            assertThat(person.getFirstName(),is(firstNameExpected));
            assertThat(person.getLastName(),is(lastNameExpected));
            assertThat(person.getContactId(),is(contactIdExpected));
            assertThat(person.getEmail(),is(emailExpected));
        }, Assert::fail);
    }

    @Test
    public void getAllPersonsPerLevel(){

        String levelName = "Level 100";
        String salesforcePurchaserId = "12347";
        long totalNumber = 3;

        List<PersonsPerLevel> personsPerLevel =  personRepository.getAllPersonsPerLevel(salesforcePurchaserId);

        assertNotNull(personsPerLevel);

        personsPerLevel.stream()
                .filter(temp -> temp.getLevelName().equals(levelName))
                .findFirst()
                .ifPresentOrElse(level->{
                    assertThat(level.getLevelName(),is(levelName));
                    assertThat(level.getTotalNumber(),is(totalNumber));
                },Assert::fail);
    }

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        long groupClassesNum = 1;
        long privateClassesNum = 0;
        long levelPassedNum = 0;
        long completedLessonsNum = 1;
        long completedUnitsNum = 0;
        long practiceHoursNum = 0;
        long totalHoursUsageNum = 50;


        LocalDate previousMonthDate = LocalDate.parse("2022-02-01");
        LocalDate currentMonthDate = LocalDate.parse("2022-03-31");

        List<ActivitiesOverview> activitiesOverviews = personRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);

        activitiesOverviews.stream()
                .filter(activitiesOverview -> activitiesOverview.getPeriod().equals(String.format("%d-%02d",currentMonthDate.getYear(),currentMonthDate.getMonthValue())))
                .findFirst()
                .ifPresentOrElse(activity -> {
                    assertThat(activity.getGroupClasses(), is(groupClassesNum));
                    assertThat(activity.getPrivateClasses(), is(privateClassesNum));
                    assertThat(activity.getLevelPassed(), is(levelPassedNum));
                    assertThat(activity.getCompletedLessons(), is(completedLessonsNum));
                    assertThat(activity.getCompletedUnits(), is(completedUnitsNum));
                    assertThat(activity.getPracticeHours(), is(practiceHoursNum));
                    assertThat(activity.getTotalHoursUsage(), is(totalHoursUsageNum));
                }, Assert::fail);
    }
}
