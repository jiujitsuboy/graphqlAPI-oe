package com.openenglish.hr.persistence.entity.aggregation;


import java.time.LocalDateTime;

public interface UsageLevels {
    long getPersonId();
    String getFirstname();
    String getLastname();
    LocalDateTime getLastActivity();
}
