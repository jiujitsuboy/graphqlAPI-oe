package com.openenglish.hr.integrationtest;

import com.openenglish.hr.MyApplication;
import com.openenglish.hr.client.ServiceClient;
import com.openenglish.hr.client.configuration.ServiceClientConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MyApplication.class, ServiceClientConfig.class},
    properties ={"HR_PORTAL_SERVICE_ACCEPT_ALL_SSL_CERTS=true","server.port=8888","HR_PORTAL_SERVICE_HOST:http://localhost:8888"},
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