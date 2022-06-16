package com.openenglish.hr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EmailServiceTest {

    @Tested
    private EmailService emailService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void sendContactUsEmail(){
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        String expectedResultMessage = String.format("Message from %s, successfully sent.",name);

        String resultMessage =  emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);

        assertNotNull(resultMessage);
        assertEquals(expectedResultMessage,resultMessage);
    }

    @Test
    public void sendContactUsEmailFailure(){
        String salesforcePurchaserId = "12346";
        String name = "fail";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        String expectedResultMessage = String.format("Message from %s, was not able to be delivered.",name);

        String resultMessage =  emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);

        assertNotNull(resultMessage);
        assertEquals(expectedResultMessage,resultMessage);
    }

    @Test
    public void sendContactUsEmailEmptyPurchaserId(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("salesforcePurchaserId should not be null or empty");
        String salesforcePurchaserId = "";
        String name = "Jack";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);
    }

    @Test
    public void sendContactUsEmailEmptyName(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("name should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "";
        String email = "jack@gmail.com";
        String message = "I need your assistance with....";
        emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);
    }
    @Test
    public void sendContactUsEmailEmptyEmail(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("email should not be null or empty");
        String salesforcePurchaserId = "12345";
        String name = "Jack";
        String email = "";
        String message = "I need your assistance with....";
        emailService.sendContactUsEmail(salesforcePurchaserId, name, email, message);
    }
}