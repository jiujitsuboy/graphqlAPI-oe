package com.openenglish.hr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.hrportal.enable:true}")
    String enableHRPortalSecurity;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if ("false".equals(enableHRPortalSecurity)) {
            // @formatter:off
            http
                    .csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/").permitAll();
            // @formatter:on
        } else {
            // @formatter:off
            http
                    .authorizeRequests()
                        .antMatchers("/ping", "/effectiveProperties").permitAll()
                        .antMatchers(HttpMethod.POST, "/graphql").authenticated()
                        .antMatchers(HttpMethod.OPTIONS, "/graphql").authenticated()
                        .anyRequest().denyAll()
                    .and()
                        .oauth2ResourceServer()
                            .jwt();
            // @formatter:on
        }
    }

    @Bean
    JwtDecoder jwtDecoder(){
        JwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation("https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_HoLcrJ6LZ");
        ((NimbusJwtDecoderJwkSupport)jwtDecoder).setJwtValidator(JwtValidators.createDefault());
        //TODO: overwrite the ISS validator, now it is disabled
        return jwtDecoder;
    }

}