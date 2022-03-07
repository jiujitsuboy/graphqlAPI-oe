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
    String accessToken = "erJraWQiOiJiS0htRVg2dGkwbk80K0VwNmFlRDhjeXh1VDVmeDdadnc5a3BZNzRJRENvPSIsImFsZyI6IlJTMjU2In0.eyJvcmlnaW5fanRpIjoiNjdlODViMjQtNDc0OS00MzM2LWE0ZDktZmU3ODNlOTBiMzkzIiwic3ViIjoiYTY3MzYxNDUtMjBhZS00MGYzLWIwYjItMTZmYTY1ZjAxMDY4IiwiZXZlbnRfaWQiOiI3NzUwZTJjMC04OTY2LTRiMDktODNhZi1mMTVlYTA5ZGMwMTIiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNjQ2Njc5MTQ1LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9uT1djWDlzRDEiLCJleHAiOjE2NDY2ODI3NDUsImlhdCI6MTY0NjY3OTE0NSwianRpIjoiODFhNmM4NmYtN2IyYy00ZDdmLWI0NzQtMjk2M2MzMGJkM2QwIiwiY2xpZW50X2lkIjoiNjF1ZzJua25icnZwNjR0aTVucWZrbzJrcHEiLCJ1c2VybmFtZSI6ImE2NzM2MTQ1LTIwYWUtNDBmMy1iMGIyLTE2ZmE2NWYwMTA2OCJ9.q-45maSb0IPKDKExrfgm0chAH5tJljrLByk57oQ_w2ND91HBFLl3lOT0tMKrKklz-IWLywME7iAANt0bEvG-iZFQUFTm6SwXMvaTSjnLibnhEQ8TgvgnqDnDtjOdniKxILCT2sF-HapUqR-Ti29HLhtaPXXQK-52uujZ8ZOaKpiXeV7WQjSd9IavyUd5Ro5Lorl7Clk2Y4IubUHe3QERToabGwjAWvi9_0XrY40nUbc9BKKcklG8sbEjB3NaoPeQW9ztUyIbZbjl1ntJJkWUO59PfKJFx1IUHdc55D5L46Pdr2YvfsnYRNIi-wv4IwQD2lcGERpB9PSoVZDtqlJ4IQ";
    String respEmail = jwtTokenService.getUserEmail(accessToken).get();
    System.out.println(respEmail);
    String respPurcharseId = jwtTokenService.getUserPurchaserId(accessToken).get();
    System.out.println(respPurcharseId);
  }
}
