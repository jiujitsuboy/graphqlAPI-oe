package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.IPersonsPerLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getStudentsBySalesforcePurchaserId(String salesforcePurchaserId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should be non null");
        return personRepository.findPersonByDetailsSalesforcePurchaserId(salesforcePurchaserId);
    }

        public List<IPersonsPerLevel> getAllPersonsByLevel() {
        return personRepository.getAllPersonsPerLevel();
    }
}
