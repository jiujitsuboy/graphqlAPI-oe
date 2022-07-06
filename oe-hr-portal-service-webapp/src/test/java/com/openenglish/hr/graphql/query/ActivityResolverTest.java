package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.api.model.UsageLevelEnum;
import com.openenglish.hr.common.dto.*;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.MappingConfig;
import java.util.Map.Entry;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@Import(MappingConfig.class)
@SpringBootTest(classes = {DgsAutoConfiguration.class, ActivityResolver.class})
public class ActivityResolverTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    private ActivityService activityService;

    @Test
    public void getAllActivitiesOverview() {

        ActivitiesOverviewDto activitiesOverviewExpected = ActivitiesOverviewDto.builder()
                .groupClasses(10)
                .privateClasses(-50)
                .completedLessons(30)
                .completedUnits(-25)
                .practiceHours(-20)
                .levelPassed(40)
                .totalHoursUsage(40)
                .build();

        Mockito.when(activityService.getCurrentMonthActivitiesOverview(anyString())).thenReturn(activitiesOverviewExpected);

        String query = "{ " +
                "  getAllActivitiesOverview(salesforcePurchaserId:\"12345\"){ " +
                "    groupClasses" +
                "    privateClasses" +
                "    levelPassed" +
                "    completedLessons" +
                "    completedUnits" +
                "    practiceHours" +
                "    totalHoursUsage" +
                "    }" +
                "}";
        String projection = "data.getAllActivitiesOverview";

        ActivitiesOverviewDto activitiesOverviewDtos = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, ActivitiesOverviewDto.class);

        assertNotNull(activitiesOverviewDtos);

        assertEquals(activitiesOverviewExpected.getGroupClasses(), activitiesOverviewDtos.getGroupClasses());
        assertEquals(activitiesOverviewExpected.getPrivateClasses(), activitiesOverviewDtos.getPrivateClasses());
        assertEquals(activitiesOverviewExpected.getCompletedLessons(), activitiesOverviewDtos.getCompletedLessons());
        assertEquals(activitiesOverviewExpected.getCompletedUnits(), activitiesOverviewDtos.getCompletedUnits());
        assertEquals(activitiesOverviewExpected.getPracticeHours(), activitiesOverviewDtos.getPracticeHours(), 0);
        assertEquals(activitiesOverviewExpected.getLevelPassed(), activitiesOverviewDtos.getLevelPassed());
        assertEquals(activitiesOverviewExpected.getTotalHoursUsage(), activitiesOverviewDtos.getTotalHoursUsage(), 0);

    }

    @Test
    public void getActivitiesStatistics() {

        List<MonthActivityStatisticsDto> monthsActivityStatisticsDto =
                List.of(MonthActivityStatisticsDto.builder().month(1).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(2).value(10).build(),
                        MonthActivityStatisticsDto.builder().month(3).value(20).build(),
                        MonthActivityStatisticsDto.builder().month(4).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(5).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(6).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(7).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(8).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(9).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(10).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(11).value(0).build(),
                        MonthActivityStatisticsDto.builder().month(12).value(0).build()
                );
        YearActivityStatisticsDto expectedYearActivityStatisticsDto = YearActivityStatisticsDto.builder()
                .monthsActivityStatistics(monthsActivityStatisticsDto)
                .total(30).build();

        Mockito.when(activityService.getActivityStatistics(anyString(), anyInt(), any(), any())).thenReturn(expectedYearActivityStatisticsDto);

        String query = "{ " +
                "  getYearActivityStatistics(salesforcePurchaserId:\"12345\", year: 2022, activity: LIVE_CLASS){ " +
                "    total " +
                "    monthsActivityStatistics{ " +
                "       month " +
                "       value" +
                "       }" +
                "    }" +
                "}";
        String projection = "data.getYearActivityStatistics";

        YearActivityStatisticsDto yearActivityStatisticsDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, YearActivityStatisticsDto.class);

        assertNotNull(yearActivityStatisticsDto);

        for (int index = 0; index < yearActivityStatisticsDto.getMonthsActivityStatistics().size(); index++) {

            MonthActivityStatisticsDto expected = monthsActivityStatisticsDto.get(index);
            MonthActivityStatisticsDto received = yearActivityStatisticsDto.getMonthsActivityStatistics().get(index);

            assertEquals(expected.getMonth(), received.getMonth());
            assertEquals(expected.getValue(), received.getValue(), 0);
        }
    }

    @Test
    public void getTopStudentsByActivityStatistics() {

        LinkedHashMap<PersonDto, Double> personsTop = new LinkedHashMap<>(5);

        PersonDto person1Id = PersonDto.builder().id(10004L).build();
        PersonDto person2Id =  PersonDto.builder().id(10005L).build();
        PersonDto person3Id =  PersonDto.builder().id(10006L).build();

        personsTop.put(person1Id, 10.0);
        personsTop.put(person2Id, 20.0);
        personsTop.put(person3Id, 30.0);

        Mockito.when(activityService.getTopStudentsByActivityStatistics(anyString(), any(), any(), anyInt())).thenReturn(personsTop);

        String query = "{ " +
                "  getTopStudentsByActivityStatistics(salesforcePurchaserId:\"12345\", year:2022, month:2, activities: [LIVE_CLASS], top: 3){ " +
                "    person{" +
                "          id " +
                "          firstName " +
                "          lastName " +
                "          contactId" +
                "    }" +
                "    totalActivities " +
                "  } " +
                "}";
        String projection = "data.getTopStudentsByActivityStatistics[*]";

        List<PersonActivityTotalDto> personActivitiesTotalDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertNotNull(personActivitiesTotalDto);

        int index = 0;
        for(Entry<PersonDto, Double> entry : personsTop.entrySet()){
            assertThat(entry.getValue(), is(personActivitiesTotalDto.get(index++).getTotalActivities()));
        }

    }

    @Test
    public void getUsageLevelOverview() {

        UsageLevelOverviewDto usageLevelsDtoExpected = UsageLevelOverviewDto.builder()
                .high(4L)
                .mediumHigh(3L)
                .mediumLow(2L)
                .low(1L)
                .build();

        Mockito.when(activityService.getUsageLevelOverview(anyString())).thenReturn(usageLevelsDtoExpected);

        String query = "{ " +
                "  getUsageLevelOverview(salesforcePurchaserId:\"12345\"){ " +
                "    high " +
                "    mediumHigh " +
                "    mediumLow " +
                "    low" +
                "    }" +
                "}";
        String projection = "data.getUsageLevelOverview";

        UsageLevelOverviewDto usageLevelsDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, UsageLevelOverviewDto.class);

        assertNotNull(usageLevelsDto);

        assertEquals(usageLevelsDtoExpected.getHigh(), usageLevelsDto.getHigh());
        assertEquals(usageLevelsDtoExpected.getMediumHigh(), usageLevelsDto.getMediumHigh());
        assertEquals(usageLevelsDtoExpected.getMediumLow(), usageLevelsDto.getMediumLow());
        assertEquals(usageLevelsDtoExpected.getLow(), usageLevelsDto.getLow());

    }

    @Test
    public void getLeastActiveStudents() {

        final int PERSONS_SIZE = 2;

        List<PersonUsageLevelDto> personUsageLevelDtos = List.of(
                PersonUsageLevelDto.builder()
                        .person(PersonDto.builder()
                            .firstName("Patrik")
                            .lastName("Smith")
                            .build())
                        .usageLevel(UsageLevelEnum.MEDIUM_LOW)
                        .build(),
                PersonUsageLevelDto.builder()
                    .person(PersonDto.builder()
                        .firstName("Michale")
                        .lastName("Bale")
                        .build())
                        .usageLevel(UsageLevelEnum.LOW)
                        .build()
        );

        Mockito.when(activityService.getLeastActiveStudents(anyString())).thenReturn(personUsageLevelDtos);

        String query = "{ " +
                "  getLeastActiveStudents(salesforcePurchaserId:\"12345\"){ " +
                "    person { "
            + "      firstName "
            + "      lastName "
            + "      contactId "
            + "    }"
            + "    start "
            + "    expiration "
            + "    usageLevel "
            + "    remainingDays "
            + "    inactiveDays" +
                "    }" +
                "}";
        String projection = "data.getLeastActiveStudents";

        List<PersonUsageLevelDto> usageLevelDtos = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertEquals(PERSONS_SIZE, usageLevelDtos.size());

        for (int index = 0; index < personUsageLevelDtos.size(); index++) {
            assertEquals(personUsageLevelDtos.get(index).getPerson().getFirstName(), usageLevelDtos.get(index).getPerson().getFirstName());
            assertEquals(personUsageLevelDtos.get(index).getPerson().getLastName(), usageLevelDtos.get(index).getPerson().getLastName());
            assertEquals(personUsageLevelDtos.get(index).getUsageLevel(), usageLevelDtos.get(index).getUsageLevel());
        }
    }

    @Test
    public void getUsageLevelOverviewPerPerson() {

        Optional<PersonUsageLevelDto> optPersonUsageLevelDto = Optional.of(PersonUsageLevelDto.builder()
            .person(PersonDto.builder()
                .id(110001)
                .firstName("Patrik")
                .lastName("Smith")
                .build())
            .usageLevel(UsageLevelEnum.MEDIUM_LOW)
            .inactiveDays(45)
            .build());

        Mockito.when(activityService.getUsageLevelOverviewPerPerson(anyString(), anyString())).thenReturn(optPersonUsageLevelDto);

        String query = "{ " +
            "  getUsageLevelOverviewPerPerson(salesforcePurchaserId:\"12345\", contactId:\"sf_synegen123\"){ " +
            "    person { "
            + "      id "
            + "      firstName "
            + "      lastName "
            + "      contactId "
            + "    }"
            + "    usageLevel "
            + "    inactiveDays" +
            "    }" +
            "}";
        String projection = "data.getUsageLevelOverviewPerPerson";

        PersonUsageLevelDto personUsageLevelDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, PersonUsageLevelDto.class);

        assertEquals(optPersonUsageLevelDto.get().getPerson().getId(), personUsageLevelDto.getPerson().getId());
        assertEquals(optPersonUsageLevelDto.get().getPerson().getFirstName(), personUsageLevelDto.getPerson().getFirstName());
        assertEquals(optPersonUsageLevelDto.get().getPerson().getLastName(), personUsageLevelDto.getPerson().getLastName());
        assertEquals(optPersonUsageLevelDto.get().getUsageLevel(), personUsageLevelDto.getUsageLevel());
        assertEquals(optPersonUsageLevelDto.get().getInactiveDays(), personUsageLevelDto.getInactiveDays());
    }

    @Test
    public void getActivitiesStatisticsByPersonId() {

        List<MonthActivityStatisticsDto> monthsActivityStatisticsDto =
            List.of(MonthActivityStatisticsDto.builder().month(1).value(0).build(),
                MonthActivityStatisticsDto.builder().month(2).value(10).build(),
                MonthActivityStatisticsDto.builder().month(3).value(20).build(),
                MonthActivityStatisticsDto.builder().month(4).value(0).build(),
                MonthActivityStatisticsDto.builder().month(5).value(0).build(),
                MonthActivityStatisticsDto.builder().month(6).value(0).build(),
                MonthActivityStatisticsDto.builder().month(7).value(0).build(),
                MonthActivityStatisticsDto.builder().month(8).value(0).build(),
                MonthActivityStatisticsDto.builder().month(9).value(0).build(),
                MonthActivityStatisticsDto.builder().month(10).value(0).build(),
                MonthActivityStatisticsDto.builder().month(11).value(0).build(),
                MonthActivityStatisticsDto.builder().month(12).value(0).build()
            );
        YearActivityStatisticsDto expectedYearActivityStatisticsDto = YearActivityStatisticsDto.builder()
            .monthsActivityStatistics(monthsActivityStatisticsDto)
            .total(30).build();

        Mockito.when(activityService.getActivityStatistics(anyString(), anyInt(), any(), any())).thenReturn(expectedYearActivityStatisticsDto);

        String query = "{ " +
            "  getYearActivityStatistics(salesforcePurchaserId:\"12345\", year: 2022, activity: LIVE_CLASS, contactId: \"sf_synegen123\"){ " +
            "    total " +
            "    monthsActivityStatistics{ " +
            "       month " +
            "       value" +
            "       }" +
            "    }" +
            "}";
        String projection = "data.getYearActivityStatistics";

        YearActivityStatisticsDto yearActivityStatisticsDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, YearActivityStatisticsDto.class);

        assertNotNull(yearActivityStatisticsDto);

        for (int index = 0; index < yearActivityStatisticsDto.getMonthsActivityStatistics().size(); index++) {

            MonthActivityStatisticsDto expected = monthsActivityStatisticsDto.get(index);
            MonthActivityStatisticsDto received = yearActivityStatisticsDto.getMonthsActivityStatistics().get(index);

            assertEquals(expected.getMonth(), received.getMonth());
            assertEquals(expected.getValue(), received.getValue(), 0);
        }
    }
    @Test
    public void getOldestActivity(){

        List<OldestActivityDto> expectedOldestActivityDto = List.of(OldestActivityDto.builder()
            .activityName("Live Class")
            .oldestActivityDate("2022-01-30 10:20:50")
            .build(),
            OldestActivityDto.builder()
                .activityName("Private Class")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Practice")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Lesson")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Unit Assessment")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Level Assessment")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("News")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Level Zero")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build(),
            OldestActivityDto.builder()
                .activityName("Idioms")
                .oldestActivityDate("2022-01-30 10:20:50")
                .build());

        Mockito.when(activityService.getOldestActivity(anyString())).thenReturn(expectedOldestActivityDto);

        String query = "{"
            + "     getOldestActivity(salesforcePurchaserId:\"12345\"){"
            + "          activityName "
            + "          oldestActivityDate"
            + "     }"
            + "}";
        String projection = "data.getOldestActivity.[*]";

        List<OldestActivityDto> oldestActivityDtos = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        for(int index = 0; index < oldestActivityDtos.size(); index++){
            assertTrue(oldestActivityDtos.get(index).getActivityName().equals(expectedOldestActivityDto.get(index).getActivityName()));
            assertTrue(oldestActivityDtos.get(index).getOldestActivityDate().equals(expectedOldestActivityDto.get(index).getOldestActivityDate()));
        }
    }
}
