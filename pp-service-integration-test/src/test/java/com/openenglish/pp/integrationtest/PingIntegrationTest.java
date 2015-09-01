package com.openenglish.pp.integrationtest;

import com.openenglish.pp.PaymentPlatformServiceApplication;
import com.openenglish.pp.client.ServiceClient;
import com.openenglish.pp.client.configuration.ServiceClientConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {PaymentPlatformServiceApplication.class, ServiceClientConfig.class})
@WebIntegrationTest({"SERVICE_ACCEPT_ALL_SSL_CERTS=true","server.port=8888","SERVICE_HOST:http://localhost:8888"})
public class PingIntegrationTest {
  @Value("${local.server.port}")
  private int port;

  @Autowired
  private ServiceClient serviceClient;

  @Test
  public void pingBinaryClient() {
    boolean success = serviceClient.ping();
    assertTrue(success);
  }
}