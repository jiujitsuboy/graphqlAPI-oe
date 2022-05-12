package com.openenglish.hr.service.util;

import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import java.time.LocalDateTime;

public class InterfaceUtil {

  public static UsageLevel createUsageLevel(long personId, String firstname, String lastname,String contactId, LocalDateTime lastActivity) {
    return new UsageLevel() {
      @Override
      public long getPersonId() {
        return personId;
      }

      @Override
      public String getFirstname() {
        return firstname;
      }

      @Override
      public String getLastname() {
        return lastname;
      }

      @Override
      public String getContactId() { return contactId;}
      @Override
      public LocalDateTime getLastActivity() {
        return lastActivity;
      }
    };
  }

}
