package com.openenglish.pp.client;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

import javax.annotation.Resource;

@Component
public class ServiceClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClient.class);

  @Value("${SERVICE_HOST}")
  private String serviceEntryPoint;

  @Resource(name = "serviceRestTemplate")
  private RestTemplate restTemplate;

  public boolean ping() {
    try {
      restTemplate.getForObject(buildUrl("/ping"), String.class);
    } catch (Exception e) {
      LOGGER.error("Failed calling ping", e);
      return false;
    }
    return true;
  }

  private String buildUrl(String relativePath) throws URISyntaxException {
    return new URIBuilder(serviceEntryPoint).setPath(relativePath).toString();
  }

}
