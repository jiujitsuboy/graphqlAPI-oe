package com.openenglish.hr.graphql.query;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.openenglish.hr.common.dto.ActivitiesOverviewWithIncrementsDto;
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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

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


        Optional<ActivitiesOverviewWithIncrementsDto> activitiesOverviewWithIncrementsExpected = Optional.of(ActivitiesOverviewWithIncrementsDto.builder()
                .groupClasses(10)
                .privateClasses(-50)
                .completedLessons(30)
                .completedUnits(-25)
                .practiceHours(-20)
                .levelPassed(40)
                .totalHoursUsage(40)
                .groupClassesIncrement(20)
                .privateClassesIncrement(15)
                .completedLessonsIncrement(2)
                .completedUnitsIncrement(1)
                .practiceHoursIncrement(40)
                .levelPassedIncrement(6)
                .totalHoursUsageIncrement(80)
                .period("2022-03")
                .build());


        Mockito.when(activityService.getCurrentMonthActivitiesOverview(anyString())).thenReturn(activitiesOverviewWithIncrementsExpected);

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


        assertEquals(activitiesOverviewWithIncrementsExpected.get().getGroupClasses(), activitiesOverviewDtos.getGroupClasses());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getGroupClassesIncrement(), activitiesOverviewDtos.getGroupClassesIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getPrivateClasses(), activitiesOverviewDtos.getPrivateClasses());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getPrivateClassesIncrement(), activitiesOverviewDtos.getPrivateClassesIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getCompletedLessons(), activitiesOverviewDtos.getCompletedLessons());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getCompletedLessonsIncrement(), activitiesOverviewDtos.getCompletedLessonsIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getCompletedUnits(), activitiesOverviewDtos.getCompletedUnits());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getCompletedUnitsIncrement(), activitiesOverviewDtos.getCompletedUnitsIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getPracticeHours(), activitiesOverviewDtos.getPracticeHours());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getPracticeHoursIncrement(), activitiesOverviewDtos.getPracticeHoursIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getLevelPassed(), activitiesOverviewDtos.getLevelPassed());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getLevelPassedIncrement(), activitiesOverviewDtos.getLevelPassedIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getTotalHoursUsage(), activitiesOverviewDtos.getTotalHoursUsage());
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getTotalHoursUsageIncrement(), activitiesOverviewDtos.getTotalHoursUsageIncrement(),0);
        assertEquals(activitiesOverviewWithIncrementsExpected.get().getPeriod(), activitiesOverviewDtos.getPeriod());

    }
}
