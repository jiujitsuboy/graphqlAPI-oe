package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.HRManagerDto;
import com.openenglish.hr.common.dto.LicenseDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.common.dto.PersonsPerLevelDto;
import com.openenglish.hr.persistence.entity.Person;
import com.openenglish.hr.persistence.entity.aggregation.PersonsPerLevel;
import com.openenglish.hr.persistence.entity.aggregation.UsageLevel;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.hr.service.mapper.Mapper;
import com.openenglish.sfdc.client.SalesforceClient;
import com.openenglish.sfdc.client.dto.SfHrManagerInfoDto;
import com.openenglish.sfdc.client.dto.SfLicenseDto;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  /**
   * Send an email from a manager to contact us
   * @param salesforcePurchaserId id of the owner of the license
   * @param name Manager name
   * @param email Manager email
   * @param message message content
   * @return String
   */
  public String sendContactUsEmail(String salesforcePurchaserId, String name, String email, String message) {
    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(name), "name should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(email), "email should not be null or empty");

    String resultMessage = String.format("Message from %s, successfully sent.",name);

    try{
      SendEmail(salesforcePurchaserId, name, email, message);
    }
    catch (Exception ex){
      resultMessage = String.format("Message from %s, was not able to be delivered.",name);
    }

    return resultMessage;
  }

  /**
   * Stub for email sending
   *  @param salesforcePurchaserId id of the owner of the license
   *  @param name Manager name
   *  @param email Manager email
   *  @param message message content
   */
  private void SendEmail(String salesforcePurchaserId, String name, String email, String message){
    if(name.equals("fail")){
      throw new RuntimeException();
    }
    System.out.println(String.format("%s,%s,%s,%s",salesforcePurchaserId, name, email, message));
  }
}