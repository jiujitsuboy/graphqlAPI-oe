package com.openenglish.hr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.openenglish.hr.common.dto.LicenseAssigneeDto;
import com.openenglish.hr.common.dto.MutationResultDto;
import com.openenglish.hr.persistence.entity.aggregation.ContactBelongPurchaserId;
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
    private HrManagerService hrManagerService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void sendContactUsMessage(){
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";

        MutationResultDto mutationResultDto =  hrManagerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);

        assertNotNull(mutationResultDto);
        assertTrue(mutationResultDto.isSuccess());
    }

    @Test
    public void sendContactUsMessageFailure(){
        String salesforcePurchaserId = "12346";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "";

        MutationResultDto mutationResultDto =   hrManagerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);

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
        hrManagerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }

    @Test
    public void sendContactUsMessageEmptyName(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("name should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        hrManagerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }
    @Test
    public void sendContactUsMessageEmptyEmail(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("email should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "";
        String message = "I need your assistance with....";
        hrManagerService.sendContactUsMessage(salesforcePurchaserId, name, email, message);
    }

    @Test
    public void sendEncouragementEmails(){
        String salesforcePurchaserId = "12345";
        String managerId = "QWE434566";
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";

        List<ContactBelongPurchaserId> emailBelongPurchaserIdList = List.of(
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen801", "josephp431@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen091", "mark0123452@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen1001", "lauren0456763@unknowdomain.com", salesforcePurchaserId, true));


        new Expectations() {{
            personRepository.findIfContactsIdBelongsToSalesforcePurchaserId(anyString, (Set<String>)any);
            returns(emailBelongPurchaserIdList);
        }};

        MutationResultDto mutationResultDto =  hrManagerService.sendEncouragementEmails(salesforcePurchaserId, managerId, contactsId, message, language);

        assertNotNull(mutationResultDto);
        assertTrue(mutationResultDto.isSuccess());
    }

    @Test
    public void sendEncouragementEmailsOneEmailNotBelongingPurchaserId(){

        String salesforcePurchaserId = "12345";
        String managerId = "QWE434566";
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";

        List<ContactBelongPurchaserId> emailBelongPurchaserIdList = List.of(
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen801", "josephp431@unknowdomain.com", salesforcePurchaserId, false),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen091", "mark0123452@unknowdomain.com", salesforcePurchaserId, true),
            InterfaceUtil.createEmailBelongPurchaserId("sf_synegen1001", "lauren0456763@unknowdomain.com", salesforcePurchaserId, true));

        final String errorMessage = String.format("%s does not belong to purchaser Id %s ", emailBelongPurchaserIdList.get(0).getContactId(), salesforcePurchaserId);

        new Expectations() {{
            personRepository.findIfContactsIdBelongsToSalesforcePurchaserId(anyString, (Set<String>)any);
            returns(emailBelongPurchaserIdList);
        }};

        MutationResultDto mutationResultDto =  hrManagerService.sendEncouragementEmails(salesforcePurchaserId, managerId, contactsId, message, language);

        assertNotNull(mutationResultDto);
        assertFalse(mutationResultDto.isSuccess());
        assertEquals(errorMessage, mutationResultDto.getMessage());
    }

    @Test
    public void sendEncouragementEmailsEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        String managerId = "QWE434566";
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";
        hrManagerService.sendEncouragementEmails(salesforcePurchaserId, managerId, contactsId, message, language);
    }

    @Test
    public void sendEncouragementEmailsEmptyManagerId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("managerId should not be null or empty");
        String salesforcePurchaserId = "12345";
        String managerId = "";
        Set<String> contactsId = Set.of("sf_synegen801","sf_synegen091","sf_synegen1001");
        String message="Test message.....";
        String language="en-US";
        hrManagerService.sendEncouragementEmails(salesforcePurchaserId, managerId, contactsId, message, language);
    }

    @Test
    public void sendEncouragementEmailsEmptyContactId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("contactsId should not be null or empty");
        String salesforcePurchaserId = "12345";
        String managerId = "QWE434566";
        Set<String> contactsId = null;
        String message="Test message.....";
        String language="en-US";
        hrManagerService.sendEncouragementEmails(salesforcePurchaserId, managerId, contactsId, message, language);
    }

    @Test
    public void reassignLicense(){

        String licenseId = "ACX456EDd";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("mary@gmail.com").build();

        MutationResultDto mutationResultDto = hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);

        assertNotNull(mutationResultDto);
         assertTrue(mutationResultDto.isSuccess());
    }

    @Test
    public void reassignLicenseEmpytLicense(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("licenseId should not be null or empty");

        String licenseId = "";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("mary@gmail.com").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }

    @Test
    public void reassignLicenseEmpytContactId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("contactId should not be null or empty");

        String licenseId = "ACX456EDd";
        String contactId = "";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("mary@gmail.com").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }

    @Test
    public void reassignLicenseEmpytCurrentAssigneeFirstName(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("currentAssignee firstname should not be null or empty");

        String licenseId = "ACX456EDd";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("mary@gmail.com").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }

    @Test
    public void reassignLicenseEmpytCurrentAssigneeEmail(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("currentAssignee email should not be null or empty");

        String licenseId = "ACX456EDd";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("mary@gmail.com").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }
    @Test
    public void reassignLicenseEmpytNewAssigneeFirstName(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("newAssignee firstname should not be null or empty");

        String licenseId = "ACX456EDd";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("").lastName("smite").email("mary@gmail.com").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }

    @Test
    public void reassignLicenseEmpytNewAssigneeEmail(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("newAssignee email should not be null or empty");

        String licenseId = "ACX456EDd";
        String contactId = "123ASDc455";
        LicenseAssigneeDto currentAssignee = LicenseAssigneeDto.builder().firstName("jack").lastName("sullivan").email("jacksul@gmail.com").build();
        LicenseAssigneeDto newAssignee = LicenseAssigneeDto.builder().firstName("mary").lastName("smite").email("").build();

        hrManagerService.reassignLicense(licenseId, contactId, currentAssignee, newAssignee);
    }
}