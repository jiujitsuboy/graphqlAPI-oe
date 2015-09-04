package com.openenglish.pp.integrationtest;

import com.openenglish.pp.ServiceApplication;
import com.openenglish.pp.client.ServiceClient;
import com.openenglish.pp.client.configuration.ServiceClientConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceApplication.class, ServiceClientConfig.class})
//@ContextConfiguration(classes = {prop2Config.class,ServiceClientConfig.class})
//@WebIntegrationTest({"server.port=8888","SERVICE_HOST:http://pp-service.dev.openenglish.com:8888"})
@WebIntegrationTest({"SERVICE_ACCEPT_ALL_SSL_CERTS=true","server.port=8888","SERVICE_HOST:http://pp-service.dev.openenglish.com:8888"})
public class PingIntegrationTestSSL {

  @Value("${local.server.port}")
  private int port;



  private RestTemplate restTemplate = new TestRestTemplate();

  @Autowired
  private ServiceClient serviceClient;

  @Test
  public void pingBinaryClient() {
    boolean success = serviceClient.ping();
    assertTrue(success);
  }



}

