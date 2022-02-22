package com.openenglish.hr.health;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * In SpringBoot 2 for health-checking each dependent service, i.e AccountManagerClient in this case, we need to create
 * a HealthIndicator and override the health method like below.
 */

@Component
public class OeAccountManagerHealthIndicator implements HealthIndicator {

  private final Logger logger = LoggerFactory.getLogger(getClass());

//  private AccountManagerClient accountManagerClient;
//
//  @Autowired
//  public OeAccountManagerHealthIndicator(
//      AccountManagerClient accountManagerClient) {
//    this.accountManagerClient = accountManagerClient;
//  }

  @Override
  public Health health() {  //TODO: implement your health check for the respective dependent service here
    logger.info("Starting healthcheck for oe-sso-service");

    boolean isAlive = true;//this.accountManagerClient.ping();
    if (!isAlive) {
      return Health.down()
          .withDetail("oe-account-manager is down!", HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return Health.up().build();
  }

}
