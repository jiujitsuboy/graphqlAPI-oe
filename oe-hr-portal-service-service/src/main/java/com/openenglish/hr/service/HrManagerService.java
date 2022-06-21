package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.persistence.entity.aggregation.EmailBelongPurchaserId;
import com.openenglish.hr.persistence.repository.PersonRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HrManagerService {

  private final PersonRepository personRepository;
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
   * Sending a message created by the manager to a list of students
   * @param salesforcePurchaserId id of the owner of the license
   * @param emails set of destination emails
   * @param contactsId set of student's contactId
   * @param message message content
   * @param language template's language
   * @return MutationResultDto
   */
  public MutationResultDto sendEmailToSF(String salesforcePurchaserId, Set<String> emails, Set<String> contactsId, String message, String language) {

    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(!CollectionUtils.isEmpty(emails), "emails should not be null or empty");
    Preconditions.checkArgument(!CollectionUtils.isEmpty(contactsId), "contactsId should not be null or empty");

    List<EmailBelongPurchaserId> emailBelongPurchaserIds = personRepository.findIfEmailsBelongsToSalesforcePurchaserId(salesforcePurchaserId, emails);

    String notBelongingEmails =  emailBelongPurchaserIds.stream()
        .filter(emailBelongPurchaserId -> !emailBelongPurchaserId.isMatchSalesForcePurchaserId())
        .map(emailBelongPurchaserId -> String.format("%s does not belong to purchaser Id %s ",emailBelongPurchaserId.getEmail(), emailBelongPurchaserId.getSalesForcePurchaserId()))
        .collect(Collectors.joining(", "));

    List<EmailBelongPurchaserId> validRecordsBelongingPurchaserId = emailBelongPurchaserIds.stream()
        .filter(emailBelongPurchaserId -> emailBelongPurchaserId.isMatchSalesForcePurchaserId())
        .collect(Collectors.toList());

    Set<String> validEmailsBelongingPurchaserId = validRecordsBelongingPurchaserId.stream()
        .map(emailBelongPurchaserId -> emailBelongPurchaserId.getEmail())
        .collect(Collectors.toSet());

    Set<String> validContactIdsBelongingPurchaserId = validRecordsBelongingPurchaserId.stream()
        .map(emailBelongPurchaserId -> emailBelongPurchaserId.getContactId())
        .collect(Collectors.toSet());

    MutationResultDto  mutationResultDto = new MutationResultDto();

    try {

      if(!notBelongingEmails.isEmpty()){
        throw new IllegalArgumentException(notBelongingEmails);
      }

      doSendEmailToSF(salesforcePurchaserId, validEmailsBelongingPurchaserId, validContactIdsBelongingPurchaserId, message, language);
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

  /**
   * Stub for email sending to SF
   * @param salesforcePurchaserId id of the owner of the license
   * @param emails set of destination emails
   * @param contactsId set of student's contactId
   * @param message message content
   * @param language template's language
   */
  private void doSendEmailToSF(String salesforcePurchaserId, Set<String> emails, Set<String> contactsId, String message, String language){
    if(message.isEmpty()){
      throw new RuntimeException();
    }
  }
}