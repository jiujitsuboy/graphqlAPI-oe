package com.openenglish.hr.persistence.entity.aggregation;


import java.time.LocalDateTime;

public interface UsageLevel {
    long getPersonId();
    String getFirstname();
    String getLastname();
    String getContactId();
    LocalDateTime getLastActivity();
}
