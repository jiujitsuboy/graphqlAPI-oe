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
                .filter(temp -> ((PersonsPerLevel)temp).getLevelName().equals(levelName))
                .findFirst()
                .ifPresentOrElse(level->{
                    PersonsPerLevel personLevel =  (PersonsPerLevel)level;
                    assertThat(personLevel.getLevelName(),is(levelName));
                    assertThat(personLevel.getTotalNumber(),is(totalNumber));
                },Assert::fail);
    }

    @Test
    public void getActivitiesOverview() {

        String salesforcePurchaserId = "12347";
        long groupClassesNum = 1;
        long privateClassesNum = 0;
        long levelPassedNum = 0;
        long learnedLessonsNum = 1;
        long completedUnitsNum = 0;
        long practiceHoursNum = 20;
        long totalHoursUsageNum = 0;


        String previousMonthDate = "2022-02";
        String currentMonthDate = "2022-03";

        List<ActivitiesOverview> activitiesOverviews = personRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);

        activitiesOverviews.stream()
                .filter(activitiesOverview -> activitiesOverview.getPeriod().equals(currentMonthDate))
                .findFirst()
                .ifPresentOrElse(activity -> {
                    assertThat(activity.getGroupClasses(), is(groupClassesNum));
                    assertThat(activity.getPrivateClasses(), is(privateClassesNum));
                    assertThat(activity.getLevelPassed(), is(levelPassedNum));
                    assertThat(activity.getLearnedLessons(), is(learnedLessonsNum));
                    assertThat(activity.getCompletedUnits(), is(completedUnitsNum));
                    assertThat(activity.getPracticeHours(), is(practiceHoursNum));
                    assertThat(activity.getTotalHoursUsage(), is(totalHoursUsageNum));
                }, Assert::fail);
    }
}
