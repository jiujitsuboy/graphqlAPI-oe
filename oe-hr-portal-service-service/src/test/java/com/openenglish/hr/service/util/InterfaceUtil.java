package com.openenglish.hr.service.util;

import com.openenglish.hr.persistence.entity.aggregation.EmailBelongPurchaserId;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import java.time.LocalDateTime;

public class InterfaceUtil {

  public static UsageLevel createUsageLevel(long personId, String firstname, String lastname,
      String contactId, LocalDateTime lastActivity) {
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
      public String getContactId() {
        return contactId;
      }

      @Override
      public LocalDateTime getLastActivity() {
        return lastActivity;
      }
    };
  }

  public static LevelsPassedByPerson createLevelsPassedByPerson(long personId, String firstName,
      String lastName, String contactId, double totalNumber) {
    return new LevelsPassedByPerson() {
      @Override
      public long getPersonId() {
        return personId;
      }

      @Override
      public String getFirstname() {
        return firstName;
      }

      @Override
      public String getLastname() {
        return lastName;
      }

      @Override
      public String getContactId() {
        return contactId;
      }

      @Override
      public double getTotalNumber() {
        return totalNumber;
      }
    };
  }

  public static EmailBelongPurchaserId createEmailBelongPurchaserId(String contactId, String email,
      String salesForcePurchaserId, boolean matchSalesForcePurchaserId){

    return new EmailBelongPurchaserId() {
      @Override
      public String getContactId() {
        return contactId;
      }

      @Override
      public String getEmail() {
        return email;
      }

      @Override
      public String getSalesForcePurchaserId() {
        return salesForcePurchaserId;
      }

      @Override
      public boolean isMatchSalesForcePurchaserId() {
        return matchSalesForcePurchaserId;
      }
    };
  }
}
