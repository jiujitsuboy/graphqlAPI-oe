package com.openenglish.hr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hr-portal.security")
public class SecurityConfigProperties {
    private boolean enabled = true;
}
