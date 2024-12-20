package com.openenglish.hr.service.util;

import com.openenglish.hr.persistence.entity.aggregation.ContactBelongPurchaserId;
import com.openenglish.hr.persistence.entity.aggregation.LevelsPassedByPerson;
import com.openenglish.hr.persistence.entity.aggregation.OldestActivity;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
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

  public static ContactBelongPurchaserId createEmailBelongPurchaserId(String contactId,
      String salesForcePurchaserId, boolean matchSalesForcePurchaserId){

    return new ContactBelongPurchaserId() {
      @Override
      public String getContactId() {
        return contactId;
      }

      @Override
      public String getSalesforcePurchaserId() {
        return salesForcePurchaserId;
      }

      @Override
      public boolean isMatchSalesforcePurchaserId() {
        return matchSalesForcePurchaserId;
      }
    };
  }

  public static OldestActivity createOldestActivity(Long courseTypeId, LocalDateTime oldestActivityDate){
     return new OldestActivity() {
       @Override
       public Long getCourseTypeId() {
         return courseTypeId;
       }

       @Override
       public LocalDateTime getOldestActivityDate() {
         return oldestActivityDate;
       }
     };
  }

  public static PersonsPerLevel createPersonPerLevel(String levelUuid, int totalNumber) {
    return new PersonsPerLevel() {
      @Override
      public String getLevelUuid() {
        return levelUuid;
      }

      @Override
      public long getTotalNumber() {
        return totalNumber;
      }
    };
  }
}
