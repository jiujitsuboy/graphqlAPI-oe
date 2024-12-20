package com.openenglish.hr.persistence.entity.aggregation;

import com.openenglish.hr.persistence.entity.Person;

public interface LevelsPassedByPerson {
    long getPersonId();
    String getFirstname();
    String getLastname();
    String getContactId();
    double getTotalNumber();
}
