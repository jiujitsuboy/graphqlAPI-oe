package com.openenglish.hr.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.openenglish.hr.common.dto.MutationResultDto;
import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HrManagerServiceTest {

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
}