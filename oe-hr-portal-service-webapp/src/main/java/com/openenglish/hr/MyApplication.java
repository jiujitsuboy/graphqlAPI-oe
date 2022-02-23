package com.openenglish.hr;

import com.openenglish.substrate.SubstrateConfig;
import com.openenglish.substrate.environment.EnvironmentPropertyConfigurer;
import com.openenglish.substrate.logging.LogbackGraylogConfig;
import com.openenglish.swagger.configuration.Swagger2Config;
import com.openenglish.web.configuration.WebMvcConfig;

import javax.servlet.Filter;

import net.bull.javamelody.MonitoringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ServiceConfig.class, SubstrateConfig.class,
        LogbackGraylogConfig.class})
public class MyApplication {

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
}
