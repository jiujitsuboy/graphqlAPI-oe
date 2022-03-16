package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getPersons(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
    }

    public List<PersonsPerLevel> getAllPersonsByLevel(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        return personRepository.getAllPersonsPerLevel(salesforcePurchaserId);
    }

    public List<ActivitiesOverview> getAllActivitiesOverview(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(1);

        String previousMonthDate = String.format("%d-%02d",previous.getYear(), previous.getMonthValue());
        String currentMonthDate = String.format("%d-%02d",now.getYear(), now.getMonthValue());

        return personRepository.getActivitiesOverview(salesforcePurchaserId, previousMonthDate, currentMonthDate);
    }
}
