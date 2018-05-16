package com.openenglish.pp.integrationtest;

import com.openenglish.pp.ServiceApplication;
import com.openenglish.pp.client.ServiceClient;
import com.openenglish.pp.client.configuration.ServiceClientConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServiceApplication.class, ServiceClientConfig.class},
    properties ={"SERVICE_ACCEPT_ALL_SSL_CERTS=true","server.port=8888","SSO_SERVICE_HOST:http://localhost:8888"},
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class PingIntegrationTest {
  @LocalServerPort
  private int port;

  @Autowired
  private ServiceClient serviceClient;

  @Test
  public void pingBinaryClient() {
    System.out.println("++++++++++++++++++PORT:"+port);
    boolean success = serviceClient.ping();
    assertTrue(success);
  }
}