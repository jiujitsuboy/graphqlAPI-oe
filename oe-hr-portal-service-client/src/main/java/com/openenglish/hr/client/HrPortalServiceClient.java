package com.openenglish.hr.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.annotation.Resource;

@Component
public class HrPortalServiceClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(HrPortalServiceClient.class);

  @Value("${HR_PORTAL_SERVICE_HOST}")
  private String serviceEntryPoint;

  @Resource(name = "hrPortalserviceRestTemplate")
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

  private  String buildUrl(String relativePath, String...pathArgs){
    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(serviceEntryPoint)
        .path(relativePath)
        .build()
        .expand(pathArgs);
    return uriComponents.toString();
  }

  private HttpHeaders headForObject(String relativePath, String... pathArgs)
      throws URISyntaxException {
    URI uri = new URI(buildUrl(relativePath, pathArgs));
    return restTemplate.headForHeaders(uri);
  }

  private <RESPONSE> RESPONSE getForObject(String relativePath, Class<RESPONSE> responseType, String... pathArgs) {
    return restTemplate.getForObject(buildUrl(relativePath, pathArgs), responseType);
  }

  private <RESPONSE> RESPONSE postForObject(String relativePath, Object requestBody, Class<RESPONSE> responseType,
                                            String... pathArgs) {

    return restTemplate.postForObject(buildUrl(relativePath, pathArgs), createJsonHttpEntity(requestBody), responseType);
  }

  private void put(String relativePath, Object requestBody, String... pathArgs) {
    restTemplate.put(buildUrl(relativePath, pathArgs), createJsonHttpEntity(requestBody));
  }

  private <RESPONSE> RESPONSE deleteForObject(String relativePath, Class<RESPONSE> responseType,
                                              String... pathArgs) {

    return restTemplate.exchange(buildUrl(relativePath, pathArgs), HttpMethod.DELETE, null, responseType).getBody();
  }

  /**
   * @param body Object that will be added as body of the request
   * @return a new instance of {@link org.springframework.http.HttpEntity}
   */
  private <T> HttpEntity<T> createJsonHttpEntity(T body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<T>(body, httpHeaders);
  }

}
