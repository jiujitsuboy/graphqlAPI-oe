package com.openenglish.hr.graphql.query;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.ActivitiesOverviewDto;
import com.openenglish.hr.common.dto.MonthActivityStatisticsDto;
import com.openenglish.hr.common.dto.PersonActivityTotalDto;
import com.openenglish.hr.common.dto.YearActivityStatisticsDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.MonthActivityStatistics;
import com.openenglish.hr.persistence.entity.aggregation.YearActivityStatistics;
import com.openenglish.hr.service.ActivityService;
import com.openenglish.hr.service.mapper.MappingConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
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

        List<MonthActivityStatistics> monthsActivityStatistics =
                List.of(MonthActivityStatistics.builder().month(1).value(0).build(),
                        MonthActivityStatistics.builder().month(2).value(10).build(),
                        MonthActivityStatistics.builder().month(3).value(20).build(),
                        MonthActivityStatistics.builder().month(4).value(0).build(),
                        MonthActivityStatistics.builder().month(5).value(0).build(),
                        MonthActivityStatistics.builder().month(6).value(0).build(),
                        MonthActivityStatistics.builder().month(7).value(0).build(),
                        MonthActivityStatistics.builder().month(8).value(0).build(),
                        MonthActivityStatistics.builder().month(9).value(0).build(),
                        MonthActivityStatistics.builder().month(10).value(0).build(),
                        MonthActivityStatistics.builder().month(11).value(0).build(),
                        MonthActivityStatistics.builder().month(12).value(0).build()
                );
        YearActivityStatistics yearActivityStatistics = YearActivityStatistics.builder()
                .monthsActivityStatistics(monthsActivityStatistics)
                .total(30).build();

        Mockito.when(activityService.getActivityStatistics(anyString(), anyInt(), anyLong())).thenReturn(yearActivityStatistics);

        String query = "{ " +
                "  getYearActivityStatistics(salesforcePurchaserId:\"12345\", year: 2022, activity: 1){ " +
                "    total " +
                "    monthsActivityStatistics{ " +
                "       month " +
                "       value" +
                "       }" +
                "    }" +
                "}";
        String projection = "data.getYearActivityStatistics";

        YearActivityStatisticsDto yearActivityStatisticsDto =dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, YearActivityStatisticsDto.class);

        assertNotNull(yearActivityStatisticsDto);

        for (int index = 0; index < yearActivityStatisticsDto.getMonthsActivityStatistics().size(); index++) {

            MonthActivityStatistics expected = monthsActivityStatistics.get(index);
            MonthActivityStatisticsDto received = yearActivityStatisticsDto.getMonthsActivityStatistics().get(index);

            assertEquals(expected.getMonth(), received.getMonth());
            assertEquals(expected.getValue(), received.getValue(), 0);
        }
    }

    @Test
    public void getTopStudentsByActivityStatistics() {

        LinkedHashMap<Person, Long> personsTop = new LinkedHashMap<>(5);
        personsTop.put(Person.builder().firstName("Carl").lastName("Thomson").build(), 10L);
        personsTop.put(Person.builder().firstName("Jake").lastName("Sullivan").build(), 20L);
        personsTop.put(Person.builder().firstName("Mark").lastName("Foster").build(), 30L);

        Mockito.when(activityService.getTopStudentsByActivityStatistics(anyString(), any(), any(), anyInt())).thenReturn(personsTop);

        String query = "{ " +
                "  getTopStudentsByActivityStatistics(salesforcePurchaserId:\"12345\", year:2022, month:2, activities: [1,2,3,4,5,6,8,10], top: 3){\n" +
                "    person{ " +
                "      firstName " +
                "      lastName " +
                "    } " +
                "    totalActivities " +
                "  } " +
                "}";
        String projection = "data.getTopStudentsByActivityStatistics[*]";

        List<PersonActivityTotalDto> personActivityTotalDto = dgsQueryExecutor.executeAndExtractJsonPathAsObject(query, projection, new TypeRef<>() {
        });

        assertNotNull(personActivityTotalDto);

        Iterator<Person> persons =  personsTop.keySet().iterator();
        assertThat(persons.next().getFirstName(), is(personActivityTotalDto.get(0).getPerson().getFirstName()));
        assertThat(persons.next().getFirstName(), is(personActivityTotalDto.get(1).getPerson().getFirstName()));
        assertThat(persons.next().getFirstName(), is(personActivityTotalDto.get(2).getPerson().getFirstName()));

    }
}
