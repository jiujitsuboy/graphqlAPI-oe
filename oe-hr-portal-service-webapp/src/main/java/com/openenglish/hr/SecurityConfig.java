package com.openenglish.hr;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.function.Predicate;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${hrportal.purchaserIdSecurityCheck.enabled:true}")
    private boolean purchaserIdSecurityCheck = true;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (purchaserIdSecurityCheck) {
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
        } else {
            // @formatter:off
            http
                    .csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/").permitAll();
            // @formatter:on
        }
    }

    @Bean
    JwtDecoder jwtDecoder() {
        JwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUrl);

        // by default the JwtDecoder is configured to use JwtTimestampValidator, JwtIssuerValidator.
        // JwtIssuerValidator relies on URL.equals() which takes into account domain name AND host ip.
        // Cognito DNS resolution returns different host IPs in minutes. Therefore, the validation is failing
        // intermittently.
        //
        // the workaround is based on the fix on issue https://github.com/spring-projects/spring-security/issues/9136
        // released in sprig security 5.5.0-M1, compare the configured issuer with the one in token as strings.
        //
        // since at least spring security 5.4, spring security  is not compatible with the current spring
        // core version ( 5.0.4 )
        //
        // Overwrite the result of JwtValidators.createDefaultWithIssuer(oidcIssuerLocation) in
        // JwtDecoders.fromOidcIssuerLocation(issuerUrl) in order to replace
        // JwtIssuerValidator with JwtStringIssuerValidator ( copied and modified to do string.equals since it cannot
        // be extended )
        ((NimbusJwtDecoderJwkSupport) jwtDecoder)
                .setJwtValidator(
                        new DelegatingOAuth2TokenValidator<>(Arrays.asList(
                                new JwtTimestampValidator(),
                                new JwtStringIssuerValidator(issuerUrl)
                        )));

        return jwtDecoder;
    }

    private static final class JwtStringIssuerValidator implements OAuth2TokenValidator<Jwt> {
        private static OAuth2Error INVALID_ISSUER =
                new OAuth2Error(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        "This iss claim is not equal to the configured issuer",
                        "https://tools.ietf.org/html/rfc6750#section-3.1");

        private final Predicate<Object> testClaimValue;

        /**
         * Constructs a {@link org.springframework.security.oauth2.jwt.JwtIssuerValidator} using the provided parameters
         *
         * @param issuer - The issuer that each {@link Jwt} should have.
         */
        public JwtStringIssuerValidator(String issuer) {
            Assert.notNull(issuer, "issuer cannot be null");

            testClaimValue = claimValue -> (claimValue != null) && issuer.equals(claimValue.toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            Assert.notNull(token, "token cannot be null");
            Object claimValue = token.getClaims().get(JwtClaimNames.ISS);

            if (this.testClaimValue.test(claimValue)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(INVALID_ISSUER);
        }

    }


}