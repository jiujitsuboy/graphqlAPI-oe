package com.openenglish.pp.client.configuration;

import com.openenglish.substrate.calltraceid.RestTemplateCallTraceIdInterceptor;
import com.openenglish.substrate.rest.RestTemplateBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(RestTemplateCallTraceIdInterceptor.class)
@ComponentScan("com.openenglish.pp.client")
public class ServiceClientConfig {

  @Value("${SERVICE_DEFAULT_MAX_PER_ROUTE_CONNECTIONS:50}")
  private int defaultMaxPerRouteConnections;

  @Value("${SERVICE_MAX_TOTAL_CONNECTIONS:100}")
  private int maxTotalConnections;

  @Value("${SERVICE_CONNECTION_TIMEOUT:20000}")
  private int connectionTimeout;

  @Value("${SERVICE_READ_TIMEOUT:60000}")
  private int readTimeout;

  @Value("${SERVICE_CONNECTION_REQUEST_TIMEOUT:30000}")
  private int connectionRequestTimeOut;

  @Value("${SERVICE_ACCEPT_ALL_SSL_CERTS:false}")
  private boolean acceptAllSSLCerts;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private RestTemplateCallTraceIdInterceptor restTemplateCallTraceIdInterceptor;

  @Bean
  public RestTemplate serviceRestTemplate() {
    return RestTemplateBuilder.create(this.applicationContext, this.restTemplateCallTraceIdInterceptor)
        .setDefaultMaxPerRouteConnections(defaultMaxPerRouteConnections)
        .setMaxTotalConnections(maxTotalConnections)
        .setConnectionTimeout(connectionTimeout)
        .setReadTimeout(readTimeout)
        .setConnectionRequestTimeOut(connectionRequestTimeOut)
        .setEnableSSLAcceptAll(acceptAllSSLCerts)
        .build();
  }
}