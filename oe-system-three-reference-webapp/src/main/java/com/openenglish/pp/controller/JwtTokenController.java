package com.openenglish.pp.controller;

import com.openenglish.pp.common.api.model.TokenDecodedInfo;
import com.openenglish.pp.service.JwtTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "sso-tokens", produces = APPLICATION_JSON_VALUE)
public class JwtTokenController {

  private final static Logger logger = LoggerFactory.getLogger(JwtTokenController.class);
  private JwtTokenService jwtTokenService;

  @Autowired
  public JwtTokenController(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }



  /**
   * Returning the decoded token
   *
   * @param accessToken
   * @return
   * status: 200 if the request gets processed and the  result payload is returned
   * status: 400 if accessToken is missing
   * status: 500 if there's an error
   */
  @RequestMapping(method = {RequestMethod.GET})
  public ResponseEntity getDecodedToken(@RequestParam(value = "accessToken",required = true) String accessToken) {
    try {
      TokenDecodedInfo result = this.jwtTokenService.getTokenDecodedInfo(accessToken);
      return new ResponseEntity(result, OK);
    } catch (Exception ex) {
      logger.error("Failed getting decoded token for: " + accessToken, ex);
      throw ex;
    }
  }
}
