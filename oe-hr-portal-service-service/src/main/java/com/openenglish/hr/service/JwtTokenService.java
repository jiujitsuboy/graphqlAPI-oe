package com.openenglish.hr.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String EMAIL = "email";
    private final String PURCHASER_ID = "custom:purchaserId";

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;


    /**
     * Return a map containing all the claims a JWT token has
     *
     * @param token JWT access token
     * @return JWT claims
     */
    public Map<String, Claim> getJWTClaims(String token) {
        return JWT.decode(token).getClaims();
    }

    /**
     * Retrieve the user's email using his JWT access token
     *
     * @param accessToken JWT access token
     * @return User's Email
     */
    public Optional<String> getUserEmail(String accessToken) {

        return getUserRequest(accessToken, EMAIL);
    }

    /**
     * Retrieve the user's purchaser Id using his JWT access token
     *
     * @param accessToken JWT access token
     * @return User's purchaser Id
     */
    public Optional<String> getUserPurchaserId(String accessToken) {

        return getUserRequest(accessToken, PURCHASER_ID);
    }

    private Optional<String> getUserRequest(String accessToken, String userAttributeName) {
        Optional<String> userAttribute;
        GetUserRequest userRequest = GetUserRequest.builder().accessToken(accessToken).build();
        GetUserResponse userResponse = cognitoIdentityProviderClient.getUser(userRequest);

        userAttribute = userResponse.userAttributes().stream()
                .filter(attribute -> attribute.name().equals(userAttributeName))
                .findFirst()
                .map(attType -> Optional.of(attType.value()))
                .orElse(Optional.empty());


        return userAttribute;
    }
}