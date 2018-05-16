package com.openenglish.pp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class UpsellController {

  private final static Logger logger = LoggerFactory.getLogger(UpsellController.class);

  @RequestMapping(value = "/upsell", method = RequestMethod.POST)
  public ResponseEntity upsell(@RequestBody UpsellInfo upsellInfo) {
    try {
      logger.info("upsell is called...."+upsellInfo);
    } catch (Exception ex) {
      logger.error("Failed running upsell", ex);
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("Finished calling upsell");
    return new ResponseEntity(upsellInfo, HttpStatus.OK);
  }
}
