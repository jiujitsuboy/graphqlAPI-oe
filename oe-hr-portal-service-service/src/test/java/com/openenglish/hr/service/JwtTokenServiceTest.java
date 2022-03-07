package com.openenglish.hr.service;

import com.auth0.jwt.interfaces.Claim;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class JwtTokenServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Injectable
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    @Tested
    private JwtTokenService jwtTokenService;

    private final String ACCESS_TOKEN = "eyJraWQiOiJGc2ZzcHJmMVJlMWVac1NKZ1h1SG52RHNMV0VEWlRaSDNQRlZHUDF5cE9rPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwYjZjZTE5MS01NjM2LTQzZDktYTE5OC02NmZkNzYzNmIzZGYiLCJldmVudF9pZCI6IjE4ODRlZjIwLTU3ODQtNDczNi05M2UzLTczYzMyMDMwODdlNSIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4gcGhvbmUgb3BlbmlkIHByb2ZpbGUgZW1haWwiLCJhdXRoX3RpbWUiOjE2NDUxMjU4MDgsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX01HOUV3MXdueSIsImV4cCI6MTY0NTEyOTQwOCwiaWF0IjoxNjQ1MTI1ODA4LCJ2ZXJzaW9uIjoyLCJqdGkiOiIwNGE0ZTQ0ZS1iZGUwLTQ5NGYtOGJmZC1mOTZhODBjMGFiMmYiLCJjbGllbnRfaWQiOiJmaWQ1Zm5iMTljNWg3bXQzcmxncWYwNHVqIiwidXNlcm5hbWUiOiIwYjZjZTE5MS01NjM2LTQzZDktYTE5OC02NmZkNzYzNmIzZGYifQ.iRRVXoklzWFkI5SeXSuLwACM8fLKKcr9pGr5QKu-VKqQu3j-nK3c2H9urVyRmYhYMdwl7Nn0fplKs6_RCekPH5eq3lG1g_8be8Furee_R8zpVqO8h3ymOJuSBPbrmO-NLADFEaSkIUj0I-b90GgFgPxxoP6AdlqugohwhQFQTtxWTKaeYiGz_RnFEJWjLlnvZye6xAYls0q38YLLYOwngAQIS6p-bkcuXj6eSgzEDMMGX97tL17wOBYIpsPqad8FpmpfnacBGThe1FFMpa8rC7f3TU2n7pq7j6OkLQKIG72hDIx5m_EF8sTGc3FugFD0SXegbpEUZ17nqeZ0ichXlA";

    @Test
    public void decodeJWTToken() {
        final String ISS = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_MG9Ew1wny";
        final String TOKEN_USE = "access";
        final String SCOPE = "aws.cognito.signin.user.admin phone openid profile email";

        Map<String, Claim> claims = jwtTokenService.getJWTClaims(ACCESS_TOKEN);
        assertNotNull(claims);
        assertEquals(ISS, claims.get("iss").asString());
        assertEquals(TOKEN_USE, claims.get("token_use").asString());
        assertEquals(SCOPE, claims.get("scope").asString());

    }

    @Test
    public void getUserEmail() {

        final String attributeTypeName = "email";
        final String attributeTypeValue = "fakeperson@openenglish.com";

        AttributeType attributeType = AttributeType.builder()
                .name(attributeTypeName)
                .value(attributeTypeValue)
                .build();

        GetUserResponse userResponse = GetUserResponse.builder()
                .userAttributes(attributeType)
                .build();

        new Expectations() {{
            cognitoIdentityProviderClient.getUser((GetUserRequest) any);
            returns(userResponse);
        }};

        Optional<String> userEmail = jwtTokenService.getUserEmail(ACCESS_TOKEN);
        assertTrue(userEmail.isPresent());
        assertEquals(attributeTypeValue, userEmail.get());
    }

    @Test
    public void getUserEmailInvalidToken() {

        new Expectations() {{
            cognitoIdentityProviderClient.getUser((GetUserRequest) any);
            result = NotAuthorizedException.builder().message("Invalid Access Token...").build();
        }};

        expectedException.expect(NotAuthorizedException.class);
        expectedException.expectMessage("Invalid Access Token...");

        jwtTokenService.getUserEmail(ACCESS_TOKEN);
    }

    @Test
    public void getUserPurchaserId() {

        final String attributeTypeName = "custom:purchaserId";
        final String attributeTypeValue = "12345";

        AttributeType attributeType = AttributeType.builder()
                .name(attributeTypeName)
                .value(attributeTypeValue)
                .build();

        GetUserResponse userResponse = GetUserResponse.builder()
                .userAttributes(attributeType)
                .build();

        new Expectations() {{
            cognitoIdentityProviderClient.getUser((GetUserRequest) any);
            returns(userResponse);
        }};

        Optional<String> userPurchaserId = jwtTokenService.getUserPurchaserId(ACCESS_TOKEN);
        assertTrue(userPurchaserId.isPresent());
        assertEquals(attributeTypeValue, userPurchaserId.get());
    }
}