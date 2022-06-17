package com.openenglish.hr;

import com.openenglish.hr.service.JwtTokenService;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecuredByClaimDataAccessInstrumentation extends SimpleInstrumentation {

    private final JwtTokenService jwtTokenService;

    Map<String, String> extractClaimCheckBindings(InstrumentationFieldFetchParameters parameters){

        Map<String, String> result = new LinkedHashMap<>();

        parameters.getField().getArguments().stream()
                .filter(arg -> arg.getDirective("SecuredByClaim") != null)
                .forEachOrdered(arg ->
                    result.put(
                                arg.getName(),
                                (String) arg.getDirective("SecuredByClaim")
                                        .getArgument("claim").getValue()
                            )
                );

        return result;

    }

    @Override
    public DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
        // We only care about user code
        if (parameters.isTrivialDataFetcher()) {
            return dataFetcher;
        }

        Map<String, String> argNameToClaimNameMap = extractClaimCheckBindings(parameters);

        return environment -> {
            if(!argNameToClaimNameMap.isEmpty()){
                JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) (SecurityContextHolder.getContext().getAuthentication());
                String accessToken = jwtAuthenticationToken.getToken().getTokenValue();

                Set<Map.Entry<String, String>> entries = argNameToClaimNameMap.entrySet();
                for (Map.Entry<String, String> claimAndArgNames: entries ) {
                    String argumentToCheck = claimAndArgNames.getKey();
                    String claimToCheck = claimAndArgNames.getValue();

                    String claimValue = jwtTokenService.getUserInfoClaim(accessToken, claimToCheck).orElseThrow(
                            () -> {throw new IllegalArgumentException("Missing required claim :" + claimToCheck);
                            }
                    );

                    String argumentValue = environment.getArgument(argumentToCheck);

                    if (!StringUtils.equals(claimValue, argumentValue)) {
                        log.error("Claim vs Argument mismatch: query={} token={}", argumentValue, claimValue);
                        throw new IllegalArgumentException("Claim vs Argument mismatch: query=" + argumentValue + " token=" + claimValue);
                    }
                }
            }

            return dataFetcher.get(environment);
        };
    }
}