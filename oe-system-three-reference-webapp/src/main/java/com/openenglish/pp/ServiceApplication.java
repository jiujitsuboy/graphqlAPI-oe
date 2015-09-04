package com.openenglish.pp;

import com.openenglish.substrate.SubstrateConfig;
import com.openenglish.substrate.environment.EnvironmentPropertyConfigurer;
import com.openenglish.swagger.configuration.Swagger2Config;

import net.bull.javamelody.MonitoringFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;

@SpringBootApplication
@Import({ServiceConfig.class, SubstrateConfig.class, Swagger2Config.class})
public class ServiceApplication extends SpringBootServletInitializer {

    /**
     * https://groups.google.com/forum/#!topic/javamelody/65xJeM-ozms
     * @return
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
    public static EnvironmentPropertyConfigurer envPropertyPlaceholderConfigurer(){
        EnvironmentPropertyConfigurer environmentPropertyConfigurer = new EnvironmentPropertyConfigurer();
      environmentPropertyConfigurer.setGlobalFileName("pp-global-config.properties");
        environmentPropertyConfigurer.setPropFileName("oe-system-three-reference.properties");
        environmentPropertyConfigurer.setOrder(1);
        return environmentPropertyConfigurer;
    }


    final static Logger logger = LoggerFactory.getLogger(ServiceApplication.class);
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServiceApplication.class, args);
    }
}