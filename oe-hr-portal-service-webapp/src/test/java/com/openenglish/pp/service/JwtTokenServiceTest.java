package com.openenglish.pp.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    private final String ACCESS_TOKEN = "eyJraWQiOiJGc2ZzcHJmMVJlMWVac1NKZ1h1SG52RHNMV0VEWlRaSDNQRlZHUDF5cE9rPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI2NzMyMGYxMS04MTQ0LTRkN2QtYmUzMi1jZWE2YzQzMDM2ZWYiLCJldmVudF9pZCI6Ijc1OTI2MmY0LWFlM2MtNGE0NC1hZjYwLTAxZmJkMmY1YTViMiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4gcGhvbmUgb3BlbmlkIHByb2ZpbGUgZW1haWwiLCJhdXRoX3RpbWUiOjE2NDQ5OTExMjgsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX01HOUV3MXdueSIsImV4cCI6MTY0NDk5NDcyOCwiaWF0IjoxNjQ0OTkxMTI4LCJ2ZXJzaW9uIjoyLCJqdGkiOiI1YmU4MDQ5My0zOTZlLTRmNGQtYmY1My1lODAwNTllOTI4MTEiLCJjbGllbnRfaWQiOiJmaWQ1Zm5iMTljNWg3bXQzcmxncWYwNHVqIiwidXNlcm5hbWUiOiI2NzMyMGYxMS04MTQ0LTRkN2QtYmUzMi1jZWE2YzQzMDM2ZWYifQ.byGBpfWepfJ6Wydk46K8kElyWhFHxshDZN6oQhHXRzbHMISL5z-TiBr822gzoq6cjNRzdADRzBAs6O7ob_I6APRUeEAjLtUeUdZJngZR73pz6OD_Rd4ogM5Ud_dvtuGy281eSOixw6otoOtWH8YaauYPZ2rIh42li2_x4FDm1amMRynA65FEfc3VyrMnvePWTrIV24jHNiDECN-jH7ewu4XONMWjcuY05kHmW5RMnKzcff82KrieTKNnDfTV6tFr9KZER9r28yUtl69rcpPV4to2lBsxhT-W1L-jyPMdFmvYAVf6WvhaL5ZwN9hNYT6_YZI_nooybiBgTrTpzLhhvg";
    private final String ID_TOKEN = "eyJraWQiOiJzOVZKZGYxT3JuTW5xQ3VCOWU1M2lQNUZDXC85N3EwVnlBdUNKSVZkTmJkdz0iLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiYlVVWmpBQnVIa0tOc0FoSWxnSWVmUSIsInN1YiI6IjY3MzIwZjExLTgxNDQtNGQ3ZC1iZTMyLWNlYTZjNDMwMzZlZiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9NRzlFdzF3bnkiLCJwaG9uZV9udW1iZXJfdmVyaWZpZWQiOnRydWUsImNvZ25pdG86dXNlcm5hbWUiOiI2NzMyMGYxMS04MTQ0LTRkN2QtYmUzMi1jZWE2YzQzMDM2ZWYiLCJhdWQiOiJmaWQ1Zm5iMTljNWg3bXQzcmxncWYwNHVqIiwiZXZlbnRfaWQiOiI3NTkyNjJmNC1hZTNjLTRhNDQtYWY2MC0wMWZiZDJmNWE1YjIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTY0NDk5MTEyOCwicGhvbmVfbnVtYmVyIjoiKzU3MzAwMjExNjQwNyIsImV4cCI6MTY0NDk5NDcyOCwiaWF0IjoxNjQ0OTkxMTI4LCJqdGkiOiIyMmVkMDZlYS04YTZkLTRiM2YtOTk0MS01ZjRiMWUzZjA1NDMiLCJlbWFpbCI6Impvc2VhbGVqb25pbm9AZ21haWwuY29tIn0.fp8jVIyrwHrsABfHrTza4WI8tZW6eTKgWXlaVvFg0Zu8-FPQL_M0x2Txqml9yzvF-SESJkiBEihsIY8aurBm4vemoCW1bcyKjxCbxtWWZI1lS9X8jNahsxNCdlkTphYc5D2Cx_MC-jI5ydqzqw5tGt6aaJOI2rJQQbmamLE2_IDoY2V2hyFuzwQjJKugCGTaD6xPZlFp28b5H3GKsjZuAkDXoQ4Z-pNQAcb70Og1xpYGwbqbOQPMWA-BwodnE8-yy5iNqRXDXO0yIFbh9SRSDMPj32GZ67xujATuFtlbxnQw2YeySUMn2z7b7s69JFwS_XtZWh-F2hGW6FPzCxDajg";

    @BeforeEach
    public void init(){
        jwtTokenService = new JwtTokenService();
    }

    @Test
    void decodeJWTToken(){
        final String ISS = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_MG9Ew1wny";
        final String TOKEN_USE = "access";
        final String SCOPE = "aws.cognito.signin.user.admin phone openid profile email";

        Map<String, Claim> claims  = jwtTokenService.getJWTClaims(ACCESS_TOKEN);
        assertNotNull(claims);
        assertEquals(ISS,claims.get("iss").asString());
        assertEquals(TOKEN_USE,claims.get("token_use").asString());
        assertEquals(SCOPE,claims.get("scope").asString());

    }

    @Test
    void getUserEmail(){
        String expectedUserEmail = "josealejonino@gmail.com";
        Optional<String> userEmail = jwtTokenService.getUserEmail(ID_TOKEN);
        assertTrue(userEmail.isPresent());
        assertEquals(expectedUserEmail,userEmail.get());
    }

    @Test
    void getUserEmailNotEmailPresent(){
        Optional<String> userEmail = jwtTokenService.getUserEmail(ACCESS_TOKEN);
        assertFalse(userEmail.isPresent());
    }
}