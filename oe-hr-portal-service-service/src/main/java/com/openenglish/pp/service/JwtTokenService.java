package com.openenglish.pp.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.openenglish.pp.common.api.model.TokenDecodedInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtTokenService {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final String COGNITO_JWKS_URL ="https://cognito-idp.us-east-1.amazonaws.com/us-east-1_MG9Ew1wny";
  private final String EMAIL = "email";

  public DecodedJWT decodeJWTToken(String token){
    return JWT.decode(token);
  }

  public Optional<String> getUserEmail(String token){

    Map<String, Claim> claims = decodeJWTToken(token).getClaims();

    String userEmail = claims.containsKey(EMAIL) ? claims.get(EMAIL).asString() : "";

    return userEmail.isEmpty() ? Optional.empty() : Optional.of(userEmail);
  }

  public boolean validateJWTToken(String token){
    boolean isValid =false;
    LocalDateTime currentTime = LocalDateTime.now();
    JwkProvider jwkProvider = new UrlJwkProvider(COGNITO_JWKS_URL);

    try {
      DecodedJWT jwt = decodeJWTToken(token);
      LocalDateTime expiredTime = jwt.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

      Jwk jwk = jwkProvider.get(jwt.getKeyId());
      Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(),null);
      algorithm.verify(jwt);

      if(!expiredTime.isBefore(currentTime)){
        isValid = true;
      }

    } catch (JwkException e) {
      logger.error(e.getMessage());
    }

    return isValid;
  }

  public TokenDecodedInfo getTokenDecodedInfo(String token) {
    Preconditions.checkArgument(StringUtils.isNotBlank(token), "token cannot be empty!");
    TokenDecodedInfo tokenDecodedInfo = new TokenDecodedInfo();
    tokenDecodedInfo.setExpiredDate(new Date());
    tokenDecodedInfo.setIssuedDate(new Date());
    tokenDecodedInfo.setPersonId(1L);
    tokenDecodedInfo.setContactId("contactId");
    tokenDecodedInfo.setRoleIds(Lists.newArrayList(1L));
    tokenDecodedInfo.setScope(Lists.newArrayList("lp2-ui"));
    return tokenDecodedInfo;
  }

}