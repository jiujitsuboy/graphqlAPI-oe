package com.openenglish.pp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.openenglish.pp.common.api.model.TokenDecodedInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtTokenService {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final String EMAIL = "email";

  public Map<String, Claim> getJWTClaims(String token){
    return JWT.decode(token).getClaims();
  }

  public Optional<String> getUserEmail(String token){

    Map<String, Claim> claims = getJWTClaims(token);

    String userEmail = claims.containsKey(EMAIL) ? claims.get(EMAIL).asString() : "";

    return userEmail.isEmpty() ? Optional.empty() : Optional.of(userEmail);
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