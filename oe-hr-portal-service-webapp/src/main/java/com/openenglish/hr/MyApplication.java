package com.openenglish.hr;

import com.openenglish.hr.service.JwtTokenService;
import com.openenglish.substrate.SubstrateConfig;
import com.openenglish.substrate.environment.EnvironmentPropertyConfigurer;
import com.openenglish.substrate.logging.LogbackGraylogConfig;
import com.openenglish.swagger.configuration.Swagger2Config;
import com.openenglish.web.configuration.WebMvcConfig;
import javax.servlet.Filter;
import net.bull.javamelody.MonitoringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ServiceConfig.class, SubstrateConfig.class, Swagger2Config.class,
    LogbackGraylogConfig.class, WebMvcConfig.class})
public class MyApplication implements CommandLineRunner {

  /**
   * https://groups.google.com/forum/#!topic/javamelody/65xJeM-ozms
   *
   * @return FilterRegistrationBean
   */
  @Bean
  public static FilterRegistrationBean javaMelodyFilterRegistration() {
    Filter javaMelodyFilter = new MonitoringFilter();
    FilterRegistrationBean javaMelodyFilterBean = new FilterRegistrationBean(javaMelodyFilter);
    javaMelodyFilterBean.addServletNames("monitoring");
    javaMelodyFilterBean.addUrlPatterns("/*");

    return javaMelodyFilterBean;
  }

  @Bean
  public static EnvironmentPropertyConfigurer envPropertyPlaceholderConfigurer() {
    EnvironmentPropertyConfigurer environmentPropertyConfigurer = new EnvironmentPropertyConfigurer();
    environmentPropertyConfigurer.setGlobalFileName("pp-global-config.properties");
    environmentPropertyConfigurer.setPropFileName("oe-hr-portal-service.properties");
    environmentPropertyConfigurer.setOrder(1);
    return environmentPropertyConfigurer;
  }


  final static Logger logger = LoggerFactory.getLogger(MyApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(MyApplication.class, args);
  }

  @Autowired
  JwtTokenService jwtTokenService;

  @Override
  public void run(String... args) throws Exception {
    String accessToken = "eyJraWQiOiJiS0htRVg2dGkwbk80K0VwNmFlRDhjeXh1VDVmeDdadnc5a3BZNzRJRENvPSIsImFsZyI6IlJTMjU2In0.eyJvcmlnaW5fanRpIjoiYWY3ZGFjNWUtYzQzNS00MWRhLWJhOGEtNmRiYmI2MjYzY2MzIiwic3ViIjoiYTY3MzYxNDUtMjBhZS00MGYzLWIwYjItMTZmYTY1ZjAxMDY4IiwiZXZlbnRfaWQiOiJkOGI0Yjk5OS1mNzA2LTQ2MzEtYjlmNi0wYzU3NDI5YjRmMmEiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNjQ2MzM4MDk1LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9uT1djWDlzRDEiLCJleHAiOjE2NDYzNDE2OTUsImlhdCI6MTY0NjMzODA5NSwianRpIjoiMmI2OTQ2ZmUtYWVlOS00NzM0LTg0YzgtNzQ2ZWRlYTYzZDkxIiwiY2xpZW50X2lkIjoiNjF1ZzJua25icnZwNjR0aTVucWZrbzJrcHEiLCJ1c2VybmFtZSI6ImE2NzM2MTQ1LTIwYWUtNDBmMy1iMGIyLTE2ZmE2NWYwMTA2OCJ9.XnPpT44_t71aq6em__fLbP6by6Q1PG3f0f5bP6ncFHJH-7TOQkkXqkDsfz30G6mJhauqsKFwAXp3V-gb4V-oLHZY5BbnsWBsKiaXYTF-Q4E3G66CVF0gbYvlENGoTfBWqlfx4o3-JWusdJPs_xhjqh81pVZdkTeq6sxy63xjRODsMVLJBOELki5gmTm6U48wx49J1xpJM_qN82aBsAy2OvKqEznaxz7St2D2zC5LQZAHJlAjlqwe21_Dm3hlf6ITawuckygycqIBdiG8zKtkaAXCDRXcRKRYuh0mA9EdX3FQTqV0frNJIfp3BLbf3C7ENft1BWchLsQyRSM-Q-BgUw";
    String respEmail = jwtTokenService.getUserEmail(accessToken).get();
    System.out.println(respEmail);
    String respPurcharseId = jwtTokenService.getUserPurchaserId(accessToken).get();
    System.out.println(respPurcharseId);
  }
}
