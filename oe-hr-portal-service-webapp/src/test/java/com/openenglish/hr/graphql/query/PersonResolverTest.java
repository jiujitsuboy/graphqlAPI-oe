package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;

import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.ActivitiesOverviewWithIncrementsDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Level;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.PersonDetail;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.service.PersonService;
import com.openenglish.hr.service.mapper.Mapper;
import com.openenglish.hr.service.mapper.MappingConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@Import(MappingConfig.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, PersonResolver.class})
public class PersonResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Mapper mapper;

    @MockBean
    private PersonService personService;

    @Test
    public void getPersons() {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
        List<Person> persons = List.of(Person.builder()
                        .id(1L)
                        .firstName("joseph")
                        .lastName("murray")
                        .email("fake1@openenglish.com")
                        .contactId("2")
                        .details(PersonDetail.builder()
                                .id(12L)
                                .salesforcePurchaserId("12345")
                                .build())
                        .workingLevel(Level.builder()
                                .id(1L)
                                .active(true)
                                .description("description1")
                                .highScoreBoundary(new BigDecimal(100))
                                .lowScoreBoundary(new BigDecimal(0))
                                .levelNum("1")
                                .name("level 1")
                                .numImmersionRequired(123)
                                .numLiveRequired(456)
                                .sequence(111)
                                .build())
                        .build(),
                Person.builder()
                        .id(2L)
                        .firstName("mary")
                        .lastName("jonshon")
                        .email("fake2@openenglish.com")
                        .contactId("3")
                        .details(PersonDetail.builder()
                                .id(22L)
                                .salesforcePurchaserId("12345")
                                .build())
                        .workingLevel(Level.builder()
                                .id(2L)
                                .active(true)
                                .description("description2")
                                .highScoreBoundary(new BigDecimal(100))
                                .lowScoreBoundary(new BigDecimal(0))
                                .levelNum("2")
                                .name("level 2")
                                .numImmersionRequired(321)
                                .numLiveRequired(654)
                                .sequence(222)
                                .build())
                        .build());

        Mockito.when(personService.getPersons(anyString())).thenReturn(persons);

        String query = "{ " +
                "  getPersons(salesforcePurchaserId:\"12345\"){ " +
                "    email" +
                "    }" +
                "}";
        String projection = "data.getPersons[*].email";

        List<String> personsEmail = dgsQueryExecutor.executeAndExtractJsonPath(query, projection);

        assertNotNull(personsEmail);
        persons.forEach(person -> assertTrue(personsEmail.contains(person.getEmail())));

    }

    @Test
    public void getAllPersonsByLevel() {

        List<PersonsPerLevel> personsPerLevelExpected = List.of(new PersonsPerLevel() {
            @Override
            public String getLevelName() {
                return "Level 1";
            }

            @Override
            public long getTotalNumber() {
                return 42;
            }
        }, new PersonsPerLevel() {
            @Override
            public String getLevelName() {
                return "Level 2";
            }

            @Override
            public long getTotalNumber() {
                return 56;
            }
        });

        Mockito.when(personService.getAllPersonsByLevel(anyString())).thenReturn(personsPerLevelExpected);

        String query = "{ " +
                "  getAllPersonsByLevel{ " +
                "    levelName " +
                "    totalNumber " +
                "  }" +
                "}";
        String projection = "data.getAllPersonsByLevel[*]";


        List<PersonsPerLevelDto> personsPerLevel = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertNotNull(personsPerLevel);

        for (int index = 0; index < personsPerLevel.size(); index++) {

            PersonsPerLevel expected = personsPerLevelExpected.get(index);
            PersonsPerLevelDto received = personsPerLevel.get(index);

            assertEquals(expected.getLevelName(), received.getLevelName());
            assertEquals(expected.getTotalNumber(), received.getTotalNumber());
        }
    }

    @Test
    public void getAllActivitiesOverview() {

        List<ActivitiesOverview> activitiesOverviewExpected =
                List.of(new ActivitiesOverview() {

                            @Override
                            public long getGroupClasses() {
                                return 10;
                            }

                            @Override
                            public long getPrivateClasses() {
                                return -50;
                            }

                            @Override
                            public long getCompletedLessons() {
                                return 30;
                            }

                            @Override
                            public long getCompletedUnits() {
                                return -25;
                            }

                            @Override
                            public long getPracticeHours() {
                                return -20;
                            }

                            @Override
                            public long getLevelPassed() {
                                return 40;
                            }

                            @Override
                            public long getTotalHoursUsage() {
                                return 40;
                            }

                            @Override
                            public String getPeriod() {
                                return "2022-02";
                            }
                        },
                        new ActivitiesOverview() {

                            @Override
                            public long getGroupClasses() {
                                return 20;
                            }

                            @Override
                            public long getPrivateClasses() {
                                return 15;
                            }

                            @Override
                            public long getCompletedLessons() {
                                return 2;
                            }

                            @Override
                            public long getCompletedUnits() {
                                return 1;
                            }

                            @Override
                            public long getPracticeHours() {
                                return 40;
                            }

                            @Override
                            public long getLevelPassed() {
                                return 6;
                            }

                            @Override
                            public long getTotalHoursUsage() {
                                return 80;
                            }

                            @Override
                            public String getPeriod() {
                                return "2022-03";
                            }
                        });

        Mockito.when(personService.getCurrentMonthActivitiesOverview(anyString())).thenReturn(activitiesOverviewExpected);

        String query = "{ " +
                "  getAllActivitiesOverview(salesforcePurchaserId:\"12345\"){ " +
                "     groupClasses" +
                "    groupClassesIncrement" +
                "    privateClasses" +
                "    privateClassesIncrement" +
                "    levelPassed" +
                "    levelPassedIncrement" +
                "    completedLessons" +
                "    completedLessonsIncrement" +
                "    completedUnits" +
                "    completedUnitsIncrement" +
                "    practiceHours" +
                "    practiceHoursIncrement" +
                "    totalHoursUsage" +
                "    totalHoursUsageIncrement" +
                "    period" +
                "    }" +
                "}";
        String projection = "data.getAllActivitiesOverview";

        ActivitiesOverviewWithIncrementsDto activitiesOverviewDtos = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, ActivitiesOverviewWithIncrementsDto.class);

        assertNotNull(activitiesOverviewDtos);


        assertEquals(activitiesOverviewExpected.get(1).getGroupClasses(), activitiesOverviewDtos.getGroupClasses());
        assertEquals(activitiesOverviewExpected.get(0).getGroupClasses(), activitiesOverviewDtos.getGroupClassesIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getPrivateClasses(), activitiesOverviewDtos.getPrivateClasses());
        assertEquals(activitiesOverviewExpected.get(0).getPrivateClasses(), activitiesOverviewDtos.getPrivateClassesIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getCompletedLessons(), activitiesOverviewDtos.getCompletedLessons());
        assertEquals(activitiesOverviewExpected.get(0).getCompletedLessons(), activitiesOverviewDtos.getCompletedLessonsIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getCompletedUnits(), activitiesOverviewDtos.getCompletedUnits());
        assertEquals(activitiesOverviewExpected.get(0).getCompletedUnits(), activitiesOverviewDtos.getCompletedUnitsIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getPracticeHours(), activitiesOverviewDtos.getPracticeHours());
        assertEquals(activitiesOverviewExpected.get(0).getPracticeHours(), activitiesOverviewDtos.getPracticeHoursIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getLevelPassed(), activitiesOverviewDtos.getLevelPassed());
        assertEquals(activitiesOverviewExpected.get(0).getLevelPassed(), activitiesOverviewDtos.getLevelPassedIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getTotalHoursUsage(), activitiesOverviewDtos.getTotalHoursUsage());
        assertEquals(activitiesOverviewExpected.get(0).getTotalHoursUsage(), activitiesOverviewDtos.getTotalHoursUsageIncrement(),0);
        assertEquals(activitiesOverviewExpected.get(1).getPeriod(), activitiesOverviewDtos.getPeriod());

    }
}
