package com.openenglish.hr.service;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LicenseAssigneeDto;
import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.persistence.entity.aggregation.ContactBelongPurchaserId;
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
   * @param managerId manager's Id
   * @param contactsId set of student's contactId
   * @param message message content
   * @param language template's language
   * @return MutationResultDto
   */
  public MutationResultDto sendEncouragementEmails(String salesforcePurchaserId, String managerId, Set<String> contactsId, String message, String language) {

    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(managerId), "managerId should not be null or empty");
    Preconditions.checkArgument(!CollectionUtils.isEmpty(contactsId), "contactsId should not be null or empty");

    List<ContactBelongPurchaserId> contactsIdBelongPurchaserIds = personRepository.findIfContactsIdBelongsToSalesforcePurchaserId(salesforcePurchaserId, contactsId);

    String notBelongingContactsId =  contactsIdBelongPurchaserIds.stream()
        .filter(emailBelongPurchaserId -> !emailBelongPurchaserId.isMatchSalesforcePurchaserId())
        .map(emailBelongPurchaserId -> String.format("%s does not belong to purchaser Id %s ",emailBelongPurchaserId.getContactId(), emailBelongPurchaserId.getSalesforcePurchaserId()))
        .collect(Collectors.joining(", "));

    Set<String> validContactIdsBelongingPurchaserId = contactsIdBelongPurchaserIds.stream()
        .filter(contactIdBelongPurchaserId -> contactIdBelongPurchaserId.isMatchSalesforcePurchaserId())
        .map(contactIdBelongPurchaserId -> contactIdBelongPurchaserId.getContactId())
        .collect(Collectors.toSet());

    MutationResultDto  mutationResultDto = new MutationResultDto();

    try {

      if(!notBelongingContactsId.isEmpty()){
        throw new IllegalArgumentException(notBelongingContactsId);
      }

      doSendEncouragementEmails(managerId, validContactIdsBelongingPurchaserId, message, language);
      mutationResultDto.setSuccess(true);
    }
    catch (Exception ex){
      mutationResultDto.setSuccess(false);
      mutationResultDto.setMessage(ex.getMessage());
    }

    return mutationResultDto;

  }

  /**
   * Reassign an active license from one student to a new one
   * @param salesforcePurchaserId id of the owner of the license
   * @param licenseId license id number
   * @param managerId id of the current license student
   * @param currentAssignee current license student
   * @param newAssignee new license student
   * @return MutationResultDto
   */
  public MutationResultDto reassignLicense(String salesforcePurchaserId, String licenseId, String managerId, LicenseAssigneeDto currentAssignee, LicenseAssigneeDto newAssignee) {
    Preconditions.checkArgument(StringUtils.isNotBlank(salesforcePurchaserId), "salesforcePurchaserId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(licenseId), "licenseId should not be null or empty");
    Preconditions.checkArgument(StringUtils.isNotBlank(managerId), "managerId should not be null or empty");
    Preconditions.checkArgument(currentAssignee!= null && StringUtils.isNotBlank(currentAssignee.getFirstName()), "currentAssignee firstname should not be null or empty");
    Preconditions.checkArgument(currentAssignee!= null && StringUtils.isNotBlank(currentAssignee.getEmail()), "currentAssignee email should not be null or empty");
    Preconditions.checkArgument(newAssignee!= null && StringUtils.isNotBlank(newAssignee.getFirstName()), "newAssignee firstname should not be null or empty");
    Preconditions.checkArgument(newAssignee!= null && StringUtils.isNotBlank(newAssignee.getEmail()), "newAssignee email should not be null or empty");

    MutationResultDto  mutationResultDto = new MutationResultDto();

    try{
      doReassignLicense(licenseId, managerId,newAssignee);
      mutationResultDto.setSuccess(true);
    }
    catch (Exception ex){
      mutationResultDto.setSuccess(false);
      mutationResultDto.setMessage(ex.getMessage());
    }

    return mutationResultDto;
  }

  /**
   * Stub Reassign license flow
   * @param licenseId license id number
   * @param contactId id of the current license student
   * @param newAssignee new license student
   */
  private void doReassignLicense(String licenseId, String contactId, LicenseAssigneeDto newAssignee) {
    if(newAssignee.getLastName()==null){
      throw new RuntimeException("Empty newAssignee lastname");
    }
  }

  /**
   * Stub for email sending
   *  @param salesforcePurchaserId id of the owner of the license
   *  @param name sender's name
   *  @param email sender's email
   *  @param message message content
   */
  private void doSendContactUsMessage(String salesforcePurchaserId, String name, String email, String message){
    if(message.isEmpty()){
      throw new RuntimeException();
    }
  }

  /**
   * Stub for email sending to SF
   * @param managerId manager's Id
   * @param contactsId set of student's contactId
   * @param message message content
   * @param language template's language
   */
  private void doSendEncouragementEmails(String managerId, Set<String> contactsId, String message, String language){
    if(message.isEmpty()){
      throw new RuntimeException("Empty message");
    }
  }
}