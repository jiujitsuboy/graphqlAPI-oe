package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.MutationResultDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HrManagerService {

  /**
   * Sending a message created by the manager to contact the account executive.
   * @param salesforcePurchaserId id of the owner of the license
   * @param name  sender's name
   * @param email sender's email
   * @param message message content
   * @return MutationResultDto
   */
  public MutationResultDto sendContactUsMessage(String salesforcePurchaserId, String name, String email, String message) {
    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(name), "name should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(email), "email should not be null or empty");

    MutationResultDto  mutationResultDto = new MutationResultDto();

    try{
      doSendContactUsMessage(salesforcePurchaserId, name, email, message);
      mutationResultDto.setSuccess(true);
    }
    catch (Exception ex){
      mutationResultDto.setSuccess(false);
      mutationResultDto.setMessage(ex.getMessage());
    }


    return mutationResultDto;
  }

  /**
   * Stub for email sending
   *  @param salesforcePurchaserId id of the owner of the license
   *  @param name sender's name
   *  @param email sender's email
   *  @param message message content
   */
  private void doSendContactUsMessage(String salesforcePurchaserId, String name, String email, String message){
    if(name.equals("fail")){
      throw new RuntimeException();
    }
  }
}