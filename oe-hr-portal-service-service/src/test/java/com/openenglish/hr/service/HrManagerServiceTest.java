package com.openenglish.hr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.persistence.entity.aggregation.EmailBelongPurchaserId;
import com.openenglish.hr.persistence.repository.PersonRepository;
import com.openenglish.hr.service.util.InterfaceUtil;
import java.util.List;
import java.util.Set;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HrManagerServiceTest {

    @Injectable
    private PersonRepository personRepository;
    @Tested
    private HrManagerService emailService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void sendContactUsMessage(){
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";

        MutationResultDto mutationResultDto =  emailService.sendContactUsMessage(salesforcePurchaserId, name, email, message);

        assertNotNull(mutationResultDto);
        assertTrue(mutationResultDto.isSuccess());
    }

    @Test
    public void sendContactUsMessageFailure(){
        String salesforcePurchaserId = "12346";
        String name = "fail";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";

        MutationResultDto mutationResultDto =   emailService.sendContactUsMessage(salesforcePurchaserId, name, email, message);

        assertNotNull(mutationResultDto);
        assertFalse(mutationResultDto.isSuccess());
    }

    @Test
    public void sendContactUsMessageEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        emailService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }

    @Test
    public void sendContactUsMessageEmptyName(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("name should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        emailService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }
    @Test
    public void sendContactUsMessageEmptyEmail(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("email should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "";
        String message = "I need your assistance with....";
        emailService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }

    @Test
    public void sendEmailToSF(){
        String salesforcePurchaserId = "12345";
        Set<String> emails = Set.of("josephp431@unknowdomain.com", "mark0123452@unknowdomain.com", "lauren0456763@unknowdomain.com");
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";

        List<EmailBelongPurchaserId> emailBelongPurchaserIdList = List.of(
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen801", "josephp431@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen091", "mark0123452@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen1001", "lauren0456763@unknowdomain.com", salesforcePurchaserId, true));


        new Expectations() {{
            personRepository.findIfEmailsBelongsToSalesforcePurchaserId(anyString, (Set<String>)any);
            returns(emailBelongPurchaserIdList);
        }};

        MutationResultDto mutationResultDto =  emailService.sendEmailToSF(salesforcePurchaserId, emails, contactsId, message, language);

        assertNotNull(mutationResultDto);
        assertTrue(mutationResultDto.isSuccess());
    }

    @Test
    public void sendEmailToSFOneEmailNotBelongingPurchaserId(){

        String salesforcePurchaserId = "12345";
        Set<String> emails = Set.of("josephp431@unknowdomain.com", "mark0123452@unknowdomain.com", "lauren0456763@unknowdomain.com");
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";

        List<EmailBelongPurchaserId> emailBelongPurchaserIdList = List.of(
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen801", "josephp431@unknowdomain.com", salesforcePurchaserId, false),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen091", "mark0123452@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen1001", "lauren0456763@unknowdomain.com", salesforcePurchaserId, true));

        final String errorMessage = String.format("%s does not belong to purchaser Id %s ", emailBelongPurchaserIdList.get(0).getEmail(), salesforcePurchaserId);

        new Expectations() {{
            personRepository.findIfEmailsBelongsToSalesforcePurchaserId(anyString, (Set<String>)any);
            returns(emailBelongPurchaserIdList);
        }};

        MutationResultDto mutationResultDto =  emailService.sendEmailToSF(salesforcePurchaserId, emails, contactsId, message, language);

        assertNotNull(mutationResultDto);
        assertFalse(mutationResultDto.isSuccess());
        assertEquals(errorMessage, mutationResultDto.getMessage());
    }

    @Test
    public void sendEmailToSFEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        Set<String> emails = Set.of("josephp431@unknowdomain.com", "mark0123452@unknowdomain.com", "lauren0456763@unknowdomain.com");
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";
        emailService.sendEmailToSF(salesforcePurchaserId, emails, contactsId, message, language);
    }

    @Test
    public void sendEmailToSFEmptyEmails(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("emails should not be null or empty");
        String salesforcePurchaserId = "12345";
        Set<String> emails = null;
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";
        emailService.sendEmailToSF(salesforcePurchaserId, emails, contactsId, message, language);
    }

    @Test
    public void sendEmailToSFEmptyContactId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("contactsId should not be null or empty");
        String salesforcePurchaserId = "12345";
        Set<String> emails = Set.of("josephp431@unknowdomain.com", "mark0123452@unknowdomain.com", "lauren0456763@unknowdomain.com");
        Set<String> contactsId = null;
        String message="Test message.....";
        String language="en-US";
        emailService.sendEmailToSF(salesforcePurchaserId, emails, contactsId, message, language);
    }
}